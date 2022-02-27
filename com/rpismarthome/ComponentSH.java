/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome;

import com.google.gson.Gson;
import com.rpismarthome.Main;
import com.rpismarthome.configuration.component.Component;
import com.rpismarthome.configuration.Configuration;
import com.rpismarthome.configuration.component.values.config.ConfigType;
import com.rpismarthome.configuration.component.values.config.ConfigValue;
import com.rpismarthome.configuration.component.Type;
import com.rpismarthome.configuration.component.Value;
import static com.rpismarthome.MainSocketServer.writeToAllClients;
import com.rpismarthome.utils.gpio.GPIOUtils;
import com.rpismarthome.utils.webSocket.WebSocketClient;
import com.rpismarthome.utils.webSocket.WebSocketUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author zipCoder933
 */
public class ComponentSH implements SocketHandler {

    HashMap<String, ConfigValue> configProperties;
    Component component = null;
    private static MainSocketServer parent;

    public ComponentSH(MainSocketServer parent) {
        configProperties = new HashMap<>();
        this.parent = parent;
    }

    @Override
    public void onMessageRecieved(WebSocketClient client, String[] message) throws Exception {
        if (message[1].equals("GET_CONFIG_VAL_LIST")) {
            System.out.println("Setting component type to " + message[2]);
            component = Configuration.getComponentClassByLabel(message[2]);
            HashMap<String, ConfigValue> map = component.getConfigProperties();

            ArrayList<String> listOfKeys = map.keySet().stream().collect(
                    Collectors.toCollection(ArrayList::new));

            Gson gson = new Gson();

            ArrayList<ConfigValue> listOfValues = map.values().stream().collect(
                    Collectors.toCollection(ArrayList::new));

            WebSocketUtils.write(client, "CONFIG_VAL_KEYS", gson.toJson(listOfKeys));
            WebSocketUtils.write(client, "CONFIG_VAL_VALS", gson.toJson(listOfValues));// Must be done in the correct
        } else if (message[1].equals("CONF_PARAM")) {
            try {
                String key = message[2];
                String value = message[3];
                System.out.println("Key: " + key + " Value: " + value);
                ConfigValue entry = component.getConfigProperties().get(key);
                if (entry.getType() == ConfigType.BOOLEAN) {
                    boolean val = value.equals("true");
                    entry.setValue(val);
                } else if (entry.getType() == ConfigType.STRING) {
                    entry.setValue(value);
                } else if (entry.getType() == ConfigType.NUMBER) {
                    double val = Double.parseDouble(value.trim());
                    entry.setValue(val);
                } else if (entry.getType() == ConfigType.PIN) {
                    System.out.println("Setting value to a new pin");
                    int pin = Integer.parseInt(value.trim());
                    pin = GPIOUtils.clampPin(pin);
                    entry.setValue(pin);
                }
            } catch (Exception e) {
                System.err.println("Configuration error: " + e.getMessage());
            }
        } else if (message[1].equals("new")) {
            System.out.println("NEW COMPONENT");

            if (component.isOnlyOneSupported()) {
                for (Component comp : Main.getConfig().getComponents()) {
                    if (comp.getType().equals(component.getType())) {
                         parent.writeInfo("<b>Cant create component:</b>"
                                + "<br>Only 1 " + comp.getTitle() + " component supported.");
                        return;
                    }
                }
            }

            try {
                String errorString = component.initialize();
                if (!errorString.equals("")) {
                     parent.writeInfo( "<b>Error creating component:</b>"
                            + "<br>(Component initialization error)"
                            + "<br>" + errorString);
                    return;
                }
                component.gatherData();
                Main.getConfig().getComponents().add(component);
                Main.getConfig().save();
                updateComponents();
               parent.writeInfo( "<b>Component created.</b>"
                        + "<br>If you have a trained assistant, you might need to retrain it to accomidate the new changes.");
            } catch (Exception e) {
                e.printStackTrace();
               parent.writeInfo( "<b>Error creating component:</b>"
                        + "<br>Exception: " + e.getMessage() + "<br>Look at your configuration parameters.");
            }
        } else if (message[1].equals("delete")) {
            String id = message[2];
            Main.getConfig().deleteComponent(id);
            Main.getConfig().save();
            System.out.println("DELETING COMPONENT " + id);
             parent.writeInfo( "<b>Component deleted.</b>"
                    + "<br>If you have a trained assistant, you might need to retrain it to accomidate the new changes.");
            updateComponents();
        } else if (message[1].equals("changeTitle")) {
            String id = message[2];
            Main.getConfig().getComponent(id).setTitle(message[3]);
           parent.writeInfo( "<b>Component title changed.</b>"
                    + "<br>If you have a trained assistant, you might need to retrain it to accomidate the new changes.");
        } else if (message[1].equals("setValue")) {
            try {
                System.out.println("Setting value...");
                String id = message[2];
                String key = message[3];
                String val = message[4];
                Type type = Main.getConfig()
                        .getComponent(id)
                        .getProperty(key).getType();
                if (null != type) {
                    Component comp = Main.getConfig().getComponent(id);
                    switch (type) {
                        case BOOLEAN:
                            boolean bool = Boolean.parseBoolean(val);
                            comp.setProperty(key, bool);
                            break;
                        case NUMBER:
                            double num = Double.parseDouble(val);
                            comp.setProperty(key, num);
                            break;
                        case STRING:
                            comp.setProperty(key, val);
                            break;
                        default:
                            break;
                    }
                    comp.handlePropertyChange(key);
                }
            } catch (Exception e) {
                System.err.println("Value setting error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void updateComponents() throws IOException {
        for (int i = 0; i < parent.clients.size(); i++) {
            WebSocketUtils.write(parent.clients.get(i), "UPDATE_COMPONENTS", Main.getConfig().renderHTML());
        }
    }

    public static void updateValue(Component comp, String key) throws IOException {
        String htmlID = "#" + Value.formatID(comp.getId(), key);
        Value val = comp.getProperty(key);
        for (int i = 0; i < parent.clients.size(); i++) {
            if (val.isWritable()) {
                if (val.getType() == Type.BOOLEAN) {
                    WebSocketUtils.write(parent.clients.get(i), "UPDATE_CHECKED_VALUE", htmlID,
                            val.getValueAsString());
                } else if (val.getType() == Type.NUMBER || val.getType() == Type.STRING) {
                    WebSocketUtils.write(parent.clients.get(i), "UPDATE_VALUE_VALUE", htmlID,
                            val.getValueAsString());
                }
            } else {
                WebSocketUtils.write(parent.clients.get(i), "UPDATE_HTML_VALUE", htmlID,
                        val.getValueAsString());
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    public static void updateWritableValues() throws Exception {
        for (int i = 0; i < parent.clients.size(); i++) {
            for (int j = 0; j < Main.getConfig().getComponents().size(); j++) {
                String compID = Main.getConfig().getComponents().get(j).getId();
//                HashMap<String, Value> map = Main.getConfig().getComponents().get(j).getProperties();

                // Convert the hashmap into an arraylist of values
                ArrayList<Value> listOfValues = Main.getConfig().getComponents().get(j).getPropertyValues();
                // Convert the hashmap into an arraylist of keys
                ArrayList<String> listOfKeys = Main.getConfig().getComponents().get(j).getPropertyKeys();

                for (int k = 0; k < listOfKeys.size(); k++) {
                    Value val = listOfValues.get(k);
                    String key = listOfKeys.get(k);

                    String htmlID = "#" + Value.formatID(compID, key);

                    if (val.isWritable()) {
                        // System.out.println("Changing content: " + "UPDATE_READONLY_VALUE|" + htmlID +
                        // "|" + val.getValueAsString());
                        if (val.getType() == Type.BOOLEAN) {
                            WebSocketUtils.write(parent.clients.get(i), "UPDATE_CHECKED_VALUE", htmlID,
                                    val.getValueAsString());
                        } else if (val.getType() == Type.NUMBER || val.getType() == Type.STRING) {
                            WebSocketUtils.write(parent.clients.get(i), "UPDATE_VALUE_VALUE", htmlID,
                                    val.getValueAsString());
                        }

                    }
                }
            }
        }
    }

    public static void updateReadOnlyValues() throws Exception {
        for (int i = 0; i < parent.clients.size(); i++) {
            for (int j = 0; j < Main.getConfig().getComponents().size(); j++) {
                String compID = Main.getConfig().getComponents().get(j).getId();

                // Convert the hashmap into an arraylist of values
                ArrayList<Value> listOfValues = Main.getConfig().getComponents().get(j).getPropertyValues();
                // Convert the hashmap into an arraylist of keys
                ArrayList<String> listOfKeys = Main.getConfig().getComponents().get(j).getPropertyKeys();

//                HashMap<String, Value> map = Main.getConfig().getComponents().get(j).getProperties();
//
//                // Convert the hashmap into an arraylist of values
//                ArrayList<Value> listOfValues = map.values().stream().collect(
//                        Collectors.toCollection(ArrayList::new));
//                // Convert the hashmap into an arraylist of keys
//                ArrayList<String> listOfKeys = map.keySet().stream().collect(
//                        Collectors.toCollection(ArrayList::new));
                for (int k = 0; k < listOfKeys.size(); k++) {
                    Value val = listOfValues.get(k);
                    String key = listOfKeys.get(k);

                    String htmlID = "#" + Value.formatID(compID, key);

                    if (!val.isWritable()) {
                        // System.out.println("Changing content: " + "UPDATE_READONLY_VALUE|" + htmlID +
                        // "|" + val.getValueAsString());
                        WebSocketUtils.write(parent.clients.get(i), "UPDATE_HTML_VALUE", htmlID,
                                val.getValueAsString());
                    }
                }
            }
        }
    }
//</editor-fold>
}
