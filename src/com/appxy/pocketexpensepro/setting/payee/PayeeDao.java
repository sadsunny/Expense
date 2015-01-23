package com.appxy.pocketexpensepro.setting.payee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.table.PayeeTable;
import com.appxy.pocketexpensepro.table.PayeeTable.Payee;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PayeeDao {
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static int selectCategoryType(Context context,int id) { // 查询Category的类型
		int categoryType = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select Category.categoryType from Category where Category._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			categoryType = mCursor.getInt(0);

		}
		mCursor.close();
		db.close();

		return categoryType;
	}
	
	
	public static List<Map<String, Object>> selectCategory(Context context,
			int type) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Category where Category.categoryType = "
				+ type + " order by lower(categoryName), categoryName ASC ";
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
	
	public static String selectCategoryUUidById(Context context,int id) {
		
		String mUUid = null ;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.uuid from Category a where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mUUid = mCursor.getString(0);
		}
		mCursor.close();
		db.close();

		return mUUid;
	}
	
	public static String selectPayeeUUidById(Context context,int id) {
		
		String mUUid = null ;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.uuid from Payee a where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mUUid = mCursor.getString(0);
		}
		mCursor.close();
		db.close();

		return mUUid;
	}
	
	
	
	public static int selectCategoryIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from Category a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();

		return id;
	}
	
	public static int selectPayeeIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from Payee a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();

		return id;
	}
	
	
	public static long updatePayeeAll( Context context, String name,String memo, int category, long dateTime, String state , String uuid ) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("memo", memo);
		cv.put("category", category);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		long row = db.update("Payee", cv, "_id = ?", new String[] { uuid });
		db.close();
		return row;
	}
	
	
	
	public static long insertPayeeAll(Context context, String name,String memo, int category, long dateTime, String state , String uuid ) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("name", name);
		cv.put("memo", memo);
		cv.put("category", category);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("Payee", null, cv);
			db.close();
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	
	}
	
	public static long deletePayee(Context context, int id, String uuid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
//		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("Payee", "uuid = ?", new String[] { uuid });
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) {
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					PayeeTable payeeTable = new PayeeTable(mDatastore, context);
					Payee payee = payeeTable.getPayee();
					payee.setUuid(uuid);
					payeeTable.updateState(uuid, "0");
					
				}
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	
	public static long updatePayee(Context context, int id, String name,String memo, int category, String uuid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		ContentValues cv = new ContentValues();
		
		long dateTime = System.currentTimeMillis() ;
		cv.put("name", name);
		cv.put("memo", memo);
		cv.put("category", category);
		cv.put("dateTime", dateTime);
		
		try {
			long row = db.update("Payee", cv, "_id = ?", new String[] { _id });
			db.close();
			
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					PayeeTable payeeTable = new PayeeTable(mDatastore, context);
					Payee payee = payeeTable.getPayee();
					
					payee.setDatetime(MEntity.getMilltoDateFormat(dateTime));
					payee.setPayee_category(category);
					payee.setPayee_memo(memo);
					payee.setPayee_name(name);
					payee.setState("1");
					payee.setUuid(uuid);
					
					payeeTable.insertRecords(payee.getFields());
					mDatastore.sync();
				}
			}
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
		
	
	}
	
	
	public static long insertPayee(Context context, String name,String memo, int category,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		long dateTime = System.currentTimeMillis();
		String state = "1";
		String uuid =  MEntity.getUUID();
		
		cv.put("name", name);
		cv.put("memo", memo);
		cv.put("category", category);
		
		cv.put("dateTime", dateTime);
		cv.put("state", 1);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("Payee", null, cv);
			db.close();
			
			if ( row > 0 ) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					PayeeTable payeeTable = new PayeeTable(mDatastore, context);
					Payee payee = payeeTable.getPayee();
					
					payee.setDatetime(MEntity.getMilltoDateFormat(dateTime));
					payee.setPayee_category(category);
					payee.setPayee_memo(memo);
					payee.setPayee_name(name);
					payee.setState(state);
					payee.setUuid(uuid);
					
					payeeTable.insertRecords(payee.getFields());
					mDatastore.sync();
				}
				
			}
			
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
	
	}
	
	
	public static List<Map<String, Object>> selectPayee(Context context) { 
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.categoryName, b.iconName from Payee a,Category b where a.category = b._id order by lower(a.name), a.name ASC ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String name = mCursor.getString(3);
			String categoryName = mCursor.getString(19);
			int iconName = mCursor.getInt(20);
			String uuid = mCursor.getString(12);

			mMap.put("_id", _id);
			mMap.put("name", name);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("uuid", uuid);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static long deletePayeeByUUid(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		
		long row = 0;
		try {
			row = db.delete("Payee", "uuid = ?", new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static List<Map<String, Object>> checkPayeeByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from Payee a where a.uuid = " + "'"+uuid+"'";
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
	
	
	
	public static List<Map<String, Object>> selectPayeeById(Context context,int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.categoryName, b.iconName, b.categoryType, b._id from Payee a,Category b where a.category = b._id and a._id = "+id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String memo = mCursor.getString(2);
			String name = mCursor.getString(3);
			String categoryName = mCursor.getString(19);
			int iconName = mCursor.getInt(20);
			int categoryType = mCursor.getInt(21);
			int categoryId = mCursor.getInt(22);
			
			mMap.put("_id", _id);
			mMap.put("name", name);
			mMap.put("memo", memo);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("categoryType", categoryType);
			mMap.put("categoryId", categoryId);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static List<Map<String, Object>>  checkPayeeByNameAndCaegory(Context context,String pName,int category) {
		
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from Payee a where a.name = "+"'"+pName+"'" + "and a.category = " +category;
		Cursor mCursor = db.rawQuery(sql, null);
		
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
			
			mList.add(mMap);
		}
		
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	
	public static List<Map<String, Object>> selectPayeeRelate(Context context,int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, b._id from Payee a,'Transaction' b where a._id = b.payee and a._id = "+id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			mMap.put("_id", _id);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
}
