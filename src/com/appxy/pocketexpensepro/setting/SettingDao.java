package com.appxy.pocketexpensepro.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SettingDao {
	

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static long updateCurrency(Context context, int currency ) { // Account更新排序字段
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("currency", currency);
		String mId = 1 + "";
		try {
			long id = db.update("Setting", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}
	
	public static long updatePasscode(Context context, String passcode ) { // Account更新排序字段
		Log.v("mtest", "password11"+passcode);
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("passcode", passcode);
		String mId = 1 + "";
		try {
			long id = db.update("Setting", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}
	
	public static List<Map<String, Object>> selectSetting(Context context) { // AccountType根据id查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Setting ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			int currency = mCursor.getInt(9);
			String passcode = mCursor.getString(56);
			mMap.put("_id", _id);
			mMap.put("currency", currency);
			mMap.put("passcode", passcode);
			Log.v("mtest", "password22"+passcode);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
}
