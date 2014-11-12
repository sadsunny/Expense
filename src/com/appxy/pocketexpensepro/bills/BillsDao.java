package com.appxy.pocketexpensepro.bills;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.db.CascadeDeleteDao;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.appxy.pocketexpensepro.table.BudgetTransferTable;
import com.appxy.pocketexpensepro.table.CategoryTable;
import com.appxy.pocketexpensepro.table.EP_BillItemTable;
import com.appxy.pocketexpensepro.table.BudgetTransferTable.BudgetTransfer;
import com.appxy.pocketexpensepro.table.CategoryTable.Category;
import com.appxy.pocketexpensepro.table.EP_BillItemTable.EP_BillItem;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable.EP_BillRule;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BillsDao {
	
	private final static long DAYMILLIS = 86400000L - 1L;
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
			}
	
	
	public static List<Map<String, Object>> checkBillRuleByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from EP_BillRule a where a.uuid = " + "'"+uuid+"'";
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
	
	public static List<Map<String, Object>> checkBillItemByUUid(Context context,
			String uuid) { // 检查accout的uuid，以及时间
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a.dateTime from EP_BillItem a where a.uuid = " + "'"+uuid+"'";
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
	
	
	public static long deleteBillRuleByUUId(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");  
		
		long row = 0;
		try {
			row = db.delete("EP_BillRule", "uuid = ?",
					new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
		
	}
	
	public static long deleteBillItemByUUId(Context context, String uuid) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");  
		
		long row = 0;
		try {
			row = db.delete("EP_BillItem", "uuid = ?",
					new String[] { uuid });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
		
	}
	
	
	
	public static long deleteBillPayById(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
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
	
	
	public static List<Map<String, Object>> selectTransactionById(Context context, int id) {  //bill payment记录
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, b.accName, b.iconName  from 'Transaction' a, Accounts b where a._id = "+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
            
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	
	public static long updateBillItem(Context context, int _id, int ep_billisDelete ,String ep_billItemAmount, long ep_billItemDueDate,
			long ep_billItemDueDateNew, long ep_billItemEndDate, String ep_billItemName,String ep_billItemNote, int ep_billItemRecurringType, 
			int ep_billItemReminderDate, long ep_billItemReminderTime, int billItemHasBillRule, int billItemHasCategory, int billItemHasPayee) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billItemAmount", ep_billItemAmount);
		cv.put("ep_billisDelete", ep_billisDelete);
		cv.put("ep_billItemDueDate", ep_billItemDueDate);
		cv.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
		cv.put("ep_billItemEndDate", ep_billItemEndDate);
		cv.put("ep_billItemName", ep_billItemName);
		cv.put("ep_billItemNote", ep_billItemNote);
		cv.put("ep_billItemRecurringType", ep_billItemRecurringType);
		cv.put("ep_billItemReminderDate", ep_billItemReminderDate);
		cv.put("ep_billItemReminderTime", ep_billItemReminderTime);
		cv.put("billItemHasBillRule", billItemHasBillRule);
		cv.put("billItemHasCategory", billItemHasCategory);
		cv.put("billItemHasPayee", billItemHasPayee);
		cv.put("dateTime", System.currentTimeMillis());
		
		String mId = _id + "";
		long row = db.update("EP_BillItem",cv, "_id = ?", new String[] { mId });
		db.close();
		return row;
	}
	
	public static long updateBillRule(Context context,int _id, double ep_billAmount, long ep_billDueDate,long ep_billEndDate,
			String ep_billName,String ep_note, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory,
			int billRuleHasPayee, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billAmount", ep_billAmount);
		cv.put("ep_billDueDate", ep_billDueDate);
		cv.put("ep_billEndDate", ep_billEndDate);
		cv.put("ep_billName", ep_billName);
		cv.put("ep_note", ep_note);
		cv.put("ep_recurringType", ep_recurringType);
		cv.put("ep_reminderDate", ep_reminderDate);
		cv.put("ep_reminderTime", ep_reminderTime);
		cv.put("billRuleHasCategory", billRuleHasCategory);
		cv.put("billRuleHasPayee", billRuleHasPayee);
		long  dateTime = System.currentTimeMillis();
		cv.put("dateTime", dateTime);
		
		String mId = _id + "";
		
		try {
			long row = db.update("EP_BillRule", cv, "_id = ?", new String[] { mId });
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
						String uuid = SyncDao.selectBillRuleUUid(context, _id);
						
						EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore, context);
						EP_BillRule	eP_BillRule = ep_BillRuleTable.getEP_BillRule();
						
						eP_BillRule.setBillrule_ep_billamount(ep_billAmount);
						eP_BillRule.setBillrule_ep_billduedate(MEntity.getMilltoDateFormat(ep_billDueDate));
						eP_BillRule.setBillrule_ep_billenddate(MEntity.getMilltoDateFormat(ep_billEndDate));
						eP_BillRule.setBillrule_ep_billname(ep_billName);
						eP_BillRule.setBillrule_ep_note(ep_note);
						eP_BillRule.setBillrule_ep_recurringtype(ep_recurringType);
						eP_BillRule.setBillrule_ep_reminderdate(ep_reminderDate);
						eP_BillRule.setBillrule_ep_remindertime(ep_reminderTime);
						eP_BillRule.setBillrulehascategory(billRuleHasCategory);
						eP_BillRule.setBillrulehaspayee(billRuleHasPayee);
						eP_BillRule.setDateTime(MEntity.getMilltoDateFormat(dateTime));
						eP_BillRule.setState("1");
						eP_BillRule.setUuid(uuid);
						
						ep_BillRuleTable.insertRecords(eP_BillRule.getFields());
						mDatastore.sync();
					
				}
				
			}
			db.close();
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
		
		
	}
	
	
	public static long updateBillDateRule(Context context, long _id, long ep_billEndDate,DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { 
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("ep_billEndDate", ep_billEndDate);
		
		long dateTime = System.currentTimeMillis();
		cv.put("dateTime", dateTime);
		
		String mId = _id + "";
		try {
			long id = db
					.update("EP_BillRule", cv, "_id = ?", new String[] { mId });
			
			if (id > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
						String uuid = SyncDao.selectBillRuleUUid(context, (int)_id);
						
						EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore, context);
						EP_BillRule eP_BillRule = ep_BillRuleTable.getEP_BillRule();
						
						eP_BillRule.setBillrule_ep_billenddate(MEntity.getMilltoDateFormat(ep_billEndDate));
						eP_BillRule.setUuid(uuid);
						eP_BillRule.setDateTime(MEntity.getMilltoDateFormat(dateTime));
						
						ep_BillRuleTable.insertRecords(eP_BillRule.getFieldsApart());
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
	
	public static List<String> selecTransactionUUidByBillRull(Context context, int id) { //查询rule所有pay的交易 
		SQLiteDatabase db = getConnection(context);
		List<String> mList = new ArrayList<String>();
		
		String sql = "select a.* from 'Transaction' a where a.transactionHasBillRule = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(16);
			mList.add(uuid);
		}
		
		return mList;
	}
	
	
	
	
	public static long updateBillPay(Context context,int _id, String amount, long dateTime,int expenseAccount, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {  //更新payment
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("expenseAccount", expenseAccount);
		long dateTime_sync = System.currentTimeMillis();
		cv.put("dateTime_sync", dateTime_sync);
		
		long row = db.insert("'Transaction'", null, cv);
		String mId = _id + "";
		try {
			long id = db.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			
			if (id > 0) {
				
			if (mDbxAcctMgr.hasLinkedAccount()) {
				
				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
				}
				
				String uuid = SyncDao.selecTransactionUUid(context, _id);
				
				TransactionTable transactionTable = new TransactionTable(mDatastore, context);
				Transaction transaction = transactionTable.getTransaction();
				
				transaction.setDateTime_sync(MEntity.getMilltoDateFormat(dateTime_sync));
				transaction.setTrans_amount(Double.valueOf(amount));
				transaction.setTrans_datetime(MEntity.getMilltoDateFormat(dateTime));
				transaction.setTrans_expenseaccount(expenseAccount);
				transaction.setUuid(uuid);
				
				transactionTable.insertRecords(transaction.getFieldsApart());
				
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
	
	public static long deleteBillPayTransaction(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		String trans_string = SyncDao.selecTransactionString(context, id);
				
		List<String> mList = selecTransactionUUidByBillRull(context, id);
		try {
			
			row = db.delete("'Transaction'", "transactionHasBillRule = ?", new String[] { _id });
			
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) {
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					for (String uuid: mList) {
						TransactionTable transactionTable = new TransactionTable(mDatastore, context);
						transactionTable.updateState(uuid,trans_string, "0");
						mDatastore.sync();
					}	
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
	
	
	public static String selectBillItemString(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from EP_BillItem a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String ep_billItemString1 = null;
		while (mCursor.moveToNext()) {
			ep_billItemString1 = mCursor.getString(16);
		}
		return ep_billItemString1;
	}

	
	public static long deleteBillVirtualObject(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { //继续删除所以的Pay交易
		SQLiteDatabase db = getConnection(context);

		long dateTime_sync = System.currentTimeMillis();
		
		ContentValues cv = new ContentValues();
		cv.put("ep_billisDelete", 1);
		cv.put("dateTime", dateTime_sync);
		
		String billitem_ep_billitemstring1 = selectBillItemString(context, id);
		String mId = id + "";
		try {
			
			long tid = db.update("EP_BillItem", cv, "_id = ?",
					new String[] { mId });
			if (tid > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
					EP_BillItem eP_BillItem = ep_BillItemTable.getEP_BillItem();
					
					eP_BillItem.setBillitem_ep_billisdelete(1);
					eP_BillItem.setBillitem_ep_billitemstring1(billitem_ep_billitemstring1);
					eP_BillItem.setDateTime(MEntity.getMilltoDateFormat(dateTime_sync));
					ep_BillItemTable.insertRecords(eP_BillItem.getFieldsApart());
					
					mDatastore.sync();
				}
					
			}
			
			
			db.close();
			return tid;
		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return 0;
		}
		
	}
	
	
	public static long deleteBillObject(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		String uuid = SyncDao.selectBillItemUUid(context, id);
		try {
			row = db.delete("EP_BillItem", "_id = ?", new String[] { _id });
			
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
					ep_BillItemTable.updateState(uuid, "0");
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	
	public static long deleteBill(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		db.execSQL("PRAGMA foreign_keys = ON ");
		
		String _id = id + "";
		long row = 0;
		String uuid = SyncDao.selectBillRuleUUid(context, id);
		try {
			
			CascadeDeleteDao.CascadedeleteBillBullRuleById(context, id, mDbxAcctMgr, mDatastore);
			
			row = db.delete("EP_BillRule", "_id = ?", new String[] { _id });
			
			if (row > 0) {
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
						EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore, context);
						ep_BillRuleTable.updateState(uuid, "0");
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
	
	public static long deleteBillObjectByParId(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		
		SQLiteDatabase db = getConnection(context);
		String _id = id + "";
		long row = 0;
		String ruleUuid = SyncDao.selectBillRuleUUid(context, id);
		try {
			row = db.delete("EP_BillItem", "billItemHasBillRule = ?", new String[] { _id });
			
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
					ep_BillItemTable.updateStateByRule(ruleUuid, "0");
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
	
	
	public static long deleteBillObjectByAfterDate(Context context, long thisDate) {
		SQLiteDatabase db = getConnection(context);
		long row = 0;
		try {
			row = db.delete("EP_BillItem", "ep_billItemDueDate >= ?", new String[] { thisDate+"" });
		} catch (Exception e) {
			// TODO: handle exception
			row = 0;
		}
		db.close();
		return row;
	}
	
	public static int selectBillRuleIdByUUid(Context context,String uuid) {
		
    	int id  = 0;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id from EP_BillRule a where a.uuid = "+"'"+uuid+"'";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			id = mCursor.getInt(0);
		}
		mCursor.close();
		db.close();

		return id;
	}
	
	public static long updateBillItemAll(Context context, int ep_billisDelete ,String ep_billItemAmount, long ep_billItemDueDate,
			long ep_billItemDueDateNew, long ep_billItemEndDate, String ep_billItemName,String ep_billItemNote, int ep_billItemRecurringType, 
			int ep_billItemReminderDate, long ep_billItemReminderTime, int billItemHasBillRule, int billItemHasCategory, int billItemHasPayee, String state, long dateTime, String uuid, String ep_billItemString1) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("ep_billItemAmount", ep_billItemAmount);
		cv.put("ep_billisDelete", ep_billisDelete);
		cv.put("ep_billItemDueDate", ep_billItemDueDate);
		cv.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
		cv.put("ep_billItemEndDate", ep_billItemEndDate);
		cv.put("ep_billItemName", ep_billItemName);
		cv.put("ep_billItemNote", ep_billItemNote);
		cv.put("ep_billItemRecurringType", ep_billItemRecurringType);
		cv.put("ep_billItemReminderDate", ep_billItemReminderDate);
		cv.put("ep_billItemReminderTime", ep_billItemReminderTime);
		cv.put("billItemHasBillRule", billItemHasBillRule);
		cv.put("billItemHasCategory", billItemHasCategory);
		cv.put("billItemHasPayee", billItemHasPayee);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		cv.put("ep_billItemString1", ep_billItemString1);
		
		long row = db.update("EP_BillItem",cv, "uuid = ?", new String[] { uuid });
		db.close();
		return row;
	}
	
	
	public static long insertBillItemAll(Context context, int ep_billisDelete ,String ep_billItemAmount, long ep_billItemDueDate,
			long ep_billItemDueDateNew, long ep_billItemEndDate, String ep_billItemName,String ep_billItemNote, int ep_billItemRecurringType, 
			int ep_billItemReminderDate, long ep_billItemReminderTime, int billItemHasBillRule, int billItemHasCategory, int billItemHasPayee, String state, long dateTime, String uuid, String ep_billItemString1) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("ep_billItemAmount", ep_billItemAmount);
		cv.put("ep_billisDelete", ep_billisDelete);
		cv.put("ep_billItemDueDate", ep_billItemDueDate);
		cv.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
		cv.put("ep_billItemEndDate", ep_billItemEndDate);
		cv.put("ep_billItemName", ep_billItemName);
		cv.put("ep_billItemNote", ep_billItemNote);
		cv.put("ep_billItemRecurringType", ep_billItemRecurringType);
		cv.put("ep_billItemReminderDate", ep_billItemReminderDate);
		cv.put("ep_billItemReminderTime", ep_billItemReminderTime);
		cv.put("billItemHasBillRule", billItemHasBillRule);
		cv.put("billItemHasCategory", billItemHasCategory);
		cv.put("billItemHasPayee", billItemHasPayee);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		cv.put("ep_billItemString1", ep_billItemString1);
		
		long row = db.insert("EP_BillItem", null, cv);
		db.close();
		return row;
	}
	
	
	
	
	public static String turnMilltoDate(long milliSeconds) {// 将毫秒转化成固定格式的年月日
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	public static long updateBillRuleAll(Context context, double ep_billAmount, long ep_billDueDate,long ep_billEndDate, String ep_billName,String ep_note, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory, int billRuleHasPayee ,long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		
		ContentValues cv = new ContentValues();
		cv.put("ep_billAmount", ep_billAmount);
		cv.put("ep_billDueDate", ep_billDueDate);
		cv.put("ep_billEndDate", ep_billEndDate);
		cv.put("ep_billName", ep_billName);
		cv.put("ep_note", ep_note);
		cv.put("ep_recurringType", ep_recurringType);
		cv.put("ep_reminderDate", ep_reminderDate);
		cv.put("ep_reminderTime", ep_reminderTime);
		cv.put("billRuleHasCategory", billRuleHasCategory);
		cv.put("billRuleHasPayee", billRuleHasPayee);
		
		cv.put("dateTime",dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		long row = db.update("EP_BillRule", cv, "uuid = ?", new String[] { uuid });
		db.close();
		return row;
	}
	
	
	public static long insertBillRuleAll(Context context, double ep_billAmount, long ep_billDueDate,long ep_billEndDate, String ep_billName,String ep_note, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory, int billRuleHasPayee ,long dateTime, String state, String uuid) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		
		cv.put("ep_billAmount", ep_billAmount);
		cv.put("ep_billDueDate", ep_billDueDate);
		cv.put("ep_billEndDate", ep_billEndDate);
		cv.put("ep_billName", ep_billName);
		cv.put("ep_note", ep_note);
		cv.put("ep_recurringType", ep_recurringType);
		cv.put("ep_reminderDate", ep_reminderDate);
		cv.put("ep_reminderTime", ep_reminderTime);
		cv.put("billRuleHasCategory", billRuleHasCategory);
		cv.put("billRuleHasPayee", billRuleHasPayee);
		
		cv.put("dateTime",dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("EP_BillRule", null, cv);
			
			db.close();
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}
	
	public static long insertBillItem(Context context, int ep_billisDelete ,String ep_billItemAmount, long ep_billItemDueDate,
			long ep_billItemDueDateNew, long ep_billItemEndDate, String ep_billItemName,String ep_billItemNote, int ep_billItemRecurringType, 
			int ep_billItemReminderDate, long ep_billItemReminderTime, int billItemHasBillRule, int billItemHasCategory, int billItemHasPayee, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		
		ContentValues cv = new ContentValues();
		long dateTime = System.currentTimeMillis();
		String state = "1";
		String uuid = MEntity.getUUID();
		
		cv.put("ep_billItemAmount", ep_billItemAmount);
		cv.put("ep_billisDelete", ep_billisDelete);
		cv.put("ep_billItemDueDate", ep_billItemDueDate);
		cv.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
		cv.put("ep_billItemEndDate", ep_billItemEndDate);
		cv.put("ep_billItemName", ep_billItemName);
		cv.put("ep_billItemNote", ep_billItemNote);
		cv.put("ep_billItemRecurringType", ep_billItemRecurringType);
		cv.put("ep_billItemReminderDate", ep_billItemReminderDate);
		cv.put("ep_billItemReminderTime", ep_billItemReminderTime);
		cv.put("billItemHasBillRule", billItemHasBillRule);
		cv.put("billItemHasCategory", billItemHasCategory);
		cv.put("billItemHasPayee", billItemHasPayee);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		String ep_billItemString1 =  SyncDao.selectBillRuleUUid(context, billItemHasBillRule)+" "+turnMilltoDate(ep_billItemDueDate);
		cv.put("ep_billItemString1", ep_billItemString1 );
		
		try {
			long row = db.insert("EP_BillItem", null, cv);
			
			if ( row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					
					EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
					EP_BillItem eP_BillItem = ep_BillItemTable.getEP_BillItem();
					
					eP_BillItem.setBillitem_ep_billisdelete(ep_billisDelete);
					eP_BillItem.setBillitem_ep_billitemamount(Double.valueOf(ep_billItemAmount));
					eP_BillItem.setBillitem_ep_billitemduedate(MEntity.getMilltoDateFormat(ep_billItemDueDate));
					eP_BillItem.setBillitem_ep_billitemduedatenew(MEntity.getMilltoDateFormat(ep_billItemDueDateNew));
					eP_BillItem.setBillitem_ep_billitemenddate(MEntity.getMilltoDateFormat(ep_billItemEndDate));
					eP_BillItem.setBillitem_ep_billitemname(ep_billItemName);
					eP_BillItem.setBillitem_ep_billitemnote(ep_billItemNote);
					eP_BillItem.setBillitem_ep_billitemrecurring(ep_billItemRecurringType);
					eP_BillItem.setBillitem_ep_billitemreminderdate(ep_billItemReminderDate);
					eP_BillItem.setBillitem_ep_billitemremindertime(ep_billItemReminderTime);
					eP_BillItem.setBillitem_ep_billitemstring1(ep_billItemString1);
					eP_BillItem.setBillitemhasbillrule(billItemHasBillRule);
					eP_BillItem.setBillitemhascategory(billItemHasCategory);
					eP_BillItem.setBillitemhaspayee(billItemHasPayee);
					eP_BillItem.setDateTime(MEntity.getMilltoDateFormat(dateTime));
					eP_BillItem.setState(state);
					eP_BillItem.setUuid(uuid);
					
					ep_BillItemTable.insertRecords(eP_BillItem.getFields());
					mDatastore.sync();
				}
				
			}
			db.close();
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}

	public static long insertBillRule(Context context, double ep_billAmount, long ep_billDueDate,long ep_billEndDate, String ep_billName,String ep_note, int ep_recurringType, int ep_reminderDate, long ep_reminderTime, int billRuleHasCategory, int billRuleHasPayee, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		long hmMills = MEntity.getHMSMill();
		
		long dateTime =  System.currentTimeMillis();
		String state = "1";
		String uuid =  MEntity.getUUID();
		
		cv.put("ep_billAmount", ep_billAmount);
		cv.put("ep_billDueDate", ep_billDueDate+hmMills);
		cv.put("ep_billEndDate", ep_billEndDate);
		cv.put("ep_billName", ep_billName);
		cv.put("ep_note", ep_note);
		cv.put("ep_recurringType", ep_recurringType);
		cv.put("ep_reminderDate", ep_reminderDate);
		cv.put("ep_reminderTime", ep_reminderTime);
		cv.put("billRuleHasCategory", billRuleHasCategory);
		cv.put("billRuleHasPayee", billRuleHasPayee);
		
		cv.put("dateTime", dateTime);
		cv.put("state", state);
		cv.put("uuid", uuid);
		
		try {
			long row = db.insert("EP_BillRule", null, cv);
			
			if (row > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { //如果连接状态开始同步 
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore, context);
					EP_BillRule	eP_BillRule = ep_BillRuleTable.getEP_BillRule();
					
					eP_BillRule.setBillrule_ep_billamount(ep_billAmount);
					eP_BillRule.setBillrule_ep_billduedate(MEntity.getMilltoDateFormat(ep_billDueDate+hmMills));
					eP_BillRule.setBillrule_ep_billenddate(MEntity.getMilltoDateFormat(ep_billEndDate));
					eP_BillRule.setBillrule_ep_billname(ep_billName);
					eP_BillRule.setBillrule_ep_note(ep_note);
					eP_BillRule.setBillrule_ep_recurringtype(ep_recurringType);
					eP_BillRule.setBillrule_ep_reminderdate(ep_reminderDate);
					eP_BillRule.setBillrule_ep_remindertime(ep_reminderTime);
					eP_BillRule.setBillrulehascategory(billRuleHasCategory);
					eP_BillRule.setBillrulehaspayee(billRuleHasPayee);
					eP_BillRule.setDateTime(MEntity.getMilltoDateFormat(dateTime));
					eP_BillRule.setState(state);
					eP_BillRule.setUuid(uuid);
					
					ep_BillRuleTable.insertRecords(eP_BillRule.getFields());
					mDatastore.sync();
					
				}
				
			}
			db.close();
			return row;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
	
	public static List<Map<String, Object>> selectBillRuleById(Context context, int id) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a._id = "+id+" and a.billRuleHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String ep_billAmount = mCursor.getString(2);
			long ep_billDueDate = mCursor.getLong(3);
			long ep_billEndDate = mCursor.getLong(4);
			String ep_billName = mCursor.getString(5);
			String ep_note = mCursor.getString(10);
			int ep_recurringType = mCursor.getInt(11);
			int ep_reminderDate = mCursor.getInt(12);
			long ep_reminderTime = mCursor.getLong(13);
			int billRuleHasCategory = mCursor.getInt(19);
			int billRuleHasPayee = mCursor.getInt(20);
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			 
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 0);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	
	public static List<Map<String, Object>> selectOrdinaryBillRuleById(Context context, int id) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a._id = "+id+" and a.ep_recurringType = 0 and a.billRuleHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String ep_billAmount = mCursor.getString(2);
			long ep_billDueDate = mCursor.getLong(3);
			long ep_billEndDate = mCursor.getLong(4);
			String ep_billName = mCursor.getString(5);
			String ep_note = mCursor.getString(10);
			int ep_recurringType = mCursor.getInt(11);
			int ep_reminderDate = mCursor.getInt(12);
			long ep_reminderTime = mCursor.getLong(13);
			int billRuleHasCategory = mCursor.getInt(19);
			int billRuleHasPayee = mCursor.getInt(20);
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			 
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 0);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	
	public static List<Map<String, Object>> selectOrdinaryBillRuleByBE(Context context, long beginTime, long endTime) { // Bill普通事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*,Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a.ep_billDueDate >= "+ beginTime+ " and a.ep_billDueDate <="+(endTime+DAYMILLIS)+ " and a.ep_recurringType = 0 and a.billRuleHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String ep_billAmount = mCursor.getString(2);
			long ep_billDueDate = mCursor.getLong(3);
			long ep_billEndDate = mCursor.getLong(4);
			String ep_billName = mCursor.getString(5);
			String ep_note = mCursor.getString(10);
			int ep_recurringType = mCursor.getInt(11);
			int ep_reminderDate = mCursor.getInt(12);
			long ep_reminderTime = mCursor.getLong(13);
			int billRuleHasCategory = mCursor.getInt(19);
			int billRuleHasPayee = mCursor.getInt(20);
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 0);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		
		return mList;
	}
	
	public static List<Map<String, Object>> selectTemplateBillRuleByBE(Context context) { // Bill模板事件
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillRule a left join Payee on a.billRuleHasPayee = Payee._id ,Category b where a.ep_recurringType > 0 and a.billRuleHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String ep_billAmount = mCursor.getString(2);
			long ep_billDueDate = mCursor.getLong(3);
			long ep_billEndDate = mCursor.getLong(4);
			String ep_billName = mCursor.getString(5);
			String ep_note = mCursor.getString(10);
			int ep_recurringType = mCursor.getInt(11);
			int ep_reminderDate = mCursor.getInt(12);
			long ep_reminderTime = mCursor.getLong(13);
			int billRuleHasCategory = mCursor.getInt(19);
			int billRuleHasPayee = mCursor.getInt(20);
			String categoryName = mCursor.getString(25);
			int iconName =  mCursor.getInt(31); 
			String payee_nameString = mCursor.getString(49);
			
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("indexflag", 1);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	public static List<Map<String, Object>> selectBillItemByBE(Context context, long beginTime, long endTime) { // Bill特例事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillItem a left join Payee on a.billItemHasPayee = Payee._id ,Category b where a.ep_billItemDueDate >= "+ beginTime+ " and a.ep_billItemDueDate <=" + (endTime+DAYMILLIS) + " and a.billItemHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int ep_billisDelete = mCursor.getInt(2);
			String ep_billAmount = mCursor.getString(3);
			long ep_billDueDate = mCursor.getLong(8);
			long ep_billItemDueDateNew = mCursor.getLong(9);
			long ep_billEndDate = mCursor.getLong(10);
			String ep_billName = mCursor.getString(11);
			String ep_note = mCursor.getString(12);
			int ep_recurringType = mCursor.getInt(13);
			int ep_reminderDate = mCursor.getInt(14);
			long ep_reminderTime = mCursor.getLong(15);
			int billItemHasBillRule = mCursor.getInt(20);
			int billRuleHasCategory = mCursor.getInt(21);
			int billRuleHasPayee = mCursor.getInt(22);
			String categoryName = mCursor.getString(27);
			int iconName =  mCursor.getInt(33); 
			String payee_nameString = mCursor.getString(51);
			
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("ep_billisDelete", ep_billisDelete);
			mMap.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
			mMap.put("billItemHasBillRule", billItemHasBillRule);
			mMap.put("indexflag", 3);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	public static List<Map<String, Object>> selectBillItemByRuleId(Context context, int id) { // Bill特例事件查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.*, b.*, Payee.* from EP_BillItem a left join Payee on a.billItemHasPayee = Payee._id ,Category b where a.billItemHasBillRule = "+ id+ " and a.billItemHasCategory = b._id ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			int ep_billisDelete = mCursor.getInt(2);
			String ep_billAmount = mCursor.getString(3);
			long ep_billDueDate = mCursor.getLong(8);
			long ep_billItemDueDateNew = mCursor.getLong(9);
			long ep_billEndDate = mCursor.getLong(10);
			String ep_billName = mCursor.getString(11);
			String ep_note = mCursor.getString(12);
			int ep_recurringType = mCursor.getInt(13);
			int ep_reminderDate = mCursor.getInt(14);
			long ep_reminderTime = mCursor.getLong(15);
			int billItemHasBillRule = mCursor.getInt(20);
			int billRuleHasCategory = mCursor.getInt(21);
			int billRuleHasPayee = mCursor.getInt(22);
			String categoryName = mCursor.getString(27);
			int iconName =  mCursor.getInt(33); 
			String payee_nameString = mCursor.getString(51);
			
			mMap.put("_id", _id);
			mMap.put("ep_billAmount", ep_billAmount);
			mMap.put("ep_billDueDate", ep_billDueDate);
			mMap.put("ep_billEndDate", ep_billEndDate);
			mMap.put("ep_billName", ep_billName);
			mMap.put("ep_note", ep_note);
			mMap.put("ep_recurringType", ep_recurringType);
			mMap.put("ep_reminderDate", ep_reminderDate);
			mMap.put("ep_reminderTime", ep_reminderTime);
			mMap.put("billRuleHasCategory", billRuleHasCategory);
			mMap.put("billRuleHasPayee", billRuleHasPayee);
			mMap.put("categoryName", categoryName);
			mMap.put("iconName", iconName);
			mMap.put("payee_nameString", payee_nameString);
			mMap.put("ep_billisDelete", ep_billisDelete);
			mMap.put("ep_billItemDueDateNew", ep_billItemDueDateNew);
			mMap.put("billItemHasBillRule", billItemHasBillRule);
			mMap.put("indexflag", 3);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();
		return mList;
	}
	
	
	public static List<Map<String, Object>> selectTransactionByBillRuleId(Context context, int id) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, b.accName, b.iconName  from 'Transaction' a, Accounts b where a.transactionHasBillRule = "+ id +" and a.expenseAccount = b._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
            String accName = mCursor.getString(4);
            int iconName = mCursor.getInt(5);
            
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("accName", accName);
			mMap.put("iconName", iconName);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectTransactionByBillItemId(Context context, int id) { 
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.amount, a.dateTime, a.expenseAccount, b.accName, b.iconName  from 'Transaction' a, Accounts b where a.transactionHasBillItem = "+ id +" and a.expenseAccount = b._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();

			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int expenseAccount = mCursor.getInt(3);
            String accName = mCursor.getString(4);
            int iconName = mCursor.getInt(5);
            
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("accName", accName);
			mMap.put("iconName", iconName);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	/*
	 * 普通Bill的pay
	 */
	
	public static long insertTransactionRule(Context context, String amount, long dateTime,int expenseAccount, int transactionHasBillRule, int category, int payee,int cleared, int recurring) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("expenseAccount", expenseAccount);
		cv.put("incomeAccount", 0);
		cv.put("transactionHasBillRule", transactionHasBillRule);
		cv.put("category", category);
		cv.put("payee", payee);
		cv.put("isClear", cleared);
		
		cv.put("recurringType", 0);
		cv.put("parTransaction", 0);
		cv.put("childTransactions", 0);
		
		cv.put("dateTime_sync", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("uuid", MEntity.getUUID());
		
		long row = db.insert("'Transaction'", null, cv);
		db.close();
		return row;
			}
	
	
	/*
	 * Bill重复特例的pay
	 */
	
	public static long insertTransactionItem(Context context, String amount, long dateTime,int expenseAccount, int transactionHasBillItem, int category,int payee,int cleared, int recurring) {
		SQLiteDatabase db = getConnection(context);
		ContentValues cv = new ContentValues();
		cv.put("amount", amount);
		cv.put("dateTime", dateTime);
		cv.put("expenseAccount", expenseAccount);
		cv.put("incomeAccount", 0);
		cv.put("transactionHasBillItem", transactionHasBillItem);
		cv.put("category", category);
		cv.put("payee", payee);
		cv.put("isClear", cleared);
		cv.put("recurringType", 0);
		cv.put("parTransaction", 0);
		cv.put("childTransactions", 0);
		
		cv.put("dateTime_sync", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("uuid", MEntity.getUUID());
		
		long row = db.insert("'Transaction'", null, cv);
		db.close();
		return row;
	}
	
	
}
