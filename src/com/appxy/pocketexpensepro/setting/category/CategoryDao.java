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

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;

public class CategoryDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static long updateCategoryName(Context context, int id,String newName) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("categoryName", newName);
		cv.put("dateTime", System.currentTimeMillis());
		String _id = id + "";
		long row =db.update("Category", cv, "_id = ?", new String[] { _id });
		db.close();
		return row;
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
	
	
	
	public static long updateCategory(Context context, int id,String categoryName,
			int categoryType, int hasBudget, int iconName, int isDefault) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("hasBudget", hasBudget);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("dateTime", System.currentTimeMillis());
		String _id = id + "";
		long row =db.update("Category", cv, "_id = ?", new String[] { _id });
		db.close();
		return row;
	}

	
	public static long insertCategory(Context context, String categoryName,
			int categoryType, int hasBudget, int iconName, int isDefault) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("hasBudget", hasBudget);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("isSystemRecord", 0);
		cv.put("dateTime", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("uuid", MEntity.getUUID());
		long row = db.insert("Category", null, cv);
		db.close();
		return row;
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

	public static long deleteCategory(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("Category", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static long deleteCategoryLike(Context context, String name) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = 0;
		try {
			row = db.delete("Category", "categoryName like ?", new String[] { name+":"+"%" });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}

}
