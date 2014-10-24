package com.appxy.pocketexpensepro.overview.budgets;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.appxy.pocketexpensepro.table.BudgetItemTable;
import com.appxy.pocketexpensepro.table.BudgetItemTable.BudgetItem;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable.BudgetTemplate;
import com.appxy.pocketexpensepro.table.BudgetTransferTable;
import com.appxy.pocketexpensepro.table.BudgetTransferTable.BudgetTransfer;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

public class BudgetsDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
	
	
	public static long deleteBudgetTemByUUId(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");  //级联删除item，以及关联的transfer
		
		long row = 0;
		try {
			row = db.delete("BudgetTemplate", "uuid = ?",
					new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
		
	}
	
	public static long deleteBudgetItemByUUId(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");  //级联删除item，以及关联的transfer
		
		long row = 0;
		try {
			row = db.delete("BudgetTemplate", "uuid = ?",
					new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
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
	
	public static List<Map<String, Object>> selectTempCategoryId(Context context,
			int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.category from BudgetTemplate a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int category = mCursor.getInt(0);
			mMap.put("category", category);
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static long updateBudgetTemplateAll(Context context, String amount,int category, int isRollover, int isNew, long startDate, String cycleType, long dateTime, String uuid, String state) {
		SQLiteDatabase db = getConnection(context);

		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("category", category);
		
		cv.put("isRollover", isRollover);
		cv.put("isNew", isNew);
		cv.put("startDate", startDate);
		cv.put("cycleType", cycleType);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		
		try {
			long mid = db.update("BudgetTemplate", cv, "uuid = ?",
					new String[] { uuid });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			db.close();

		}

		return 1;
	}
	
	
	
	public static long insertBudgetTemplateAll(Context context, String amount,int category, int isRollover, int isNew, long startDate, String cycleType, long dateTime, String uuid, String state) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("amount", amount);
		cv.put("category", category);
		
		cv.put("isRollover", isRollover);
		cv.put("isNew", isNew);
		cv.put("startDate", startDate);
		cv.put("cycleType", cycleType);
		
		cv.put("uuid",uuid);
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		
		Log.v("mtag", "ContentValues"+cv);
		try {
			long row = db.insert("BudgetTemplate", null, cv);
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
		
	}
	
	
	public static void updateBudgetTransfer(Context context, int id ,String amount,long dateTime ,int fromBudget, int toBudget, String uuid,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);

		long dateTime_sync = System.currentTimeMillis();
		
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("fromBudget", fromBudget);
		cv.put("toBudget", toBudget);
		cv.put("dateTime_sync", dateTime_sync);
		String mId = id + "";
		try {
			long tid = db.update("BudgetTransfer", cv, "_id = ?",
					new String[] { mId });
			
			if (tid > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore, context);
					BudgetTransfer budgetTransfer = budgetTransferTable.getBudgetTransfer();
					
					budgetTransfer.setBudget_frombudget(fromBudget);
					budgetTransfer.setBudget_tobudget(toBudget);
					budgetTransfer.setBudgettransfer_amount(Double.valueOf(amount));
					budgetTransfer.setBudgettransfer_datetime(MEntity.getMilltoDateFormat(dateTime));
					budgetTransfer.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
					budgetTransfer.setState("1");
					budgetTransfer.setUuid(uuid);
					budgetTransferTable.insertRecords(budgetTransfer.getFields());
					mDatastore.sync();
				}
					
			}
			
			
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
		}
		
	}
	
	
	public static long updateBudget(Context context, int _id, String amount,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);

		List<Map<String, Object>> itemList = selectItemById(context, _id);
		int tem_id = (Integer) itemList.get(0).get("budgetTemplate");

		ContentValues cv = new ContentValues();
		cv.put("amount", amount);

		String mId = _id + "";
				
		try {
			long id = db.update("BudgetItem", cv, "_id = ?",
					new String[] { mId });
			
			if (id > 0 ) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, context);
					BudgetItem budgetItem = budgetItemTable.getBudgetItem();
					
					budgetItem.setBudgetitem_amount(Double.valueOf(amount));
					budgetItem.setBudgetitem_budgettemplate(tem_id);
					budgetItem.setDateTime(MEntity.getMilltoDateFormat(System.currentTimeMillis()));
					
					budgetItemTable.insertRecords(budgetItem.getFields());
					mDatastore.sync();
				}

			}
			
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
		}

		List<Map<String, Object>> tmList  = selectTempCategoryId(context, tem_id);
		int categortId =  (Integer)tmList.get(0).get("category");
		
		String tem_idString = tem_id + "";
		try {
			long mid = db.update("BudgetTemplate", cv, "_id = ?",
					new String[] { tem_idString });
			
			if (mid > 0 ) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, context);
					BudgetTemplate budgetTemplate = budgetTemplateTable.getBudgetTemplate();
					
					budgetTemplate.setBudgettemplate_category(categortId);
					budgetTemplate.setDatetime(MEntity.getMilltoDateFormat(System.currentTimeMillis()));
					budgetTemplate.setBudgettemplate_amount(Double.valueOf(amount));
					
					budgetTemplateTable.insertRecords(budgetTemplate.getFields());
					mDatastore.sync();
				}
			}
			
			db.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			db.close();

		}
		

		return 1;
	}

	
	
	
	public static long deleteBudget(Context context, int id,String itemUUId,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON "); // 级联删除几颗解决问题   
		
		List<Map<String, Object>> itemList = selectItemById(context, id);
		int tem_id = (Integer) itemList.get(0).get("budgetTemplate");

		String _id = id + "";
		long row = 0;
		try {
			
			row = db.delete("BudgetItem", "_id = ?", new String[] { _id });
			
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, context);
					budgetItemTable.updateState(itemUUId, "0");
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}

		String tem_idString = tem_id + "";
		String tempUUidString = SyncDao.selectBudgetTemplateUUid(context, tem_id);
		
		long trow = 0;
		try {
			trow = db.delete("BudgetTemplate", "_id = ?",
					new String[] { tem_idString });
			if (trow > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, context);
					budgetTemplateTable.updateState(tempUUidString, "0");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			trow = 0;
			
		}
		db.close();
		return row;
	}

	public static long insertBudgetTemplate(Context context, String amount,
			int category, long dateTime, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		String cycleType = "No Cycle";
		String uuid = MEntity.getUUID();
		
		cv.put("amount", amount);
		cv.put("category", category);
		
		cv.put("isRollover", 0);
		cv.put("isNew", 1);
		cv.put("startDate", dateTime);
		cv.put("cycleType", cycleType);
		
		cv.put("uuid", uuid);
		cv.put("dateTime", dateTime);
		cv.put("state", 1);
		
		try {
			long row = db.insert("BudgetTemplate", null, cv);
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, context);
					BudgetTemplate budgetTemplate = budgetTemplateTable.getBudgetTemplate();
					budgetTemplate.setBudgettemplate_amount(Double.valueOf(amount));
					budgetTemplate.setBudgettemplate_category(category);
					budgetTemplate.setBudgettemplate_cycletype(cycleType);
					budgetTemplate.setBudgettemplate_isnew(1);
					budgetTemplate.setBudgettemplate_isrollover(0);
					budgetTemplate.setBudgettemplate_startdate(MEntity.getMilltoDateFormat(dateTime));
					budgetTemplate.setDatetime(MEntity.getMilltoDateFormat(dateTime));
					budgetTemplate.setState("1");
					budgetTemplate.setUuid(uuid);
					budgetTemplateTable.insertRecords(budgetTemplate.getFields());
					mDatastore.sync();
				}
			}
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
		
	}

	
	public static List<Map<String, Object>> checkBudgetTemplateByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from BudgetTemplate a where a.uuid = " + "'"+uuid+"'";
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
	
	
	public static int selectBudgetTemplateIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from BudgetTemplate a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();
		Log.v("mtag", "父ID"+id);
		return id;
	}
	
	public static int selectBudgetItemIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from BudgetItem a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();

		return id;
	}

	
	public static List<Map<String, Object>> checkBudgetTransferByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime_sync from BudgetTransfer a where a.uuid = " + "'"+uuid+"'";
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
	
	public static List<Map<String, Object>> checkBudgetItemByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from BudgetItem a where a.uuid = " + "'"+uuid+"'";
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
	
	
	public static long updateBudgetItemAll(Context context, String amount,int budgetTemplate, long startDate, int isRollover,long endDate,double rolloverAmount, long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);

		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("budgetTemplate", budgetTemplate);
		
		cv.put("startDate", dateTime);
		cv.put("isRollover",isRollover);
		cv.put("endDate", endDate);
		cv.put("rolloverAmount", rolloverAmount);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long mid = db.update("BudgetItem", cv, "uuid = ?", new String[] { uuid });
			db.close();
			return mid;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}

		
	}
	
	public static long updateBudgetTransferAll(Context context, String amount,long dateTime ,int fromBudget, int toBudget, long dateTime_sync, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("fromBudget", fromBudget);
		cv.put("toBudget", toBudget);
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", state);
		
		try {
			long row = db.update("BudgetTransfer", cv, "uuid = ?", new String[] { uuid });
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}
	
	
	public static long insertBudgetTransferAll(Context context, String amount,long dateTime ,int fromBudget, int toBudget, long dateTime_sync, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("fromBudget", fromBudget);
		cv.put("toBudget", toBudget);
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", state);
		cv.put("uuid",uuid);
		
		try {
			long row = db.insert("BudgetTransfer", null, cv);
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}
	
	

	public static long insertBudgetItemAll(Context context, String amount,int budgetTemplate, long startDate, int isRollover,long endDate,double rolloverAmount, long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("amount", amount);
		cv.put("budgetTemplate", budgetTemplate);
		
		cv.put("startDate", dateTime);
		cv.put("isRollover",isRollover);
		cv.put("endDate", endDate);
		cv.put("rolloverAmount", rolloverAmount);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("BudgetItem", null, cv);
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}

	public static long insertBudgetTransfer(Context context, String amount,long dateTime ,int fromBudget, int toBudget,DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		long dateTime_sync =  System.currentTimeMillis();
		String uuid = MEntity.getUUID();
		
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("fromBudget", fromBudget);
		cv.put("toBudget", toBudget);
		
		cv.put("dateTime_sync", dateTime_sync);
		cv.put("state", 1);
		cv.put("uuid", uuid);
		try {
			long row = db.insert("BudgetTransfer", null, cv);
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore, context);
					BudgetTransfer budgetTransfer = budgetTransferTable.getBudgetTransfer();
					
					budgetTransfer.setBudget_frombudget(fromBudget);
					budgetTransfer.setBudget_tobudget(toBudget);
					budgetTransfer.setBudgettransfer_amount(Double.valueOf(amount));
					budgetTransfer.setBudgettransfer_datetime(MEntity.getMilltoDateFormat(dateTime));
					budgetTransfer.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
					budgetTransfer.setState("1");
					budgetTransfer.setUuid(uuid);
					
					budgetTransferTable.insertRecords(budgetTransfer.getFields());
					mDatastore.sync();
					
				}
			}
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}
	
	
	
	public static long insertBudgetItem(Context context, String amount,
			int budgetTemplate, long dateTime,  DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2113);
		
		String uuid =  MEntity.getUUID();
		cv.put("amount", amount);
		cv.put("budgetTemplate", budgetTemplate);
		
		cv.put("startDate", dateTime);
		cv.put("isRollover", 0);
		cv.put("endDate", calendar.getTimeInMillis());
		cv.put("rolloverAmount", 0);
		
		cv.put("dateTime", dateTime);
		cv.put("state", 1);
		cv.put("uuid",uuid);
		try {
			long row = db.insert("BudgetItem", null, cv);
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, context);
					BudgetItem budgetItem = budgetItemTable.getBudgetItem();
					
					budgetItem.setBudgetitem_amount(Double.valueOf(amount));
					budgetItem.setBudgetitem_budgettemplate(budgetTemplate);
					budgetItem.setBudgetitem_enddate(MEntity.getMilltoDateFormat(calendar.getTimeInMillis()));
					budgetItem.setBudgetitem_isrollover(0);
					budgetItem.setBudgetitem_rolloveramou(0);
					budgetItem.setBudgetitem_startdate(MEntity.getMilltoDateFormat(calendar.getTimeInMillis()));
					budgetItem.setDateTime(MEntity.getMilltoDateFormat(calendar.getTimeInMillis()));
					budgetItem.setState("1");
					budgetItem.setUuid(uuid);
					budgetItemTable.insertRecords(budgetItem.getFields());
					mDatastore.sync();
					
				}
			}
			
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
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
