/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rpismarthome.utils;

/**
 *
 * @author zipCoder933
 */
public class Timer {

    private long start;

    public Timer() {
        start = System.currentTimeMillis();
    }

    public long getElapedTime() {
        return (System.currentTimeMillis() - start);
    }

    public long getElapedTimeInSeconds() {
        return (System.currentTimeMillis() - start) / 1000;
    }

    public long getElapedTimeInMinutes() {
        return getElapedTimeInSeconds() / 60;
    }

    public boolean elaspedTimeInMinutesIsOver(int val) {
        return getElapedTimeInMinutes() > (val - 1);
    }

    public boolean elaspedTimeInSecondsIsOver(int val) {
        return getElapedTimeInSeconds() > (val - 1);
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

}
