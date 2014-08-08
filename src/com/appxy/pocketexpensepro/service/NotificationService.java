package com.appxy.pocketexpensepro.service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.bills.RecurringEventBE;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.R.integer;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {

	NotificationManager mNM;
	int uid = 0;
	private final static long days3 = 3 * 24 * 3600 * 1000L;
	private final static long day = 24 * 3600 * 1000L;
	private AlarmManager am;
	private PendingIntent pi;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		SharedPreferences notificationData = NotificationService.this
				.getSharedPreferences("NotificationAmount", 0);
		int nAmount = notificationData.getInt("NotiAmount", 0);
		for (int i = 0; i < nAmount; i++) {

			Intent mintent = new Intent(NotificationService.this,
					AlamrReceiver.class);
			mintent.setData(Uri.parse("custom://" + i));
			mintent.setAction(String.valueOf(i));
			AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			PendingIntent mpi = PendingIntent.getBroadcast(
					NotificationService.this, i, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			mAlarmManager.cancel(mpi);
		}

		Thread thr = new Thread(null, mTask, "AlarmService_Service");
		thr.start();

		return START_REDELIVER_INTENT;
	}

	Runnable mTask = new Runnable() {
		public void run() {
			long nowMills = MEntity.getNowMillis();
			int uniqueCode = 0;

			List<Map<String, Object>> mDataList = RecurringEventBEHasReminder
					.recurringData(NotificationService.this, nowMills, nowMills
							+ days3);

			if (mDataList.size() > 0) {

				for (Map<String, Object> iMap : mDataList) {
					long bk_billDuedate = (Long) iMap.get("ep_billDueDate");
					int bk_billReminderDate = (Integer) iMap
							.get("ep_reminderDate");
					long bk_billReminderTime = (Long) iMap
							.get("ep_reminderTime");

					long reminderTime = reminderDate(bk_billDuedate,
							bk_billReminderDate, bk_billReminderTime);

					if (bk_billReminderDate > 0
							&& reminderTime > System.currentTimeMillis()) {

						Intent intent = new Intent(NotificationService.this,
								AlamrReceiver.class);
						intent.putExtra("dataMap", (Serializable) iMap);
						intent.setData(Uri.parse("custom://" + uniqueCode));
						intent.setAction(String.valueOf(uniqueCode));

						am = (AlarmManager) getSystemService(ALARM_SERVICE);
						pi = PendingIntent.getBroadcast(
								NotificationService.this, uniqueCode, intent,
								PendingIntent.FLAG_CANCEL_CURRENT);
						am.set(AlarmManager.RTC_WAKEUP, reminderTime, pi);
						uniqueCode = uniqueCode + 1;

					}
				}

				SharedPreferences notificationData = NotificationService.this
						.getSharedPreferences("NotificationAmount", 0);
				SharedPreferences.Editor localEditor = notificationData.edit();
				localEditor.putInt("NotiAmount", uniqueCode);
				localEditor.commit();

			} else {
				SharedPreferences notificationData = NotificationService.this
						.getSharedPreferences("NotificationAmount", 0);
				SharedPreferences.Editor localEditor = notificationData.edit();
				localEditor.putInt("NotiAmount", 0);
				localEditor.commit();
			}

			NotificationService.this.stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	public long reminderDate(long dueDate, long remindDate, long remindAt) {
		int before = 0;
		if (remindDate == 1) {
			before = 1;
		} else if (remindDate == 2) {
			before = 2;
		} else if (remindDate == 3) {
			before = 3;
		} else if (remindDate == 4) {
			before = 7;
		} else if (remindDate == 5) {
			before = 14;
		} else if (remindDate == 6) {
			before = 0;
		}
		long reminder = dueDate - before * day + remindAt;
		return reminder;
	}

}
