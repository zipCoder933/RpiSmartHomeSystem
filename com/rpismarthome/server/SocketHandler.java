/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rpismarthome.server;

import com.rpismarthome.utils.webSocket.WebSocketClient;

/**
 *
 * @author sampw
 */
public interface SocketHandler {
    public void onMessageRecieved(WebSocketClient client, String[] message) throws Exception;
}
