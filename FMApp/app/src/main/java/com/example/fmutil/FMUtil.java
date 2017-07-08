package com.example.fmutil;


import android.util.Log;

import com.apical.apicalradio.RadioInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static List<RadioInfo> mRadioList= new ArrayList<RadioInfo>();
    private static int radioIndex=0;

    public static void setRadioList(List<RadioInfo> radioList){
        mRadioList=radioList;
    }

    public static RadioInfo getNextRadio(){
        if(mRadioList.size()==0){
            return null;
        }
        radioIndex=radioIndex+1>=mRadioList.size()?0:radioIndex+1;
        return mRadioList.get(radioIndex);
    }
    public static RadioInfo getPrepRadio(){
        if(mRadioList.size()==0){
            return null;
        }
        radioIndex=radioIndex-1<0?mRadioList.size()-1:radioIndex-1;
        return mRadioList.get(radioIndex);
    }

    public static int[] FREQS=new int[]{9050,9090,9110,9300,9450,10280,10300,10430,10490,10570,10710};

    public static int[] ALL_FREQS= new int[] { 6440, 7440, 8740, 8750, 8760, 8800, 8810, 8820,
            8830, 8850, 8860, 8870, 8880, 8890, 8900, 8910, 8920, 8930,
            8940, 8950, 8960, 8970, 8980, 9000, 9010, 9020, 9030, 9040,
            9060, 9070, 9080, 9090, 9100, 9110, 9120, 9140, 9150, 9160,
            9170, 9180, 9210, 9220, 9230, 9240, 9250, 9260, 9270, 9280,
            9290, 9300, 9310, 9320, 9330, 9340, 9350, 9360, 9370, 9390,
            9400, 9410, 9420, 9430, 9440, 9460, 9470, 9480, 9490, 9500,
            9510, 9520, 9530, 9540, 9550, 9560, 9570, 9590, 9600, 9610,
            9620, 9630, 9640, 9650, 9660, 9670, 9680, 9690, 9700, 9710,
            9720, 9730, 9740, 9750, 9760, 9770, 9780, 9790, 9800, 9810,
            9820, 9830, 9840, 9850, 9860, 9870, 9890, 9900, 9910, 9920,
            9930, 9940, 9950, 9960, 9970, 9980, 10000, 10010, 10020, 10030,
            10040, 10050, 10060, 10070, 10080, 10090, 10100, 10110, 10130,
            10140, 10150, 10160, 10170, 10180, 10190, 10200, 10210, 10220,
            10240, 10250, 10260, 10270, 10280, 10290, 10300, 10310, 10320,
            10330, 10340, 10350, 10360, 10370, 10380, 10390, 10400, 10410,
            10420, 10430, 10440, 10450, 10460, 10470, 10480, 10490, 10500,
            10510, 10520, 10530, 10540, 10550, 10560, 10570, 10580, 10590,
            10600, 10610, 10620, 10630, 10640, 10650, 10660, 10670, 10680,
            10690, 10700, 10710, 10730, 10740, 10750, 10760, 10770, 10780,
            10790 };

    public static int getFreqIndex(int freq){
        for (int i=0;i<ALL_FREQS.length;i++){
            if(ALL_FREQS[i]>freq){
                return i;
            }
        }
        return 0;
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

        final int FMSM_TH = 38;
        final int IFC = 1;


        boolean bool=(result[LEVEL]>=38)&&(result[USN]<15)&&(result[WAM]<25)&&(result[BANDWIDTH]>=-54&&IFC<74);
        if(false){//一般电台
            r=1;
        }else if((freq==99100)&&(result[BANDWIDTH]>1280)&&(result[LEVEL]>=FMSM_TH)&&(result[USN]<154)&&(result[WAM]<154)&&(IFC<106)){//99.1特殊电台0x6a=106
            r=2;
        }else if((result[LEVEL]>=FMSM_TH)&&(result[USN]<128)&&(result[WAM]<128)&&(IFC<106)&&freq==94200){//94.2特殊电台
            r=3;
        }

        boolean hasFreq=false;
        for(int f : FREQS){
            if(freq==f){
                hasFreq = true;
            }
        }
        if(hasFreq){
            Log.v("bao","compareResult freq: "+freq+" result: "+ Arrays.toString(result));
        }

        if(result[LEVEL]>=40){
            r=1;
        }

//        final int _OFFSET_=3;
//        if((result[LEVEL]>=35-_OFFSET_)&&(result[USN]<=15+_OFFSET_)&&(result[WAM]<=25+_OFFSET_)&&(result[BANDWIDTH]>=-9-_OFFSET_)&&freq==9050){
//            r=1;
//        }
//        if((result[LEVEL]>=30-_OFFSET_)&&(result[USN]<=22+_OFFSET_)&&(result[WAM]<=41+_OFFSET_)&&(result[BANDWIDTH]>=-5-_OFFSET_)&&freq==9090){
//            r=1;
//        }
//        if((result[LEVEL]>=44-_OFFSET_)&&(result[USN]<=11+_OFFSET_)&&(result[WAM]<=25+_OFFSET_)&&(result[BANDWIDTH]>=-3-_OFFSET_)&&freq==9110){
//            r=1;
//        }
//        if((result[LEVEL]>=50-_OFFSET_)&&(result[USN]<=6+_OFFSET_)&&(result[WAM]<=11+_OFFSET_)&&(result[BANDWIDTH]>=11-_OFFSET_)&&freq==9300){
//            r=1;
//        }if((result[LEVEL]>=53-_OFFSET_)&&(result[USN]<=3+_OFFSET_)&&(result[WAM]<=4+_OFFSET_)&&(result[BANDWIDTH]>=-7-_OFFSET_)&&freq==9450){
//            r=1;
//        }if((result[LEVEL]>=38-_OFFSET_)&&(result[USN]<=13+_OFFSET_)&&(result[WAM]<=12+_OFFSET_)&&(result[BANDWIDTH]>=-65-_OFFSET_)&&freq==10280){
//            r=1;
//        }if((result[LEVEL]>=37-_OFFSET_)&&(result[USN]<=6+_OFFSET_)&&(result[WAM]<=18+_OFFSET_)&&(result[BANDWIDTH]>=15-_OFFSET_)&&freq==10300){
//            r=1;
//        }if((result[LEVEL]>=53-_OFFSET_)&&(result[USN]<=3+_OFFSET_)&&(result[WAM]<=4+_OFFSET_)&&(result[BANDWIDTH]>=47-_OFFSET_)&&freq==10430){
//            r=1;
//        }if((result[LEVEL]>=36-_OFFSET_)&&(result[USN]<=2+_OFFSET_)&&(result[WAM]<=21+_OFFSET_)&&(result[BANDWIDTH]>=11-_OFFSET_)&&freq==10490){
//            r=1;
//        }if((result[LEVEL]>=32-_OFFSET_)&&(result[USN]<=22+_OFFSET_)&&(result[WAM]<=25+_OFFSET_)&&(result[BANDWIDTH]>=-42-_OFFSET_)&&freq==10570){
//            r=1;
//        }if((result[LEVEL]>=37-_OFFSET_)&&(result[USN]<=15+_OFFSET_)&&(result[WAM]<=13+_OFFSET_)&&(result[BANDWIDTH]>=39-_OFFSET_)&&freq==10710) {
//            r = 1;
//        }
        /*
        *
V/bao     (24413): mOnSaveChannelClick freq:9050 read : [36, 11, 23, -6, 50]
V/bao     (24413): mOnSaveChannelClick freq:9090 read : [30, 19, 39, 0, -54]
V/bao     (24413): mOnSaveChannelClick freq:9110 read : [44, 11, 25, -3, 48]
V/bao     (24413): mOnSaveChannelClick freq:9300 read : [50, 6, 11, 11, -48]
V/bao     (24413): mOnSaveChannelClick freq:9450 read : [53, 3, 4, -7, -48]
V/bao     (24413): mOnSaveChannelClick freq:10280 read : [38, 13, 12, -65, -48]
V/bao     (26938): mOnSaveChannelClick freq:10300 read : [37, 6, 18, 15, -26]
V/bao     (26938): mOnSaveChannelClick freq:10430 read : [53, 3, 4, 47, -48]-----------------------------------------------
V/bao     (26938): mOnSaveChannelClick freq:10490 read : [36, 2, 21, 11, 116]
V/bao     (26938): mOnSaveChannelClick freq:10570 read : [32, 22, 25, -42, 116]
V/bao     (26938): mOnSaveChannelClick freq:10710 read : [37, 15, 13, 39, -26]
        * */
        /*if((result[BANDWIDTH]>2005)&&((result[LEVEL]>=FMSM_TH)&&(result[USN]<112)&&(result[WAM]<112)&&IFC<74)){//一般电台0x4a=74
            r=1;
        }else if((freq==99100)&&(result[BANDWIDTH]>1280)&&(result[LEVEL]>=FMSM_TH)&&(result[USN]<154)&&(result[WAM]<154)&&(IFC<106)){//99.1特殊电台0x6a=106
            r=2;
        }else if((result[LEVEL]>=FMSM_TH)&&(result[USN]<128)&&(result[WAM]<128)&&(IFC<106)&&freq==94200){//94.2特殊电台
            r=3;
        }*/

        /*//0x07d5: 2005 0x70: 112 0x4a： 74
        if((result[BANDWIDTH]>0x07d5)&&((result[LEVEL]>=FMSM_TH)&&(result[USN]<0x70)&&(result[WAM]<0x70)&&IFC<0x4a)){//一般电台0x4a=74
            r=1;
        //0x0500： 1280 0x9a: 154 0x6a: 106
        }else if((freq==99100)&&(result[BANDWIDTH]>0x0500)&&(result[LEVEL]>=FMSM_TH)&&(result[USN]<0x9a)&&(result[WAM]<0x9a)&&(IFC<0x6a)){//99.1特殊电台0x6a=106
            r=2;
        //0x80： 128 0x80: 128 0x6a: 106
        }else if((result[LEVEL]>=FMSM_TH)&&(result[USN]<0x80)&&(result[WAM]<0x80)&&(IFC<0x6a)&&freq==94200){//94.2特殊电台
            r=3;
        }

V/bao     ( 9250): FMUtil.read : [55, 5, 4, -14, 56] freq: 6016 一般电台
V/bao     ( 9250): FMUtil.read : [55, 5, 4, -7, 56] freq: 6017 一般电台
V/bao     ( 9250): FMUtil.read : [55, 5, 4, 7, 56] freq: 6018 一般电台

V/bao     ( 9250): FMUtil.read : [33, 31, 37, 31, -54] freq: 10703
V/bao     ( 9250): FMUtil.read : [32, 31, 25, -125, -54] freq: 10704
V/bao     ( 9250): FMUtil.read : [31, 34, 27, -76, -54] freq: 10705
V/bao     (15622): mOnSaveChannelClick freq:9050 read : [41, 10, 21, -7, 116]
V/bao     (15622): mOnSaveChannelClick freq:10430 read : [55, 3, 4, 5, -48]
V/bao     (15622): mOnSaveChannelClick freq:9050 read : [41, 11, 24, -36, 116]
V/bao     (15622): mOnSaveChannelClick freq:10430 read : [55, 3, 5, 40, -48]

*/
        return r;
    }
}
