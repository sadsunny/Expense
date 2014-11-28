package com.appxy.pocketexpensepro.accounts;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R.id;
import com.appxy.pocketexpensepro.db.CascadeDeleteDao;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.appxy.pocketexpensepro.table.AccountTypeTable;
import com.appxy.pocketexpensepro.table.BudgetTransferTable;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.AccountTypeTable.AccountType;
import com.appxy.pocketexpensepro.table.AccountsTable;
import com.appxy.pocketexpensepro.table.AccountsTable.Accounts;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccountDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		return db;
	}
	
	public static String selectAccountTypeUUidById(Context context,int id) {
		
		String mUUid = null ;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.uuid from AccountType a where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mUUid = mCursor.getString(0);
		}
		mCursor.close();
		db.close();

		return mUUid;
	}

	public static int getAccountTypeIdByUUID(Context context, String uuid) {

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from AccountType a where a.uuid = " + "'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);

		int theId = 0;
		while (mCursor.moveToNext()) {
			theId = mCursor.getInt(0);
		}
		
		mCursor.close();
		db.close();

		return theId;
	}
	
	public static List<Map<String, Object>> checkAccountTypeByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from AccountType a where a.uuid = " + "'"+uuid+"'";
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
	

	public static List<Map<String, Object>> checkAccountByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime_sync from Accounts a where a.uuid = " + "'"+uuid+"'";
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

	public static int selectAccountRelate(Context context, int id) {
		int size = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select b._id from Accounts a,'Transaction' b where (a._id = b.expenseAccount or a._id = b.incomeAccount) and a._id = "
				+ id;
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

	public static List<Map<String, Object>> selectTransactionRecurringOverToday(
			Context context, long today) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a.recurringType > 0 and a.dateTime < "
				+ today + " order by a.dateTime DESC , a._id DESC ";
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
			String uuid = mCursor.getString(16);

			mMap.put("uuid", uuid);
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
	
	
	public static long updateAccountTypeAll(Context context, int iconName,int isDefault, String typeName, long dateTime, String state, String uuid) { 
		
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("typeName", typeName);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);

		try {
			long id = db.update("AccountType", cv, "uuid = ?", new String[] { uuid });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}
	
	
	public static long insertAccountTypeAll(Context context, int iconName,int isDefault, String typeName, long dateTime, String state, String uuid) { // AccountType插入

		
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("typeName", typeName);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);

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

	

	public static long insertAccountType(Context context, int iconName,int isDefault, String typeName, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { // AccountType插入

		long  dateTime = System.currentTimeMillis();
		String state  = "1";
		String uuid = MEntity.getUUID();
		
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("typeName", typeName);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);

		try {
			long id = db.insert("AccountType", null, cv);
			if (id > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					AccountTypeTable accountTypeTable = new AccountTypeTable(mDatastore, context);
					AccountType accountType = accountTypeTable.getAccountType();
					accountType.setAccounttype_iconname(iconName);
					accountType.setAccounttype_isdefault(isDefault);
					accountType.setAccounttype_typename(typeName);
					accountType.setDatetime(dateTime);
					accountType.setState(state);
					accountType.setUuid(uuid);
					accountTypeTable.insertRecords(accountType.getFields());
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

	public static List<Map<String, Object>> selectAccountType(Context context) { // AccountType查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from AccountType order by AccountType.typeName ASC";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			int iconName = mCursor.getInt(2);
			String typeName = mCursor.getString(7);
			String uuid = mCursor.getString(8);
			
			mMap.put("_id", _id);
			mMap.put("iconName", iconName);
			mMap.put("typeName", typeName);
			mMap.put("uuid", uuid);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static long updateAccountAll(Context context, String accName,
			String amount, long dateTime, int autoClear, int accountType,
			String state, String uuid, long dateTime_sync, int orderIndex) { // Account插入
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();

		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		cv.put("orderIndex", orderIndex);
		cv.put("state", state);
		cv.put("dateTime_sync", dateTime_sync);

		try {
			long id = db.update("Accounts", cv, "uuid = ?", new String[] { uuid });
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}
	

	public static long insertAccountAll(Context context, String accName,
			String amount, long dateTime, int autoClear, int accountType,
			String state, String uuid, long dateTime_sync, int orderIndex) { // Account插入
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();

		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		cv.put("orderIndex", orderIndex);
		
		cv.put("state", state);
		cv.put("uuid", uuid);
		cv.put("dateTime_sync", dateTime_sync);

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
	
	public static long deleteAccount(Context context, int id, String uuid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			
			CascadeDeleteDao.CascadedeleteTransByAccount(context, id, mDbxAcctMgr, mDatastore);
			
			row = db.delete("Accounts", "_id = ?", new String[] { _id });
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					AccountsTable  accountsTable  = new AccountsTable(mDatastore, context);
					accountsTable.updateState(uuid, "0");
					mDatastore.sync();
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static long updateAccount(Context context, long _id, String accName,
			String amount, long dateTime, int autoClear, int accountType, String uuid,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		long dateTime_sync = System.currentTimeMillis();
		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		cv.put("dateTime_sync", dateTime_sync);
		
		String mId = _id + "";
		try {
			long id = db.update("Accounts", cv, "_id = ?", new String[] { mId });
			
			if (id > 0) {
				
				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
				}
				AccountsTable  accountsTable  = new AccountsTable(mDatastore, context);
				Accounts accounts = accountsTable.getAccounts();
				
				accounts.setAccountType(accountType);
				accounts.setAmount(Double.parseDouble(amount));
				accounts.setAutoclear(autoClear);
				accounts.setDatetime(MEntity.getMilltoDateFormat(dateTime));
				accounts.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
				accounts.setName(accName);
				accounts.setState("1");
				accounts.setUuid(uuid);
				
				accountsTable.insertRecords(accounts.getFields());
				mDatastore.sync();
				
				
			}
			
			db.close();
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

	}

	
	
	public static long updateAccountIndex(Context context, int tId, String accName,
			String amount, long dateTime, int autoClear, int accountType,int orderIndex,String uuid, long  dateTime_sync, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { // Account更新排序字段
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("orderIndex", orderIndex);
		cv.put("dateTime_sync", System.currentTimeMillis());
		
		String mId = tId+"";
		try {
			long id = db.update("Accounts", cv, "_id = ?", new String[] { mId });
			
			if ( id > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					AccountsTable  accountsTable  = new AccountsTable(mDatastore, context);
					Accounts accounts = accountsTable.getAccounts();
					
					accounts.setAccountType(accountType);
					accounts.setAmount(Double.parseDouble(amount));
					accounts.setAutoclear(autoClear);
					accounts.setDatetime(MEntity.getMilltoDateFormat(dateTime));
					accounts.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
					accounts.setName(accName);
					accounts.setOrderindex(orderIndex);
					accounts.setState("1");
					accounts.setUuid(uuid);
					
					accountsTable.insertRecords(accounts.getFields());
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

	public static long insertAccount(Context context, String accName,
			String amount, long dateTime, int autoClear, int accountType,int orderIndex, String uuid, long  dateTime_sync, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { // Account插入
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();

		String state = "1";
		
		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		cv.put("orderIndex", 10000);
		
		cv.put("state", state);
		cv.put("uuid",  uuid);
		cv.put("dateTime_sync", dateTime_sync);

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
	
	public static long deleteAccountTypeByUUID(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = 0;
		try {
			row = db.delete("AccountType", "uuid = ?", new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static long deleteAccountTypeByID(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = 0;
		try {
			
			CascadeDeleteDao.CascadedeleteAccountTypeByID(context, id, mDbxAcctMgr, mDatastore);
			
			row = db.delete("AccountType", "_id = ?", new String[] { id+""});
			
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					AccountTypeTable accountTypeTable = new AccountTypeTable(mDatastore, context);
					AccountType accountType = accountTypeTable.getAccountType();
					accountType.setUuid(uuid);
					accountTypeTable.updateState(uuid, "0");
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	

	public static long deleteAccountByUUID(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = 0;
		try {
			row = db.delete("Accounts", "uuid = ?", new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}


	
	public static List<Map<String, Object>> selectAccountTypeByID(
			Context context, int id) { // AccountType根据id查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from AccountType a where a._id = "+id;
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
			String uuid  = mCursor.getString(15);

			mMap.put("_id", _id);
			mMap.put("accName", accName);
			mMap.put("amount", amount);
			mMap.put("autoClear", autoClear);
			mMap.put("dateTime", dateTime);
			mMap.put("accountType", accountType);
			mMap.put("iconName", iconName);
			mMap.put("typeName", typeName);
			mMap.put("uuid", uuid);

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
			String uuid = mCursor.getString(16);
			
			int recurringType = mCursor.getInt(10);
			int category = mCursor.getInt(18);
			String childTransactions = mCursor.getString(19);
			int expenseAccount = mCursor.getInt(20);
			int incomeAccount = mCursor.getInt(21);
			int parTransaction = mCursor.getInt(22);
			int payee = mCursor.getInt(23);
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);

			mMap.put("uuid", uuid);
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

	public static List<Map<String, Object>> selectTransactionByID(
			Context context, int id) { // Account查询
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

	public static void deleteTransactionChildById(Context context, int parId,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.* from 'Transaction' a,Category b where a.parTransaction = "
				+ parId
				+ " and a.childTransactions = 1 and b._id = a.category ";

		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(16);
			
			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mList.add(mMap);
		}
		mCursor.close();

		for (Map<String, Object> iMap : mList) {
			int _id = (Integer) iMap.get("_id");
			String uuid = (String) iMap.get("uuid");
			
			deleteTransaction(context, _id, uuid, mDbxAcctMgr ,mDatastore);
		}

		db.close();

	}

	public static List<Map<String, Object>> selectTransactionChildById(
			Context context, int parId) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.* from 'Transaction' a,Category b where a.parTransaction = "
				+ parId
				+ " and a.childTransactions = 1 and b._id = a.category ";

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

	
	public static long updateTransactionAll(int _id, Context context,
			String amount, long dateTime, int isClear, String notes,
			String photoName, int recurringType, int category,
			String childTransactions, int expenseAccount, int incomeAccount,
			int parTransaction, int payee ,String uuid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore ) { 

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
		
		long dateTime_sync =  System.currentTimeMillis();
		cv.put("dateTime_sync", dateTime_sync);

		String mId = _id + "";
		try {
			long id = db.update("'Transaction'", cv, "_id = ?",
					new String[] { mId });
			
			if (id > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) {
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					Transaction transaction = transactionTable.getTransaction();
					
					transaction.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
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
					transaction.setUuid(uuid);
					transaction.setState("1");
					
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

	public static long deleteTransaction(Context context, int id,String uuid , DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		String trans_string = SyncDao.selecTransactionString(context, id);
		try {
			row = db.delete("'Transaction'", "_id = ?", new String[] { _id });
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					transactionTable.updateState(uuid, trans_string,"0");
					mDatastore.sync();
				}
					
			}
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}

	public static long deleteBudgetTransfer(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		String uuid = SyncDao.selectBudgetTransferUUidById(context, id);
		
		long row = 0;
		try {
			row = db.delete("'BudgetTransfer'", "_id = ?", new String[] { _id });
			
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
				   BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore, context);
				   budgetTransferTable.updateState(uuid, "0");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}

	public static long deleteTransactionChild(Context context, int pid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String p_id = pid + "";
		long row = 0;
		try {
			row = db.delete("'Transaction'", "parTransaction = ?",
					new String[] { p_id });
			if (row > 0) {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}

	public static long updateTransactionClear( Context context, long _id, int clear, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore ) { // Account更新排序字段
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		long dateTime_sync = System.currentTimeMillis() ;
		
		cv.put("isClear", clear);
		cv.put("dateTime_sync", dateTime_sync);
		
		String mId = _id + "";
		String uuid = SyncDao.selecTransactionUUid(context, (int)_id);
		try {
			long id = db.update("'Transaction'", cv, "_id = ?",
					new String[] { mId });
			if (id > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) {
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					Transaction transaction = transactionTable.getTransaction();
					
					transaction.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
					transaction.setTrans_isclear(clear);
					transaction.setUuid(uuid);
					
					transactionTable.insertRecords(transaction.getFieldsClear());
					
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

}
