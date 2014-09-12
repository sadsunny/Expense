package com.appxy.pocketexpensepro.accounts;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R.id;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		return db;
	}
	
	public static int selectAccountRelate(Context context,
			int id) { 
		int size =  0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select b._id from Accounts a,'Transaction' b where (a._id = b.expenseAccount or a._id = b.incomeAccount) and a._id = "+id;
		Cursor mCursor = db.rawQuery(sql, null);
		size = mCursor.getCount();
		mCursor.close();
		db.close();

		return size;
	}
	
	
	public static int selectTransactionAllSize(Context context) { // Account查询
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a ";
		int size = 0;
		Cursor mCursor = db.rawQuery(sql, null);
		size = mCursor.getCount();
		mCursor.close();
		db.close();

		return size;
	}
	
	public static List<Map<String, Object>> selectTransactionRecurringOverToday(Context context, long today) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a.recurringType > 0 and a.dateTime < "+today+" order by a.dateTime DESC , a._id DESC ";
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
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			
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
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("notes", notes);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	

	public static long insertAccountType(Context context, int iconName,
			int isDefault, String typeName) { // AccountType插入

		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("typeName", typeName);

		try {
			long id = db.insert("AccountType", null, cv);
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}

	public static List<Map<String, Object>> selectAccountType(Context context) { // AccountType查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from AccountType";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
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

	public static long insertAccount(Context context, String accName,
			String amount, long dateTime, int autoClear, int accountType) { // Account插入
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		cv.put("state", "1");
		cv.put("uuid", MEntity.getUUID());
		cv.put("dateTime_sync", dateTime);
		
		try {
			long id = db.insert("Accounts", null, cv);
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}

	public static long deleteAccount(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("Accounts", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}

	public static long updateAccountIndex(Context context, long _id,
			long orderIndex) { // Account更新排序字段
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("orderIndex", orderIndex);
		String mId = _id + "";
		try {
			long id = db
					.update("Accounts", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}

	public static long updateAccount(Context context, long _id, String accName,
			String amount, long dateTime, int autoClear, int accountType) { 
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		String mId = _id + "";
		try {
			long id = db
					.update("Accounts", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}

	public static List<Map<String, Object>> selectAccountTypeByID(
			Context context, int id) { // AccountType根据id查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from AccountType ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
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

	public static List<Map<String, Object>> selectAccount(Context context) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.iconName, b.typeName from Accounts a,AccountType b where a.accountType = b._id order by a.orderIndex ASC ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String accName = mCursor.getString(1);
			String amount = mCursor.getString(2);
			int autoClear = mCursor.getInt(3);
			long dateTime = mCursor.getLong(6);
			int accountType = mCursor.getInt(16);
			int iconName = mCursor.getInt(22);
			String typeName = mCursor.getString(23);


			mMap.put("_id", _id);
			mMap.put("accName", accName);
			mMap.put("amount", amount);
			mMap.put("autoClear", autoClear);
			mMap.put("dateTime", dateTime);
			mMap.put("accountType", accountType);
			mMap.put("iconName", iconName);
			mMap.put("typeName", typeName);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectAccountById(Context context,
			int id) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.iconName, b.typeName ,b._id from Accounts a,AccountType b where a.accountType = b._id and a._id = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String accName = mCursor.getString(1);
			String amount = mCursor.getString(2);
			int autoClear = mCursor.getInt(3);
			long dateTime = mCursor.getLong(6);
			int accountType = mCursor.getInt(16);
			int iconName = mCursor.getInt(22);
			String typeName = mCursor.getString(23);
			int tpye_id = mCursor.getInt(24);


			mMap.put("_id", _id);
			mMap.put("accName", accName);
			mMap.put("amount", amount);
			mMap.put("autoClear", autoClear);
			mMap.put("dateTime", dateTime);
			mMap.put("accountType", accountType);
			mMap.put("iconName", iconName);
			mMap.put("typeName", typeName);
			mMap.put("tpye_id", tpye_id);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectTransactionOnClear(
			Context context, int clear) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a.isClear = "
				+ clear
				+ " and a.childTransactions != 1 and (expenseAccount <= 0 or incomeAccount <= 0)";
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
	
	public static List<Map<String, Object>> selectTransactionAllList(
			Context context) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where (a.expenseAccount > 0  or a.incomeAccount > 0) and a.childTransactions != 1 order by a.dateTime DESC , a._id DESC ";
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
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			
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
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	

	public static List<Map<String, Object>> selectTransactionByAccount(
			Context context, int accountId) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where (a.expenseAccount = "
				+ accountId
				+ " or a.incomeAccount = "
				+ accountId
				+ ") and a.childTransactions != 1 order by a.dateTime DESC , a._id DESC ";
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
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			
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
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	
	
	public static List<Map<String, Object>> selectTransactionByID(Context context, int id) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a._id = " + id;
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
			mMap.put("notes", notes);
			mMap.put("photoName", photoName);
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
	
	public static List<Map<String, Object>> selectTransactionAllAndClear(
			Context context) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where (a.expenseAccount > 0 or a.incomeAccount > 0) and a.childTransactions != 1 and isClear != 1 order by a.dateTime DESC, a._id DESC  ";
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
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			
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
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	
	public static List<Map<String, Object>> selectTransactionByAccountAndClear(
			Context context, int accountId) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where (a.expenseAccount = "
				+ accountId
				+ " or a.incomeAccount = "
				+ accountId
				+ ") and a.childTransactions != 1 and isClear != 1 order by a.dateTime DESC, a._id DESC  ";
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
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			
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
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectCategory(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		
		String sql = "select c.* from Category c where c.categoryType = 0 order by categoryName ASC";
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
	
	public static void deleteTransactionChildById(Context context, int parId) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.* from 'Transaction' a,Category b where a.parTransaction = "+parId +" and a.childTransactions = 1 and b._id = a.category ";
				
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			
			mMap.put("_id", _id);
			mList.add(mMap);
		}
		mCursor.close();
		
		for (Map<String, Object> iMap: mList) {
			int _id  = (Integer) iMap.get("_id");
			deleteTransaction(context, _id);
		}
	
		db.close();

	}
	
	
	public static List<Map<String, Object>> selectTransactionChildById(Context context, int parId) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.* from 'Transaction' a,Category b where a.parTransaction = "+parId +" and a.childTransactions = 1 and b._id = a.category ";
				
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
			
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			
			String categoryName = mCursor.getString(30);
			int categoryType = mCursor.getInt(32);
			int hasBudget = mCursor.getInt(35);
			int iconName = mCursor.getInt(36);
			int isDefault = mCursor.getInt(37);

			mMap.put("categoryName", categoryName);
			mMap.put("categoryType", categoryType);
			mMap.put("hasBudget", hasBudget);
			mMap.put("iconName", iconName);
			mMap.put("isDefault", isDefault);
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("recurringType", recurringType);
			mMap.put("categoryId", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	
	public static long updateTransactionAll(int _id, Context context, String amount,long dateTime, int isClear, String notes, String photoName, int recurringType, int category, String childTransactions, int expenseAccount , int incomeAccount, int parTransaction, int payee) { // AccountType插入

		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("isClear", isClear);
		cv.put("notes", notes);
		cv.put("photoName", photoName);
		cv.put("recurringType", recurringType);
		cv.put("category", category);
		cv.put("childTransactions", childTransactions);
		cv.put("expenseAccount", expenseAccount);
		cv.put("incomeAccount", incomeAccount);
		cv.put("parTransaction", parTransaction);
		cv.put("payee", payee);
		
		String mId = _id + "";
		try {
			long id = db
					.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}
	
	

	public static List<Map<String, Object>> selectCategoryById(Context context,
			int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);

		String sql = "select c.* from Category c where c._id = " + id;
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

	public static List<Map<String, Object>> selectPayeeById(Context context,
			int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Payee a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String name = mCursor.getString(3);

			mMap.put("_id", _id);
			mMap.put("name", name);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static long deleteTransaction(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
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
	
	public static long deleteBudgetTransfer(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("'BudgetTransfer'", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static long deleteTransactionChild(Context context, int pid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String p_id = pid + "";
		long row = 0;
		try {
			row = db.delete("'Transaction'", "parTransaction = ?", new String[] { p_id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	
	public static long updateTransactionClear(Context context, long _id, int clear) { // Account更新排序字段
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("isClear", clear);
		String mId = _id + "";
		try {
			long id = db
					.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}

}
