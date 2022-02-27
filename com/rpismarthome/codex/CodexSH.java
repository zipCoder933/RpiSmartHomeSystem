/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.codex;

import com.rpismarthome.MainSocketServer;
import com.rpismarthome.SocketHandler;
import com.rpismarthome.codex.CodexAssistant;
import com.rpismarthome.utils.FileUtils;
import com.rpismarthome.utils.webSocket.WebSocketClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class CodexSH implements SocketHandler {

    CodexAssistant codex;
//    boolean requested = false;

    public CodexSH() throws IOException {
        codex = new CodexAssistant();

//        try {
//            requested = Boolean.parseBoolean(FileUtils.readString("requested.txt"));
//        } catch (IOException e) {
//            requested = false;
//        }
//        System.out.println("Requested = " + requested);
//        (new Thread() {
//
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        Thread.sleep(5000);
//
//                        if (codex.getTrainingLevel() > 1 && requested) {
//                            Thread.sleep(60000);
//                            System.out.println("INITIATING AUTO COMMAND...");
//                            codex.autoCommand();
//                        } else {
//                            System.out.println("Model training: " + codex.getTrainingLevel() + ", user has prompted the model: " + requested);
//                        }
//                    }
//                } catch (Exception ex) {
//                    Logger.getLogger(CodexSH.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//        }).start();
    }

    @Override
    public void onMessageRecieved(WebSocketClient client, String[] message) throws Exception {
        if (message[1].equals("COMMAND")) {
            processCommand(message[2]);
        } else if (message[1].equals("TRAIN")) {
            try {
                System.out.println("CODEX TRAIN: " + message[2]);
                codex.trainOnRequest(message[2]);
                System.out.println("Trained succesfully.");
            } catch (Exception e) {
                System.out.println("Request has no text.");
                MainSocketServer.writeInfo("Error: No text entered.");
            }
        } else if (message[1].equals("RESET")) {
            codex.reset();
        }
    }

    private void saveRequested(boolean requested) throws IOException {
        String str = requested + "";
        FileUtils.writeString("requested.txt", str);
    }

    public void processCommand(String message) {
        try {
            if (message.equals("")) {
                MainSocketServer.writeInfo("You havent entered any text.");
                return;
            }
            System.out.println("CODEX COMMAND: " + message);
            codex.askCommand(message);
        } catch (Exception e) {
            e.printStackTrace();
            MainSocketServer.writeInfo("Error processing command.\nPerhaps you should check your API Key.");
        }
    }

    public void voiceCommand(String message) {
        try {
            if (message.equals("")) {
                MainSocketServer.writeInfo("You havent entered any text.");
                return;
            }
            System.out.println("VOICE COMMAND: " + message);
            codex.voiceCommand(message);
        } catch (Exception e) {
            e.printStackTrace();
            MainSocketServer.writeInfo("Error processing command.\nPerhaps you should check your API Key.");
        }
    }

}
