/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils.webSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;
import org.xml.sax.InputSource;

/**
 *
 * @author zipCoder933
 */
public class WebSocketClient {

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the closed
     */
    public boolean isClosed() {
        return closed;
    }
    
    public void write(String str) throws UnsupportedEncodingException, IOException {
        outputStream.write(WebSocketServer.encode(str.getBytes("UTF-8")));
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @return the outputStream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * @return the inputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return the scanner
     */
    public Scanner getScanner() {
        return scanner;
    }
    
    public void closeConnection() throws IOException {
        socket.close();
        closed = true;
    }
    
    private final Socket socket;
    private final OutputStream outputStream;
    private final InputStream inputStream;
    private final Scanner scanner;
    private boolean closed = false;
    private String id;
    
    public WebSocketClient(Socket client, int clientID) throws IOException {
        this.id = Integer.toHexString(clientID);
        this.socket = client;
        outputStream = client.getOutputStream();
        inputStream = client.getInputStream();
        scanner = new Scanner(client.getInputStream(), "UTF-8");
    }
}
