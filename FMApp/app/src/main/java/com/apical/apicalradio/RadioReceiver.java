package com.apical.apicalradio;


import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
//import android.app.DvdControl;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class RadioReceiver extends BroadcastReceiver
{
    private static final String TAG = "qulinglingRadio RadioReceiver***";
    public static final String ACTION_TYPE_APK_SETUP = "apk_bc_setup";
    public static final String ACTION_TYPE_TSCAL_COMPLETED = "com.apical.tscal.FIRST_TSCAL_COMPLETED";
    public static final String RADIO_HAD_RUN= "radio_had_run";
    public static final String START_TSCAL = "com.apical.tscal.START_TSCAL";
    private RadioController mDvdController;
    
	@SuppressLint("NewApi")
	@Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive(" + context + ", " + intent + ")");
        Log.e(TAG,"RadioReceiver  intent = "+intent);
		mDvdController = new RadioController(context);
        
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
			Log.d(TAG, "QLLRadio BOOT_COMPLETED begin"); 
		
            intent.setClass(context, RadioService.class);
            context.startService(intent);
            
        	Log.d(TAG, "QLLRadio COMPLETE bBootComplete="+AppRunState.IsBootComplete()+" bReceiveXGMode="+AppRunState.GetXGMode());        	 
        	AppRunState.SetBootComplete(true);
    	    Bundle bundle;
    		bundle = new Bundle();
			mDvdController = new RadioController(context);
			mDvdController.GetSysSettings("WorkMode", 1, bundle);
			byte bWorkMode = bundle.getByte("WorkMode");
            if (bWorkMode == RadioController.RADIO_XG_MODE)
            {
            	Log.d(TAG, "QLLRadio startActivity BOOT_COMPLETED");        	 
            	//启动radio
				Intent loadIntent = new Intent(context,RadioMainActivity.class);
				loadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				loadIntent.putExtra("setModeFlag", 1) ;
	            context.startActivity(loadIntent);
            }
        }
        //��ֵ��ӦBrocast
        else if (intent.getAction().equals("Apical_Key_Broadcast")) 
        {
        	Log.d(TAG,"DvdServiceLoader  equals(Apical_Key_Broadcast)  ");
        	
			//����service
            intent.setClass(context, RadioService.class);
            context.startService(intent);
        	
            //��ȡ��Ӧ�������
			Bundle keyBundle = new Bundle();
			keyBundle = intent.getExtras();
			boolean downState = keyBundle.getBoolean("Action");
			int keyCode = keyBundle.getInt("Code");
			
			Log.d(TAG,"DvdServiceLoader  equals(Apical_Key_Broadcast)  keyCode="+keyCode+"  " +
					"  downState="+downState+ +KeyEvent.ACTION_UP);
			
			if (downState == true)//up
			{
				mDvdController = new RadioController(context);
				if (keyCode == KeyEvent.KEYCODE_MEDIA_EJECT/*129*/) 
				{
				}
				else if (keyCode == KeyEvent.KEYCODE_PROG_YELLOW/*164*/)
				{
				}
			}
		}
        //校准成功 touch screen success
        else if (intent.getAction().equals(ACTION_TYPE_TSCAL_COMPLETED))
		{
        	Log.d(TAG, "----------------------->reciver the ACTION_TYPE_TSCAL_COMPLETED!!!!!!!!!!");
			SharedPreferences radioSP = context.getSharedPreferences("ApicalRadio", context.MODE_PRIVATE);
			Editor editor = radioSP.edit();
			editor.putString("First_Start_Up", RADIO_HAD_RUN);
			editor.commit();
			
			String modeString = radioSP.getString("Enter_Radio_Mode", "none");
			if (modeString.equals("RADIO_MODE"))
			{
				if (AppRunState.IsAppRun()==false)
				{
	            	Log.d(TAG, "QLLRadio startActivity ACTION_TYPE_TSCAL_COMPLETED");        	 
					Intent loadIntent = new Intent(context,RadioMainActivity.class);
					loadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                context.startActivity(loadIntent);
				}
			}
		}
        //开始校准 begin to touch screen
        //enter cal in use ,and cal is not success,and restart
        else if (intent.getAction().equals(START_TSCAL))
        {
			SharedPreferences radioSP = context.getSharedPreferences("ApicalRadio", context.MODE_PRIVATE);
			Editor editor = radioSP.edit();
			editor.putString("First_Start_Up", "none");
			editor.commit();
        }
        
        //xgģʽ�л�
        else if (intent.getAction().equals(ACTION_TYPE_APK_SETUP))
		{
			//xgģʽ�л�
            Bundle data = intent.getExtras();
            int cmd = data.getInt("cmd");
            byte mode = data.getByte("WorkMode");
			Log.d(TAG, "QLLRadio----cmd="+cmd+"   mode="+mode);
//            if (cmd == DvdControl.SETUP_MCU_RET_WORKMODE)
//			{
//            	//�������
//				SharedPreferences radioSP = context.getSharedPreferences("ApicalRadio", context.MODE_PRIVATE);
//				Editor editor = radioSP.edit();
//				if (mode != RadioController.RADIO_XG_MODE)
//				{
//	        		Log.d(TAG,"QLLRadio not RADIO_XG_MODE");
//			        Intent exitIntent = new Intent(BaseActivity.RADIO_EXIT);
//			        context.sendBroadcast(exitIntent);
//			        
//			        //�ͷ���Դ
//			        ApicalHardwareCtrl mApicalHawreCtrl = new ApicalHardwareCtrl(ApicalHardwareCtrl.APP_RADIO);
//			        mApicalHawreCtrl.AudioSourceRelease();
//			        
//					editor.putString("Enter_Radio_Mode", "OHTER_MODE");
//					editor.commit();
//				}
//				else if (mode==RadioController.RADIO_XG_MODE)
//				{
//					Log.d(TAG, "QLLRadio------>reciver the xg radio mode!!!!!!!!!!");				
//					
//					//�����һ����󣬵����У׼����������
//					//if machine is  not C,should test cla 
//					mDvdController.GetSysSettings("TouchType", 1, data);			
//					Log.d(TAG, "QLLRadio ----------------------->reciver the xg radio mode----1-----TouchType="+data.getByte("TouchType"));
//					if(data.getByte("TouchType") != 1)  
//					{ 
//						editor.putString("Enter_Radio_Mode", "RADIO_MODE");
//						editor.commit();
//
//						String packageName = "org.zeroxlab.util.tscal";
//						ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
//						List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
//						Log.d(TAG,"MediaCenterReceiver the top app is tasksInfo.size()="+tasksInfo.size());
//						if(tasksInfo.size() > 0)
//						{
//							Log.d(TAG,"MediaCenterReceiver the top app "+tasksInfo.get(0).topActivity.getPackageName());
//							//坐标校准位于堆栈的顶层
//							if(packageName.equals(tasksInfo.get(0).topActivity.getPackageName()))
//							{
//								Log.d(TAG,"MediaCenterReceiver the top app is TS ");
//								return ;
//							}
//						}						
//					}
//					
//		        	Log.d(TAG, "QLLRadio  AppRunState.IsAppRun()="+AppRunState.IsAppRun());        	 
//					
//					if (AppRunState.IsAppRun()==false)
//					{		 
//			        	Log.d(TAG, "QLLRadio  bBootComplete="+AppRunState.IsBootComplete()+" bReceiveXGMode="+AppRunState.GetXGMode());        	 
//						if (AppRunState.IsBootComplete() == true)
//						{
//			            	Log.d(TAG, "QLLRadio startActivity RADIO_XG_MODE "); 
//			           	
//							Intent loadIntent = new Intent(context,RadioMainActivity.class);
//							loadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
//									| Intent.FLAG_ACTIVITY_SINGLE_TOP);
//							loadIntent.putExtra("setModeFlag", 1) ;
//				            context.startActivity(loadIntent);
//						}
//						else
//						{
//							AppRunState.SetXGMode(true);
//						}
//					}
//					else
//					{
//						String packageName = "com.apical.apicalradio";
//						ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
//						List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
//						Log.d(TAG,"MediaCenterReceiver the top app is tasksInfo.size()="+tasksInfo.size());
//						if(tasksInfo.size() > 0)
//						{
//							Log.d(TAG,"MediaCenterReceiver the top app "+tasksInfo.get(0).topActivity.getPackageName());
//							//坐标校准位于堆栈的顶层
//							if(packageName.equals(tasksInfo.get(0).topActivity.getPackageName()))
//							{
//								Log.d(TAG,"MediaCenterReceiver the top app is TS ");
//							}
//							else
//							{
//								Log.d(TAG, "QLLRadio back to foreground=");
//								Activity startRadio = AppRunState.GetActivity();
//								final ActivityManager am = (ActivityManager)
//							                context.getSystemService(Context.ACTIVITY_SERVICE);
//								am.moveTaskToFront(startRadio.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);		
//							}
//						}	
//					}
//				}
//			}
//            else if (cmd == DvdControl.SETUP_MCU_RET_MB)
//	        {
//            	RadioMainActivity.mMBType =  data.getByte("MbType");
//    			Log.d(TAG, "QLLRadio mMBType="+RadioMainActivity.mMBType);
//	        }
		}                              

    }
    
}
