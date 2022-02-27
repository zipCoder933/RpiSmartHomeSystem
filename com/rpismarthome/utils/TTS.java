package com.rpismarthome.utils;

import com.rpismarthome.Main;
import java.io.IOException;

/**
 *
 * @author zipCoder933
 */
public class TTS {

    public static void speak(String text) throws InterruptedException, IOException {
        text = text.replace("\n", "").replace("\"", "");
        System.out.println("Speaking: \"" + text + "\"");
        if (!Main.isRunningFromLocal()) {
            CommandUtils.execCommand(true, FileUtils.getResourcePath()+"speech.sh \"" + text + "\"");
        }
    }
}
