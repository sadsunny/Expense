package com.appxy.pocketexpensepro.bills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BillsDao {
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}

	public static long insertBillRule(Context context, double ep_billAmount, long ep_billDueDate,long ep_billEndDate, String ep_billName,String ep_note, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory, int billRuleHasPayee) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billAmount", ep_billAmount);
		cv.put("ep_billDueDate", ep_billDueDate);
		cv.put("ep_billEndDate", ep_billEndDate);
		cv.put("ep_billName", ep_billName);
		cv.put("ep_note", ep_note);
		cv.put("ep_recurringType", ep_recurringType);
		cv.put("ep_reminderDate", ep_reminderDate);
		cv.put("ep_reminderTime", ep_reminderTime);
		cv.put("billRuleHasCategory", billRuleHasCategory);
		cv.put("billRuleHasPayee", billRuleHasPayee);
		long row = db.insert("EP_BillRule", null, cv);
		db.close();
		return row;
	}
	
	public static List<Map<String, Object>> selectOrdinaryBillRuleByBE(Context context, long beginTime, long endTime) { // Bill普通事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*,c.* from EP_BillRule a,Category b, Payee c left join Payee on a.billRuleHasPayee = c._id where a.ep_billDueDate >= "+ beginTime+ " and a.ep_billDueDate <="+endTime+ " and ep_recurringType = 0 and a.billRuleHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String ep_billAmount = mCursor.getString(2);
			long ep_billDueDate = mCursor.getLong(3);
			long ep_billEndDate = mCursor.getLong(4);
			String ep_billName = mCursor.getString(5);
			String ep_note = mCursor.getString(10);
			int ep_recurringType = mCursor.getInt(11);
			int ep_reminderDate = mCursor.getInt(12);
			long ep_reminderTime = mCursor.getLong(13);
			int billRuleHasCategory = mCursor.getInt(19);
			int billRuleHasPayee = mCursor.getInt(20);
			String categoryName = mCursor.getString(26);
			int iconName =  mCursor.getInt(31); 
			int payee_id =  mCursor.getInt(47); 
            String payee_nameString = mCursor.getString(50);

			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_id", payee_id);
			mMap.put("payee_nameString", payee_nameString);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	public static List<Map<String, Object>> selectTemplateBillRuleByBE(Context context, long beginTime, long endTime) { // Bill重复母本事件
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from EP_BillRule a, Category b where ep_recurringType > 0 and a.billRuleHasCategory = b._id left join Payee c on a.billRuleHasPayee = c._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String ep_billAmount = mCursor.getString(2);
			long ep_billDueDate = mCursor.getLong(3);
			long ep_billEndDate = mCursor.getLong(4);
			String ep_billName = mCursor.getString(5);
			String ep_note = mCursor.getString(10);
			int ep_recurringType = mCursor.getInt(11);
			int ep_reminderDate = mCursor.getInt(12);
			long ep_reminderTime = mCursor.getLong(13);
			int billRuleHasCategory = mCursor.getInt(19);
			int billRuleHasPayee = mCursor.getInt(20);
			
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}

}
