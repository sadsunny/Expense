package com.appxy.pocketexpensepro.passcode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.entity.MyApplication;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.appxy.pocketexpensepro.table.AccountsTable;
import com.appxy.pocketexpensepro.table.AccountsTable.Accounts;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;

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

public abstract class BaseHomeActivity extends FragmentActivity {

	SharedPreferences sharedPreferences;
	// private static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	private String passCode;

	private DbxAccountManager mDbxAcctMgr;
	public DbxDatastore mDatastore;
	private static final String APP_KEY = "6rdffw1lvpr4zuc";
	private static final String APP_SECRET = "gxqx0uiav4744o3";

	Context context;
	HomeKeyEventBroadCastReceiver receiver;

	private DbxDatastore.SyncStatusListener mDatastoreListener = new DbxDatastore.SyncStatusListener() {
		@Override
		public void onDatastoreStatusChange(DbxDatastore ds) {

			if (ds.getSyncStatus().hasIncoming) {
				try {

					Map<String, Set<DbxRecord>> mMap = mDatastore.sync();

					dataHasIncoming(mMap);
					
					mDatastore.sync();
					syncDateChange();

				} catch (DbxException e) {
					handleException(e);
				}
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Log.v("mtag", "Base的 onCreate1");

		List<Map<String, Object>> mList = SettingDao
				.selectSetting(BaseHomeActivity.this);
		passCode = (String) mList.get(0).get("passcode");
		if (passCode != null && passCode.length() > 2) {
			isPasscode = 1;
		} else {
			isPasscode = 0;
		}

		context = this;
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		Log.v("mtag", "Base的 onCreate3");
	}

	public abstract void syncDateChange();

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.v("mtag", " Base onResume");

		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				APP_KEY, APP_SECRET);
		try {

			if (mDbxAcctMgr.hasLinkedAccount()) {
				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}
				Log.v("mtag", "Base的 onCreate2");
				mDatastore.addSyncStatusListener(mDatastoreListener);
			}

		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("mtag", "Base的 onCreate异常");
		}

		if (MyApplication.isHomePress) {
			MyApplication.isHomePress = false;

			List<Map<String, Object>> mList = SettingDao
					.selectSetting(BaseHomeActivity.this);
			passCode = (String) mList.get(0).get("passcode");
			if (passCode != null && passCode.length() > 2) {
				isPasscode = 1;
			} else {
				isPasscode = 0;
			}

			if (isPasscode == 1) {
				Intent intent = new Intent(this, Activity_HomeBack.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 45);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDatastore != null) {
			mDatastore.removeSyncStatusListener(mDatastoreListener);
			mDatastore.close();
			mDatastore = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

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
	
	private void dataHasIncoming(Map<String, Set<DbxRecord>> mMap) throws DbxException{//处理同步数据incoming
		
		if (mMap.containsKey("db_account_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_account_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					AccountsTable accountsTable = new AccountsTable(mDatastore, BaseHomeActivity.this);
					Accounts accounts =accountsTable.getAccounts();
					accounts.setAccountsDownData(iRecord);
					accounts.insertOrUpdate();
				}
			}
			
		}
		
		
		
	}
	
	
	

}
