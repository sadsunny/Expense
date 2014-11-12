package com.appxy.pocketexpensepro.overview.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

public class TransactionDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static long deleteTransactionByUUId(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");  //级联删除item，以及关联的transfer
		
		long row = 0;
		try {
			row = db.delete("'Transaction'", "uuid = ?",
					new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
		
	}
	
	public static List<Map<String, Object>> checkTransactionByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime_sync from 'Transaction' a where a.uuid = " + "'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		Map<String, Object> mMap;

		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			long dateTime_sync = mCursor.getLong(0);
			mMap.put("dateTime_sync", dateTime_sync);
			mList.add(mMap);
		}

		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static Cursor selectPayee(Context context, String keyWord) { 
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.categoryName, b.iconName, b.categoryType from Payee a,Category b where a.category = b._id and a.name like '%"+keyWord+"%' ";
		Cursor mCursor = db.rawQuery(sql, null);

		return mCursor;
	}
	
	
	public static List<Map<String, Object>> selectPayeeBykey(Context context, String keyWord) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.categoryName, b.iconName from Payee a,Category b where a.category = b._id and a.name like '%"+keyWord+"%' ";
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
	

	public static List<Map<String, Object>> selectPayee(Context context) { // 查询Category
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
	
	public static int selectAccountsIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from Accounts a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();

		return id;
	}
	
	public static int selectTransactionIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from 'Transaction' a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();

		return id;
	}
	
	public static long updateTransactionAllData(Context context, String amount,long dateTime, int isClear, String notes, int recurringType, int category, String childTransactions, int expenseAccount , int incomeAccount, int parTransaction, int payee,String transactionstring,  long dateTime_sync, String state, String uuid) { // AccountType插入

		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("isClear", isClear);
		cv.put("notes", notes);
		cv.put("recurringType", recurringType);
		cv.put("category", category);
		cv.put("childTransactions", childTransactions);
		cv.put("expenseAccount", expenseAccount);
		cv.put("incomeAccount", incomeAccount);
		cv.put("parTransaction", parTransaction);
		cv.put("payee", payee);
		
		cv.put("transactionstring", transactionstring);
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", state);
		cv.put("uuid", uuid);

		try {
			long id = db.update("'Transaction'", cv, "uuid = ?",
					new String[] { uuid });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}

	public static long insertTransactionAllData(Context context, String amount,long dateTime, int isClear, String notes, int recurringType, int category, String childTransactions, int expenseAccount , int incomeAccount, int parTransaction, int payee,String transactionstring,  long dateTime_sync, String state, String uuid) { // AccountType插入

		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("isClear", isClear);
		cv.put("notes", notes);
		cv.put("recurringType", recurringType);
		cv.put("category", category);
		cv.put("childTransactions", childTransactions);
		cv.put("expenseAccount", expenseAccount);
		cv.put("incomeAccount", incomeAccount);
		cv.put("parTransaction", parTransaction);
		cv.put("payee", payee);
		
		cv.put("transactionstring", transactionstring);
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long id = db.insert("'Transaction'", null, cv);
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}
	
	
	public static long insertTransactionAll(Context context, String amount,long dateTime, int isClear, String notes, String photoName, int recurringType, int category, String childTransactions, int expenseAccount , int incomeAccount, 
			int parTransaction, int payee, String transactionstring,int trans_billitem,int trans_billrule, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {  

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
		cv.put("transactionstring", transactionstring);
		cv.put("transactionHasBillItem", trans_billitem);
		cv.put("transactionHasBillRule", trans_billrule);
		
		long dateTime_sync =  System.currentTimeMillis();
		String state = "1";
		String uuid = MEntity.getUUID();
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long id = db.insert("'Transaction'", null, cv);
			
			if (id > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					Transaction transaction = transactionTable.getTransaction();
					
					transaction.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
					transaction.setState(state);
					transaction.setTrans_amount(Double.valueOf(amount));
					transaction.setTrans_billitem(trans_billitem);
					transaction.setTrans_billrule(trans_billrule);
					transaction.setTrans_category(category);
					transaction.setTrans_datetime(MEntity.getMilltoDateFormat(dateTime));
					transaction.setTrans_expenseaccount(expenseAccount);
					transaction.setTrans_incomeaccount(incomeAccount);
					transaction.setTrans_isclear(isClear);
					transaction.setTrans_notes(notes);
					transaction.setTrans_partransaction(parTransaction);
					transaction.setTrans_payee(payee);
					transaction.setTrans_recurringtype(recurringType);
					transaction.setTrans_string(transactionstring);
					transaction.setUuid(uuid);
					transactionTable.insertRecords(transaction.getFields());
					mDatastore.sync();
				}
				
			}
			
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}
	
	public static void insertTransactionOne(SQLiteDatabase db ,Context context, String amount,long dateTime, int isClear, String notes, String photoName, int recurringType, int category, String childTransactions, int expenseAccount , int incomeAccount, int parTransaction, int payee, String transactionstring, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { // AccountType插入

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
		cv.put("transactionstring", transactionstring);
		
		long dateTime_sync =  System.currentTimeMillis();
		String uuid  = MEntity.getUUID();
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", 1);
		cv.put("uuid", uuid);
		
		try {
			long id = db.insert("'Transaction'", null, cv);
					
			if (id > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					Transaction transaction = transactionTable.getTransaction();
					
					transaction.setDateTime_sync( MEntity.getMilltoDateFormat(dateTime_sync) );
					transaction.setState("1");
					transaction.setTrans_amount(Double.valueOf(amount));
					transaction.setTrans_category(category);
					transaction.setTrans_datetime(MEntity.getMilltoDateFormat(dateTime));
					transaction.setTrans_expenseaccount(expenseAccount);
					transaction.setTrans_incomeaccount(incomeAccount);
					transaction.setTrans_isclear(isClear);
					transaction.setTrans_notes(notes);
					transaction.setTrans_partransaction(parTransaction);
					transaction.setTrans_payee(payee);
					transaction.setTrans_recurringtype(recurringType);
					transaction.setTrans_string(transactionstring);
					transaction.setUuid(uuid);
					transactionTable.insertRecords(transaction.getFields());
					mDatastore.sync();
					
				}

			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	
	public static long updateParTransactionChild(Context context, long _id, String childTransactions) { // Account更新排序字段
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("childTransactions", childTransactions);
		String mId = _id + "";
		try {
			long id = db.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	}
	
	
	public static long insertTransaction(Context context, String amount,long dateTime, int isClear, String notes, String photoName, int recurringType, int category, int expenseAccount, int payee) { // AccountType插入

		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("isClear", isClear);
		cv.put("notes", notes);
		cv.put("photoName", photoName);
		cv.put("recurringType", recurringType);
		cv.put("category", category);
		cv.put("expenseAccount", expenseAccount);
		cv.put("payee", payee);

		try {
			long id = db.insert("'Transaction'", null, cv);
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

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
	
	public static List<Map<String, Object>> selectCategoryAll(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		
		String sql = "select c.* from Category c order by categoryName ASC";
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


	
}
