package com.apical.apicalradio;

//import android.app.DvdControl;
//import android.app.DvdControl.OnCmdCompletedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RadioController
{
    private static final String TAG = "qulinglingRadio RadioController***";
    private static final String APP_TYPE_RADIO = "com.csr.dvd.APP_RADIO";
    private static final String ACTION_TYPE_RADIO = "com.csr.dvd.ACTION_RADIO";
    
    //�������������owl
    //�ֶ�����
    public static final Byte RADIO_HANDLE_SEARCH_UP = 0x00;
    //�ֶ�����
    public static final Byte RADIO_HANDLE_SEARCH_DOWN = 0x01;
    //��һ��Ԥ��̨
    public static final Byte RADIO_PRE_FREQ = 0x02;
    //��һ��Ԥ��̨
    public static final Byte RADIO_NEXT_FREQ = 0x03;
    //�Զ���������
    public static final Byte RADIO_AUTO_SEARCH_UP = 0x04;
    //�Զ���������
    public static final Byte RADIO_AUTO_SEARCH_DOWN = 0x05;
    //���Ԥ��̨
    public static final Byte RADIO_PRESET_BROWSE = 0x06;
    //ɨ�貢�Զ��洢��̨
    public static final Byte RADIO_AUTO_SEARCH_SAVE = 0x07;
    //AM�л�
    public static final Byte RADIO_AM_SWITCH = 0x08;
    //FM�л�
    public static final Byte RADIO_FM_SWITCH = 0x09;
    //���ɨ��
    public static final Byte RADIO_FM_BROWER = 0x0B; 
    //LOC�л�
    public static final Byte RADIO_LOC_SWITCH = 0x10;
    //�л���AM1
    public static final Byte RADIO_SWITCH_AM1 = 0x12;
    //�л���AM1
    public static final Byte RADIO_SWITCH_AM2 = 0x13;
    //�л���FM1
    public static final Byte RADIO_SWITCH_FM1 = 0x14;
    //�л���FM2
    public static final Byte RADIO_SWITCH_FM2 = 0x15;
    //�л���FM3
    public static final Byte RADIO_SWITCH_FM3 = 0x16;
    //ֹͣɨ��
    public static final Byte RADIO_STOP_SCAN = 0x17;
    //AF
    public static final Byte RADIO_SWITCH_AF = 0x0D;
    //TA
    public static final Byte RADIO_SWITCH_TA = 0x0E;

    
    //��Ч�ֽ�
    public static final Byte RADIO_NULL_BYTE = 0x00;
    
    //ѡ̨��ʼ���//�����0x00��ʼ�� ��ģ��0x01
    public static final Byte RADIO_SELECT_CHANNEL_BEGIN = 0x00;
    
    //��������Ϣ
    public static final Byte NOT_MAININTERFACE = 0x00;
    public static final Byte IN_MAININTERFACE = 0x01;
    public static final Byte REQUES_STATE_INFO = 0x02;
    //������ģʽ
    public static final Byte RADIO_XG_MODE = 0x01;
    //DVDģʽ
    public static final Byte DVD_XG_MODE = 0x02;
    //iPod
    public static final Byte IPOD_XG_MODE = 0x07;
    //main menu mode
    public static final Byte MAIN_MENU_MODE = 0x01;

//    private DvdControl mRadioControl;
    private Context mContext;

    public RadioController(Context context)
    {
        Log.d(TAG, "DvdController(" + context + ")");
//        mRadioControl = new DvdControl(context, APP_TYPE_RADIO);
        mContext = context;
    }
//    
//    public void setOnCmdCompletedListener(OnCmdCompletedListener listener)
//    {
//        Log.d(TAG, "setOnCmdCompletedListener(" + listener + ")");
//        mRadioControl.setOnCmdCompletedListener(listener);
//    }
//
//    public void setOffCmdCompletedListener(OnCmdCompletedListener listener)
//    {
//        Log.d(TAG, "setOffCmdCompletedListener(" + listener + ")");
//        mRadioControl.setOffCmdCompletedListener(listener);
//    }

    public void StartRadioService(byte cmdID, Bundle param)
    {
        Log.d(TAG, "getDvdStatus(" + cmdID + ", " + param + ")");
        Intent intent = new Intent(mContext, RadioService.class);
        intent.setAction(RadioService.ACTION_TYPE_RADIO);
        intent.putExtra("cmd", cmdID);
        if (param != null)
        {
            intent.putExtras(param);
        }
        mContext.startService(intent);        
    }
    
    public void StopService() {
        Intent intent = new Intent(mContext, RadioService.class);
        intent.setAction(RadioService.ACTION_TYPE_RADIO);
		mContext.stopService(intent);
	}

    public void getConnectionStatus()
    {
        Log.d(TAG, "getConnectionStatus()");
        Bundle param = new Bundle();
//        mRadioControl.sendCommand(DvdControl.NATIVE_CMD_GET_DVDSTATUS, param);
    }

    public void getSystemSetup(byte type)
    {
        Log.d(TAG, "getSystemSetup(" + type + ")");
        Bundle param = new Bundle();
        param.putByte("setting_type", type);
//        mRadioControl.sendCommand(DvdControl.NATIVE_CMD_GET_DVDSETTING, param);
    }

    public void setSystemSetup(Bundle param)
    {
        Log.d(TAG, "setSystemSetup(" + param + ")");
//        mRadioControl.sendCommand(DvdControl.SOC_CMD_SYS_SETUP, param);
    }
    
	public void GetSysSettings(String setting, int iBytes, Bundle param) 
	{
//		mRadioControl.GetSysSettings(setting, iBytes, param);
	}
	
    public void getSource()
    {
        Log.d(TAG, "getSource()");
//        mRadioControl.sendCommand(DvdControl.SOC_CMD_GET_SOURCE, null);
    }

    public void setSource(byte type)
    {
        Log.d(TAG, "setSource(" + type + ")");
        Bundle param = new Bundle();
        param.putByte("source_type", type);
//        mRadioControl.sendCommand(DvdControl.SOC_CMD_SET_SOURCE, param);
    }

    public void getDeviceStatus()
    {
        Log.d(TAG, "getDeviceStatus()");
//        mRadioControl.sendCommand(DvdControl.SOC_CMD_GET_DEVICESTATUS, null);
    }

    public void previous()
    {
        Log.d(TAG, "previous()");
    }

    public void next()
    {
        Log.d(TAG, "next()");
    }

    public void play()
    {
        Log.d(TAG, "play()");
    }

    public void pause()
    {
        Log.d(TAG, "pause()");
    }

    public void stop()
    {
        Log.d(TAG, "stop()");
    }
    
    //�������ն��������
    public void RadioCtrl(Byte CtrlCode) 
    {
    	Log.d(TAG,"RadioCtrl---------------->CtrlCode = "+CtrlCode);
        Bundle param = new Bundle();
        param.putByte("CtrlCode", CtrlCode);
//    	mRadioControl.sendCommand(DvdControl.SOC_RADIO_CTRL, param);	
	}
    
    public void SetRadioPty(Byte CtrlCode) 
    {
    	Log.d(TAG,"RadioCtrl---------------->CtrlCode = "+CtrlCode);
        Bundle param = new Bundle();
        param.putByte("PTY_Type", CtrlCode);
//    	mRadioControl.sendCommand(DvdControl.SOC_SORCH_PTY, param);	
	}    
	
    //��ȡ��ǰ������Ƶ��
    public void GetRadioCurFreq()
	{
        Bundle param = new Bundle();
        param.putByte("null", RADIO_NULL_BYTE);
//    	mRadioControl.sendCommand(DvdControl.SOC_GET_CUR_FREQ, param);	
	}
    
    //��ȡ��ǰ������״̬
    public void GetRadioCurState()
	{
        Bundle param = new Bundle();
        param.putByte("null", RADIO_NULL_BYTE);
//    	mRadioControl.sendCommand(DvdControl.SOC_GET_RADIO_STATUS, param);	
	}
    
    //���ŵ�ǰ����ָ����̨�б�λ�õĵ� ̨
    public void PlayRadioInList(Byte posIndex)
	{
    	Log.d(TAG,"PlayRadioInList-------->posIndex="+posIndex);
        Bundle param = new Bundle();
        param.putByte("Pos", posIndex);
//    	mRadioControl.sendCommand(DvdControl.SOC_PLAY_INLIST, param);	
	}
    
    //�����л�
    public void GetRadioSwitchBand(Byte band)
	{
        Bundle param = new Bundle();
        param.putByte("Band", band);
//    	mRadioControl.sendCommand(DvdControl.SOC_SWITCH_BAND, param);	
	}
    
    //�ڵ�ǰ��������Ƶ�ʲ���
    public void SetRadioFreq(Byte FreqH, Byte FreqL)
	{
    	Log.d(TAG,"SetRadioFreq-------->FreqH="+FreqH + "  FreqL"+FreqL);
        Bundle param = new Bundle();
        param.putByte("FreqH", FreqH);
        param.putByte("FreqL", FreqL);
//    	mRadioControl.sendCommand(DvdControl.SOC_PLAY_FREQ, param);	
	}    
    
    //��ȡ����Ƶ���б�
    public void GetRadioFreqList()
	{
        Bundle param = new Bundle();
        param.putByte("null", RADIO_NULL_BYTE);
//    	mRadioControl.sendCommand(DvdControl.SOC_GET_FREQLIST, param);	
	}
    
    //���浱ǰ����Ƶ����ָ��λ��
    public void SaveRadioFreq(Byte posIndex)
	{
        Bundle param = new Bundle();
        param.putByte("Pos", posIndex);
//    	mRadioControl.sendCommand(DvdControl.SOC_SAVE_CUR_FREQ, param);	
	}
    
    //��ȡ�洢�ĵ�̨Ƶ��
    public void GetRadioSaveFreq()
	{
        Bundle param = new Bundle();
        param.putByte("null", RADIO_NULL_BYTE);
//    	mRadioControl.sendCommand(DvdControl.SOC_GET_SAVED_FREQ, param);	
	}
    
    //�������ʼ��
    public void InitRadio()
	{
        Bundle param = new Bundle();
        byte DEFAULTNUM = -1;
        byte DEFAULTZERO = 0;
        byte INIT = 1;
        param.putByte("En", INIT);
        param.putByte("Band", DEFAULTNUM);
        param.putByte("Area", DEFAULTNUM);
        param.putByte("FreqH", DEFAULTZERO);
        param.putByte("FreqL", DEFAULTZERO);
//    	mRadioControl.sendCommand(DvdControl.RADIO_SOC_INIT, param);	
	}
    
    //�ر�������
    public void CloseRadio()
	{
    	Log.d(TAG, "QLLRadio-------------CloseRadio");
        Bundle param = new Bundle();
        byte DEFAULTNUM = -1;
        byte DEFAULTZERO = 0;
        byte CLOSE = 0;
        param.putByte("En", CLOSE);
        param.putByte("Band", DEFAULTNUM);
        param.putByte("Area", DEFAULTNUM);
        param.putByte("FreqH", DEFAULTZERO);
        param.putByte("FreqL", DEFAULTZERO);
//    	mRadioControl.sendCommand(DvdControl.RADIO_SOC_INIT, param);	
	}
    
    public void SetRadioMode()
	{
    	Log.d(TAG, "SetRadioMode------------->send radio mode");
        Bundle param = new Bundle();
        param.putByte("WorkMode", RADIO_XG_MODE);
//    	mRadioControl.sendCommand(DvdControl.SETUP_SOC_SET_WORKMODE, param);
	}
    
  
    /*
     * qulingling 20131224
     * 释放WORKMODE
     */
    public void ReleaseWorkMode(byte workMode) 
	{
        Bundle param = new Bundle();
        param.putByte("WorkMode", workMode);
//        mRadioControl.sendCommand(DvdControl.SETUP_SOC_REL_WORKMODE, param);	
 	}
    
    /*
     * qulingling 20131226
     * 后台WORKMODE
     */
    public void BackWorkMode(byte workMode) 
	{
        Bundle param = new Bundle();
        param.putByte("WorkMode", workMode);
//        mRadioControl.sendCommand(DvdControl.SETUP_MODE_BACKGROUND, param);	
 	}
    
}
