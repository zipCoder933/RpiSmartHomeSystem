package com.rpismarthome.utils.webSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java

public abstract class WebSocketServer {

    //https://stackoverflow.com/questions/7040078/how-to-deconstruct-data-frames-in-websockets-hybi-08/7045885#7045885
    public static byte[] decode(byte[] bytes) {
        int length_code = (bytes[1] & 127);
        byte[] masks = new byte[4];
        byte[] data = new byte[bytes.length - 6];
        if (length_code == 126) {
            masks = Arrays.copyOfRange(bytes, 4, 8);
            data = Arrays.copyOfRange(bytes, 8, bytes.length);
        } else if (length_code == 127) {
            masks = Arrays.copyOfRange(bytes, 10, 14);
            data = Arrays.copyOfRange(bytes, 14, bytes.length);
        } else {
            masks = Arrays.copyOfRange(bytes, 2, 6);
            data = Arrays.copyOfRange(bytes, 6, bytes.length);
        }
        byte[] decoded = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            decoded[i] = (byte) (data[i] ^ masks[i % 4]);
        }
        return decoded;
    }

    public static byte[] encode(byte[] bytes) {
        int length = bytes.length;
        byte[] encoded = new byte[length + 2];
        encoded[0] = (byte) 0x81;
        if (length <= 125) {
            encoded[1] = (byte) length;
        } else if (length <= 65535) {
            encoded[1] = (byte) 126;
            encoded[2] = (byte) (length >> 8);
            encoded[3] = (byte) (length & 0xFF);
        } else {
            encoded[1] = (byte) 127;
            encoded[2] = (byte) (length >> 56);
            encoded[3] = (byte) (length >> 48);
            encoded[4] = (byte) (length >> 40);
            encoded[5] = (byte) (length >> 32);
            encoded[6] = (byte) (length >> 24);
            encoded[7] = (byte) (length >> 16);
            encoded[8] = (byte) (length >> 8);
            encoded[9] = (byte) (length & 0xFF);
        }
        for (int i = 0; i < length; i++) {
            encoded[i + 2] = (byte) (bytes[i]);//^ 0x80
        }
        return encoded;
    }

    private ServerSocket server;
    private int clientID = 0;

    Runnable clientListener = new Runnable() {

        @Override
        public void run() {
            WebSocketClient client;
            try {
                client = new WebSocketClient(server.accept(), clientID);
                handshakeClient(client);
                onNewClientConnected(client);
                clientID++;
            } catch (Exception e) {
                onError(e);
                Thread thread = new Thread(clientListener);
                thread.start();
                return;
            }
            Thread thread = new Thread(clientListener);
            thread.start();
            try {
                InputStream in = client.getInputStream();
//                try {
                while (!client.isClosed()) {
                    if (in.available() > 0) {
                        byte[] read = in.readNBytes(in.available());
                        byte[] decoded = decode(read);
                        if (decoded.length == 2) {
                            if (decoded[0] == 3 && decoded[1] == -23) {
                                client.closeConnection();
                                onClientDisconnected(client);
                                return;
                            }
                        }
                        onClientMessage(client, new String(decoded));
                    }
                }
                client.closeConnection();
                onClientDisconnected(client);
            } catch (Exception ex) {
                onError(ex);
            }
        }

    };

    public void initialize(ServerSocket server) throws IOException, NoSuchAlgorithmException, InterruptedException {
        this.server = server;
        Thread thread = new Thread(clientListener);
        thread.start();
    }

    public void initialize(InetSocketAddress insa) throws IOException, NoSuchAlgorithmException, InterruptedException {
        this.server = new ServerSocket();
        server.bind(insa);
        Thread thread = new Thread(clientListener);
        thread.start();
    }

    public abstract void onNewClientConnected(WebSocketClient client) throws Exception;

    public abstract void onClientMessage(WebSocketClient client, String decoded) throws Exception;

    public abstract void onClientDisconnected(WebSocketClient client) throws Exception;

    public void onError(Exception ex) {
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
    }

    private void handshakeClient(WebSocketClient client2) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        String data = client2.getScanner().useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);
        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                    + "\r\n\r\n").getBytes("UTF-8");
            client2.getOutputStream().write(response, 0, response.length);
        }

    }

}
