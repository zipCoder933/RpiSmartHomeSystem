/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils.webSocket;

import com.rpismarthome.utils.webSocket.WebSocketClient;
import java.io.IOException;

/**
 *
 * @author zipCoder933
 */
public class WebSocketUtils {

    private final static String delimiter = "|";

    public static void write(WebSocketClient client, String... content) throws IOException {
        // Remove all the "|" characters from content[]
        String[] newContent = new String[content.length];
        for (int i = 0; i < content.length; i++) {
            newContent[i] = content[i].replace(delimiter, "");
        }
        // Concatinate content into singleContent with | as the delimiter
        String singleContent = String.join(delimiter, content);
        // Split the content into chunks of 100 characters
        String[] chunks = singleContent.split("(?<=\\G.{100})");
        if (chunks.length > 1) {
            for (String chunk : chunks) {
                client.write(chunk);
            }
            client.write("DONE");
        } else {
            client.write(chunks[0] + delimiter + "DONE");
        }
    }

    public static String[] preprocessInput(String content) throws IOException {
        return content.split("[|]");
    }
}
