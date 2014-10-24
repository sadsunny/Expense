package com.appxy.pocketexpensepro.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbDao {
	
	

	public static Map<String, Integer> selectAccountType(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from AccountType";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}

	public static long updateAccountType(SQLiteDatabase db, int _id,
			String state, String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		try {
			long id = db.update("AccountType", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	public static Map<String, Integer> selectCategory(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from Category";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}

	public static long updateCategory(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		try {
			long id = db.update("Category", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	public static Map<String, Integer> selectPayee(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from Payee";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updatePayee(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		try {
			long id = db.update("Payee", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectAccount(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from Accounts";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateAccount(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime_sync", dateTime_sync);

		try {
			long id = db.update("Accounts", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	public static long updateAccountOrder(SQLiteDatabase db, int _id, int orderIndex) {

		ContentValues cv = new ContentValues();

		cv.put("orderIndex", orderIndex);

		try {
			long id = db.update("Accounts", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectBudgetTemplate(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from BudgetTemplate";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateBudgetTemplate(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		cv.put("isRollover", 0);
		cv.put("isNew", 1);
		cv.put("startDate", dateTime_sync);
		cv.put("cycleType", "No Cycle");
		
		try {
			long id = db.update("BudgetTemplate", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectBudgetItem(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from BudgetItem";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateBudgetItem(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2113);
		
		cv.put("startDate", dateTime_sync);
		cv.put("isRollover", 0);
		cv.put("endDate", calendar.getTimeInMillis() );
		cv.put("rolloverAmount", 0);
		
		try {
			long id = db.update("BudgetItem", cv, "_id = ?", new String[] { _id+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectBudgetTransfer(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from BudgetTransfer";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateBudgetTransfer(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime_sync", dateTime_sync);

		try {
			long id = db.update("BudgetTransfer", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectEP_BillRule(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from EP_BillRule";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateEP_BillRule(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		try {
			long id = db.update("EP_BillRule", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectEP_BillItem(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from EP_BillItem";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateEP_BillItem(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		try {
			long id = db.update("EP_BillItem", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	public static Map<String, Integer> selectTransaction(SQLiteDatabase db) {

		Map<String, Integer> mMap = new HashMap<String, Integer>();
		String sql = "select * from 'Transaction' ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
		}
		mCursor.close();
		return mMap;
	}
	
	public static long updateTransaction(SQLiteDatabase db, int _id, String state,
			String uuid, long dateTime_sync) {

		ContentValues cv = new ContentValues();

		cv.put("uuid", uuid);
		cv.put("state", state);
		cv.put("dateTime", dateTime_sync);

		try {
			long id = db.update(" 'Transaction' ", cv, "_id = ?", new String[] { _id
					+ "" });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
	
	
	
	

}
