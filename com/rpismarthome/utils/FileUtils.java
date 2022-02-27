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

    public static String getResourcePath() {
        return Main.RESOURCE_PATH;
    }

    public static File file(String path) {
        path = path.replace("\\", "/");
        return new File(Main.RESOURCE_PATH + path);
    }

    public static String readString(String path) throws IOException {
        path = path.replace("\\", "/");
        File f = new File(Main.RESOURCE_PATH + path);
        return Files.readString(f.toPath());
    }

    public static void writeString(String path, String value) throws IOException {
        path = path.replace("\\", "/");

        File f = new File(Main.RESOURCE_PATH + path);
//        System.out.println("Writing to "+f.getAbsolutePath());
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        Files.write(f.toPath(), value.getBytes());
    }

}
