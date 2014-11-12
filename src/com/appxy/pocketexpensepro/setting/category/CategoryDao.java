package com.appxy.pocketexpensepro.setting.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appxy.pocketexpensepro.db.CascadeDeleteDao;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.table.CategoryTable;
import com.appxy.pocketexpensepro.table.CategoryTable.Category;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

public class CategoryDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	
	public static List<Map<String, Object>> selectCategoryChild(Context context, String parString) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Category a where a.categoryName like '"+ parString+":"+"%'" ;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String categoryName = mCursor.getString(3);
			int categoryType = mCursor.getInt(5);
			int hasBudget = mCursor.getInt(8);
			int iconName = mCursor.getInt(9);
			int isDefault = mCursor.getInt(10);
			String uuid = mCursor.getString(15);

			mMap.put("_id", _id);
			mMap.put("categoryName", categoryName);
			mMap.put("categoryType", categoryType);
			mMap.put("hasBudget", hasBudget);
			mMap.put("iconName", iconName);
			mMap.put("isDefault", isDefault);
			mMap.put("uuid", uuid);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	
	public static int selectCategoryRelate(Context context, int _id) { // 查询Category
		
		int size = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from 'Transaction' a, Category b where b._id = a.category and b._id = "+_id;
		Cursor mCursor = db.rawQuery(sql, null);
		size = mCursor.getCount();
		
		mCursor.close();
		db.close();
		
		return size;
	}
	
	
	

	public static List<Map<String, Object>> checkCategoryByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from Category a where a.uuid = " + "'"+uuid+"'";
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
	
	public static long updateCategoryAll(Context context,String categoryName,
			int categoryType, int iconName, int isSystemRecord,  int isDefault, long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("iconName", iconName);
		cv.put("isSystemRecord", isSystemRecord);
		cv.put("isDefault", isDefault);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		long row =db.update("Category", cv, "uuid = ?", new String[] { uuid });
		db.close();
		return row;
	}

	
	public static long insertCategoryAll(Context context, String categoryName,
			int categoryType, int iconName,int isSystemRecord, int isDefault ,long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("isSystemRecord", isSystemRecord);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("Category", null, cv);
			db.close();
			
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
		
	}
	
	public static long updateCategoryName(Context context, int id,String newName, String uuid,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		long dateTime = System.currentTimeMillis();
		
		cv.put("categoryName", newName);
		cv.put("dateTime", dateTime);
		
		String _id = id + "";
		try {
			long row =db.update("Category", cv, "_id = ?", new String[] { _id });
			db.close();
			if (row > 0 ) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					CategoryTable categoryTable = new CategoryTable(mDatastore, context);
					Category category = categoryTable.getCategory();
					
					category.setCategory_categoryname(newName);
					category.setUuid(uuid);
					category.setDatetime( MEntity.getMilltoDateFormat(dateTime) );
					categoryTable.insertRecords(category.getFieldsName());
					
				}


			}
			
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
		
	}
	
	public static long updateCategory(Context context, int id,String categoryName,
			int categoryType, int hasBudget, int iconName, int isDefault,String uuid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		long dateTime = System.currentTimeMillis();
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("hasBudget", hasBudget);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("dateTime", dateTime);
		String _id = id + "";
		try {
			long row =db.update("Category", cv, "_id = ?", new String[] { _id });
			db.close();
			
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					CategoryTable categoryTable = new CategoryTable(mDatastore, context);
					Category category = categoryTable.getCategory();
					category.setCategory_categoryname(categoryName);
					category.setCategory_categorytype(categoryType+"");
					category.setCategory_iconname(iconName+"");
					category.setCategory_isdefault(isDefault);
					category.setCategory_issystemrecord(0);
					category.setUuid(uuid);
					category.setDatetime( MEntity.getMilltoDateFormat(dateTime) );
					categoryTable.insertRecords(category.getFields());
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
	
	
	
	public static long deleteCategoryLike(Context context, String name, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		
		List<Map<String, Object>> mList = selectCategoryLikeName(context, name);
				
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = 0;
		try {
			row = db.delete("Category", "categoryName like ?", new String[] { name+":"+"%" });
			
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					for (Map<String, Object> iMap:mList) {
						String uuid = (String) iMap.get("uuid");
						CategoryTable categoryTable = new CategoryTable(mDatastore, context);
						Category category = categoryTable.getCategory();
						category.setUuid(uuid);
						categoryTable.updateState(uuid, "0");
						mDatastore.sync();
					}
					
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}

	public static long deleteCategory(Context context, int id,String uuid, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			
			CascadeDeleteDao.CascadedeleteCategoryById(context, id, mDbxAcctMgr, mDatastore);
			
			row = db.delete("Category", "_id = ?", new String[] { _id });
			
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					CategoryTable categoryTable = new CategoryTable(mDatastore, context);
					Category category = categoryTable.getCategory();
					category.setUuid(uuid);
					categoryTable.updateState(uuid, "0");
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
	
	
	public static long insertCategory(Context context, String categoryName,
			int categoryType, int hasBudget, int iconName, int isDefault, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore ,long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("hasBudget", hasBudget);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("isSystemRecord", 0);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("Category", null, cv);
			db.close();
			
			if(row > 0){
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					CategoryTable categoryTable = new CategoryTable(mDatastore, context);
					Category category = categoryTable.getCategory();
					category.setCategory_categoryname(categoryName);
					category.setCategory_categorytype(categoryType+"");
					category.setCategory_iconname(iconName+"");
					category.setCategory_isdefault(isDefault);
					category.setCategory_issystemrecord(0);
					category.setDatetime( MEntity.getMilltoDateFormat(dateTime) );
					category.setState(state);
					category.setUuid(uuid);
					categoryTable.insertRecords(category.getFields());
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

	
	public static List<Map<String, Object>> selectCategoryById(Context context, int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Category a where a._id ="+id;
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
	
	public static List<Map<String, Object>> selectCategoryByName(Context context, String cName) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from Category a where a.categoryName = "+"'"+cName+"'";
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

	
	
	public static List<Map<String, Object>> selectCategoryAll(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Category order by categoryName ASC";
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
			String uuid = mCursor.getString(15);

			mMap.put("_id", _id);
			mMap.put("categoryName", categoryName);
			mMap.put("categoryType", categoryType);
			mMap.put("hasBudget", hasBudget);
			mMap.put("iconName", iconName);
			mMap.put("isDefault", isDefault);
			mMap.put("uuid", uuid);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
	public static long deleteCategoryByUUid(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = 0;
		try {
			row = db.delete("Category", "uuid = ?", new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	
	public static List<Map<String, Object>> selectCategoryLikeName(Context context, String name) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from Category where Category.categoryName like " + "'" +name+":" +"%'" ;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String uuid = mCursor.getString(15);

			mMap.put("uuid", uuid);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
	

}
