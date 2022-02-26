/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.utils.backend;

import com.rpismarthome.utils.FileUtils;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author zipCoder933
 */
public class FrontendParsingUtils {

    public static String loadHtmlFile(String path, InsertHandler inserter, boolean verbose) throws IOException {
        String lines[] = FileUtils.readString(path).split("\n");
        if (verbose) {
            System.out.println("Preprocessing file: " + path);
        }
        while (true) {
            boolean encounteredAnySpecialTags = false;
            StringBuilder ret = new StringBuilder();
            int i = 0;
            for (String line : lines) {
                i++;
                try {
                    if (line.trim().startsWith("/*load")) {
                        encounteredAnySpecialTags = true;
                        String piece = line.split("/*load")[1];
                        piece = piece.replace("*/", "").trim();
                        String component = FileUtils.readString(piece);
                        line = "\n\n/*LOADED FROM " + piece + "*/\n" + component + "\n/*...*/\n\n";
                        if (verbose) {
                            System.out.println("\tSuccesflly loaded (/**/) resource " + piece);
                        }
                    } else if (line.trim().startsWith("/*insert") && inserter != null) {
                        encounteredAnySpecialTags = true;
                        String piece = line.split("/*insert")[1];
                        piece = piece.replace("*/", "").trim();
                        String component = inserter.insert(piece);
                        if (component == null) {
                            return printError(i, new NoSuchFieldException("Field \"" + piece + "\" does not exist"));
                        }
                        line = "\n\n/*LOADED FROM " + piece + "*/\n" + component + "\n/*...*/\n\n";
                        if (verbose) {
                            System.out.println("\tSuccesflly inserted (/**/) resource " + piece);
                        }
                    } else if (line.trim().startsWith("<!--load")) {
                        encounteredAnySpecialTags = true;
                        String piece = line.split("<!--load")[1];
                        piece = piece.replace("-->", "").trim();
                        String component = FileUtils.readString(piece);
                        line = "\n\n<!--LOADED FROM " + piece + "-->\n" + component + "\n<!--...-->\n\n";
                        if (verbose) {
                            System.out.println("\tSuccesflly loaded resource " + piece);
                        }
                    } else if (line.trim().startsWith("<!--insert") && inserter != null) {
                        encounteredAnySpecialTags = true;
                        String piece = line.split("<!--insert")[1];
                        piece = piece.replace("-->", "").trim();
                        String component = inserter.insert(piece);
                        if (component == null) {
                            return printError(i, new NoSuchFieldException("Field \"" + piece + "\" does not exist"));
                        }
                        line = "\n\n<!--LOADED FROM " + piece + "-->\n" + component + "\n<!--...-->\n\n";
                        if (verbose) {
                            System.out.println("\tSuccesflly inserted resource " + piece);
                        }
                    }
                } catch (Exception e) {
                    return printError(i, e);
                }
                ret.append(line).append("\n");
            }
            if (!encounteredAnySpecialTags) {
                if (verbose) {
                    System.out.println("No tags encountered");
                }
                return ret.toString();
            }
            if (verbose) {
                System.out.println("Checking for more tags...");
            }
            lines = ret.toString().split("\n");
        }
    }

    private static String printError(int lineNo, Exception e) {
        System.err.println("A parsing error occured (Line " + lineNo + ")");
        e.printStackTrace();
        return "<html><head><style>*{font-family:Helvetica,arial;}</style></head>"
                + "<body><h1 style='color:red;'>A parsing error occured.</h1>"
                + "<h2>(Line " + lineNo + ")</h2>"
                + "<h3>" + e.getClass() + "<br/>" + e.getMessage() + "</h3>"
                + "<p><b>Stack Trace:</b><br/>" + Arrays.toString(e.getStackTrace()) + "</p></body></html>";
    }

}
