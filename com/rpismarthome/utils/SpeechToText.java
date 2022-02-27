package com.rpismarthome.utils;

import com.rpismarthome.MainSocketServer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 *
 * @author zipCoder933
 */
public class SpeechToText {

    public SpeechToText() throws FileNotFoundException, InterruptedException {
        try {
            CommandUtils.execCommandAsync(true,"python3 "+FileUtils.getResourcePath()+"stt.py");
            Thread.sleep(5000);
            File file = FileUtils.file("stt_out.txt");
            RandomAccessFile in = new RandomAccessFile(file, "r");

            (new Thread() {
                @Override
                public void run() {
                    String line;
                    while (true) {
                        try {
                            if ((line = in.readLine()) != null) {
                                line = line.replace("\n", "");
                                String chunks[] = line.split("|");

                                if (chunks[0].equals("STT")) {
                                    System.out.println("You said" + chunks[1]);
                                    MainSocketServer.voiceCommand(chunks[1]);
                                } else if (chunks[0].equals("LISTENING")) {
                                    System.out.println("Listening...");
                                }

                            } else {
                                Thread.sleep(20); // poll the file every 2 seconds …
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
