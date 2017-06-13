package com.apical.apicalradio;

//import android.hardware.ApicalCtrlsManager;
import android.util.Log;

//ApicalӲ��������ؽӿڷ�װ
public class ApicalHardwareCtrl {
	
	private static final String TAG="qulinglingRadio ApicalHardwareCtrl***";
	
    //apical��ӿ�
//    private ApicalCtrlsManager mApicalCtrlsManager;
    //��Դ����
    public static final byte APP_DVD = 0x01;
    public static final byte APP_RADIO = 0x02;
    public static final byte APP_IPOD = 0x03;
    
    private static final String APP_DVD_NAME = "ApicalDVD";
    private static final String APP_RADIO_NAME = "ApicalRadio";
    private static final String APP_IPOD_NAME = "ApicalIpod";
    
    private static final String AUDIO_TYPE_DVD = "DVD";
    private static final String AUDIO_TYPE_RADIO = "RADIO";
    private static final String AUDIO_TYPE_IPOD = "ApicalIpod";
    
    private String mAppNameString = "";
    private String mVedioTypeString = "";
    
    public ApicalHardwareCtrl(byte AppType) 
    {
        //��ӿڿ���
//        mApicalCtrlsManager = new ApicalCtrlsManager();
        
        switch (AppType)
		{
		case APP_DVD:
		    mAppNameString = APP_DVD_NAME;
		    mVedioTypeString = AUDIO_TYPE_DVD;
			break;
		case APP_RADIO:
		    mAppNameString = APP_RADIO_NAME;
		    mVedioTypeString = AUDIO_TYPE_RADIO;
			break;
		case APP_IPOD:
		    mAppNameString = APP_IPOD_NAME;
		    mVedioTypeString = AUDIO_TYPE_IPOD;
			break;
		default:
			break;
		}
	}
	
    //��ƵԴ����
    public int VideoSourceRequest() 
    {
    	try 
    	{	
//    		mApicalCtrlsManager.VideoSourceRequest(mAppNameString, mVedioTypeString, 2);
		} 
    	catch (Exception e) 
    	{
			Log.e(TAG,"VideoSourceRequest()--->" + e);
		}
		return 0;
	}
    
    //��ƵԴ�ͷ�
    public int VideoSourceRelease() {
    	
    	try 
    	{	
//    		mApicalCtrlsManager.VideoSourceRelease(mAppNameString);
		} 
    	catch (Exception e) 
    	{
			Log.e(TAG,"VideoSourceRelease()--->" + e);
		}
		return 0;
	}
    
    //��ƵԴ����
    public int AudioSourceRequest() {
    	
    	try 
    	{	
    		Log.d(TAG,"AudioSourceRequest()--->Request");
//    		mApicalCtrlsManager.AudioSourceRequst(mAppNameString, mVedioTypeString, 2);
		} 
    	catch (Exception e) 
    	{
			Log.e(TAG,"AudioSourceRequest()--->" + e);
		}
    	
    	return 0;
	}
    
    //��ƵԴ�ͷ�
    public int AudioSourceRelease() {
    	
    	try 
    	{	
    		Log.d(TAG,"AudioSourceRequest()--->Release");
//    		mApicalCtrlsManager.AudioSourceDelayRelease(mAppNameString, 0);
		} 
    	catch (Exception e) 
    	{
			Log.e(TAG,"AudioSourceRequest()--->" + e);
		}
    	
    	return 0;
	}
    
    //�����ӿ�
    public int MuteRequest() {
    	try 
    	{	
//    		mApicalCtrlsManager.PaMuteRequest(mAppNameString, 1);
		} 
    	catch (Exception e) 
    	{
			Log.e(TAG,"MuteRequest()--->" + e);
		}
    	
    	return 0;
	}

    
}
