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
import android.util.Log;

public class BillsDao {
	
	private final static long DAYMILLIS = 86400000L - 1L;
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static long deleteBillPayById(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("'Transaction'", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static long updateBillPay(Context context,int _id, String amount, long dateTime,int expenseAccount) {  //更新payment
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("expenseAccount", expenseAccount);
		long row = db.insert("'Transaction'", null, cv);
		String mId = _id + "";
		try {
			long id = db.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}
	
	public static List<Map<String, Object>> selectTransactionById(Context context, int id) {  //bill payment记录
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, b.accName, b.iconName  from 'Transaction' a, Accounts b where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
            
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	
	public static long deleteBillPayTransaction(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("'Transaction'", "transactionHasBillRule = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	
	public static long updateBillDateRule(Context context, long _id, long ep_billEndDate) { 
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billEndDate", ep_billEndDate);
		String mId = _id + "";
		try {
			long id = db
					.update("EP_BillRule", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}
	
	public static long updateBillItem(Context context, int _id, int ep_billisDelete ,String ep_billItemAmount, long ep_billItemDueDate,
			long ep_billItemDueDateNew, long ep_billItemEndDate, String ep_billItemName,String ep_billItemNote, int ep_billItemRecurringType, 
			int ep_billItemReminderDate, long ep_billItemReminderTime, int billItemHasBillRule, int billItemHasCategory, int billItemHasPayee) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billItemAmount", ep_billItemAmount);
		cv.put("ep_billisDelete", ep_billisDelete);
		cv.put("ep_billItemDueDate", ep_billItemDueDate);
		cv.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
		cv.put("ep_billItemEndDate", ep_billItemEndDate);
		cv.put("ep_billItemName", ep_billItemName);
		cv.put("ep_billItemNote", ep_billItemNote);
		cv.put("ep_billItemRecurringType", ep_billItemRecurringType);
		cv.put("ep_billItemReminderDate", ep_billItemReminderDate);
		cv.put("ep_billItemReminderTime", ep_billItemReminderTime);
		cv.put("billItemHasBillRule", billItemHasBillRule);
		cv.put("billItemHasCategory", billItemHasCategory);
		cv.put("billItemHasPayee", billItemHasPayee);
		String mId = _id + "";
		long row = db.update("EP_BillItem",cv, "_id = ?", new String[] { mId });
		db.close();
		return row;
	}
	
	public static long updateBillRule(Context context,int _id, double ep_billAmount, long ep_billDueDate,long ep_billEndDate, String ep_billName,String ep_note, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory, int billRuleHasPayee) {
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
		String mId = _id + "";
		long row = db.update("EP_BillRule", cv, "_id = ?", new String[] { mId });
		db.close();
		return row;
	}
	
	
	public static long deleteBill(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("EP_BillRule", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	public static long deleteBillObjectByParId(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("EP_BillItem", "billItemHasBillRule = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static long deleteBillObject(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("EP_BillItem", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	public static long deleteBillObjectByAfterDate(Context context, long thisDate) {
		SQLiteDatabase db = getConnection(context);
		long row = 0;
		try {
			row = db.delete("EP_BillItem", "ep_billItemDueDate >= ?", new String[] { thisDate+"" });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	
	
	public static long insertBillItem(Context context, int ep_billisDelete ,String ep_billItemAmount, long ep_billItemDueDate,
			long ep_billItemDueDateNew, long ep_billItemEndDate, String ep_billItemName,String ep_billItemNote, int ep_billItemRecurringType, 
			int ep_billItemReminderDate, long ep_billItemReminderTime, int billItemHasBillRule, int billItemHasCategory, int billItemHasPayee) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billItemAmount", ep_billItemAmount);
		cv.put("ep_billisDelete", ep_billisDelete);
		cv.put("ep_billItemDueDate", ep_billItemDueDate);
		cv.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
		cv.put("ep_billItemEndDate", ep_billItemEndDate);
		cv.put("ep_billItemName", ep_billItemName);
		cv.put("ep_billItemNote", ep_billItemNote);
		cv.put("ep_billItemRecurringType", ep_billItemRecurringType);
		cv.put("ep_billItemReminderDate", ep_billItemReminderDate);
		cv.put("ep_billItemReminderTime", ep_billItemReminderTime);
		cv.put("billItemHasBillRule", billItemHasBillRule);
		cv.put("billItemHasCategory", billItemHasCategory);
		cv.put("billItemHasPayee", billItemHasPayee);
		long row = db.insert("EP_BillItem", null, cv);
		db.close();
		return row;
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
	
	public static List<Map<String, Object>> selectBillRuleById(Context context, int id) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a._id = "+id+" and a.billRuleHasCategory = b._id ";
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
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			 
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
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 0);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	
	public static List<Map<String, Object>> selectOrdinaryBillRuleById(Context context, int id) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a._id = "+id+" and a.ep_recurringType = 0 and a.billRuleHasCategory = b._id ";
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
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			 
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
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 0);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	
	public static List<Map<String, Object>> selectOrdinaryBillRuleByBE(Context context, long beginTime, long endTime) { // Bill普通事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*,Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a.ep_billDueDate >= "+ beginTime+ " and a.ep_billDueDate <="+(endTime+DAYMILLIS)+ " and a.ep_recurringType = 0 and a.billRuleHasCategory = b._id ";
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
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			
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
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 0);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	public static List<Map<String, Object>> selectTemplateBillRuleByBE(Context context) { // Bill模板事件
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a.ep_recurringType > 0 and a.billRuleHasCategory = b._id ";
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
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			
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
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 1);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	public static List<Map<String, Object>> selectBillItemByBE(Context context, long beginTime, long endTime) { // Bill特例事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillItem a left join Payee on a.billItemHasPayee = Payee._id ,Category b where a.ep_billItemDueDate >= "+ beginTime+ " and a.ep_billItemDueDate <=" + (endTime+DAYMILLIS) + " and a.billItemHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int ep_billisDelete = mCursor.getInt(2);
			String ep_billAmount = mCursor.getString(3);
			long ep_billDueDate = mCursor.getLong(8);
			long ep_billItemDueDateNew = mCursor.getLong(9);
			long ep_billEndDate = mCursor.getLong(10);
			String ep_billName = mCursor.getString(11);
			String ep_note = mCursor.getString(12);
			int ep_recurringType = mCursor.getInt(13);
			int ep_reminderDate = mCursor.getInt(14);
			long ep_reminderTime = mCursor.getLong(15);
			int billItemHasBillRule = mCursor.getInt(20);
			int billRuleHasCategory = mCursor.getInt(21);
			int billRuleHasPayee = mCursor.getInt(22);
			String categoryName = mCursor.getString(27);
			int iconName =  mCursor.getInt(33); 
			String payee_nameString = mCursor.getString(51);
			
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
			mMap.put("payee_nameString", payee_nameString);
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
	
	public static List<Map<String, Object>> selectBillItemByRuleId(Context context, int id) { // Bill特例事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillItem a left join Payee on a.billItemHasPayee = Payee._id ,Category b where a.billItemHasBillRule = "+ id+ " and a.billItemHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int ep_billisDelete = mCursor.getInt(2);
			String ep_billAmount = mCursor.getString(3);
			long ep_billDueDate = mCursor.getLong(8);
			long ep_billItemDueDateNew = mCursor.getLong(9);
			long ep_billEndDate = mCursor.getLong(10);
			String ep_billName = mCursor.getString(11);
			String ep_note = mCursor.getString(12);
			int ep_recurringType = mCursor.getInt(13);
			int ep_reminderDate = mCursor.getInt(14);
			long ep_reminderTime = mCursor.getLong(15);
			int billItemHasBillRule = mCursor.getInt(20);
			int billRuleHasCategory = mCursor.getInt(21);
			int billRuleHasPayee = mCursor.getInt(22);
			String categoryName = mCursor.getString(27);
			int iconName =  mCursor.getInt(33); 
			String payee_nameString = mCursor.getString(51);
			
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
			mMap.put("payee_nameString", payee_nameString);
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
	
	
	public static List<Map<String, Object>> selectTransactionByBillRuleId(Context context, int id) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, b.accName, b.iconName  from 'Transaction' a, Accounts b where a.transactionHasBillRule = "+ id +" and a.expenseAccount = b._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
            String accName = mCursor.getString(4);
            int iconName = mCursor.getInt(5);
            
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("accName", accName);
			mMap.put("iconName", iconName);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectTransactionByBillItemId(Context context, int id) { 
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, b.accName, b.iconName  from 'Transaction' a, Accounts b where a.transactionHasBillItem = "+ id +" and a.expenseAccount = b._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
            String accName = mCursor.getString(4);
            int iconName = mCursor.getInt(5);
            
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("accName", accName);
			mMap.put("iconName", iconName);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	

	public static long insertTransactionRule(Context context, String amount, long dateTime,int expenseAccount, int transactionHasBillRule, int category, int payee,int cleared, int recurring) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("expenseAccount", expenseAccount);
		cv.put("incomeAccount", 0);
		cv.put("transactionHasBillRule", transactionHasBillRule);
		cv.put("category", category);
		cv.put("payee", payee);
		cv.put("isClear", cleared);
		cv.put("recurringType", 0);
		cv.put("parTransaction", 0);
		cv.put("childTransactions", 0);
		
		long row = db.insert("'Transaction'", null, cv);
		db.close();
		return row;
	}
	
	public static long insertTransactionItem(Context context, String amount, long dateTime,int expenseAccount, int transactionHasBillItem, int category,int payee,int cleared, int recurring) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("expenseAccount", expenseAccount);
		cv.put("transactionHasBillItem", transactionHasBillItem);
		cv.put("category", category);
		cv.put("payee", payee);
		cv.put("isClear", cleared);
		cv.put("recurringType", 0);
		cv.put("parTransaction", 0);
		cv.put("childTransactions", 0);
		long row = db.insert("'Transaction'", null, cv);
		db.close();
		return row;
	}
	
	
}
