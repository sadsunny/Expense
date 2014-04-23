package com.appxy.pocketexpensepro.overview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class OverViewDao {
	
	private final static long DAYMILLIS = 86400000L;
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static List<Map<String, Object>> selectTransactionByTime(Context context, long time) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a.dateTime >= "+time+" and a.dateTime <= "+time+DAYMILLIS+" and a.parTransaction != -1 order by a.dateTime DESC , a._id DESC ";
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
			String childTransactions = mCursor.getString(19);
			int expenseAccount = mCursor.getInt(20);
			int incomeAccount = mCursor.getInt(21);
			int parTransaction = mCursor.getInt(22);
			int payee = mCursor.getInt(23);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}

}
