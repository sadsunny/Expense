package com.appxy.pocketexpensepro.passcode;

import com.crashlytics.android.Crashlytics;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxDatastoreManager.ListListener;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.flurry.android.FlurryAgent;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.EncodingUtils;


import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.MyApplication;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.TransactionRecurringCheck;
import com.appxy.pocketexpensepro.accounts.AccountActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.service.NotificationService;
import com.appxy.pocketexpensepro.service.PastDueService;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.appxy.pocketexpensepro.setting.category.CategoryDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.appxy.pocketexpensepro.table.AccountTypeTable;
import com.appxy.pocketexpensepro.table.AccountsTable;
import com.appxy.pocketexpensepro.table.BudgetItemTable;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable;
import com.appxy.pocketexpensepro.table.BudgetTransferTable;
import com.appxy.pocketexpensepro.table.CategoryTable;
import com.appxy.pocketexpensepro.table.EP_BillItemTable;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable;
import com.appxy.pocketexpensepro.table.PayeeTable;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.AccountTypeTable.AccountType;
import com.appxy.pocketexpensepro.table.AccountsTable.Accounts;
import com.appxy.pocketexpensepro.table.BudgetItemTable.BudgetItem;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable.BudgetTemplate;
import com.appxy.pocketexpensepro.table.BudgetTransferTable.BudgetTransfer;
import com.appxy.pocketexpensepro.table.CategoryTable.Category;
import com.appxy.pocketexpensepro.table.EP_BillItemTable.EP_BillItem;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable.EP_BillRule;
import com.appxy.pocketexpensepro.table.PayeeTable.Payee;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity_Start extends FragmentActivity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	SharedPreferences sharedPreferences;
	public static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;

	private PendingIntent mAlarmSender;
	private PendingIntent pAlarmSender;
	private static final long days31 = 31 * 24 * 3600 * 1000L;
	private static final long days = 24 * 3600 * 1000L;
	private static final long nineHours = 9 * 3600 * 1000L;

	private AlarmManager am;
	private AlarmManager pm;

	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;
	private LayoutInflater inflater;
	private String passCode;
	
	private DbxAccount mDbxAcct;
	public DbxAccountManager mDbxAcctMgr;
	public DbxDatastore mDatastore;
	private static final String APP_KEY = "6rdffw1lvpr4zuc";
	private static final String APP_SECRET = "gxqx0uiav4744o3";
	private HashMap<String, Boolean> mUuidHashMap = new HashMap<String, Boolean>();
	
	private SharedPreferences mSyncPreferences;
	private boolean isSyncSuccessed = false;
	
	private Map<String, Set<DbxRecord>> mAllMap = new HashMap<String, Set<DbxRecord>>();
	private ProgressDialog progressDialog;
	private boolean isNeedStartDown = false;
	
	private DbxAccount.Listener mAccountListener = new DbxAccount.Listener() {

		@Override
		public void onAccountChange(DbxAccount arg0) {
			// TODO Auto-generated method stub
			Log.e("mtag", "arg0.isLinked()"+arg0.isLinked());
		}

	};
	
	
	private DbxDatastore.SyncStatusListener mDatastoreListener = new DbxDatastore.SyncStatusListener() {
		@Override
		public void onDatastoreStatusChange(DbxDatastore ds) {

			Log.e("mtag","isDownloading"+ds.getSyncStatus().isDownloading);
			Log.e("mtag","hasIncoming"+ds.getSyncStatus().hasIncoming);
			
			if (ds.getSyncStatus().hasIncoming) {
				
			}
			

		}
	};
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				
				if (isPasscode == 1) {
					Intent intent = new Intent(Activity_Start.this,
							Activity_Login.class);
					startActivity(intent);
					Activity_Start.this.finish();
				} else {
					Intent intent = new Intent(Activity_Start.this,
							MainActivity.class);
					startActivity(intent);
					Activity_Start.this.finish();
				}

				break;

			case MSG_FAILURE:
				Toast.makeText(Activity_Start.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		loadIsPaid();
		
		if ( Common.mIsPaid) {
			FlurryAgent.onStartSession(this, "33T77JQRQZ46VDGSTHN2");
		} else {
			FlurryAgent.onStartSession(this, "T7WHZHCBWS3FWWPWXSBN");
		}
		
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		long sa = System.currentTimeMillis();
		
		Crashlytics.start(this);
		
		long end = System.currentTimeMillis();
		setContentView(R.layout.activity_start);
		
		mSyncPreferences= getSharedPreferences("IsSyncSuccess", MODE_PRIVATE);
		isSyncSuccessed = mSyncPreferences.getBoolean("isSyncSuccess", true);;
		
		Log.e("mtag", "isSyncSuccessed "+isSyncSuccessed);
		
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				APP_KEY, APP_SECRET);
		
		try {

			Log.e("mtag", "1");
			
			if (mDbxAcctMgr.hasLinkedAccount()) {
				
				if (null == mDbxAcct) {
					mDbxAcct = mDbxAcctMgr.getLinkedAccount();
				}
				
				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}
				
				Log.e("mtag", "2");
				mDbxAcct.addListener(mAccountListener);
				mDatastore.addSyncStatusListener(mDatastoreListener);
				
				if (!isSyncSuccessed) {
					
					Log.e("mtag", "3");
					if(MEntity.isNetworkAvailable(this)){
						
						progressDialog = ProgressDialog.show(this, null,
								"The latest syncing process was terminated unexpectedly. It might take a little while to download data from server. Please wait for a moment...");
						FlurryAgent.logEvent("01_SYNC_RE");
						Log.e("mtag", "开始");
						
						Thread mThread = new Thread(new Runnable() {

							@Override
							public void run() {
								
								// TODO Auto-generated method stub
								boolean isFinishDown = true;
								while (mDatastore.getSyncStatus().isDownloading ) {
									if (!MEntity.isNetworkAvailable(Activity_Start.this)) {
										isFinishDown = false;
										mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
										
										break;
									}
								}
								
								if (isFinishDown) {
									
								try {
									
									getAllDropDatas();
									upLoadAllDate();
									
									SharedPreferences.Editor syncEditor = mSyncPreferences.edit();
									syncEditor.putBoolean("isSyncSuccess", true);
									syncEditor.commit();
									
									Thread.sleep(500);
									doNextWithOut();
								} catch (DbxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
								
								isNeedStartDown = true;
							}
						});
						mThread.start();
						
						
						
					}else {
						doNext();
					}
				
				}else {
					doNext();
				}
				
				
			}else {
				doNext();
			}

		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			doNext();
		}
		

	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			if (PendingIntent.getService(Activity_Start.this, 0, new Intent(
					Activity_Start.this, NotificationService.class),
					PendingIntent.FLAG_NO_CREATE) != null) {
			} else {
				mAlarmSender = PendingIntent.getService(Activity_Start.this, 0,
						new Intent(Activity_Start.this, NotificationService.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
				long firstTime = SystemClock.elapsedRealtime();
				// Schedule the alarm!
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
				am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days,
						mAlarmSender);
			}

			if (PendingIntent.getService(Activity_Start.this, 1, new Intent(
					Activity_Start.this, PastDueService.class),
					PendingIntent.FLAG_NO_CREATE) != null) {
			} else {
				pAlarmSender = PendingIntent.getService(Activity_Start.this, 1,
						new Intent(Activity_Start.this, PastDueService.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
				long firstTime = SystemClock.elapsedRealtime();
				// Schedule the alarm!
				pm = (AlarmManager) getSystemService(ALARM_SERVICE);
				pm.set(AlarmManager.RTC_WAKEUP, firstTime, pAlarmSender);
				pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days,
						pAlarmSender);
			}

			try {
				List<Map<String, Object>> mCategoryList = CategoryDao
						.selectCategoryById(Activity_Start.this, 1);
				List<Map<String, Object>> mAccountList = AccountDao
						.selectAccountById(Activity_Start.this, 1);
			} catch (Exception e) {
				// TODO: handle exception
			}

			List<Map<String, Object>> mList = SettingDao
					.selectSetting(Activity_Start.this);
			passCode = (String) mList.get(0).get("passcode");

			if (passCode != null && passCode.length() > 2) {
				isPasscode = 1;
			} else {
				isPasscode = 0;
			}
			
			TransactionRecurringCheck.recurringCheck(Activity_Start.this,
					MEntity.getNowMillis(), mDbxAcctMgr, mDatastore); // Check
																		// transaction
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	public void doNext() {
		
		if (MyApplication.isFirstIn == 0) {

			new CountDownTimer(900, 100) {

				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub

					MyApplication.isFirstIn = 1;
					Thread mThread = new Thread(mTask);
					mThread.start();

				}
			}.start();

		} else {

			MyApplication.isFirstIn = 1;

			Thread mThread = new Thread(mTask);
			mThread.start();
			

		}

	}
	
	public void doNextWithOut() {
		
		if (MyApplication.isFirstIn == 0) {


					MyApplication.isFirstIn = 1;
					if (PendingIntent.getService(Activity_Start.this, 0, new Intent(
							Activity_Start.this, NotificationService.class),
							PendingIntent.FLAG_NO_CREATE) != null) {
					} else {
						mAlarmSender = PendingIntent.getService(Activity_Start.this, 0,
								new Intent(Activity_Start.this, NotificationService.class),
								PendingIntent.FLAG_UPDATE_CURRENT);
						long firstTime = SystemClock.elapsedRealtime();
						// Schedule the alarm!
						am = (AlarmManager) getSystemService(ALARM_SERVICE);
						am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
						am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days,
								mAlarmSender);
					}

					if (PendingIntent.getService(Activity_Start.this, 1, new Intent(
							Activity_Start.this, PastDueService.class),
							PendingIntent.FLAG_NO_CREATE) != null) {
					} else {
						pAlarmSender = PendingIntent.getService(Activity_Start.this, 1,
								new Intent(Activity_Start.this, PastDueService.class),
								PendingIntent.FLAG_UPDATE_CURRENT);
						long firstTime = SystemClock.elapsedRealtime();
						// Schedule the alarm!
						pm = (AlarmManager) getSystemService(ALARM_SERVICE);
						pm.set(AlarmManager.RTC_WAKEUP, firstTime, pAlarmSender);
						pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days,
								pAlarmSender);
					}

					try {
						List<Map<String, Object>> mCategoryList = CategoryDao
								.selectCategoryById(Activity_Start.this, 1);
						List<Map<String, Object>> mAccountList = AccountDao
								.selectAccountById(Activity_Start.this, 1);
					} catch (Exception e) {
						// TODO: handle exception
					}

					List<Map<String, Object>> mList = SettingDao
							.selectSetting(Activity_Start.this);
					passCode = (String) mList.get(0).get("passcode");

					if (passCode != null && passCode.length() > 2) {
						isPasscode = 1;
					} else {
						isPasscode = 0;
					}
					
					TransactionRecurringCheck.recurringCheck(Activity_Start.this,
							MEntity.getNowMillis(), mDbxAcctMgr, mDatastore); // Check
																				// transaction
					
					mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();

		

		} else {

			MyApplication.isFirstIn = 1;

			if (PendingIntent.getService(Activity_Start.this, 0, new Intent(
					Activity_Start.this, NotificationService.class),
					PendingIntent.FLAG_NO_CREATE) != null) {
			} else {
				mAlarmSender = PendingIntent.getService(Activity_Start.this, 0,
						new Intent(Activity_Start.this, NotificationService.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
				long firstTime = SystemClock.elapsedRealtime();
				// Schedule the alarm!
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
				am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days,
						mAlarmSender);
			}

			if (PendingIntent.getService(Activity_Start.this, 1, new Intent(
					Activity_Start.this, PastDueService.class),
					PendingIntent.FLAG_NO_CREATE) != null) {
			} else {
				pAlarmSender = PendingIntent.getService(Activity_Start.this, 1,
						new Intent(Activity_Start.this, PastDueService.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
				long firstTime = SystemClock.elapsedRealtime();
				// Schedule the alarm!
				pm = (AlarmManager) getSystemService(ALARM_SERVICE);
				pm.set(AlarmManager.RTC_WAKEUP, firstTime, pAlarmSender);
				pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days,
						pAlarmSender);
			}

			try {
				List<Map<String, Object>> mCategoryList = CategoryDao
						.selectCategoryById(Activity_Start.this, 1);
				List<Map<String, Object>> mAccountList = AccountDao
						.selectAccountById(Activity_Start.this, 1);
			} catch (Exception e) {
				// TODO: handle exception
			}

			List<Map<String, Object>> mList = SettingDao
					.selectSetting(Activity_Start.this);
			passCode = (String) mList.get(0).get("passcode");

			if (passCode != null && passCode.length() > 2) {
				isPasscode = 1;
			} else {
				isPasscode = 0;
			}
			
			TransactionRecurringCheck.recurringCheck(Activity_Start.this,
					MEntity.getNowMillis(), mDbxAcctMgr, mDatastore); // Check
																		// transaction
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
			

		}

	}

	public String getMilltoDate(long milliSeconds) {// ????????????????????????
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public long getNineTime() { // ???????
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis() + nineHours; // ??????????????????????????????????????
		return nowMillis;
	}

	public long getZeroTime() {
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis() + days; // ??????????????????????????????????????
		return nowMillis;
	}

	private void handleException(DbxException e) {
		e.printStackTrace();
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}
	
	
	  void loadIsPaid() { //查询是否支付
      	
		    SharedPreferences sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);  
	        Common.mIsPaid = sharedPreferences.getBoolean("isPaid", false);
	}
	  
	private void getAllDropDatas() throws DbxException{
		DbxTable mTable;
		DbxTable.QueryResult results;
		mTable = mDatastore.getTable("db_category_table");
		results = mTable.query();
		
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				CategoryTable categoryTable = new CategoryTable(mDatastore,
						this);
				Category category = categoryTable.getCategory();
				category.setIncomingData(iRecord);
				category.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_accounttype_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				AccountTypeTable accountTypeTable = new AccountTypeTable(
						mDatastore, this);
				AccountType accountType = accountTypeTable.getAccountType();
				accountType.setIncomingData(iRecord);
				accountType.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_payee_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				PayeeTable payeeTable = new PayeeTable(mDatastore, this);
				Payee payee = payeeTable.getPayee();
				payee.setIncomingData(iRecord);
				payee.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_account_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				AccountsTable accountsTable = new AccountsTable(mDatastore,
						this);
				Accounts accounts = accountsTable.getAccounts();
				accounts.setIncomingData(iRecord);
				accounts.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_budgettemplate_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(
						mDatastore, this);
				BudgetTemplate budgetTemplate = budgetTemplateTable
						.getBudgetTemplate();
				budgetTemplate.setIncomingData(iRecord);
				budgetTemplate.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_budgetitem_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				BudgetItemTable budgetItemTable = new BudgetItemTable(
						mDatastore, this);
				BudgetItem budgetItem = budgetItemTable.getBudgetItem();
				budgetItem.setIncomingData(iRecord);
				budgetItem.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_budgettransfer_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				BudgetTransferTable budgetTransferTable = new BudgetTransferTable(
						mDatastore, this);
				BudgetTransfer budgetTransfer = budgetTransferTable
						.getBudgetTransfer();
				budgetTransfer.setIncomingData(iRecord);
				budgetTransfer.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_ep_billrule_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(
						mDatastore, this);
				EP_BillRule eP_BillRule = ep_BillRuleTable.getEP_BillRule();
				eP_BillRule.setIncomingData(iRecord);
				eP_BillRule.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_ep_billitem_table");
		results = mTable.query();
		for (DbxRecord iRecord : results.asList()) {
			if (!iRecord.isDeleted()) {

				EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(
						mDatastore, this);
				EP_BillItem eP_BillItem = ep_BillItemTable.getEP_BillItem();
				eP_BillItem.setIncomingData(iRecord);
				eP_BillItem.insertOrUpdate();

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}

			}
		}
		
		mTable = mDatastore.getTable("db_transaction_table");
		results = mTable.query();
		List<DbxRecord> tempList = new ArrayList<DbxRecord>();
		for (DbxRecord iRecord : results.asList()) {

			if (iRecord.hasField("trans_partransaction")) {

				tempList.add(iRecord);

			} else {

				if (!iRecord.isDeleted()) {

					TransactionTable transactionTable = new TransactionTable(
							mDatastore, this);
					Transaction transaction = transactionTable
							.getTransaction();
					transaction.setIncomingData(iRecord);
					transaction.insertOrUpdate();

					if (iRecord.hasField("uuid")) {
						String uuid = iRecord.getString("uuid");
						mUuidHashMap.put(uuid, true);
					}

				}

			}

		}
		
		if (tempList != null && tempList.size() > 0) {

			for (DbxRecord iRecord : tempList) {

				TransactionTable transactionTable = new TransactionTable(
						mDatastore, this);
				Transaction transaction = transactionTable.getTransaction();
				transaction.setIncomingData(iRecord);
				transaction.insertOrUpdate();

				if (iRecord.hasField("trans_partransaction")) {
					TransactionDao.updateParTransactionByUUID(this,
							iRecord.getString("trans_partransaction"));
				}

				if (iRecord.hasField("uuid")) {
					String uuid = iRecord.getString("uuid");
					mUuidHashMap.put(uuid, true);
				}
			}

		}
		
		
		
	}
	  
	  private void dataHasIncoming(Map<String, Set<DbxRecord>> mMap)
				throws DbxException {// 处理同步数据incoming

			long inb = System.currentTimeMillis();

			Log.d("mtag", "Data size" + mMap.size());

			if (mMap.containsKey("db_category_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_category_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						CategoryTable categoryTable = new CategoryTable(mDatastore,
								this);
						Category category = categoryTable.getCategory();
						category.setIncomingData(iRecord);
						category.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}
			}

			Log.d("mtag", "db_category_table");

			if (mMap.containsKey("db_accounttype_table")) {
				Set<DbxRecord> incomeDate = mMap.get("db_accounttype_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						AccountTypeTable accountTypeTable = new AccountTypeTable(
								mDatastore, this);
						AccountType accountType = accountTypeTable.getAccountType();
						accountType.setIncomingData(iRecord);
						accountType.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}
			}

			Log.d("mtag", "db_accounttype_table");

			if (mMap.containsKey("db_payee_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_payee_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						PayeeTable payeeTable = new PayeeTable(mDatastore, this);
						Payee payee = payeeTable.getPayee();
						payee.setIncomingData(iRecord);
						payee.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}
			}

			Log.d("mtag", "db_payee_table");

			if (mMap.containsKey("db_account_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_account_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						AccountsTable accountsTable = new AccountsTable(mDatastore,
								this);
						Accounts accounts = accountsTable.getAccounts();
						accounts.setIncomingData(iRecord);
						accounts.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}

			}

			Log.d("mtag", "db_account_table");

			if (mMap.containsKey("db_budgettemplate_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_budgettemplate_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(
								mDatastore, this);
						BudgetTemplate budgetTemplate = budgetTemplateTable
								.getBudgetTemplate();
						budgetTemplate.setIncomingData(iRecord);
						budgetTemplate.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}

			}

			Log.d("mtag", "db_budgettemplate_table");

			if (mMap.containsKey("db_budgetitem_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_budgetitem_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						BudgetItemTable budgetItemTable = new BudgetItemTable(
								mDatastore, this);
						BudgetItem budgetItem = budgetItemTable.getBudgetItem();
						budgetItem.setIncomingData(iRecord);
						budgetItem.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}

			}

			Log.d("mtag", "db_budgetitem_table");

			if (mMap.containsKey("db_budgettransfer_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_budgettransfer_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						BudgetTransferTable budgetTransferTable = new BudgetTransferTable(
								mDatastore, this);
						BudgetTransfer budgetTransfer = budgetTransferTable
								.getBudgetTransfer();
						budgetTransfer.setIncomingData(iRecord);
						budgetTransfer.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}

			}

			Log.d("mtag", "db_budgettransfer_table");

			if (mMap.containsKey("db_ep_billrule_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_ep_billrule_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(
								mDatastore, this);
						EP_BillRule eP_BillRule = ep_BillRuleTable.getEP_BillRule();
						eP_BillRule.setIncomingData(iRecord);
						eP_BillRule.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}

			}

			Log.d("mtag", "db_ep_billrule_table");

			if (mMap.containsKey("db_ep_billitem_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_ep_billitem_table");
				for (DbxRecord iRecord : incomeDate) {
					if (!iRecord.isDeleted()) {

						EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(
								mDatastore, this);
						EP_BillItem eP_BillItem = ep_BillItemTable.getEP_BillItem();
						eP_BillItem.setIncomingData(iRecord);
						eP_BillItem.insertOrUpdate();

						if (iRecord.hasField("uuid")) {
							String uuid = iRecord.getString("uuid");
							mUuidHashMap.put(uuid, true);
						}

					}
				}

			}

			Log.d("mtag", "db_ep_billitem_table");

			List<DbxRecord> tempList = new ArrayList<DbxRecord>();

			if (mMap.containsKey("db_transaction_table")) {

				Set<DbxRecord> incomeDate = mMap.get("db_transaction_table");
				for (DbxRecord iRecord : incomeDate) {

					if (iRecord.hasField("trans_partransaction")) {

						tempList.add(iRecord);

					} else {

						if (!iRecord.isDeleted()) {

							TransactionTable transactionTable = new TransactionTable(
									mDatastore, this);
							Transaction transaction = transactionTable
									.getTransaction();
							transaction.setIncomingData(iRecord);
							transaction.insertOrUpdate();

							if (iRecord.hasField("uuid")) {
								String uuid = iRecord.getString("uuid");
								mUuidHashMap.put(uuid, true);
							}

						}

					}

				}

			}

			if (tempList != null && tempList.size() > 0) {

				for (DbxRecord iRecord : tempList) {

					TransactionTable transactionTable = new TransactionTable(
							mDatastore, this);
					Transaction transaction = transactionTable.getTransaction();
					transaction.setIncomingData(iRecord);
					transaction.insertOrUpdate();

					if (iRecord.hasField("trans_partransaction")) {
						TransactionDao.updateParTransactionByUUID(this,
								iRecord.getString("trans_partransaction"));
					}

					if (iRecord.hasField("uuid")) {
						String uuid = iRecord.getString("uuid");
						mUuidHashMap.put(uuid, true);
					}
				}

			}

			Log.d("mtag", "db_transaction_table");

			long ine = System.currentTimeMillis();
			Log.d("mtag", "income 时间" + (ine - inb));
			Log.d("mtag", "mUuidHashMap 大小 " + mUuidHashMap.size());

		}
	  
	  
	  public void upLoadAllDate() throws DbxException { // 上传所有数据

		  Log.d("mtag", "CategoryList wotm都还没上次呢00000");
			long upb = System.currentTimeMillis();

			int pageSize = 0;

			Log.d("mtag", "CategoryList wotm都还没上次呢");
			List<Map<String, Object>> CategoryList = SyncDao
					.selectCategory(Activity_Start.this);
			for (Map<String, Object> iMap : CategoryList) {

				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");

					if (!mUuidHashMap.containsKey(uuid)) {

						CategoryTable categoryTable = new CategoryTable(mDatastore,
								this);
						Category category = categoryTable.getCategory();
						category.setCategoryData(iMap);
						categoryTable.insertRecords(category.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
							Log.d("mtag", "CategoryList sync");
						}

					}

				}

			}

			Log.d("mtag", "CategoryList");

			List<Map<String, Object>> AccountTypeList = SyncDao
					.selectAccountType(Activity_Start.this);
			for (Map<String, Object> iMap : AccountTypeList) {

				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {

						AccountTypeTable accountTypeTable = new AccountTypeTable(
								mDatastore, this);
						AccountType accountType = accountTypeTable.getAccountType();
						accountType.setAccountTypeData(iMap);
						accountTypeTable.insertRecords(accountType.getFields());
						pageSize++;

						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}

					}
				}

			}

			Log.d("mtag", "AccountTypeList");

			List<Map<String, Object>> AccountsList = SyncDao
					.selectAccount(Activity_Start.this);
			
			 for (Map<String, Object> iMap : AccountsList) {

				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						AccountsTable accountsTable = new AccountsTable(mDatastore, this);
						Accounts accounts = accountsTable.getAccounts();
						accounts.setAccountsData(iMap);
						accountsTable.insertRecords(accounts.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}


					}
				}
				
				
			}

			Log.d("mtag", "AccountsList");

			List<Map<String, Object>> PayeeList = SyncDao
					.selectPayee(Activity_Start.this);
			for (Map<String, Object> iMap : PayeeList) {
				
				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						PayeeTable payeeTable = new PayeeTable(mDatastore, this);
						Payee payee = payeeTable.getPayee();
						payee.setPayeeData(iMap);
						payeeTable.insertRecords(payee.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
						
					}
				}
				

			}

			Log.d("mtag", "PayeeList");

			List<Map<String, Object>> BudgetTemplateList = SyncDao
					.selectBudgetTemplate(Activity_Start.this);
			for (Map<String, Object> iMap : BudgetTemplateList) {
				
				
				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(
								mDatastore, this);
						BudgetTemplate budgetTemplate = budgetTemplateTable
								.getBudgetTemplate();
						budgetTemplate.setBudgetTemplateData(iMap);
						budgetTemplateTable.insertRecords(budgetTemplate.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
						
					}
				}

			}

			Log.d("mtag", "BudgetTemplateList");

			List<Map<String, Object>> BudgetItemList = SyncDao
					.selectBudgetItem(Activity_Start.this);
			for (Map<String, Object> iMap : BudgetItemList) {
				
				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore,
								this);
						BudgetItem budgetItem = budgetItemTable.getBudgetItem();
						budgetItem.setBudgetItemData(iMap);
						budgetItemTable.insertRecords(budgetItem.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
					}
				}
				

			}

			Log.d("mtag", "BudgetItemList");

			List<Map<String, Object>> BudgetTransferList = SyncDao
					.selectBudgetTransfer(Activity_Start.this);
			for (Map<String, Object> iMap : BudgetTransferList) {
				
				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						BudgetTransferTable budgetTransferTable = new BudgetTransferTable(
								mDatastore, this);
						BudgetTransfer budgetTransfer = budgetTransferTable
								.getBudgetTransfer();
						budgetTransfer.setBudgetTransferData(iMap);
						budgetTransferTable.insertRecords(budgetTransfer.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
						
					}
				}
				

			}

			Log.d("mtag", "BudgetTransferList");

			List<Map<String, Object>> EP_BillRuleList = SyncDao
					.selectEP_BillRule(Activity_Start.this);
			for (Map<String, Object> iMap : EP_BillRuleList) {
				
				
				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(
								mDatastore, this);
						EP_BillRule ep_BillRule = ep_BillRuleTable.getEP_BillRule();
						ep_BillRule.setEP_BillRuleData(iMap);
						ep_BillRuleTable.insertRecords(ep_BillRule.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
						
					}
				}
				

			}

			Log.d("mtag", "EP_BillRuleList");

			List<Map<String, Object>> EP_BillItemList = SyncDao
					.selectEP_BillItem(Activity_Start.this);
			for (Map<String, Object> iMap : EP_BillItemList) {

				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(
								mDatastore, this);
						EP_BillItem ep_BillItem = ep_BillItemTable.getEP_BillItem();
						ep_BillItem.setEP_BillItemData(iMap);
						ep_BillItemTable.insertRecords(ep_BillItem.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
						
					}
				}
			

			}

			Log.d("mtag", "EP_BillItemList");

			List<Map<String, Object>> TransactionList = SyncDao
					.selectTransaction(Activity_Start.this);

			for (Map<String, Object> iMap : TransactionList) {
				
				if (iMap.containsKey("uuid")) {

					String uuid = (String) iMap.get("uuid");
					if (!mUuidHashMap.containsKey(uuid)) {
						
						TransactionTable transactionTable = new TransactionTable(
								mDatastore, this);
						Transaction transaction = transactionTable.getTransaction();
						transaction.setTransactionData(iMap);
						transactionTable.insertRecords(transaction.getFields());

						pageSize++;
						if (pageSize % 500 == 0) {
							mDatastore.sync();
						}
					}
						
				}	
				
			}

			Log.d("mtag", "TransactionList");

			mDatastore.sync();
			Log.e("mtag", "Local pageSize" + pageSize);

			long upe = System.currentTimeMillis();
			Log.d("mtag", "上传时间" + (upe - upb));

		}
	

	  @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (mDatastore != null) {
			mDatastore.removeSyncStatusListener(mDatastoreListener);
			mDatastore.close();
			mDatastore = null;
		}
		if (mDbxAcct != null) {
			mDbxAcct.removeListener(mAccountListener);
			mDbxAcct = null;
		}
	}
}
