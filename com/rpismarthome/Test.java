/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome;
import com.rpismarthome.utils.SpeechToText;
import com.weather.weather.OpenWeatherAPIClient;
import java.io.IOException;

/**
 *
 * @author zipCoder933
 */
public class Test {

    public static void main(String args[]) throws IOException, NoSuchMethodException, InterruptedException {
        OpenWeatherAPIClient weather = new OpenWeatherAPIClient();
        weather.findMyAPIKey();
    }
}
