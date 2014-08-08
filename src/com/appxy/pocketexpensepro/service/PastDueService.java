package com.appxy.pocketexpensepro.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.bills.BillsDao;
import com.appxy.pocketexpensepro.bills.RecurringEventBE;
import com.appxy.pocketexpensepro.passcode.Activity_Start;

import android.R.integer;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
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
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class PastDueService extends Service {

	NotificationManager mNM;
	int uid = 0;
	private final static long day = 24 * 3600 * 1000L;
	private AlarmManager am;
	private PendingIntent pi;
	int uniqueCode = 0;
	public ArrayList<String> accountArray;
	
	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		accountArray = new ArrayList<String>();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		SharedPreferences notificationData = PastDueService.this
				.getSharedPreferences("PastDueAmount", 0);
		int nAmount = notificationData.getInt("PastAmount", 0);
		
		for (int i = 0; i < nAmount; i++) {
			
			Intent mIntent = new Intent(PastDueService.this,PastDueReceiver.class);
			mIntent.setData(Uri.parse("custom://" + 1));
			mIntent.setAction(String.valueOf(1));
			
			AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			PendingIntent mpi = PendingIntent.getBroadcast(PastDueService.this, uniqueCode, mIntent,PendingIntent.FLAG_CANCEL_CURRENT);
			mAlarmManager.cancel(mpi);
			
		}
		
		Thread thr = new Thread(null, mTask, "PastDueService_Service");
		thr.start();
		return START_REDELIVER_INTENT;
	}

	Runnable mTask = new Runnable() {
		public void run() {

			// show the icon in the status bar
			List<Map<String, Object>> mDataList = RecurringEventBE.recurringData(PastDueService.this, 0, getNowexactMillis() );
			judgePayment(mDataList);
			
			for (Map<String, Object> iMap : mDataList) {
				
				String bk_accountName = (String) iMap.get("ep_billName");
				int payState = (Integer) iMap.get("payState");

				if (payState != 2 ) {
					accountArray.add(bk_accountName);
				}
				
			}
			if (accountArray.size() > 0) {

				Intent intent = new Intent(PastDueService.this,PastDueReceiver.class);
				intent.putExtra("dataList", (Serializable)accountArray);
				intent.setData(Uri.parse("custom://" + 1));
				intent.setAction(String.valueOf(1));
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				pi = PendingIntent.getBroadcast(PastDueService.this, uniqueCode, intent,PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pi);
				
				SharedPreferences notificationData = PastDueService.this
						.getSharedPreferences("PastDueAmount", 0);
				SharedPreferences.Editor localEditor = notificationData.edit();
				localEditor.putInt("PastAmount", 1);
				localEditor.commit();
				
			}else {
				
				SharedPreferences notificationData = PastDueService.this
						.getSharedPreferences("PastDueAmount", 0);
				SharedPreferences.Editor localEditor = notificationData.edit();
				localEditor.putInt("PastAmount",0);
				localEditor.commit();
				
			}
			
			PastDueService.this.stopSelf();
		}
	};

	public String getMilltoDate(long milliSeconds) { 
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public long getNowexactMillis() { // 获得现在的精确时间

		Calendar c = Calendar.getInstance();
		long nowMillis = c.getTimeInMillis(); // 获取当天年月日对应的毫秒数，出去时分秒，便于整除
		return nowMillis;
	}

	/**
	 * This is the object that receives interactions from clients. See
	 * RemoteService for a more complete example.
	 */
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	public long reminderDate(long dueDate, long before, long remindAt) {
		long reminder = dueDate - before * day + remindAt;
		return reminder;
	}

	public long getNowMillis() {
		 Date date1=new Date(); 
	     SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
	     String nowTime = formatter.format(date1);
	     Calendar c = Calendar.getInstance();
	     try {
				c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     long nowMillis = c.getTimeInMillis();
	     
	     return nowMillis;
	}

	public void judgePayment(List<Map<String, Object>> dataList) {

		for (Map<String, Object> iMap : dataList) {

			int _id = (Integer) iMap.get("_id");
			String ep_billAmount = (String) iMap.get("ep_billAmount");
			int indexflag = (Integer) iMap.get("indexflag");
			BigDecimal b0 = new BigDecimal(ep_billAmount);
			
			if (indexflag == 0 || indexflag == 1) {

				List<Map<String, Object>> pDataList = BillsDao.selectTransactionByBillRuleId(PastDueService.this, _id);
				
				BigDecimal b1 = new BigDecimal(0.0); // pay的总额
				for (Map<String, Object> pMap : pDataList) {
					String pAmount = (String) pMap.get("amount");
					BigDecimal b2 = new BigDecimal(pAmount);
					b1 = b1.add(b2);
				}
				double remain = (b1.subtract(b0)).doubleValue();
				double payTotal = b1.doubleValue();
				if (payTotal > 0) {

					if (remain >= 0) {
						iMap.put("payState", 2);// 全部支付
					} else {
						iMap.put("payState", 1);// 部分支付
					}

				} else {
					iMap.put("payState", 0);// 完全未支付
				}

			} else if (indexflag == 2) {

				iMap.put("payState", 0);

			}
			if (indexflag == 3) {

				List<Map<String, Object>> pDataList = BillsDao
						.selectTransactionByBillItemId(PastDueService.this, _id);
				BigDecimal b1 = new BigDecimal(0.0); // pay的总额
				for (Map<String, Object> pMap : pDataList) {
					String pAmount = (String) pMap.get("amount");
					BigDecimal b2 = new BigDecimal(pAmount);
					b1 = b1.add(b2);
				}
				double remain = (b1.subtract(b0)).doubleValue();
				double payTotal = b1.doubleValue();
				if (payTotal > 0) {

					if (remain >= 0) {
						iMap.put("payState", 2);
					} else {
						iMap.put("payState", 1);
					}

				} else {
					iMap.put("payState", 0);
				}
			}

		}

	}
	

}
