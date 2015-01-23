package com.appxy.pocketexpensepro.passcode;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.entity.MyApplication;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

public abstract class BaseHomeSyncActivity extends FragmentActivity{
	
	SharedPreferences sharedPreferences;
//	private static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	private String passCode;
	
	Context context;
	HomeKeyEventBroadCastReceiver receiver;
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		List<Map<String, Object>> mList = SettingDao.selectSetting(BaseHomeSyncActivity.this);
	 	passCode = (String) mList.get(0).get("passcode");
		if (passCode != null && passCode.length() > 2) {
 			isPasscode = 1;
 		} else {
 			isPasscode = 0;
 		}
		
		context = this;
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}

	 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		  
		if(MyApplication.isHomePress){
			MyApplication.isHomePress = false;
			
			List<Map<String, Object>> mList = SettingDao.selectSetting(BaseHomeSyncActivity.this);
		 	passCode = (String) mList.get(0).get("passcode");
			if (passCode != null && passCode.length() > 2) {
	 			isPasscode = 1;
	 		} else {
	 			isPasscode = 0;
	 		}
			
			if(isPasscode==1){
				Intent intent = new Intent(this, Activity_HomeBack.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent ,45);
			}
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";//home key
		static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						MyApplication.isHomePress = true;
						
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						MyApplication.isHomePress = true;
					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	 private void handleException(DbxException e) {
	        e.printStackTrace();
	        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	    }
	
}
