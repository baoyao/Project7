package com.apical.apicalradio;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
//import android.app.DvdControl;
//import android.app.DvdControl.OnCmdCompletedListener;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RadioService extends Service/* implements OnCmdCompletedListener*/
{
    private static final String TAG = "qulinglingRadio RadioService***";
    //owl
    public static final String ACTION_TYPE_RADIO = "com.csr.dvd.ACTION_RADIO";
    
    public static final String ACTION_TYPE_RADIO_EVENT = "com.csr.dvd.ACTION_RADIO_EVENT";
    public static final String ACTION_TYPE_GET_STATUS = "com.csr.dvd.ACTION_GET_STATUS";
    public static final String ACTION_TYPE_EXIT_APP = "com.csr.dvd.ACTION_TYPE_EXIT_APP";
    public static final String ACTION_TYPE_DVD_SETUP = "com.csr.dvd.ACTION_TYPE_DVD_SETUP";
    public static final String ACTION_TYPE_APK_SETUP = "apk_bc_setup";

    public static final byte SOURCE_TYPE_NULL = 0;
    public static final byte SOURCE_TYPE_LOCAL = 1;
    public static final byte SOURCE_TYPE_RADIO = 2;
    public static final byte SOURCE_TYPE_DVD = 3;
    public static final byte SOURCE_TYPE_AUX = 4;

    public static final byte EXCEPTION_TIMEOUT = 5;

    public static final int MAX_DISC_ID = Integer.MAX_VALUE;
    public static final int REPEAT_MODE_CMD_DELAY = 200;
    public static final int MUTE_MODE_CMD_DELAY = 200;
    public static final int PLAYSTATE_CMD_DELAY = 400;
    public static final int CURRENT_INDEX_CMD_DELAY = 400;
    public static final int COMMON_CMD_DELAY = 500;
    public static final int FILE_LIST_BATCH = 100;
    public static final int FILE_LIST_CMD_DELAY = 1000;
    public static final int SEEK_BAR_MAX = 1000;
    public static final int STOP_CMD_DELAY = 1000;
    public static final int APP_QUIT_TIMEOUT = 1500;
    public static final int SETTING_QUIT_TIMEOUT = 1200;

    private static RadioController mRadioController;
    private static RadioConfigure mRadioConfigurator;
    private static Class<?> mEntryActivityClass = RadioMainActivity.class;
    
    public static Class<?> getEntryActivityClass()
    {
        Log.d(TAG, "getEntryActivityClass()");
        synchronized (mEntryActivityClass)
        {
            return mEntryActivityClass;
        }
    }
    
    public static void setEntryActivityClass(Class<?> activityClass)
    {
        Log.d(TAG, "setEntryActivityClass(" + activityClass + ")");
        synchronized (mEntryActivityClass)
        {
            mEntryActivityClass = activityClass;
        }
    }
    
    public static void exitApp(Activity activity, boolean switch_source)
    {
        Log.d(TAG, "exitApp(" + activity + ", " + switch_source + ")");
        Intent intent = new Intent(RadioService.ACTION_TYPE_EXIT_APP, null, activity,
                mEntryActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("switch_source", switch_source);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "onBind(" + intent + ")");
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate()");
        mRadioConfigurator = new RadioConfigure(this);
        mRadioController = mRadioConfigurator.getRadioController();
//        mRadioController.setOnCmdCompletedListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId + ")");
        
        if (intent != null)
		{
            String action = intent.getAction();
            if (action.equals(ACTION_TYPE_GET_STATUS))
            {
                Bundle data = intent.getExtras();
                byte cmd = data.getByte("cmd");
                Log.d(TAG, "onStartCommand intent.extras cmd = "+cmd);
//                switch (cmd)
//                {
//                case DvdControl.SOC_CMD_GET_FREQLIST:
//                    break;
//                case DvdControl.SOC_CMD_GET_RADIOINFO:
//                    break;
//                case DvdControl.SOC_CMD_TUNETOFREQ:
//                    break;
//                case DvdControl.SOC_CMD_SEARCHRADIO:
//                    break;
//                }
            }
		}

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
//        mRadioController.setOffCmdCompletedListener(this);
    }

    public void onEvent_ACK(Bundle data)
    {
        Log.d(TAG, "onEvent_ACK(" + data + ")");
    }

    public void onEvent_EXCEPTION(Bundle data)
    {
        Log.d(TAG, "onEvent_EXCEPTION(" + data + ")");
        byte send_id = data.getByte("send_id");
        byte type = data.getByte("exception_type");
        Log.d(TAG, "send_id=" + send_id + ", exception_type=" + type);
    }

    public synchronized boolean onCmdCompleted(int cmd, Bundle data)
    {
        Log.d(TAG, "head onCmdCompleted(" + cmd + ", " + data + ")");

//        switch (cmd)
//        {
//        case DvdControl.MPEG_CMD_ACK:
//            onEvent_ACK(data);
//            return true;
//        case DvdControl.MCU_RET_FREQLIST:
//        	Log.d(TAG, "onCmdCompleted---->MCU_RET_FREQLIST--->recv  data="+data);
//            break;        
//        case DvdControl.MCU_RET_CUR_FREQ:
//        case DvdControl.MCU_RET_CUR_FREQ_EX:
//        	Log.d(TAG, "onCmdCompleted---->MCU_RET_CUR_FREQ--->recv  data="+data);
//            break;
//        case DvdControl.MCU_RET_RADIO_STATUS:
//        	Log.d(TAG, "onCmdCompleted---->MCU_RET_RADIO_STATUS--->recv  data="+data);
//            break;        
//        case DvdControl.MCU_RET_SAVED_FREQ:
//        	Log.d(TAG, "onCmdCompleted---->MCU_RET_SAVED_FREQ--->recv  data="+data);
//            break;        
//        case DvdControl.SETUP_MCU_RET_WORKMODE:
//        	Log.d(TAG, "onCmdCompleted---->SETUP_MCU_RET_WORKMODE--->recv  data="+data);
//            break;
//        }
        Intent intent = new Intent(ACTION_TYPE_RADIO_EVENT);
        intent.putExtras(data);
        sendBroadcast(intent);
        return true;
    }
}
