package com.appxy.pocketexpensepro.setting.sync;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.table.AccountsTable;
import com.appxy.pocketexpensepro.table.AccountsTable.Accounts;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SyncActivity extends BaseHomeActivity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private static final String APP_KEY = "lyil3ng9zuj5eht";
	private static final String APP_SECRET = "sxxce9lm9kgvyhg";
	private static final int REQUEST_LINK_TO_DBX = 0;

	private DbxAccountManager mDbxAcctMgr;
	private DbxDatastore mDatastore;
	private DbxDatastoreManager mLocalManager;
	private DbxDatastoreManager mDatastoreManager;
	private DbxAccount mDbxAcct;

	private Switch syncSwitch;
	private TextView accountNameTextView;
	private SharedPreferences mPreferences;
	private boolean isSync;
	private ProgressDialog progressDialog;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				Toast.makeText(SyncActivity.this, "Sync Succ",
						Toast.LENGTH_SHORT).show();

				break;

			case MSG_FAILURE:
				Toast.makeText(SyncActivity.this, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	
	DbxAccount.Listener mAccountListener = new DbxAccount.Listener() {

		@Override
		public void onAccountChange(DbxAccount arg0) {
			// TODO Auto-generated method stub
			if (isSync) {

				String name = mDbxAcct.getAccountInfo().displayName;
				accountNameTextView.setVisibility(View.VISIBLE);
				accountNameTextView.setText(name);

				SharedPreferences.Editor meditor = mPreferences.edit();
				meditor.putString("accountName", name);
				meditor.commit();
			} else {

				accountNameTextView.setVisibility(View.GONE);
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				APP_KEY, APP_SECRET);
		setContentView(R.layout.activity_sync);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mPreferences = getSharedPreferences("ExpenseSync", MODE_PRIVATE);

		isSync = mPreferences.getBoolean("isSync", false);

		accountNameTextView = (TextView) findViewById(R.id.account_name);
		syncSwitch = (Switch) findViewById(R.id.sync_switch);
		syncSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {

					if (!mDbxAcctMgr.hasLinkedAccount()) { 
						mDbxAcctMgr.startLink(SyncActivity.this,
								REQUEST_LINK_TO_DBX);

					}
						
				} else {

					accountNameTextView.setVisibility(View.INVISIBLE);
					isSync = false;
					SharedPreferences.Editor meditor = mPreferences.edit();
					meditor.putBoolean("isSync", isSync);
					meditor.commit();
					if (mDbxAcctMgr != null) {
						mDbxAcctMgr.unlink();
					}

				}

			}
		});

	}
	
	public void upLoadAllDate() throws DbxException { //上传所有数据
		
		List<Map<String, Object>> mList = SyncDao.selectAccount(SyncActivity.this);
		
		for (Map<String, Object> iMap:mList) {
			AccountsTable accountsTable = new AccountsTable(mDatastore);
			Accounts accounts = accountsTable.getAccounts();
			accounts.setAccountsData(iMap);
			accountsTable.insertRecords(accounts.getFields());
		}
	
		mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		
	}
	
	public void justSync(boolean justLinked) { // 同步并上传数据库
		
		Log.v("mtag", "是否链接"+mDbxAcctMgr.hasLinkedAccount());
		
		if (isSync) {
			if (mDbxAcctMgr.hasLinkedAccount()) {

				try {
					
					if (null == mDbxAcct) {
						 mDbxAcct=mDbxAcctMgr.getLinkedAccount();
						 Log.v("mtag", "mDbxAcctMgr重新链接么"+mDbxAcctMgr);
			         }
					
					if (null == mDatastore) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
						 Log.v("mtag", "mDatastore 重新链接么"+mDatastore);
					}

					progressDialog = ProgressDialog.show(SyncActivity.this, null, "Syncing....");
				 
					Thread mThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								upLoadAllDate();
							} catch (DbxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					mThread.start();
					
					
					 mDbxAcct.addListener(mAccountListener);

					 String accName = mPreferences.getString("accountName", "");
					 accountNameTextView.setVisibility(View.VISIBLE);
					 accountNameTextView.setText(accName);
					 
				} catch (DbxException e) {
					handleException(e);
				}

				syncSwitch.setChecked(true);
			}
		} else {
           accountNameTextView.setVisibility(View.INVISIBLE);
           syncSwitch.setChecked(false);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		justSync(false) ;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mDbxAcct != null) {
			mDbxAcct.removeListener(mAccountListener);
			mDbxAcct = null;
			Log.v("mtag", "onPause");
	    }

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == RESULT_OK) {
				isSync = true;

				if (mDbxAcctMgr != null && mDbxAcctMgr.hasLinkedAccount()) {

					SharedPreferences.Editor meditor = mPreferences.edit();
					meditor.putBoolean("isSync", isSync);
					meditor.commit();
				}
				
			} else {
				// ... Link failed or was cancelled by the user.
				Toast.makeText(this, "Link to Dropbox failed.",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void handleException(DbxException e) {
		e.printStackTrace();
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}

}
