package com.appxy.pocketexpensepro.overview.budgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;

public class BudgetsDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	public static void updateBudgetTransfer(Context context, int id ,String amount,long dateTime ,int fromBudget, int toBudget) {
		SQLiteDatabase db = getConnection(context);

		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("fromBudget", fromBudget);
		cv.put("toBudget", toBudget);
		cv.put("dateTime_sync", System.currentTimeMillis());
		String mId = id + "";
		try {
			long tid = db.update("BudgetTransfer", cv, "_id = ?",
					new String[] { mId });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
		}
	}

	public static long updateBudget(Context context, int _id, String amount) {
		SQLiteDatabase db = getConnection(context);

		List<Map<String, Object>> itemList = selectItemById(context, _id);
		int tem_id = (Integer) itemList.get(0).get("budgetTemplate");

		ContentValues cv = new ContentValues();
		cv.put("amount", amount);

		String mId = _id + "";
		try {
			long id = db.update("BudgetItem", cv, "_id = ?",
					new String[] { mId });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
		}

		String tem_idString = tem_id + "";
		try {
			long mid = db.update("BudgetTemplate", cv, "_id = ?",
					new String[] { tem_idString });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			db.close();

		}

		return 1;
	}

	public static long deleteBudget(Context context, int id) {
		SQLiteDatabase db = getConnection(context);

		List<Map<String, Object>> itemList = selectItemById(context, id);
		int tem_id = (Integer) itemList.get(0).get("budgetTemplate");

		String _id = id + "";
		long row = 0;
		try {
			row = db.delete("BudgetItem", "_id = ?", new String[] { _id });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}

		String tem_idString = tem_id + "";
		long trow = 0;
		try {
			trow = db.delete("BudgetTemplate", "_id = ?",
					new String[] { tem_idString });
		} catch (Exception e) {
			// TODO: handle exception
			trow = 0;
		}
		db.close();
		return row;
	}
	
	
	
	public static long deleteBudgetAll(Context context) {
		SQLiteDatabase db = getConnection(context);
		long trow = 0;
		try {
			trow = db.delete("BudgetTemplate", "_id > ?",
					new String[] { "0" });
		} catch (Exception e) {
			// TODO: handle exception
			trow = 0;
		}
		
		long row = 0;
		try {
			row = db.delete("BudgetItem", "_id > ?", new String[] { "0" });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}

		
		db.close();
		return row;
	}
	

	public static List<Map<String, Object>> selectItemById(Context context,
			int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetItem a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int budgetTemplate = mCursor.getInt(11);

			mMap.put("_id", _id);
			mMap.put("budgetTemplate", budgetTemplate);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static long insertBudgetTemplate(Context context, String amount,
			int category) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("uuid", MEntity.getUUID());
		cv.put("category", category);
		cv.put("isRollover", 0);
		cv.put("isNew", 1);
		
		cv.put("dateTime", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("startDate", System.currentTimeMillis());
		cv.put("amount", amount);
		cv.put("cycleType", "No Cycle");
		
		
		long row = db.insert("BudgetTemplate", null, cv);
		return row;
	}

	public static long insertBudgetTransfer(Context context, String amount,long dateTime ,int fromBudget, int toBudget) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("fromBudget", fromBudget);
		cv.put("toBudget", toBudget);
		cv.put("dateTime_sync", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("uuid", MEntity.getUUID());
		long row = db.insert("BudgetTransfer", null, cv);
		return row;
	}

	public static long insertBudgetItem(Context context, String amount,
			int budgetTemplate) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("uuid", MEntity.getUUID());
		cv.put("startDate", System.currentTimeMillis());
		cv.put("amount", amount);
		cv.put("endDate", System.currentTimeMillis());
		cv.put("rolloverAmount", 0);
		
		cv.put("dateTime", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("budgetTemplate", budgetTemplate);
		cv.put("isRollover", 0);
		
		long row = db.insert("BudgetItem", null, cv);
		return row;
	}

	public static List<Map<String, Object>> selectTem(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetTemplate ";
		Cursor mCursor = db.rawQuery(sql, null);
		Log.v("mtest", "mCursor" + mCursor.getCount());
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int category = mCursor.getInt(13);

			mMap.put("_id", _id);
			mMap.put("category", category);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectItem(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetItem ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int budgetTemplate = mCursor.getInt(11);

			mMap.put("_id", _id);
			mMap.put("budgetTemplate", budgetTemplate);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectCategoryNotIn(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);

		String sql = "select c.* from Category c where c.categoryType = 0 and c._id not in (select c._id from BudgetItem a,BudgetTemplate b,Category c where a.budgetTemplate=b._id and b.category=c._id and c.categoryType = 0) order by categoryName ASC";
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
	
	public static List<Map<String, Object>> selectCategoryLeftBudget(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);

		String sql = "select * from Category c left join (select c._id from BudgetItem a,BudgetTemplate b,Category c where a.budgetTemplate=b._id and b.category=c._id ) tem on c._id = tem._id where c.categoryType = 0 and categoryName not like '%:%' order by lower(categoryName), categoryName ASC";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String categoryName = mCursor.getString(3);
			int categoryType = mCursor.getInt(5);
			int hasBudget = mCursor.getInt(8);
			int iconName = mCursor.getInt(9);
			int isDefault = mCursor.getInt(10);
			int hasChild = mCursor.getInt(24);

			mMap.put("category", _id);
			mMap.put("categoryName", categoryName);
			mMap.put("categoryType", categoryType);
			mMap.put("hasBudget", hasBudget);
			mMap.put("iconName", iconName);
			mMap.put("isDefault", isDefault);
			mMap.put("hasChild", hasChild);
			mMap.put("amount", "0.00");
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	

	public static List<Map<String, Object>> selectBudgetById(Context context,
			int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetItem a, BudgetTemplate b,Category c where a._id = "
				+ id + " and a.budgetTemplate = b._id and  b.category = c._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			int tem_id = mCursor.getInt(14);
			int category = mCursor.getInt(27);
			String categoryName = mCursor.getString(31);
			int iconName = mCursor.getInt(37);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("tem_id", tem_id);
			mMap.put("category", category);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

}
