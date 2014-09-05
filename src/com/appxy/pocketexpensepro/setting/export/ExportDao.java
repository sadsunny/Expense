package com.appxy.pocketexpensepro.setting.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class ExportDao {
	
	private final static long DAYMILLIS = 86400000L - 1L;
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static List<Map<String, Object>> selectTransactionByTimeBE(Context context, long beginTime, long endTime, int type) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String conditionString = "";
		
		if (type == 0) {
			conditionString = "(a.expenseAccount <= 0 and a.incomeAccount > 0)";
		} else {
			conditionString = "(a.expenseAccount > 0 and a.incomeAccount <= 0)";
		}
				
		String sql = "select a.* from 'Transaction' a, Category b where a.category = b._id and a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and a.parTransaction != -1 and  "+conditionString+" order by lower(b.categoryName) ASC, b.categoryName ASC";
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
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	
	public static String selectCategoryName(Context context, int id) { 
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.categoryName from Category a where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			String categoryName = mCursor.getString(1);
			mMap.put("categoryName", categoryName);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		if (mList != null && mList.size() > 0) {
			return (String) mList.get(0).get("categoryName");
		} else {
			return " ";
		}
		
	}
	
	public static String selectAccountName(Context context, int id) { 
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.accName from Accounts a where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			String accName = mCursor.getString(1);
			mMap.put("accName", accName);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		if (mList != null && mList.size() > 0) {
			return (String) mList.get(0).get("accName");
		} else {
			return " ";
		}
		
	}
	
	
	public static List<Map<String, Object>> selectTransferByTimeBE(Context context, long beginTime, long endTime, HashSet<Integer> accountSet, int accountSize) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		
		
		String accountCondition = "( a.expenseAccount > 0 and a.incomeAccount > 0) and ( a.expenseAccount = b._id or a.incomeAccount= b._id ) ";
		if (accountSet.size() != accountSize) {
			accountCondition = accountCondition +" and ( ";
			int j = 0;
			for (int aId:accountSet) {
				
				if (j == 0) {
					accountCondition = accountCondition + "( a.expenseAccount = "+aId+" or a.incomeAccount = "+aId+" )";
				} else {
					accountCondition = accountCondition +"or" + "( a.expenseAccount = "+aId+" or a.incomeAccount = "+aId+" )";
				}
				
				j++;
			}
			accountCondition = accountCondition +" ) ";
		}
		
		String sql = "select a.dateTime, d.name, a.amount, a.expenseAccount, a.incomeAccount, a.notes, a._id from 'Transaction' a, Accounts b, Payee d where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.payee = d._id and "+accountCondition+" group by a._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			long dateTime = mCursor.getLong(0);
			String payeeName = mCursor.getString(1);
			String amount = mCursor.getString(2);
		    int expenseAccount = mCursor.getInt(3);
		    int incomeAccount = mCursor.getInt(4);
		    String notes =  mCursor.getString(5);
		    
			mMap.put("dateTime", dateTime);
			mMap.put("payeeName", payeeName);
			mMap.put("amount", amount);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("notes", notes);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
	
	public static List<Map<String, Object>> selectTransactionByTimeBE(Context context, long beginTime, long endTime, int seqType, int groupType, HashSet<Integer> categorySet,int categorySize, HashSet<Integer> accountSet, int accountSize) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String AscDese = "";
		if (seqType == 0) {
			AscDese = " ASC";
		}else {
			AscDese = " DESC";
		}
		
		String orderbyString = "";
		if (groupType == 0) {
			orderbyString = " c.categoryName";	
		} else {
			orderbyString = " b.accName";
		}
		
		String categoryCondition = " a.category = c._id ";
		if (categorySet.size() != categorySize ) {
			
			categoryCondition = categoryCondition + " and ( ";
			int i = 0;
			for (int cId:categorySet) {
				
				
				if ( i == 0 ) {
					categoryCondition = (categoryCondition +" c._id = "+ cId );
				} else {
					categoryCondition = ( categoryCondition + " or " +" c._id = "+ cId );
				}
				i++;
			}
			
			categoryCondition = categoryCondition +" ) ";
		}
		
		String accountCondition = " ( a.expenseAccount = b._id or a.incomeAccount= b._id ) ";
		if (accountSet.size() != accountSize) {
			accountCondition = accountCondition +" and ( ";
			int j = 0;
			for (int aId:accountSet) {
				
				if (j == 0) {
					accountCondition = accountCondition + "( a.expenseAccount = "+aId+" or a.incomeAccount = "+aId+" )";
				} else {
					accountCondition = accountCondition +"or" + "( a.expenseAccount = "+aId+" or a.incomeAccount = "+aId+" )";
				}
				
				j++;
			}
			accountCondition = accountCondition +" ) ";
		}
		
		String sql = "select a.dateTime, b.accName, c.categoryName, d.name, a.amount, a.isClear, a.notes, a.expenseAccount, a.incomeAccount, a.category from 'Transaction' a, Accounts b, Category c, Payee d where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.parTransaction != -1 and a.payee = d._id and "+categoryCondition+" and "+accountCondition+" order by lower("+orderbyString+")" + AscDese+" , " + orderbyString + AscDese;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			long dateTime = mCursor.getLong(0);
			String accName = mCursor.getString(1);
			String categoryName = mCursor.getString(2);
			String payeeName = mCursor.getString(3);
			String amount = mCursor.getString(4);
			int isClear = mCursor.getInt(5);
		    String notes =  mCursor.getString(6);
		    int expenseAccount = mCursor.getInt(7);
		    int incomeAccount = mCursor.getInt(8);
		    int category = mCursor.getInt(9);
		    
			mMap.put("dateTime", dateTime);
			mMap.put("accName", accName);
			mMap.put("categoryName", categoryName);
			mMap.put("payeeName", payeeName);
			mMap.put("amount", amount);
			mMap.put("isClear", isClear);
			mMap.put("notes", notes);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("category", category);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
	
	
}
