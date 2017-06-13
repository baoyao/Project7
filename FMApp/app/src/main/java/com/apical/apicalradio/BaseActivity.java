package com.apical.apicalradio;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 * 所有Activity继承该类，该类监测到home键的点击时的事件
 * 作为基类，用来处理所有程序退出 by owl
 */
public class BaseActivity extends Activity 
{
	private static final String TAG="qulinglingRadio BaseActivity";
	//注册标记
	static final String SYSTEM_REASON = "reason";
	static final String SYSTEM_HOME_KEY = "homekey";// home key
	static final String SYSTEM_RECENT_APPS = "recentapps";// long home key
	static final String RADIO_EXIT = "Apical.radio.exit";
    static final String RADIO_TO_FRONT = "com.csr.radio.TO_FRONT";

	// 监听 全局信息
	ActivityBroadCastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// 监听home键广播
		receiver = new ActivityBroadCastReceiver();
		IntentFilter activityFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		activityFilter.addAction("com.apk.home.event");
		registerReceiver(receiver, activityFilter);

		AppRunState.SetActivity(this);		
	}

	@Override
	protected void onDestroy() 
	{
		unregisterReceiver(receiver);
		AppRunState.SetActivity(null);		
		super.onDestroy();
	}

	class ActivityBroadCastReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) 
			{
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) 
					{
						Log.e("RadioReceiver", "SYSTEM_HOME_KEY");
						
					} 
					else if (reason.equals(SYSTEM_RECENT_APPS)) 
					{
						// long homekey处理点
						// Log.e("homekey", "home long press");
						// Toast.makeText(BaseActivity.this, "home long press", Toast.LENGTH_SHORT).show();
					}
				}
			}
			else if(action.equals("com.apk.home.event"))
			{
    			AppRunState.SetHomeBack(true);	
    			finish();
			}
		}
	}
}
