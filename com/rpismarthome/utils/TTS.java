/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils;

import com.rpismarthome.Main;
import java.io.IOException;

/**
 *
 * @author zipCoder933
 */
public class TTS {

    public static void speak(String text) throws InterruptedException, IOException {
        text = text.replace("\n", "").replace("\"", "");
        System.out.println("Speaking: \"" + text + "\"");
        if (!Main.isRunningFromLocal()) {
            CommandUtils.execCommand(true, "/home/pi/Documents/smartHome/speech.sh \"" + text + "\"");
        }
    }
}
