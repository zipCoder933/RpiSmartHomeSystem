///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.rpismarthome.server;
//
//import com.rpismarthome.configuration.Component;
//import com.rpismarthome.configuration.values.value.Type;
//import com.rpismarthome.configuration.values.value.Value;
//import java.util.ArrayList;
//
///**
// *
// * @author zipCoder933
// */
//public class DeepParameterTuner {
//
//    public void train() {
//
//    }
//
//    public void setParameters() {
//        ArrayList<Double> inputs = new ArrayList<>();
//        ArrayList<Double> outputs = new ArrayList<>();
//
//        ArrayList<String> inputKeys = new ArrayList<>();
//        ArrayList<String> outputKeys = new ArrayList<>();
//
//        System.out.println("Setting parameters...");
//        for (Component comp : Main.getConfig().getComponents()) {
//            for (String property : comp.getPropertyKeys()) {
//                //Get the property
//                Value value = comp.getProperty(property);
//                //Check if the property is a number
//                if (value.getType() == Type.NUMBER) {
//                    double val = value.getValueAsNumber();
//                    if (comp.isSensor()) {
//                        inputs.add(val);
//                    } else {
//                        outputs.add(val);
//                    }
//
//                    if (comp.isSensor()) {
//                        inputKeys.add(comp.getId() + "|" + property);
//                    } else {
//                        outputKeys.add(comp.getId() + "|" + property);
//                    }
//
//                } else if (value.getType() == Type.BOOLEAN) {
//                    boolean val = value.getValueAsBoolean();
//                    if (comp.isSensor()) {
//                        if (val) {
//                            inputs.add(1.0);
//                            inputs.add(0.0);
//                        } else {
//                            inputs.add(0.0);
//                            inputs.add(1.0);
//                        }
//                    } else {
//                        if (val) {
//                            outputs.add(1.0);
//                            outputs.add(0.0);
//                        } else {
//                            outputs.add(0.0);
//                            outputs.add(1.0);
//                        }
//                    }
//                    if (comp.isSensor()) {
//                        inputKeys.add(comp.getId() + "|" + property+"|true");
//                        inputKeys.add(comp.getId() + "|" + property+"|false");
//                    } else {
//                        outputKeys.add(comp.getId() + "|" + property+"|true");
//                        outputKeys.add(comp.getId() + "|" + property+"|false");
//                    }
//
//                }
//
//            }
//        }
//        
//         System.out.println("\nINPUTS:");
//        for (int i=0;i< inputKeys.size();i++) {
//            System.out.println(inputKeys.get(i)+"\t"+inputs.get(i));
//        }
//        System.out.println("\nOUTPUTS:");
//        for (int i=0;i< inputKeys.size();i++) {
//            System.out.println(outputKeys.get(i)+"\t"+outputs.get(i));
//        }
//      
//    }
//
//}
