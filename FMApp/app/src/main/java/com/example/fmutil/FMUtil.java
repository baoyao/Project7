package com.example.fmutil;


public class FMUtil {

    static{
        System.loadLibrary("fmutil");
    }
    /**
     * path ="/dev/fm"
     */
    public static native void openDevice(String path);

    public static native void closeDevice();

    /**
     * arg = 1
     */
    public static native void powerOn(int arg);

    /**
     * arg = 0
     */
    public static native void powerDown(int arg);

    /**
     * arg = 8980
     * frequency:89.8
     */
    public static native void tune(int arg);

    public static native void setMute(int arg);

    public static native void setVolume(int arg);

    public static native void seek(int arg);

    public static native byte[] read();

    public static void onApplicationCreate(){
        openDevice("/dev/fm");
        powerOn(1);
    }

    public static void onApplicationDestory(){
        powerDown(0);
        closeDevice();
    }

    /**
     *判断是否是好台
     */
    public static int compareResult(int freq,byte[] result){
        int r = -1;
        //INDEX for [level, usn, wam, offset, bandwidth]
        final int LEVEL=0;
        final int USN=1;
        final int WAM=2;
        final int OFFSET=3;
        final int BANDWIDTH=4;

        final int FMSM_TH = -1;
        final int IFC = 1;

        if((result[BANDWIDTH]>0x07d5)&&((result[LEVEL]>=FMSM_TH)&&(result[USN]<0x70)&&(result[WAM]<0x70)&&IFC<0x4a)){//一般电台
            r=1;
        }else if((freq==99100)&&(result[BANDWIDTH]>0x0500)&&(result[LEVEL]>=FMSM_TH)&&(result[USN]<0x9a)&&(result[WAM]<0x9a)&&(IFC<0x6a)){//99.1特殊电台
            r=2;
        }else if((result[LEVEL]>=FMSM_TH)&&(result[USN]<0x80)&&(result[WAM]<0x80)&&(IFC<0x6a)&&freq==94200){//94.2特殊电台
            r=3;
        }
        return r;
    }
}
