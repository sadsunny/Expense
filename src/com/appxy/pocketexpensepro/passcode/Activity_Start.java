package com.appxy.pocketexpensepro.passcode;


import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.TransactionRecurringCheck;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.entity.MyApplication;
import com.appxy.pocketexpensepro.service.NotificationService;
import com.appxy.pocketexpensepro.service.PastDueService;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.appxy.pocketexpensepro.setting.category.CategoryDao;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity_Start extends Activity {
	SharedPreferences sharedPreferences;
	public static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	
	private PendingIntent mAlarmSender;
	private PendingIntent pAlarmSender;
	private static final long days31 = 31*24*3600*1000L;
	private static final long days = 24*3600*1000L;
	private static final long nineHours = 9*3600*1000L;
	
	private AlarmManager am;
	private AlarmManager pm;
	
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;
	private LayoutInflater inflater;
	private String passCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		 
		 if (PendingIntent.getService(Activity_Start.this, 0, new Intent(Activity_Start.this, NotificationService.class), PendingIntent.FLAG_NO_CREATE) !=null) {
			 Log.v("mtest", "判定NotificationService");
	 		} else {
	 			 mAlarmSender = PendingIntent.getService(Activity_Start.this, 0, new Intent(Activity_Start.this, NotificationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	 	         long firstTime = SystemClock.elapsedRealtime();
	 	         //   Schedule the alarm!
	 	         am = (AlarmManager)getSystemService(ALARM_SERVICE);
	 	         am.set(AlarmManager.RTC_WAKEUP,firstTime, mAlarmSender);
	 	         am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days, mAlarmSender);
	 		}
	         
	         if (PendingIntent.getService(Activity_Start.this, 1, new Intent(Activity_Start.this, PastDueService.class), PendingIntent.FLAG_NO_CREATE) !=null) {
	        	 Log.v("mtest", "判定PastDueService");
	  		} else {
	  			 pAlarmSender = PendingIntent.getService(Activity_Start.this, 1, new Intent(Activity_Start.this, PastDueService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	  	         long firstTime = SystemClock.elapsedRealtime();
	  	         //   Schedule the alarm!
	  	         pm = (AlarmManager)getSystemService(ALARM_SERVICE);
	  	         pm.set(AlarmManager.RTC_WAKEUP,firstTime, pAlarmSender);
	  	         pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days, pAlarmSender);
	  		}
	         
	         try {
	        	 List<Map<String, Object>> mCategoryList = CategoryDao.selectCategoryById(Activity_Start.this, 1); // 查询一下初始的category还有account,以便触发升级
	 	         List<Map<String, Object>> mAccountList = AccountDao.selectAccountById(Activity_Start.this, 1);
			} catch (Exception e) {
				// TODO: handle exception
			}
	       
	         
	         
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
						
					List<Map<String, Object>> mList = SettingDao.selectSetting(Activity_Start.this);
			 		passCode = (String) mList.get(0).get("passcode");
			 		
			 		if (passCode != null && passCode.length() > 2) {
			 			isPasscode = 1;
			 		} else {
			 			isPasscode = 0;
			 		}
			 		
			     	 
			         if (isPasscode == 1) {
			 			Intent intent = new Intent(Activity_Start.this, Activity_Login.class);
			 			startActivity(intent);
			 			Activity_Start.this.finish();
			 		} else {
			 			Intent intent = new Intent(Activity_Start.this, MainActivity.class);
			 			startActivity(intent);
			 			Activity_Start.this.finish();
			 		}
			         
				         
				}
			}.start();
			
	 		} else {
	 			
	 			MyApplication.isFirstIn = 1;
	 			
	 			List<Map<String, Object>> mList = SettingDao.selectSetting(Activity_Start.this);
		 		passCode = (String) mList.get(0).get("passcode");
		 		
		 		if (passCode != null && passCode.length() > 2) {
		 			isPasscode = 1;
		 		} else {
		 			isPasscode = 0;
		 		}
		 		
		     	 
		         if (isPasscode == 1) {
		 			Intent intent = new Intent(Activity_Start.this, Activity_Login.class);
		 			startActivity(intent);
		 			Activity_Start.this.finish();
		 		} else {
		 			Intent intent = new Intent(Activity_Start.this, MainActivity.class);
		 			startActivity(intent);
		 			Activity_Start.this.finish();
		 		}
	 			

			}
		 
        
//         if (PendingIntent.getService(Activity_Start.this, 0, new Intent(Activity_Start.this, BillNotificationService.class), PendingIntent.FLAG_NO_CREATE) !=null) {
// 		} else {
// 			 mAlarmSender = PendingIntent.getService(Activity_Start.this, 0, new Intent(Activity_Start.this, BillNotificationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
// 	         long firstTime = SystemClock.elapsedRealtime();
// 	         //   Schedule the alarm!
// 	         am = (AlarmManager)getSystemService(ALARM_SERVICE);//锟叫讹拷锟斤拷锟斤拷执锟斤拷一锟斤拷
// 	         am.set(AlarmManager.RTC_WAKEUP,firstTime, mAlarmSender);
// 	         am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days, mAlarmSender);
// 		}
//         
//         if (PendingIntent.getService(Activity_Start.this, 1, new Intent(Activity_Start.this, BillPastDueService.class), PendingIntent.FLAG_NO_CREATE) !=null) {
//  		} else {
//  			 pAlarmSender = PendingIntent.getService(Activity_Start.this, 1, new Intent(Activity_Start.this, BillPastDueService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//  	         long firstTime = SystemClock.elapsedRealtime();
//  	         //   Schedule the alarm!
//  	         pm = (AlarmManager)getSystemService(ALARM_SERVICE);//锟叫讹拷锟斤拷锟斤拷执锟斤拷一锟斤拷
//  	         pm.set(AlarmManager.RTC_WAKEUP,firstTime, pAlarmSender);
//  	         pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days, pAlarmSender);
//  		}
		
	}
	
	
     public String getMilltoDate(long milliSeconds) {//锟斤拷锟斤拷锟斤拷转锟斤拷锟缴固讹拷锟斤拷式锟斤拷锟斤拷锟斤拷锟斤拷
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			return formatter.format(calendar.getTime());
		}
	  
	  public long  getNineTime() { //每锟斤拷诺憧�始
			 Date date1=new Date(); 
			 SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		     String nowTime = formatter.format(date1);
		     Calendar c = Calendar.getInstance();
		     c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
		     try {
					c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     long nowMillis = c.getTimeInMillis()+nineHours; //锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷锟秸讹拷应锟侥猴拷锟斤拷锟斤拷锟饺ナ憋拷锟斤拷耄�锟斤拷锟斤拷锟斤拷锟�
		     return nowMillis;
}
	
	public long  getZeroTime() {
		 Date date1=new Date(); 
		 SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
	     String nowTime = formatter.format(date1);
	     Calendar c = Calendar.getInstance();
	     c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
	     try {
				c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     long nowMillis = c.getTimeInMillis()+days; //锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷锟秸讹拷应锟侥猴拷锟斤拷锟斤拷锟饺ナ憋拷锟斤拷耄�锟斤拷锟斤拷锟斤拷锟�
	     return nowMillis;
	}
	
}
