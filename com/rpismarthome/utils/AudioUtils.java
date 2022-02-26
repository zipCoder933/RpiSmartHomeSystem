/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class AudioUtils {

    public static void setVolume(int vol) throws IOException {
        vol = MathUtils.clamp(vol, 0, 100);
        CommandUtils.execCommand(true,"sudo /home/pi/Documents/smartHome/volume.sh " + vol);
    }
}
