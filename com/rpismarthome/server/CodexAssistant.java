/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.server;

import com.codex4j.*;
import com.codex4j.request.CodexOutput;
import com.codex4j.request.CodexRequest;
import com.codex4j.request.Engine;
import com.rpismarthome.configuration2.component.Component;
import com.rpismarthome.configuration2.component.Value;
import com.rpismarthome.Main;
import com.rpismarthome.utils.FileUtils;
import com.rpismarthome.utils.TTS;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author zipCoder933
 */
public class CodexAssistant {

    /**
     * @return the trainingLevel
     */
    public int getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * @return the conversationSize
     */
    public int getConversationSize() {
        return conversationSize;
    }

    private int trainingLevel = 0;

    /**
     * @param conversationSize the conversationSize to set
     */
    public void setConversationSize(int conversationSize) {
        this.conversationSize = conversationSize;
    }

    // <editor-fold defaultstate="collapsed" desc="comment">
    public String formatComponentNameToVar(String title) {
        return title.replace(" ", "_").toLowerCase();
    }

    public String parseCodexOutputExample() {
        ArrayList<Component> components = Main.getConfig().getComponents();
        StringBuilder sb = new StringBuilder();
        // Iterate over all components
        for (Component component : components) {
            ArrayList<Value> listOfValues = component.getPropertyValues();
            ArrayList<String> listOfKeys = component.getPropertyKeys();

            // Iterate over all the keys
            for (int i = 0; i < listOfKeys.size(); i++) {
//                if (listOfValues.get(i).isHasChanged()) {
                if (listOfValues.get(i).isWritable()) {
                    sb.append(formatComponentNameToVar(component.getTitle()))
                            .append(".").append(listOfKeys.get(i))
                            .append("(").append(listOfValues.get(i)
                            .getValueAsString())
                            .append(")\n");
                }
//                }
            }
//            component.resetChangeTags();
            sb.append("\n");

        }
        String ret = sb.toString() + "####\n";
        //remove all the unnecessary newlines using regex
        ret = ret.replaceAll("\n+", "\n");
        return ret;
    }

    public String parseSensorData() {
        ArrayList<Component> components = Main.getConfig().getComponents();
        StringBuilder sb = new StringBuilder();
        // Iterate over all components
        for (Component component : components) {
            // if the component is a sensor

            if (component.isSensor()) {
                sb.append("#").append(component.getTitle()).append(": ");
                ArrayList<Value> listOfValues = component.getPropertyValues();
                ArrayList<String> listOfKeys = component.getPropertyKeys();

                // Iterate over all the keys
                if (component.getPropertiesString() != null) {
                    sb.append(component.getPropertiesString());
                } else {
                    for (int i = 0; i < listOfKeys.size(); i++) {
                        sb.append(listOfKeys.get(i) + "= " + listOfValues.get(i).getValueAsString() + ", ");
                    }
                }
                sb.append("\n");

            }
        }
        return sb.toString();
    }
    // </editor-fold>

    public String startPrompt() {
        String str = "#This is the code for programming a smart home system.\n"
                //                + "the AUTO_SET_PARAMS comment means the system should automatically decide what parameters to tweak based on the above sensor data.\n"
                + "the COMMAND comment means the user is asking the system to do something.\n"
                + "import components;\n\n";
        ArrayList<Component> components = Main.getConfig().getComponents();
        // Iterate over all components
        for (Component component : components) {
            String title = component.getTitle();
            String type = component.getType();
            // Capitalize the first letter of the title
            title = title.replace(" ", "_").toLowerCase();
            // Convert the first letter to uppercase
            type = type.substring(0, 1).toUpperCase() + type.substring(1);
            type = type.replace("-", "_");
            str += type + " " + title + "\n";
        }
        return str + "\n\n";
    }

    private String requestAndOutputExample(String command) {
        String text = "#COMMAND: " + command.replace("\n", "");
        if (command.trim().equals("")) {
            text = "#COMMAND: " + autoRequest;
        }
        return parseSensorData() + text + "\n" + parseCodexOutputExample() + "\n";
    }

    private String request(String command) {
        String text = "#COMMAND: " + command.replace("\n", "");
        if (command.trim().equals("")) {
            text = "#COMMAND: " + autoRequest;
        }
        return parseSensorData() + text + "\n";
    }

