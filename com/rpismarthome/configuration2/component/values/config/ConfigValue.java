/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.configuration2.component.values.config;

/**
 *
 * @author zipCoder933
 */
public class ConfigValue {

    /**
     * @return the type
     */
    public ConfigType getType() {
        return type;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    private ConfigType type;
    private Object value;

    public ConfigValue(ConfigType type, Object defaultValue) {
        this.type = type;
        this.value = defaultValue;
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

    public String renderHTML(String key) {
        if (getType() == ConfigType.BOOLEAN) {
            return ("<tr><td>" + key + ":</td><td><label class=\"switch\"><input type=\"checkbox\" checked><span class=\"slider round\"></span></label></td></tr>");
        } else if (getType() == ConfigType.NUMBER) {
            try {
                double number = (double) value;
                return number(key, number);
            } catch (java.lang.ClassCastException e) {
                try {
                    int number = (int) value;
                    return number(key, number);
                } catch (java.lang.ClassCastException e1) {
                    try {
                        long number = (long) value;
                        return number(key, number);
                    } catch (java.lang.ClassCastException e2) {
                        try {
                            short number = (short) value;
                            return number(key, number);
                        } catch (java.lang.ClassCastException e3) {
                            try {
                                float number = (float) value;
                                return number(key, number);
                            } catch (java.lang.ClassCastException e4) {
                                e4.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else if (getType() == ConfigType.PIN) {
            try {
                double number = (double) value;
                return pin(key, number);
            } catch (java.lang.ClassCastException e) {
                try {
                    int number = (int) value;
                    return pin(key, number);
                } catch (java.lang.ClassCastException e1) {
                    try {
                        long number = (long) value;
                        return pin(key, number);
                    } catch (java.lang.ClassCastException e2) {
                        try {
                            short number = (short) value;
                            return pin(key, number);
                        } catch (java.lang.ClassCastException e3) {
                            try {
                                float number = (float) value;
                                return pin(key, number);
                            } catch (java.lang.ClassCastException e4) {
                                e4.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String number(String key, double d) {
        return "<tr><td>" + key + ":</td><td><input placeholder=\"Value\" value='" + d + "' type=\"number\"></td></tr>";
    }

    private String pin(String key, double d) {
        return "<tr><td>" + key + ":</td><td><input placeholder=\"PI4J Pin (0-31)\" value='' type=\"number\"></td></tr>";
    }
}
