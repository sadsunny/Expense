package com.appxy.pocketexpensepro.passcode;

import com.crashlytics.android.Crashlytics;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.EncodingUtils;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.TransactionRecurringCheck;
import com.appxy.pocketexpensepro.accounts.AccountActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.entity.MyApplication;
import com.appxy.pocketexpensepro.service.NotificationService;
import com.appxy.pocketexpensepro.service.PastDueService;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.appxy.pocketexpensepro.setting.category.CategoryDao;
import com.appxy.pocketexpensepro.setting.sync.SyncActivity;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity_Start extends BaseHomeActivity {

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

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				
				
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
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		long sa = System.currentTimeMillis();
		
		Crashlytics.start(this);
		
		long end = System.currentTimeMillis();
		Log.v("mtag", "奔溃监听时间"+(end - sa ));
		setContentView(R.layout.activity_start);
		
		
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
																		// Recurring
			try {
				
				if (mDatastore != null) {
					mDatastore.sync();
				}

			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();

		}
	};

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

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub

	}

}