    CodexRequestHandler codex;
    String startPrompt = "";
    ArrayList<String> promptConversation;
    private int conversationSize = 8;

    public void trainOnRequest(String command) throws IOException {
        trainingLevel++;
        promptConversation.add(requestAndOutputExample(command));
        // if promptConversation.size() > 10, remove the first element
        if (promptConversation.size() > getConversationSize()) {
            promptConversation.remove(0);
        }
        MainSocketServer.writeInfo( "Trained succesfully");
        save();
    }

    public static final String autoRequest = "Set parameters based on the above sensor data.";

    public void askCommand(String command) throws Exception {
      MainSocketServer.writeInfo( "Processing command...");
        executeCodexPrompt(request(command));
    }
    
        public void voiceCommand(String command) throws Exception {
        MainSocketServer.writeInfo( "You said: "+command);
        executeCodexPrompt(request(command));
    }

    public void autoCommand() throws Exception {
       MainSocketServer.writeInfo( "Processing AI parameter tuning...");
        executeCodexPrompt(request(autoRequest));
    }

    private void executeCodexPrompt(String promptPiece) throws IOException, Exception {
        boolean saidSomething = false;
        CodexRequest request = new CodexRequest();
        request.addStopSequence("\n####");
        request.addStopSequence("#COMMAND:");
        String prompt = startPrompt + String.join("\n####\n", promptConversation) + promptPiece;
        prompt = prompt.replaceAll("\n+", "\n");
        request.setPrompt(prompt);
        request.setMax_tokens(140);
        CodexOutput out = codex.generate(request, Engine.CUSHMAN_CODEX);
        String output = out.getChoices().get(0).getText();
        if (output.endsWith("\n")) {
            promptConversation.add(promptPiece + output + "####\n");
        } else {
            promptConversation.add(promptPiece + output + "\n####\n");
        }

        if (promptConversation.size() > getConversationSize()) {
            promptConversation.remove(0);
        }

        for (String item : output.split("\n")) {
            item = item.replace("#", "");
            if (!item.trim().equals("")) {
                try {
                    //Each line looks something like this: living_room.Activated(false)
                    //remove the ) at the end of the line
                    String[] split = item.split("\\(");
                    String[] split2 = split[0].split("\\.");
                    String componentName = split2[0];
                    String propertyName = split2[1];
                    String propertyValue = split[1].substring(0, split[1].length() - 1);
                    //iterate over each component in Main.config and find the one that has a matching componentName
                    for (Component component : Main.getConfig().getComponents()) {
                        if (formatComponentNameToVar(component.getTitle()).equals(componentName)) {
                            //iterate over each property in the component and find the one that has a matching propertyName
                            for (String key : component.getPropertyKeys()) {
                                if (key.equals(propertyName)) {
//                                    System.out.println(">>>Setting component " + component.getTitle() + "'s " + key + " parameter to " + propertyValue);
                                    //set the value of the property to the value of the propertyValue
                                    component.setPropertyBasedOnType(key, propertyValue);
                                    component.handlePropertyChange(key);
                                    if (component.getType().equals("verbose-output")) {
                                        MainSocketServer.writeInfo( "\"" + propertyValue + "\"");
                                        TTS.speak(propertyValue);
                                        saidSomething = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Codex Parsing error. " + e.getMessage());
                }
            }
        }
        System.out.println("Updated values on UI");
        if (!saidSomething) {
             MainSocketServer.writeInfo( "done.");
        }
        save();
    }

    public void save() throws IOException {
        FileUtils.writeString("prompt.txt", String.join("\n####\n", promptConversation));
    }

    public void reset() throws IOException {
        FileUtils.writeString("prompt.txt", "");
        promptConversation = new ArrayList<>();
        System.out.println("Codex log succesfully reset");
        trainingLevel = 0;
    }

    public CodexAssistant() throws IOException {
        codex = new CodexRequestHandler(FileUtils.readString("CodexAPIKey.txt"));
        startPrompt = startPrompt();
        promptConversation = new ArrayList<>();
        File f = FileUtils.file("prompt.txt");
        if (f.exists()) {
            String str = FileUtils.readString("prompt.txt");
            if (!str.trim().equals("")) {
                String file[] = str.split("\n####\n");
                for (String chunk : file) {
                    if (!chunk.trim().equals("")) {
                        promptConversation.add(chunk + "\n####\n");
                        trainingLevel++;
                    }
                }
            }
        }

    }
}
