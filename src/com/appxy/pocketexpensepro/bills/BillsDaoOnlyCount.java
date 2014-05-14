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

public class BillsDaoOnlyCount {
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}

	public static List<Map<String, Object>> selectOrdinaryBillRuleByBE(Context context, long beginTime, long endTime) { // Bill普通事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from EP_BillRule a where a.ep_billDueDate >= "+ beginTime+ " and a.ep_billDueDate <="+endTime+ " and a.ep_recurringType = 0 ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	public static List<Map<String, Object>> selectTemplateBillRuleByBE(Context context) { // Bill模板事件
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id , a.ep_billDueDate , a.ep_billEndDate,  a.ep_recurringType from EP_BillRule a where a.ep_recurringType > 0";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			long ep_billDueDate = mCursor.getLong(1);
			long ep_billEndDate = mCursor.getLong(2);
			int ep_recurringType = mCursor.getInt(3);
			
			mMap.put("_id", _id);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_recurringType", ep_recurringType);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	public static List<Map<String, Object>> selectBillItemByBE(Context context, long beginTime, long endTime) { // Bill特例事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.ep_billisDelete, a.ep_billItemDueDate, a.ep_billItemDueDateNew, a.ep_billItemEndDate, a.billItemHasBillRule  from EP_BillItem a where a.ep_billItemDueDate >= "+ beginTime+ " and a.ep_billItemDueDate <= "+endTime;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int ep_billisDelete = mCursor.getInt(0);
			long ep_billDueDate = mCursor.getLong(1);
			long ep_billItemDueDateNew = mCursor.getLong(2);
			long ep_billEndDate = mCursor.getLong(3);
			int billItemHasBillRule = mCursor.getInt(4);
			
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billisDelete", ep_billisDelete);
			mMap.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
			mMap.put("billItemHasBillRule", billItemHasBillRule);
			mMap.put("indexflag", 3);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
}
