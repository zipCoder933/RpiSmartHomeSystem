/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome;

import com.rpismarthome.Main;
import com.rpismarthome.utils.webSocket.WebSocketClient;
import com.rpismarthome.utils.webSocket.WebSocketServer;
import com.rpismarthome.utils.webSocket.WebSocketUtils;
import static com.rpismarthome.utils.webSocket.WebSocketUtils.preprocessInput;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.rpismarthome.Main.ipAdress;
import com.rpismarthome.codex.CodexSH;
import com.rpismarthome.utils.MathUtils;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author zipCoder933
 */
public class MainSocketServer extends WebSocketServer {

    static ArrayList<WebSocketClient> clients;
    ComponentSH newComponent;
    static CodexSH codex;
    private InetSocketAddress addr;

    public static void voiceCommand(String msg) {
        codex.voiceCommand(msg);
    }

    public MainSocketServer() throws IOException, NoSuchAlgorithmException, InterruptedException {
        addr = new InetSocketAddress(ipAdress, 80);
        initialize(addr);
        System.out.println("Websocket at adress: " + addr.toString());
        clients = new ArrayList<>();
        newComponent = new ComponentSH(this);
        codex = new CodexSH();
    }

    @Override
    public void onNewClientConnected(WebSocketClient client) throws Exception {
        System.out.println("NEW CLIENT");
        clients.add(client);
    }

    private boolean containsAllAsciiCharacters(String str) {
        // Returns a charset object for the named charset.
        CharsetDecoder decoder = StandardCharsets.US_ASCII.newDecoder();
        try {
            CharBuffer buffer = decoder.decode(ByteBuffer.wrap(str.getBytes()));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    @Override
    public void onClientMessage(WebSocketClient client, String decoded) throws Exception {
        String[] msg = preprocessInput(decoded);
        System.out.println("\tMessage: " + Arrays.toString(msg));

        if (containsAllAsciiCharacters(decoded)) {
            try {
                String command = msg[0];
//        msg = Arrays.copyOfRange(msg, 1, msg.length);
                //====================================
                if (Main.isUsingScreen()) {
                    Main.getUIFrame().setRealAct(msg[0].length() * 100);
                }

                if (command.equals("component")) {
                    newComponent.onMessageRecieved(client, msg);
                } else if (command.equals("CODEX")) {
                    codex.onMessageRecieved(client, msg);
                } else {
                    if (command.equals("ResetConfig")) {
                        Main.resetConfig();
                    } else if (command.equals("setUpdateFreq")) {
                        long updateFrequency = Long.parseLong(msg[1]);
                        updateFrequency = (long) MathUtils.clamp(updateFrequency, 2000, 60000);
                        System.out.println("Setting update frequency to " + updateFrequency);
                        Main.getConfig().setDataGatheringUpdateFrequency(updateFrequency);
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                if (e.getMessage().equals("Broken pipe (Write failed)")) {
                    System.out.println("SOCKET ISSUES");
                    writeInfo("The websocket is expiriencing some issues");
//                    fixBrokenPipeError();
                } else {
                    alertUserOfException(e);
                }
            }
        } else {
            System.err.println("Message is corrupted.");
            writeInfo("<b>Error</b><br>Unable to process request<br>(Recieved corruped websocket message). Please try again.");
        }
    }

    @Override
    public void onClientDisconnected(WebSocketClient client) throws Exception {
        System.out.println("CLIENT DISCONNECTED");
        clients.remove(client);
    }

    public static void alertUserOfException(Exception e) {
        writeInfo("<b>Unknown Error</b><br>" + e.getMessage());
    }

    public static void write(String... content) {
        for (WebSocketClient client : clients) {
            try {
                WebSocketUtils.write(client, content);
            } catch (IOException ex) {
                Logger.getLogger(MainSocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void writeInfo(String str) {

        writeToAllClients("INFO", str);

        if (Main.isUsingScreen()) {
            String strRegEx = "<[^>]*>";
            Main.getUIFrame().setInfo(str.replace("<br/>", "\n").replace("<br />", "\n").replace("<br>", "\n").replaceAll(strRegEx, ""));
            Main.getUIFrame().setRealAct(5);
        }
    }

    public void onError(Exception e) {
        if (e.getMessage().equals("Broken pipe (Write failed)")) {
            System.out.println("SOCKET ISSUES");
            writeInfo("The websocket is expiriencing some issues");
        }
        if (!e.getMessage().equals("No match found")) {
            e.printStackTrace();
        }
    }

    public static void writeToAllClients(String... str) {
        for (int i = 0; i < clients.size(); i++) {
            try {
                WebSocketUtils.write(clients.get(i), str);
            } catch (IOException ex) {
                Logger.getLogger(MainSocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    public void fixBrokenPipeError() throws IOException, InterruptedException, NoSuchAlgorithmException {
//        System.out.println("FIXING BROKEN PIPE...");
//        for (int i = 0; i < clients.size(); i++) {
//            clients.get(i).closeConnection();
//        }
//        Thread.sleep(1000);
//        initialize(addr);
//        System.out.println("Websocket at adress: " + addr.toString());
//        clients = new ArrayList<>();
//        newComponent = new ComponentSH(this);
//        codex = new CodexSH();
//    }
}
