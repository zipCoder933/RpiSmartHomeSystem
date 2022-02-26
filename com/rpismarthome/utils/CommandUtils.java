/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils;

import com.rpismarthome.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author zipCoder933
 */
public class CommandUtils {

    private static long id = 0;

    public static String[] parseCommand(String... command) throws IOException {
        if (Main.isRunningFromLocal()) {
            String[] tempArr = new String[command.length + 1];
            tempArr[0] = "powershell.exe";
            System.arraycopy(command, 0, tempArr, 1, command.length);
//            System.out.println("EXECUTING COMMAND: "+Arrays.toString(tempArr));
            return tempArr;
        }
        String[] output = {"/bin/bash", "-c"};
        return concatWithStream(output, command);
    }

    static String[] concatWithStream(String[] array1, String[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(size -> (String[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }

    public static void execCommandAsync(boolean verbose, String... str) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    execCommand(verbose, str);
                } catch (IOException ex) {
                    Logger.getLogger(CommandUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    public static void execCommand(boolean verbose, String... str) throws IOException {
        if (!Main.isRunningFromLocal()) {
            try {
                if (verbose) {
                    System.out.println(Arrays.toString(str));
                }
                Process proc = Runtime.getRuntime().exec(parseCommand(str));
                InputStreamReader isr = new InputStreamReader(proc.getInputStream());
                BufferedReader rdr = new BufferedReader(isr);

                String line;
                while ((line = rdr.readLine()) != null) {
                    if (verbose) {
                        System.out.println(line);
                    }
                }

                isr = new InputStreamReader(proc.getErrorStream());
                rdr = new BufferedReader(isr);
                while ((line = rdr.readLine()) != null) {
                    if (verbose) {
                        System.err.println(line);
                    }
                }
                int waitFor = proc.waitFor(); // Wait for the process to complete
                System.out.println("Exit code: " + proc.exitValue());
                proc.destroy();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
