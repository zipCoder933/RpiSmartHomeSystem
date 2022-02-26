/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils;

import com.rpismarthome.Main;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author zipCoder933
 */
public class FileUtils {

    public static String RESOURCE_PATH = "/home/pi/Documents/smartHome/resources/";

    public static void init() {
        if (Main.isRunningFromLocal()) {
            RESOURCE_PATH = "C:\\Local Files\\Java\\Projects\\RPISmartHomeAnt\\src\\com\\rpismarthome\\resources\\";
        } else {
            RESOURCE_PATH = "/home/pi/Documents/smartHome/resources/";
        }
    }
    
        public static void initLocal() {
            RESOURCE_PATH = "C:\\Local Files\\Java\\Projects\\RPISmartHomeAnt\\src\\com\\rpismarthome\\resources\\";
       
    }

    public static File file(String path) {
        path = path.replace("\\", "/");
        return new File(RESOURCE_PATH + path);
    }

    public static String readString(String path) throws IOException {
        path = path.replace("\\", "/");
        File f = new File(RESOURCE_PATH + path);
        return Files.readString(f.toPath());
    }

    public static void writeString(String path, String value) throws IOException {
        path = path.replace("\\", "/");
        
        File f = new File(RESOURCE_PATH + path);
//        System.out.println("Writing to "+f.getAbsolutePath());
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        Files.write(f.toPath(), value.getBytes());
    }

}
