package com.appxy.pocketexpensepro.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.appxy.pocketexpensepro.table.AccountTypeTable;
import com.appxy.pocketexpensepro.table.AccountsTable;
import com.appxy.pocketexpensepro.table.BudgetItemTable;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable;
import com.appxy.pocketexpensepro.table.BudgetTransferTable;
import com.appxy.pocketexpensepro.table.CategoryTable;
import com.appxy.pocketexpensepro.table.EP_BillItemTable;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.AccountTypeTable.AccountType;
import com.appxy.pocketexpensepro.table.CategoryTable.Category;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.CommonDataKinds.Im;

public class CascadeDeleteDao {

	public static SQLiteDatabase getConnection(Context context) {

		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}

	/*
	 * 级联删除，同步删除AccountType
	 */

	public static void CascadedeleteAccountTypeByID(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {

		try {

			if (mDbxAcctMgr.hasLinkedAccount()) { // 如果连接状态开始同步

				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}

				List<Map<String, Object>> accontList = selecAccountByType(
						context, id);

				for (Map<String, Object> iMap : accontList) {

					int a_id = (Integer) iMap.get("_id");
					String a_uuid = (String) iMap.get("uuid");
					AccountsTable accountsTable = new AccountsTable(mDatastore,
							context);
					accountsTable.updateState(a_uuid, "0");

					List<Map<String, Object>> transList = selecTransactionByAccount(
							context, a_id);
					for (Map<String, Object> tMap : transList) {

						String t_uuid = (String) tMap.get("uuid");
						String transactionstring = (String) tMap
								.get("transactionstring");

						TransactionTable transactionTable = new TransactionTable(
								mDatastore, context);
						transactionTable.updateState(t_uuid, transactionstring,
								"0");
					}

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	/*
	 * 级联删除，同步删除Account
	 */

	public static void CascadedeleteTransByAccount(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {

		try {

			if (mDbxAcctMgr.hasLinkedAccount()) { // 如果连接状态开始同步

				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}

					List<Map<String, Object>> transList = selecTransactionByAccount(context, id);
					for (Map<String, Object> tMap : transList) {

						String t_uuid = (String) tMap.get("uuid");
						String transactionstring = (String) tMap
								.get("transactionstring");

						TransactionTable transactionTable = new TransactionTable(
								mDatastore, context);
						transactionTable.updateState(t_uuid, transactionstring,
								"0");
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	/*
	 * 级联删除，同步删除Category
	 */

	
	public static void CascadedeleteCategoryById(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {

		try {

			if (mDbxAcctMgr.hasLinkedAccount()) { // 如果连接状态开始同步

				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}

					List<Map<String, Object>> transList = selecTransactionByCategory(context, id);
					for (Map<String, Object> tMap : transList) {

						String t_uuid = (String) tMap.get("uuid");
						String transactionstring = (String) tMap
								.get("transactionstring");

						TransactionTable transactionTable = new TransactionTable(
								mDatastore, context);
						transactionTable.updateState(t_uuid, transactionstring,
								"0");
				  }
					
					
					List<Map<String, Object>> budgetTemplateList = selecBudgetTemplateByCategory(context, id);
					
					for (Map<String, Object> iMap: budgetTemplateList) {
						
						int tem_id = (Integer) iMap.get("_id");
						String temUuid = (String)iMap.get("uuid");
						BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, context);
						budgetTemplateTable.updateState(temUuid, "0");
						
						List<Map<String, Object>> budgetItemList = selecBudgetItemByTemplate(context, tem_id);
						for (Map<String, Object> tMap:budgetItemList) {
							
							int itm_id = (Integer) tMap.get("_id");
							String itmUuid = (String)tMap.get("uuid");
							BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, context);
							budgetItemTable.updateState(itmUuid, "0");
							
							List<Map<String, Object>> budgetTransferList = selecBudgetTransferByItem(context, itm_id);
							for (Map<String, Object> sMap:budgetTransferList) {
								
								int s_id = (Integer) sMap.get("_id");
								String sUuid = (String)sMap.get("uuid");
								BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore, context);
								budgetTransferTable.updateState(sUuid, "0");
							}
							
						}
						
					}
					
					List<Map<String, Object>> billRuleList = selecEP_BillRuleByCategory(context, id);
					for (Map<String, Object> iMap:billRuleList) {
						
						int b_id = (Integer) iMap.get("_id");
						String bUuid = (String)iMap.get("uuid");
						
						EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore, context);
						ep_BillRuleTable.updateState(bUuid, "0");
						
						List<Map<String, Object>> transByRuleList = selecTransactionByBillRule(context, b_id);
						for (Map<String, Object> tMap : transByRuleList) {

							String t_uuid = (String) tMap.get("uuid");
							String transactionstring = (String) tMap
									.get("transactionstring");

							TransactionTable transactionTable = new TransactionTable(
									mDatastore, context);
							transactionTable.updateState(t_uuid, transactionstring,
									"0");
					  }
						List<Map<String, Object>> billItemList = selecEP_BillItemByRule(context, b_id);
						for (Map<String, Object> bMap: billItemList) {
							
							int i_id = (Integer) bMap.get("_id");
							String iUuid = (String)bMap.get("uuid");
							
							EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
							ep_BillItemTable.updateStateByRule(iUuid, "0");
							
							List<Map<String, Object>> transByItemList = selecTransactionByBillItem(context, i_id);
							for (Map<String, Object> tMap : transByItemList) {

								String t_uuid = (String) tMap.get("uuid");
								String transactionstring = (String) tMap
										.get("transactionstring");

								TransactionTable transactionTable = new TransactionTable(
										mDatastore, context);
								transactionTable.updateState(t_uuid, transactionstring,
										"0");
						  }
						}
						
					}
					
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	/*
	 * 级联删除，同步删除BillBullRule
	 */

	public static void CascadedeleteBillBullRuleById(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {

		try {

			if (mDbxAcctMgr.hasLinkedAccount()) { // 如果连接状态开始同步

				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}
				
				List<Map<String, Object>> transByRuleList = selecTransactionByBillRule(context,id);
				for (Map<String, Object> tMap : transByRuleList) {

					String t_uuid = (String) tMap.get("uuid");
					String transactionstring = (String) tMap
							.get("transactionstring");

					TransactionTable transactionTable = new TransactionTable(
							mDatastore, context);
					transactionTable.updateState(t_uuid, transactionstring,
							"0");
			  }
				
				List<Map<String, Object>> billItemList = selecEP_BillItemByRule(context, id);
				for (Map<String, Object> bMap: billItemList) {
					
					int i_id = (Integer) bMap.get("_id");
					String iUuid = (String)bMap.get("uuid");
					
					EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
					ep_BillItemTable.updateStateByRule(iUuid, "0");
					
					List<Map<String, Object>> transByItemList = selecTransactionByBillItem(context, i_id);
					for (Map<String, Object> tMap : transByRuleList) {

						String t_uuid = (String) tMap.get("uuid");
						String transactionstring = (String) tMap
								.get("transactionstring");

						TransactionTable transactionTable = new TransactionTable(
								mDatastore, context);
						transactionTable.updateState(t_uuid, transactionstring,
								"0");
				  }
				}


			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	
	/*
	 * 级联删除，同步删除BillBullItem
	 */

	public static void CascadedeleteBillBullItemById(Context context, int id, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {

		try {

			if (mDbxAcctMgr.hasLinkedAccount()) { // 如果连接状态开始同步

				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}
				
				List<Map<String, Object>> transByItemList = selecTransactionByBillItem(context, id);
				for (Map<String, Object> tMap : transByItemList) {

					String t_uuid = (String) tMap.get("uuid");
					String transactionstring = (String) tMap
							.get("transactionstring");

					TransactionTable transactionTable = new TransactionTable(
							mDatastore, context);
					transactionTable.updateState(t_uuid, transactionstring,
							"0");
			  }
				
				
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public static List<Map<String, Object>> selecTransactionByCategory(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime_sync,a.transactionstring from 'Transaction' a where a.category = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);
			String transactionstring = mCursor.getString(3);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("transactionstring", transactionstring);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecTransactionByAccount(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime_sync,a.transactionstring from 'Transaction' a where a.expenseAccount = "
				+ id + " or a.incomeAccount = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);
			String transactionstring = mCursor.getString(3);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("transactionstring", transactionstring);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecTransactionByBillRule(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime_sync,a.transactionstring from 'Transaction' a where a.transactionHasBillRule = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);
			String transactionstring = mCursor.getString(3);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("transactionstring", transactionstring);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecTransactionByBillItem(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime_sync,a.transactionstring from 'Transaction' a where a.transactionHasBillItem = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);
			String transactionstring = mCursor.getString(3);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("transactionstring", transactionstring);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecAccountByType(Context context,
			int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime_sync from Accounts a where a.accountType = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecBudgetTemplateByCategory(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime from BudgetTemplate a where a.category = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecBudgetItemByTemplate(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime from BudgetItem a where a.budgetTemplate = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecBudgetTransferByItem(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime_sync from BudgetTransfer a where a.fromBudget = "
				+ id + " or a.toBudget = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecEP_BillRuleByCategory(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime from EP_BillRule a where a.billRuleHasCategory = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);

			mList.add(mMap);
		}

		return mList;

	}

	public static List<Map<String, Object>> selecEP_BillItemByRule(
			Context context, int id) { // 查询所有的
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

		SQLiteDatabase db = getConnection(context);
		String sql = "select a._id, a.uuid, a.dateTime from EP_BillItem a where a.billItemHasBillRule = "
				+ id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			Map<String, Object> mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String uuid = mCursor.getString(1);
			long dateTime_sync = mCursor.getLong(2);

			mMap.put("_id", _id);
			mMap.put("uuid", uuid);
			mMap.put("dateTime_sync", dateTime_sync);

			mList.add(mMap);
		}

		return mList;

	}

}
