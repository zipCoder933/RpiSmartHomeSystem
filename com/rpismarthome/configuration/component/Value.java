/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.configuration.component;

import com.google.gson.Gson;
import com.rpismarthome.configuration.component.IsWritable;
import com.rpismarthome.configuration.component.Type;
import com.rpismarthome.configuration.component.Type;

/**
 *
 * @author zipCoder933
 */
public class Value {


    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return value.toString();
    }

    private int getValueAsInt() {
        return (int) value;
    }

    public boolean getValueAsBoolean() {
        return (boolean) value;
    }

    private double getValueAsDouble() {
        return (double) value;
    }

    public double getValueAsNumber() {
        try {
            return getValueAsDouble();
        } catch (Exception e) {
            try {
                return (double) getValueAsInt();
            } catch (Exception e1) {
                try {
                    return (double) getValueAsFloat();
                } catch (Exception e2) {
                    try {
                        return (double) getValueAsLong();
                    } catch (Exception e3) {
                        return (double) getValueAsShort();
                    }
                }
            }

        }
    }
    
    protected void setValue(Object obj){
    value = obj;
    }

    private float getValueAsFloat() {
        return (float) value;
    }

    private long getValueAsLong() {
        return (long) value;
    }

    private short getValueAsShort() {
        return (short) value;
    }

    public byte getValueAsByte() {
        return (byte) value;
    }

    public char getValueAsChar() {
        return (char) value;
    }

    public boolean isWritable() {
        return writable == IsWritable.WRITABLE;
    }

    private Type type;
    private Object value;
    IsWritable writable;

    public Value(Type type, IsWritable writable, Object defaultValue) {
        this.type = type;
        this.writable = writable;
        this.value = defaultValue;
    }

    public static String formatID(String id, String key) {
        return id + "-" + key.replace(" ", "_").replace("(", "")
                .replace(")", "").replace("%", "").replace("/", "").replace("\\", "").toLowerCase();
    }

    private String getID(String id, String key) {
        return "id='" + formatID(id,key) + "'";
    }

    private String getChangeEvent(String id, String key, String value) {
        return "onchange='componentSetValue(\"" + id + "\",\"" + key + "\"," + value + ")'";
    }

    public String renderHTML(String compID, String key) {
        if (getType() == Type.BOOLEAN && isWritable()) {
            boolean bool = Boolean.parseBoolean(value.toString());
            return ("<tr><td>" + key
                    + ":</td><td><label class=\"switch\"><input " + getID(compID, key) + " " + getChangeEvent(compID, key, "$(this).is(\":checked\")") + " type=\"checkbox\" " + (bool == true ? "checked" : "")
                    + "><span class=\"slider round\"></span></label></td></tr>");
        } else if (getType() == Type.BOOLEAN) {
            boolean bool = Boolean.parseBoolean(value.toString());
            return ("<tr><td>" + key + ":</td><td><span class=\"readonlyBoolean\" " + getID(compID, key) + ">"
                    + (bool ? "ON" : "OFF") + "</span></td></tr>");
        } else if (getType() == Type.NUMBER && isWritable()) {
            try {
                double number = ((double) value);
                return number(compID, key, number);
            } catch (java.lang.ClassCastException e) {
                try {
                    int number = ((int) value);
                    return number(compID, key, number);
                } catch (java.lang.ClassCastException e1) {
                    try {
                        long number = ((long) value);
                        return number(compID, key, number);
                    } catch (java.lang.ClassCastException e2) {
                        try {
                            short number = ((short) value);
                            return number(compID, key, number);
                        } catch (java.lang.ClassCastException e3) {
                            try {
                                float number = ((float) value);
                                return number(compID, key, number);
                            } catch (java.lang.ClassCastException e4) {
                                e4.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else if (getType() == Type.NUMBER && !isWritable()) {
            try {
                double number = (double) value;
                return readOnlyNumber(compID, key, number);
            } catch (java.lang.ClassCastException e) {
                try {
                    int number = (int) value;
                    return readOnlyNumber(compID, key, number);
                } catch (java.lang.ClassCastException e1) {
                    try {
                        long number = (long) value;
                        return readOnlyNumber(compID, key, number);
                    } catch (java.lang.ClassCastException e2) {
                        try {
                            short number = (short) value;
                            return readOnlyNumber(compID, key, number);
                        } catch (java.lang.ClassCastException e3) {
                            try {
                                float number = (float) value;
                                return readOnlyNumber(compID, key, number);
                            } catch (java.lang.ClassCastException e4) {
                                e4.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else if (getType() == Type.STRING && isWritable()) {
            String str = value.toString();
            return "<tr><td>" + key + ":</td><td><input " + getID(compID, key) + " " + getChangeEvent(compID, key, "this.value") + " placeholder=\"Value\" value='" + str + "' type=\"text\"></td></tr>";
        } else if (getType() == Type.STRING) {
            String str = value.toString();
            return "<tr><td>" + key + ":</td><td><span class=\"readonlyString\" " + getID(compID, key) + ">" + str
                    + "</span></td>" + "</tr>";
        }
        return null;
    }

    // make a method that creates a random value for each type
    public void setToRandomValue() {
        if (getType() == Type.BOOLEAN) {
            value = (Math.random() > 0.5);
        } else if (getType() == Type.NUMBER) {
            value = 100 - (Math.random() * 200);
        } else if (getType() == Type.STRING) {
            // Generate a random string using the time converted to hex
            value = Long.toHexString(System.currentTimeMillis());
        }
    }

    private String readOnlyNumber(String compID, String key, double d) {
        return "<tr><td>" + key + ":</td><td><span class=\"readonlyNumber\" " + getID(compID, key) + ">" + d
                + "</span></td>" + "</tr>";
    }

    private String number(String compID, String key, double d) {
        return "<tr><td>" + key + ":</td><td><input " + getID(compID, key) + " " + getChangeEvent(compID, key, "this.value") + " placeholder=\"Value\" value='" + d + "' type=\"number\"></td></tr>";
    }

    public String toString() {
        // Use gson to convert this object to json
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}
