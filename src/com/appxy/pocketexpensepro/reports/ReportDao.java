package com.appxy.pocketexpensepro.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class ReportDao{
	
	private final static long DAYMILLIS = 86400000L - 1L;
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	
	public static ArrayList<HashMap<String, Object>> selectTransactionByIDTimeBE(
			Context context, int id ,long beginTime, long endTime) { // Account查询
		ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, a.incomeAccount, Payee.name, b.categoryName, b.categoryType, b._id  from Category b, 'Transaction' a left join Payee on a.payee = Payee._id  where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and a.parTransaction != -1 and  (a.expenseAccount <= 0 or a.incomeAccount <= 0) and a._id = "+id ;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			double amount = mCursor.getDouble(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
			int incomeAccount = mCursor.getInt(4);
			String payeeName = mCursor.getString(5);
			String category_name = mCursor.getString(6);
			int categoryType = mCursor.getInt(7);
			int categoryId = mCursor.getInt(8);
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payeeName", payeeName);
			mMap.put("categoryName", category_name);
			mMap.put("categoryType", categoryType);
			mMap.put("categoryId", categoryId);

			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
	
	
	public static  ArrayList<HashMap<String, Object>>  selectTransactionSumByNameLikeGroup (
			Context context, String categoryName,long startTime, long endTime, int categoryType ) { // 根据category的name查询该name下的所有总和，group by name 并且排序
		ArrayList<HashMap<String,Object>> mList = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select total(a.amount) as sumAmount , b.categoryName from 'Transaction' a, Category b where a.dateTime >= "+ startTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.category = b._id and (a.expenseAccount <= 0  or a.incomeAccount <= 0) and b.categoryType = "+categoryType+" and b.categoryName like '" +categoryName +"%'  group by  b.categoryName order by sumAmount DESC ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();
			double sum = mCursor.getDouble(0);
			String category_name  = mCursor.getString(1);
			
			mMap.put("sum", sum);
			mMap.put("categoryName", category_name);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static double selectTransactionSumByNameLike(
			Context context, String categoryName,long startTime, long endTime, int categoryType) { // 根据category的name查询该name下的所有总和
		double sum = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select total(a.amount) from 'Transaction' a, Category b where a.dateTime >= "+ startTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.category = b._id and b.categoryName like '" +categoryName+"%' and b.categoryType = "+categoryType +" and (a.expenseAccount <= 0  or a.incomeAccount <= 0) ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			sum = mCursor.getDouble(0);
		}
		mCursor.close();
		db.close();

		return sum;
	}
	
	
	public static ArrayList<HashMap<String, Object>> selectTransactionByNameLikeLeftJoin(
			Context context, String categoryName,long startTime, long endTime , int mCategoryType) { // CategoryReport list点入后的插叙，根据category名字，查询like
		ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, a.incomeAccount, Payee.name, b.categoryName, b.categoryType, b._id, a.childTransactions, a.parTransaction from  Category b, 'Transaction' a left join Payee on a.payee = Payee._id where a.dateTime >= "+ startTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.category = b._id and (a.expenseAccount <= 0  or a.incomeAccount <= 0)  and b.categoryType = "+mCategoryType+" and b.categoryName like '" +categoryName+"' order by b.dateTime DESC,a._id DESC ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			double amount = mCursor.getDouble(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
			int incomeAccount = mCursor.getInt(4);
			String payeeName = mCursor.getString(5);
			String category_name = mCursor.getString(6);
			int categoryType = mCursor.getInt(7);
			int categoryId = mCursor.getInt(8);
			String childTransactions = mCursor.getString(9);
			int parTransaction = mCursor.getInt(10);
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payeeName", payeeName);
			mMap.put("categoryName", category_name);
			mMap.put("categoryType", categoryType);
			mMap.put("categoryId", categoryId);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static ArrayList<HashMap<String, Object>> selectTransactionByNameLikeLeftJoinLimit(
			Context context, String categoryName,long startTime, long endTime, int allSize) { // CategoryReport list点入后的插叙，根据category名字，查询like
		ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, a.incomeAccount, Payee.name, b.categoryName, b.categoryType, b._id from  Category b, 'Transaction' a left join Payee on a.payee = Payee._id where a.dateTime >= "+ startTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.category = b._id and b.categoryName like '" +categoryName+"%' and a.childTransactions != 1 order by b.categoryName ASC,a._id ASC limit 30 offset "+ allSize;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			double amount = mCursor.getDouble(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
			int incomeAccount = mCursor.getInt(4);
			String payeeName = mCursor.getString(5);
			String category_name = mCursor.getString(6);
			int categoryType = mCursor.getInt(7);
			int categoryId = mCursor.getInt(8);
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payeeName", payeeName);
			mMap.put("categoryName", category_name);
			mMap.put("categoryType", categoryType);
			mMap.put("categoryId", categoryId);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static HashMap<String, Double> selectTransactionGroup(Context context, long startTime, long endTime) { // Group 查询transaction的总额，通过type，一段时间内的expense和income
		
		HashMap<String, Double> mChildMap = new HashMap<String, Double>(); 
		
		SQLiteDatabase db = getConnection(context);
		
		String sqltest = "select total(a.amount) , strftime('%m-%d',datetime( a.dateTime/1000, 'unixepoch'),'localtime') as customDate, b.categoryType from 'Transaction' a,  Category b where  a.dateTime >= "+ startTime +" and a.dateTime <  "+(endTime+DAYMILLIS) +" and a.category = b._id and (a.expenseAccount <= 0  or a.incomeAccount <= 0)  group by customDate, b.categoryType" ;
		Cursor mCursortest = db.rawQuery(sqltest, null);
		
		while (mCursortest.moveToNext()) {
			
			double sum = mCursortest.getDouble(0);
			String DateString = mCursortest.getString(1);
			int categoryType = mCursortest.getInt(2);

			String dateKey = DateString+"-"+categoryType;
			mChildMap.put(dateKey, sum);
		}
		
		mCursortest.close();
		db.close();

		return mChildMap;
	}
	
	
	public static List<Map<String, Object>>  selectCategoryAllSum(Context context, long startTime, long endTime) { // Group 查询transaction的总额，通过type，一段时间内的expense和income
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		SQLiteDatabase db = getConnection(context);
		String sql = "select total(a.amount), b.categoryType from 'Transaction' a , Category b where a.dateTime >= "+ startTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.parTransaction != -1 and a.category = b._id  group by b.categoryType";
		Cursor mCursor = db.rawQuery(sql, null);
		
		Map<String, Object> mMap;
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();
			double sum = mCursor.getDouble(0);
			int categoryType = mCursor.getInt(1);

			mMap.put("sum", sum);
			mMap.put("categoryType", categoryType);
			mList.add(mMap);
		}
		
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectTransactionByTimeBEPay(
			Context context, long beginTime, long endTime) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* , Payee.name from 'Transaction' a left join Payee on a.payee = Payee._id where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+(endTime+DAYMILLIS)+ " and a.parTransaction != -1 order by a.dateTime DESC , a._id DESC ";
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
			String payeeName =  mCursor.getString(27);

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
			mMap.put("payeeName", payeeName);
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

}
