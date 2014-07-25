package com.appxy.pocketexpensepro.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class TransactionRecurringDao {
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}

	public static List<Map<String, Object>> selectTransactionRecurring(Context context) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Transaction a where a.recurringType > 0";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int isClear = mCursor.getInt(5);
			String notes = mCursor.getString(6);
			String photoName = mCursor.getString(9);
			int recurringType = mCursor.getInt(10);
			int category = mCursor.getInt(18);
			int expenseAccount =mCursor.getInt(20);
			int payee = mCursor.getInt(23);
			
			int iconName = mCursor.getInt(2);
			String typeName = mCursor.getString(7);
			mMap.put("_id", _id);
			mMap.put("iconName", iconName);
			mMap.put("typeName", typeName);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

}
