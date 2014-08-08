package com.appxy.pocketexpensepro.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.bills.BillDetailsActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AlamrReceiver extends BroadcastReceiver {
	NotificationManager mNM;
	private Map<String, Object> mMap;
	private Map<String, Object> tMap = new HashMap<String, Object>();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mMap = (Map<String, Object>) intent.getSerializableExtra("dataMap");

		if (mMap != null) {
			
		String bk_accountName = (String) mMap.get("ep_billName");
		long bk_billDuedate = (Long) mMap.get("ep_billDueDate");
		String billamount = (String) mMap.get("ep_billAmount");
		int bk_categoryIconName = (Integer) mMap.get("iconName");

		showNotification(context, bk_accountName, bk_billDuedate, billamount, bk_categoryIconName, mMap);
		}
	}

	public String getMilltoDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	private void showNotification(Context context, String aName, long dueDate,
			String amount, int cIcon, Map<String, Object> tMap) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = aName;
		String subTitleString = "Due on " + getMilltoDate(dueDate);
		String currencyString = "";
		currencyString = "   " + Common.CURRENCY_SIGN[Common.CURRENCY]+ MEntity.doubl2str(amount+"");

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setContentTitle(text);
		builder.setContentText(subTitleString + currencyString);
		builder.setSmallIcon(Common.CATEGORY_ICON[cIcon]);
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
//		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
//				Common.CATEGORY_ICON[cIcon]);
//		builder.setLargeIcon(bm);
		builder.setAutoCancel(true);

		Intent intent = new Intent(context, BillDetailsActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(BillDetailsActivity.class);
		intent.putExtra("dataMap", (Serializable) tMap);
		// intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		// intent.setAction(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);

		stackBuilder.addNextIntent(intent);
		PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		// intent, 0);
		builder.setContentIntent(contentIntent);

		// nm.notify("direct_tag", 1, builder.build());

		// Intent intent = new Intent(context, Activity_Start.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		// intent.setAction(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		// PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		// intent, 0);

		// Set the info for the views that show in the notification panel.
		// notification.setLatestEventInfo(context, text+currencyString
		// ,subTitleString, contentIntent);
		//
		// notification.flags =Notification.FLAG_AUTO_CANCEL;

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		int uniqueRequestCode = (int) System.currentTimeMillis();
		mNM.notify(uniqueRequestCode, builder.build());

	}
}
