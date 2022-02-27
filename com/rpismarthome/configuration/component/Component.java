/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.configuration.component;

import com.rpismarthome.configuration.ComponentMethodHandler;
import com.google.gson.Gson;
import com.rpismarthome.configuration.component.values.config.ConfigValue;
import com.rpismarthome.configuration.component.Type;
import com.rpismarthome.ComponentSH;
import com.rpismarthome.Main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author zipCoder933
 */
public class Component {

    /**
     * @return the UIpropertiesString
     */
    public String getUIpropertiesString() {
        return UIpropertiesString;
    }

    /**
     * @param UIpropertiesString the UIpropertiesString to set
     */
    public void setUIpropertiesString(String UIpropertiesString) {
        this.UIpropertiesString = UIpropertiesString;
    }

    /**
     * @return the onlyOneSupported
     */
    public boolean isOnlyOneSupported() {
        return onlyOneSupported;
    }

    /**
     * @param onlyOneSupported the onlyOneSupported to set
     */
    public void setOnlyOneSupported(boolean onlyOneSupported) {
        this.onlyOneSupported = onlyOneSupported;
    }

    private boolean onlyOneSupported = false;

    /**
     * @return the propertiesString
     */
    public String getPropertiesString() {
        return propertiesString;
    }

    /**
     * @param propertiesString the propertiesString to set
     */
    public void setPropertiesString(String propertiesString) {
        this.propertiesString = propertiesString;
    }

    public void clearPropertiesString() {
        this.propertiesString = null;
    }

    /**
     * @return the sensor
     */
    public boolean isSensor() {
        return sensor;
    }

    /**
     * @param configProperties the configProperties to set
     */
    public void setConfigProperties(HashMap<String, ConfigValue> configProperties) {
        this.configProperties = configProperties;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(HashMap<String, Value> properties) {
        this.properties = properties;
    }

    /**
     * @return the method
     */
    public ComponentType getMethod() {
        return method;
    }

    /**
     * @param gatheringMethod the method to set
     */
    public void setMethod(ComponentType gatheringMethod) {
        this.method = gatheringMethod;
    }

    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }

    /**
     * @return the type
     */
    public final String getType() {
        return type;
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    public final void gatherData() throws Exception {
        ComponentMethodHandler.gatherData(getMethod(), this);
    }

    public final String initialize() throws Exception {
        return ComponentMethodHandler.initializeComponent(getMethod(), this);
    }

    public final Value getProperty(String key) {
        return properties.get(key);
    }

    public final void setProperty(String key, double val) throws IOException {
        getProperty(key).setValue(val);
        ComponentSH.updateValue(this, key);
    
    }

    public final void setProperty(String key, String val) throws IOException {
        getProperty(key).setValue(val);
        ComponentSH.updateValue(this, key);
   
    }

    public final void setProperty(String key, boolean val) throws IOException {
        getProperty(key).setValue(val);
        ComponentSH.updateValue(this, key);
    }

    public final void handlePropertyChange(String key) throws IOException {
        try {
            ComponentMethodHandler.setValue(this, key);
        } catch (InterruptedException ex) {
            Logger.getLogger(Component.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void setPropertyBasedOnType(String key, String value) throws IOException {
      
            Type type = getProperty(key).getType();
            switch (type) {
                case BOOLEAN:
                    getProperty(key).setValue(Boolean.parseBoolean(value));
                    break;
                case NUMBER:
                    getProperty(key).setValue(Double.parseDouble(value));
                    break;
                case STRING:
                    getProperty(key).setValue(value);
                    break;
            }
            ComponentSH.updateValue(this, key);
       
    }

    public final void setProperty(String key, Object value) throws IOException {
     
            getProperty(key).setValue(value);
            ComponentSH.updateValue(this, key);
       
    }

    private boolean sensor;

    public Component(String title, boolean isSensor) {
        this.title = title;
        configProperties = new HashMap<>();
        properties = new HashMap<>();
        this.type = title.replace(" ", "-").toLowerCase().trim();
        method = ComponentType.DEFAULT;
        sensor = isSensor;
        id = UUID.randomUUID().toString();
    }

    private String id;
    private HashMap<String, ConfigValue> configProperties;
    private String type;
    private String title;
    private HashMap<String, Value> properties;
    private ComponentType method;

    /**
     * @return the properties
     */
    protected final HashMap<String, Value> getProperties() {
        return properties;
    }

    /**
     * @return the configProperties
     */
    public final HashMap<String, ConfigValue> getConfigProperties() {
        return configProperties;
    }

    public final String renderHTML(StringBuilder str) {
        str.append("<div class='component col-md-5'><h3><input onchange='changeTitle(\"")
                .append(getId())
                .append("\",this.value)' value='")
                .append(getTitle()).append("'/><i class='fa delete' onclick='deleteComponent(\"")
                .append(getId()).append("\")'>&#xf1f8;</i></h3><div class='type'>")
                .append(type).append("</div>");

        if (propertiesString != null) {
            str.append("<h6 class='propStr' id='")
                    .append(id).append("'>")
                    .append(propertiesString)
                    .append("</h6>");
        }
        str.append("<table>");
        for (String key : getProperties().keySet()) {
            str.append(getProperties().get(key).renderHTML(id, key));
        }
        str.append("</table></div>");

        return str.toString();
    }

    // public final String renderConfigValuesHTML() {
    // StringBuilder str = new StringBuilder();
    // for (String key : getProperties().keySet()) {
    // str.append(getProperties().get(key).renderHTML(key));
    // }
    // return str.toString();
    // }
    public final String printConfigProperties() {
        // Use gson to convert configProperties to json
        Gson gson = new Gson();
        String json = gson.toJson(getConfigProperties());
        return json;
    }

    public final ArrayList<Value> getPropertyValues() {
        // Convert the hashmap into an arraylist of values
        ArrayList<Value> listOfValues = getProperties().values().stream().collect(
                Collectors.toCollection(ArrayList::new));
        return listOfValues;
    }

    public final ArrayList<String> getPropertyKeys() {
        // Convert the hashmap into an arraylist of keys
        ArrayList<String> listOfKeys = getProperties().keySet().stream().collect(
                Collectors.toCollection(ArrayList::new));
        return listOfKeys;
    }

    @Override
    public String toString() {
        // Use gson to convert this object to json
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

//    public void resetChangeTags() {
//       //Iterate over each property and set its hasChanged value to false
//        for (String key : getProperties().keySet()) {
//            getProperties().get(key).setHasChanged(false);
//        }
//    }
    private String propertiesString = null;
     private String UIpropertiesString = null;
}
