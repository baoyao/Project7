package com.example.fmutil;


public class FMUtil {
    /**
     * path ="/dev/fm"
     * @param path
     */
    public static native void openDevice(String path);

    public static native void closeDevice();

    /**
     * arg = 1
     * @param arg
     */
    public static native void powerOn(int arg);

    /**
     * arg = 0
     * @param arg
     */
    public static native void powerDown(int arg);

    /**
     * arg = 8980
     * frequency:89.8
     * @param arg
     */
    public static native void tune(int arg);

    public static native void setMute(int arg);

    public static native void setVolume(int arg);

    public static native void seek(int arg);


    public static void onApplicationCreate(){
        openDevice("/dev/fm");
        powerOn(1);
    }

    public static void onApplicationDestory(){
        powerDown(0);
        closeDevice();
    }
}
