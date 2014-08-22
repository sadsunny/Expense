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

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class ExportDao {
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
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
				
				if (i == (categorySize-1) || categorySet.size() == 1) {
					categoryCondition = categoryCondition +" c._id = "+ cId ;
				}else {
					categoryCondition = categoryCondition +" c._id = "+ cId + " or ";
				}
				i++;
			}
			
			categoryCondition = categoryCondition +" ) ";
		}
		
		String accountCondition = " ( a.expenseAccount = b._id or a.incomeAccount= b._id ) and ( a.expenseAccount <= 0 or a.incomeAccount <= 0 )";
		if (accountSet.size() != accountSize) {
			accountCondition = accountCondition +" and ( ";
			int j = 0;
			for (int aId:accountSet) {
				
				if (j == (accountSize-1) || accountSet.size() == 1) {
					accountCondition = accountCondition + "( a.expenseAccount = "+aId+" or a.incomeAccount = "+aId+" )";
				}else {
					accountCondition = accountCondition + "( a.expenseAccount = "+aId+" or a.incomeAccount = "+aId+" )"+" or ";
				}
				j++;
			}
			accountCondition = accountCondition +" ) ";
		}
		
		String sql = "select a.dateTime, b.accName, c.categoryName, d.name, a.amount, a.isClear, a.notes from 'Transaction' a, Accounts b, Category c, Payee d where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ endTime+ " and a.parTransaction != -1 and a.payee = d._id and "+categoryCondition+" and "+accountCondition+"order by lower("+orderbyString+")" + AscDese+" , " + orderbyString + AscDese;
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
		    
			mMap.put("dateTime", dateTime);
			mMap.put("accName", accName);
			mMap.put("categoryName", categoryName);
			mMap.put("payeeName", payeeName);
			mMap.put("amount", amount);
			mMap.put("isClear", isClear);
			mMap.put("notes", notes);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

}
