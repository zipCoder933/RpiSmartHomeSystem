/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rpismarthome.utils;

import java.util.ArrayList;

/**
 *
 * @author sampw
 */
public class MathUtils {

    public static final boolean inBounds(final float posX, final float posY, final float x, final float y, final float width, final float height) {
        return posX > x && posX < x + width && posY > y && posY < y + height;
    }

    public static final boolean inBounds(final int posX, final int posY, final int x, final int y, final int width, final int height) {
        return posX > x && posX < x + width && posY > y && posY < y + height;
    }

    public static final float clamp(float val, float min, float max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

    public static final int clamp(int val, int min, int max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

    public static final double clamp(double val, double min, double max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

//    public static final boolean inBounds(final PApplet f, final float x, final float y, final float width, final float height) {
//        return f.mouseX > x && f.mouseX < x + width && f.mouseY > y && f.mouseY < y + height;
//    }
//
//    public static final boolean inBounds(final PApplet f, final int x, final int y, final int width, final int height) {
//        return f.mouseX > x && f.mouseX < x + width && f.mouseY > y && f.mouseY < y + height;
//    }
//    public static final Color lerpColor(final Color x, final Color y, final float blending) {
//        final float inverse_blending = 1.0f - blending;
//        final float red = x.getRed() * blending + y.getRed() * inverse_blending;
//        final float green = x.getGreen() * blending + y.getGreen() * inverse_blending;
//        final float blue = x.getBlue() * blending + y.getBlue() * inverse_blending;
//        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f);
//    }
    public static final float map(final float value, final float start1, final float stop1, final float start2, final float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    public static final float lerp(final float start, final float stop, final float amt) {
        return start + (stop - start) * amt;
    }

    public static double standardDeviation(final ArrayList table) {
        final double mean = mean(table);
        double temp = 0.0;
        for (int i = 0; i < table.size(); ++i) {
            final double val = (double) table.get(i);
            final double squrDiffToMean = Math.pow(val - mean, 2.0);
            temp += squrDiffToMean;
        }
        final double meanOfDiffs = temp / table.size();
        return Math.sqrt(meanOfDiffs);
    }

    public static double mean(final ArrayList table) {
        int total = 0;
        for (int i = 0; i < table.size(); ++i) {
            final double currentNum = (double) table.get(i);
            total += (int) currentNum;
        }
        return total / table.size();
    }
}
