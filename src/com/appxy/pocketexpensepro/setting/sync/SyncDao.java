package com.appxy.pocketexpensepro.setting.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SyncDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		return db;
	}

	public static List<Map<String, Object>> selectAccount(Context context) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Accounts a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String name = mCursor.getString(1);
			String state = mCursor.getString(14);
			long datetime = mCursor.getLong(6);
			int autoclear = mCursor.getInt(3);
			double amount = mCursor.getDouble(2);
			String accountType = mCursor.getString(16);
			int orderindex = mCursor.getInt(10);
			long dateTime_sync = mCursor.getLong(7);
			String uuid = mCursor.getString(15);

			mMap.put("name", name);
			mMap.put("state", state);
			mMap.put("datetime", datetime);
			mMap.put("autoclear", autoclear);
			mMap.put("amount", amount);
			mMap.put("accountType", accountType);
			mMap.put("orderindex", orderindex);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("uuid", uuid);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
}
