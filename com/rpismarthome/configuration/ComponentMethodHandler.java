/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.configuration;

import com.rpismarthome.configuration.component.Component;
import com.rpismarthome.configuration.component.ComponentType;
import com.rpismarthome.configuration.component.Value;
import com.rpismarthome.configuration.component.values.config.ConfigValue;
import com.rpismarthome.MainSocketServer;
import com.rpismarthome.utils.AudioUtils;
import com.rpismarthome.utils.FileUtils;
import com.rpismarthome.utils.Timer;
import com.rpismarthome.utils.gpio.GPIOUtils;
import com.weather.weather.OpenWeatherAPIClient;
import com.weather.weather.weatherConditions.WeatherConditions;
import java.io.IOException;

/**
 *
 * @author zipCoder933
 */
public class ComponentMethodHandler {
    
    static Timer weatherCooldownTime;
    public static OpenWeatherAPIClient weatherClient;
    
    public static void init() throws Exception, InterruptedException {
        weatherClient = new OpenWeatherAPIClient();
        weatherClient.setKEY(FileUtils.readString("OpenWeatherAPIKey.txt"));
        weatherCooldownTime = new Timer();
    }

//<editor-fold defaultstate="collapsed" desc="When component properties are set...">
    public static void setValue(Component comp, String propKey) throws IOException, InterruptedException {
        ComponentType method = comp.getMethod();
        Value val = comp.getProperty(propKey);
        
        switch (method) {
            case LIGHT:
                writePin(comp, "Pin", val.getValueAsNumber());
                break;
            case RGB_LIGHT:
                if (propKey.equalsIgnoreCase("Red")) {
                    writePin(comp, "Red PIN", val.getValueAsNumber());
                } else if (propKey.equalsIgnoreCase("Green")) {
                    writePin(comp, "Green PIN", val.getValueAsNumber());
                } else if (propKey.equalsIgnoreCase("Blue")) {
                    writePin(comp, "Blue PIN", val.getValueAsNumber());
                }
                break;
            case VERBOSE_OUTPUT:
                if (propKey.equalsIgnoreCase("volume")) {
                    AudioUtils.setVolume((int) val.getValueAsNumber());
                }
        }
    }
    
    static void writePin(Component comp, String configKey, double pwm) throws IOException, InterruptedException {
        ConfigValue config = comp.getConfigProperties().get(configKey);
        GPIOUtils.writePin((int) config.getValueAsNumber(), (int) pwm);
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Component initialization (Catches errors)">
    public static String initializeComponent(ComponentType gatheringMethod, Component comp) throws InterruptedException, Exception {
        
        switch (gatheringMethod) {
            case WEATHER_API:
                try {
                WeatherConditions w
                        = weatherClient.getWeather(comp.getConfigProperties().get("Location").getValueAsString());
                comp.setProperty("Temperature (C)", w.main.temp);
                comp.setProperty("Feels Like (C)", w.main.feels_like);
                comp.setProperty("Min Temperature (C)", w.main.temp_min);
                comp.setProperty("Max Temperature (C)", w.main.temp_max);
                comp.setProperty("Pressure (hPa)", w.main.pressure);
                comp.setProperty("Humidity (%)", w.main.humidity);
                comp.setProperty("Wind Speed (m/s)", w.wind.speed);
                comp.setProperty("Wind Gust (m/s)", w.wind.gust);
                comp.setProperty("Wind Direction", w.wind.deg);
            } catch (Exception e) {
                return "Unkown Location. change your location parameter.";
            }
            case MICROPHONE:
                MainSocketServer.writeInfo("USB Microphone component added. Restart your Raspberry pi for these changes to take effect.");
                break;
        }
        
        return "";
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Component data gathering">
    public static void gatherData(ComponentType gatheringMethod, Component comp) throws InterruptedException, Exception {
        switch (gatheringMethod) {
            case TIME:
                //<editor-fold defaultstate="collapsed" desc="comment">
                int hour = java.time.LocalTime.now().getHour();
                int minute = (int) (System.currentTimeMillis() / (1000 * 60)) % 60;
                int second = (int) (System.currentTimeMillis() / 1000) % 60;
                comp.setProperty("Hour", hour);
                comp.setProperty("Minute", minute);
                comp.setProperty("Second", second);
                
                String ampm = "AM";
                if (hour > 12) {
                    hour -= 12;
                    ampm = "PM";
                }
                String min = minute + "";
                if (minute < 10) {
                    min = "0" + min;
                }
                comp.setPropertiesString(hour + ":" + min + " " + ampm);
                comp.setUIpropertiesString(hour + ":" + min + " " + ampm);
//</editor-fold>
                break;
            case WEATHER_API:
                //<editor-fold defaultstate="collapsed" desc="comment">

//                if (weatherCooldownTime.getElapedTimeInMinutes() > 2) {
                System.out.println("weather data (every 2 min)");
                weatherCooldownTime.reset();
                WeatherConditions w
                        = weatherClient.getWeather(comp.getConfigProperties().get("Location").getValueAsString());
                comp.setProperty("Temperature (C)", w.main.temp);
                comp.setProperty("Feels Like (C)", w.main.feels_like);
                comp.setProperty("Min Temperature (C)", w.main.temp_min);
                comp.setProperty("Max Temperature (C)", w.main.temp_max);
                comp.setProperty("Pressure (hPa)", w.main.pressure);
                comp.setProperty("Humidity (%)", w.main.humidity);
                comp.setProperty("Wind Speed (m/s)", w.wind.speed);
                comp.setProperty("Wind Gust (m/s)", w.wind.gust);
                comp.setProperty("Wind Direction", w.wind.deg);
                comp.setUIpropertiesString("Weather: " + w.main.temp + " C, "
                        + " Wind: " + w.wind.speed + " m/s (" + w.wind.deg + " deg), "
                        + w.main.humidity + "% Humidity");
//                }

                //</editor-fold>
                break;
        }
    }
//</editor-fold>

}
