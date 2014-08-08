package com.appxy.pocketexpensepro.setting.payee;

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

public class PayeeDao {
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static List<Map<String, Object>> selectCategory(Context context,
			int type) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Category where Category.categoryType = "
				+ type + " order by lower(categoryName), categoryName ASC ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String categoryName = mCursor.getString(3);
			int categoryType = mCursor.getInt(5);
			int hasBudget = mCursor.getInt(8);
			int iconName = mCursor.getInt(9);
			int isDefault = mCursor.getInt(10);

			mMap.put("_id", _id);
			mMap.put("categoryName", categoryName);
			mMap.put("categoryType", categoryType);
			mMap.put("hasBudget", hasBudget);
			mMap.put("iconName", iconName);
			mMap.put("isDefault", isDefault);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static long insertPayee(Context context, String name,String memo, int category) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("memo", memo);
		cv.put("category", category);
		long row = db.insert("Payee", null, cv);
		db.close();
		return row;
	}
	
	public static long updatePayee(Context context, int id, String name,String memo, int category) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("memo", memo);
		cv.put("category", category);
		long row = db.update("Payee", cv, "_id = ?", new String[] { _id });
		db.close();
		return row;
	}
	
	
	public static List<Map<String, Object>> selectPayee(Context context) { 
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.categoryName, b.iconName from Payee a,Category b where a.category = b._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String name = mCursor.getString(3);
			String categoryName = mCursor.getString(19);
			int iconName = mCursor.getInt(20);

			mMap.put("_id", _id);
			mMap.put("name", name);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static long deletePayee(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("Payee", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static List<Map<String, Object>> selectPayeeById(Context context,int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.categoryName, b.iconName, b.categoryType, b._id from Payee a,Category b where a.category = b._id and a._id = "+id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String memo = mCursor.getString(2);
			String name = mCursor.getString(3);
			String categoryName = mCursor.getString(19);
			int iconName = mCursor.getInt(20);
			int categoryType = mCursor.getInt(21);
			int categoryId = mCursor.getInt(22);
			
			mMap.put("_id", _id);
			mMap.put("name", name);
			mMap.put("memo", memo);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("categoryType", categoryType);
			mMap.put("categoryId", categoryId);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectPayeeRelate(Context context,int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, b._id from Payee a,'Transaction' b where a._id = b.payee and a._id = "+id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
}
