/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils.gpio;

import com.rpismarthome.utils.CommandUtils;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author zipCoder933
 */
public class GPIOUtils {

    static HashMap<Integer, PinMode> pins;

    public static int clampPin(int pin) {
        if (pin < 2) {
            System.err.println("Pin was set to an unsafe pin number " + pin);
            pin = 2;
        } else if (pin > 27) {
            System.err.println("Pin was set to an unsafe pin number " + pin);
            pin = 27;
        }
        return pin;
    }


    public static void start() throws IOException, InterruptedException {
        CommandUtils.execCommand(false,"sudo pigpiod");
        pins = new HashMap<>();
    }

    public static void setPinMode(int p, PinMode mode) throws IOException, InterruptedException {
        p = clampPin(p);
        String modeStr = "w";
        if (mode == PinMode.INPUT) {
            modeStr = "r";
        }
        CommandUtils.execCommand(false,"pigs m " + p + " " + modeStr);
    }

    public static void writePin(int p, int value) throws IOException, InterruptedException {
        p = clampPin(p);
        if (!pins.containsKey(p)) {
            pins.put(p, PinMode.OUTPUT);
            setPinMode(p, PinMode.OUTPUT);
        } else if (pins.get(p) == PinMode.INPUT) {
            pins.put(p, PinMode.OUTPUT);
            setPinMode(p, PinMode.OUTPUT);
        }
        if (value > 255) {
            value = 255;
        } else if (value < 0) {
            value = 0;
        }
        CommandUtils.execCommand(false,"pigs p " + p + " " + value);
    }
}
