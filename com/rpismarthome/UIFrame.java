/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome;

import com.rpismarthome.configuration.component.Component;
import com.rpismarthome.configuration.component.Value;
import com.rpismarthome.utils.MathUtils;
import java.util.ArrayList;
import processing.core.*;

/**
 *
 * @author zipCoder933
 */
public class UIFrame extends PApplet implements PConstants {

    /**
     * @return the realAct
     */
    public double getRealAct() {
        return realAct;
    }

    /**
     * @param realAct the realAct to set
     */
    public void setRealAct(double realAct) {
        this.realAct += realAct;
    }

    public UIFrame() {
        super();
        PApplet.runSketch(new String[]{this.getClass().getSimpleName()}, this);
    }

    @Override
    public void settings() {
        if (!Main.isRunningFromLocal()) {
            fullScreen();
        } else {
            size(800, 460);
        }
    }
    PFont myFont;

    public void setup() {
        noStroke();
//        String[] fontList = PFont.list();
//        printArray(fontList);
//        myFont = createFont(fontList[0], 64);
//        textFont(myFont);
    }

    double activity;
    private double realAct = 100;
    float textColor = 0;

    public String parseSensorData() {
        ArrayList<Component> components = Main.getConfig().getComponents();
        StringBuilder sb = new StringBuilder();
        // Iterate over all components
        for (Component component : components) {
            if (component.getUIpropertiesString() != null) {
                sb.append(component.getUIpropertiesString()).append("\n");
            }

        }
        return sb.toString();
    }

    public void setInfo(String msg) {
        message = msg;
        textColor = 255;
    }

    String message = "";

    @Override
    public void draw() {
        background(0, 0, 20);
        textAlign(LEFT, TOP);
        String ampm = "AM";
        String minute = minute() + "";
        if (minute() < 10) {
            minute = "0" + minute;
        }
        int hour = hour();
        if (hour > 12) {
            ampm = "PM";
            hour -= 12;
        }

        textSize(18);
        fill(255, 150);
        text(parseSensorData(), 50, 20, width - 100, 100);
        textSize(68);
        fill(255);
        textAlign(CENTER, CENTER);
        text(hour + ":" + minute + " " + ampm, width / 2, height / 3);
        realAct = realAct * 0.99;
        realAct = MathUtils.clamp(realAct, 6, 50);

        activity = (getRealAct() + activity) * 0.9;

        float val = map((float) realAct, 0, 50, 0, 1);
        fill(lerpColor(color(255, 0, 0), color(255, 255, 0), val));
        colorBar((int) activity, 0);

        fill(100, 100, 100, textColor);
        textColor = (float) (textColor * 0.99);
        textColor = MathUtils.clamp(textColor, 25, 255);
        rect(0, height - 190, width, height, 6);
        textAlign(CENTER, TOP);
        fill(255);
        textSize(18);

        text(message.toUpperCase(),
                50, height - 150, width - 100, 130);
    }

    private void colorBar(int w, int off) {
        rect(width / 2 - w / 2 + off, height / 2 - 5, w, 10, 6);
    }
}
