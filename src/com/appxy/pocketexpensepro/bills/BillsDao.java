package com.appxy.pocketexpensepro.bills;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BillsDao {
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}

	public static long insertBillRule(Context context, double ep_billAmount, long ep_billDueDate,long ep_billEndDate, String ep_billName, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory, int billRuleHasPayee) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billAmount", ep_billAmount);
		cv.put("ep_billDueDate", ep_billDueDate);
		cv.put("ep_billEndDate", ep_billEndDate);
		cv.put("ep_billName", ep_billName);
		cv.put("ep_recurringType", ep_recurringType);
		cv.put("ep_reminderDate", ep_reminderDate);
		cv.put("ep_reminderTime", ep_reminderTime);
		cv.put("billRuleHasCategory", billRuleHasCategory);
		cv.put("billRuleHasPayee", billRuleHasPayee);
		long row = db.insert("EP_BillRule", null, cv);
		db.close();
		return row;
	}
	public static List<Map<String, Object>> selectBillByBE(long beginTime, long endTime) {
		
		return null;
	}

}
