/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome;

import com.rpismarthome.configuration.Configuration;
import com.rpismarthome.configuration.component.Component;
import com.rpismarthome.utils.FileUtils;
import com.rpismarthome.utils.SpeechToText;
import com.rpismarthome.utils.backend.FrontendParsingUtils;
import com.rpismarthome.utils.backend.InsertHandler;
import com.rpismarthome.utils.gpio.GPIOUtils;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class Main {

    //==YOU NEED TO EDIT THESE LINES ===================
    public static String ipAdress = "192.168.0.40";
    private static boolean usingScreen = true;
    public static String RESOURCE_PATH = "/home/pi/Documents/smartHome/resources/";
    public static String LOCAL_RESOURCE_PATH = "C:\\Local Files\\Java\\Projects\\RPISmartHomeAnt\\src\\com\\rpismarthome\\resources\\";
    //==================================================

    /**
     * @return the frame
     */
    public static UIFrame getUIFrame() {
        return frame;
    }

    /**
     * @return the usingScreen
     */
    public static boolean isUsingScreen() {
        return usingScreen;
    }

    /**
     * @return the config
     */
    public static Configuration getConfig() {
        return config;
    }

    /**
     * @return the runningFromLocal
     */
    public static boolean isRunningFromLocal() {
        return runningFromLocal;
    }

    private static boolean runningFromLocal = false;

    public static MainSocketServer websocket;
    private static Configuration config;
    private static UIFrame frame;
    static SpeechToText stt;

    public static void main(String[] args) throws Exception {
        runningFromLocal = (args.length > 0);
        System.out.println("Running from local: " + runningFromLocal);

        if (isRunningFromLocal()) {
            ipAdress = "localhost";
            RESOURCE_PATH = LOCAL_RESOURCE_PATH;
        }

        config = new Configuration(false);
        InetSocketAddress addr = new InetSocketAddress(ipAdress, 8500);
        HttpServer server = HttpServer.create(addr, 0);
        System.out.println("Hosting at " + addr.getAddress() + ":" + addr.getPort());
        HttpContext context = server.createContext("/");
        context.setHandler(Main::handleRequest);
        server.start();

        websocket = new MainSocketServer();
        GPIOUtils.start();
        if (isUsingScreen()) {
            frame = new UIFrame();
        }
        for (Component comp : config.getComponents()) {
            if (comp.getType().equals("usb-microphone")) {
                System.out.println("MICROPHONE CONNECTED");
                stt = new SpeechToText();
                break;
            }
        }
    }

    public static InsertHandler inserts = new InsertHandler() {
        @Override
        public String insert(String piece) throws NoSuchFieldError {
            if (piece.equals("components")) {
                return getConfig().renderHTML();
            } else if (piece.equals("config-properties")) {
                return "<tr><td>config properties</td></tr>";
            } else if (piece.equals("ipAdress")) {
                return "var ip = \"" + ipAdress + "\"";
            } else if (piece.equals("dgUpdateFreq")) {
                return "<input   value=\"" + config.getDataGatheringUpdateFrequency() + "\" onchange = \"wsWrite('setUpdateFreq|'+this.value)\" type=\"number\"  placeholder=\"Update Frequency MS\" />";
            }
            return null;
        }
    };

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String response = FrontendParsingUtils.loadHtmlFile("index.html", inserts, false);
        exchange.sendResponseHeaders(200, response.getBytes().length);// response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void resetConfig() {
        try {
            config = new Configuration(true);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
