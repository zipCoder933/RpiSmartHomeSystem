/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.configuration2;

import com.rpismarthome.configuration2.component.components.LightSensor;
import com.rpismarthome.configuration2.component.components.Time;
import com.rpismarthome.configuration2.component.components.VerboseOutput;
import com.rpismarthome.configuration2.component.components.VolumeSensor;
import com.rpismarthome.configuration2.component.components.Light;
import com.rpismarthome.configuration2.component.components.Microphone;
import com.rpismarthome.configuration2.component.components.RGBLight;
import com.rpismarthome.configuration2.component.components.TempratureSensor;
import com.rpismarthome.configuration2.component.components.WeatherAPI;
import com.rpismarthome.configuration2.component.Component;
import com.google.gson.Gson;
import com.rpismarthome.Test;
import com.rpismarthome.server.ComponentSH;
import com.rpismarthome.server.MainSocketServer;
import com.rpismarthome.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class Configuration {

    /**
     * @return the dataGatheringUpdateFrequency
     */
    public long getDataGatheringUpdateFrequency() {
        return dataGatheringUpdateFrequency;
    }

    /**
     * @param dataGatheringUpdateFrequency the dataGatheringUpdateFrequency to
     * set
     */
    public void setDataGatheringUpdateFrequency(long dataGatheringUpdateFrequency) {
        this.dataGatheringUpdateFrequency = dataGatheringUpdateFrequency;
    }

    public static Component getComponentClassByLabel(String label) throws NoSuchMethodException {
        if (label.equals("light")) {
            return new Light();
        } else if (label.equals("light-sensor")) {
            return new LightSensor();
        } else if (label.equals("rgb-light")) {
            return new RGBLight();
        } else if (label.equals("temprature-sensor")) {
            return new TempratureSensor();
        } else if (label.equals("time")) {
            return new Time();
        } else if (label.equals("volume-sensor")) {
            return new VolumeSensor();
        } else if (label.equals("weather-api")) {
            return new WeatherAPI();
        } else if (label.equals("verbose-output")) {
            return new VerboseOutput();
        } else if (label.equals("usb-microphone")) {
            return new Microphone();
        } else {
            return null;
        }
    }

    public Component getComponent(String id) {
        for (Component component : root.components) {
            if (component.getId().equals(id)) {
                return component;
            }
        }
        return null;
    }

    public ArrayList<Component> getComponents() {
        return root.components;
    }

    public void deleteComponent(String id) {
        // root.components.remove(id);
        //Iterate through the components using a regular for loop and remove the one with the id
        for (int i = 0; i < root.components.size(); i++) {
            if (root.components.get(i).getId().equals(id)) {
                root.components.remove(i);
                break;
            }
        }
    }

    static Gson gson;
    static String file = "config.json";
    static Root root;
    private long dataGatheringUpdateFrequency = 5000;
//    private DeepParameterTuner tuner;

    public void save() throws IOException {
        String json = gson.toJson(root);
        FileUtils.writeString(file, json);
    }

    public String renderHTML() {
        StringBuilder sb = new StringBuilder();
        for (Component c : root.components) {
            c.renderHTML(sb);
        }
        return sb.toString();
    }

    public Configuration(boolean overwriteConfig) throws IOException, Exception {
//        tuner = new DeepParameterTuner();
        ComponentMethodHandler.init();
        gson = new Gson();
        File f = FileUtils.file(file);
        if (!f.exists() || overwriteConfig) {
            System.out.println("Creating new configuration...");
            root = new Root();
        } else {
            String json = FileUtils.readString(file);
            root = gson.fromJson(json, Root.class);

        }
        System.out.println("Data gathering update frequency: " + dataGatheringUpdateFrequency);

        (new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(getDataGatheringUpdateFrequency());
                        for (int i = 0; i < root.components.size(); i++) {
                            Component comp = root.components.get(i);
                            comp.gatherData();
                            if (comp.getPropertiesString() != null) {
                                MainSocketServer.writeToAllClients("UPDATE_HTML_VALUE", "#" + comp.getId(), comp.getPropertiesString());
                            }
                        }
//                        tuner.setParameters();
                    } catch (Exception ex) {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                         MainSocketServer.writeInfo( "Component data gathering error: " + ex.getMessage());
                    }
                }
            }
        }).start();

        (new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        save();
                    } catch (Exception ex) {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }

}
