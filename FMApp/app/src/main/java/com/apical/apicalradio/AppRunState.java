package com.apical.apicalradio;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.util.Log;

//�����ж�Ӧ���Ƿ����� 
public class AppRunState
{
	private static final String TAG="qulinglingRadio AppRunState***";
	public static boolean AppRun = false;
	private static boolean bBootComplete = false;
	private static boolean bReceiveXGMode = false;
	private static boolean bHomeBack = false;
	public static Activity activityRadio = null;
	
	static public void SetAppRun()
	{
		AppRun=true;
	}
	
	static public void SetAppStop()
	{
		AppRun=false;
	}
	
	//
	static public boolean IsAppRun()
	{
		return AppRun;
	}
	
	static public void SetBootComplete(boolean bComplete)
	{
		bBootComplete = bComplete;
	}
	
	static public boolean IsBootComplete()
	{
		return bBootComplete;
	}	
	
	static public void SetXGMode(boolean bReceiveXG)
	{
		bReceiveXGMode = bReceiveXG;
	}
	
	static public boolean GetXGMode()
	{
		return bReceiveXGMode;
	}
	
	
	static public boolean GetHomeBack()
	{
		Log.d(TAG, "QLLRadio bHomeBack="+bHomeBack);
		return bHomeBack;
	}	
	
	static public void SetHomeBack(boolean bHome)
	{
		Log.d(TAG, "QLLRadio SetHomeBack bHome="+bHome);
		bHomeBack = bHome;
	}
	
	
	static public RunningTaskInfo getRunningTaskInfo( )
	{
		System.out.println("getRunningServiceInfo()...");
		
        ActivityManager activityManager = null;//(ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);  
		List taskList = activityManager.getRunningTasks(30);		  
		System.out.println("taskList.size()="+taskList.size());
		for(int i=0; i<taskList.size(); i++)
		{
			RunningTaskInfo taskInfo = (RunningTaskInfo) taskList.get(i);
		    String packageName = taskInfo.baseActivity.getPackageName();	   
		    System.out.println("QLLRadio package name: " + packageName);
		    if (packageName.equals("com.apical.apicalradio"))
		    {
			   return taskInfo;
		    }
		}
		return  null;
	}
	
	static public Activity GetActivity()
	{
		return activityRadio;
	}
	
	static public void SetActivity(Activity activityIn)
	{
		activityRadio = activityIn;
	}
	
}

