package com.appxy.pocketexpensepro.setting.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.Common;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SyncDao {

	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		return db;
	}

	public static List<Map<String, Object>> selectAccount(Context context) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Accounts a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String name = mCursor.getString(1);
			String state = mCursor.getString(14);
			long datetime = mCursor.getLong(6);
			int autoclear = mCursor.getInt(3);
			double amount = mCursor.getDouble(2);
			String accountType = mCursor.getString(16);
			int orderindex = mCursor.getInt(10);
			long dateTime_sync = mCursor.getLong(7);
			String uuid = mCursor.getString(15);

			mMap.put("name", name);
			mMap.put("state", state);
			mMap.put("datetime", datetime);
			mMap.put("autoclear", autoclear);
			mMap.put("amount", amount);
			mMap.put("accountType", accountType);
			mMap.put("orderindex", orderindex);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("uuid", uuid);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectAccountType(Context context) { // AccountType查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from AccountType a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String accounttype_typename = mCursor.getString(7);
			String accounttype_iconname = mCursor.getString(2);
			long datetime = mCursor.getLong(1);
			String state = mCursor.getString(6);
			int accounttype_isdefault = mCursor.getInt(3);
			String uuid = mCursor.getString(8);

			mMap.put("accounttype_typename", accounttype_typename);
			mMap.put("accounttype_iconname", accounttype_iconname);
			mMap.put("datetime", datetime);
			mMap.put("state", state);
			mMap.put("accounttype_isdefault", accounttype_isdefault);
			mMap.put("uuid", uuid);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectBudgetItem(Context context) { // BudgetItem查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from BudgetItem a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String uuid = mCursor.getString(10);
			long budgetitem_startdate = mCursor.getLong(8);
			double budgetitem_amount = mCursor.getDouble(1);
			long budgetitem_enddate = mCursor.getLong(3);

			double budgetitem_rolloveramount = mCursor.getDouble(7);
			long dateTime = mCursor.getLong(2);
			String state = mCursor.getString(9);
			String budgetitem_budgettemplate = mCursor.getString(11);
			int budgetitem_isrollover = mCursor.getInt(5);

			mMap.put("uuid", uuid);
			mMap.put("budgetitem_startdate", budgetitem_startdate);
			mMap.put("budgetitem_amount", budgetitem_amount);
			mMap.put("budgetitem_enddate", budgetitem_enddate);

			mMap.put("budgetitem_rolloveramount", budgetitem_rolloveramount);
			mMap.put("dateTime", dateTime);
			mMap.put("state", state);
			mMap.put("budgetitem_budgettemplate", budgetitem_budgettemplate);
			mMap.put("budgetitem_isrollover", budgetitem_isrollover);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static String selectBudgetTemplateUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from BudgetTemplate a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = "";
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(10);
		}
		mCursor.close();
		db.close();
		return uuid;
	}

	public static List<Map<String, Object>> selectBudgetTemplate(Context context) { // BudgetItem查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from BudgetTemplate a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String uuid = mCursor.getString(10);
			String budgettemplate_category = mCursor.getString(13);
			int budgettemplate_isrollover = mCursor.getInt(5);
			int budgettemplate_isnew = mCursor.getInt(4);

			long datetime = mCursor.getLong(3);
			String state = mCursor.getString(9);
			long budgettemplate_startdate = mCursor.getLong(7);
			double budgettemplate_amount = mCursor.getDouble(1);
			String budgettemplate_cycletype = mCursor.getString(2);

			mMap.put("uuid", uuid);
			mMap.put("budgettemplate_category", budgettemplate_category);
			mMap.put("budgettemplate_isrollover", budgettemplate_isrollover);
			mMap.put("budgettemplate_isnew", budgettemplate_isnew);
			mMap.put("datetime", datetime);
			mMap.put("state", state);
			mMap.put("budgettemplate_startdate", budgettemplate_startdate);
			mMap.put("budgettemplate_amount", budgettemplate_amount);
			mMap.put("budgettemplate_cycletype", budgettemplate_cycletype);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static String selectCategoryUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Category a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(15);
		}
		mCursor.close();
		db.close();
		return uuid;
	}

	public static List<Map<String, Object>> selectBudgetTransfer(Context context) { // BudgetTransfer查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from BudgetTransfer a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String uuid = mCursor.getString(5);
			long budgettransfer_datetime = mCursor.getLong(2);
			String budget_frombudget = mCursor.getString(6);
			String state = mCursor.getString(4);
			long dateTime_sync = mCursor.getLong(3);
			double budgettransfer_amount = mCursor.getDouble(1);
			String budget_tobudget = mCursor.getString(7);

			mMap.put("uuid", uuid);
			mMap.put("budgettransfer_datetime", budgettransfer_datetime);
			mMap.put("budget_frombudget", budget_frombudget);
			mMap.put("state", state);
			mMap.put("dateTime_sync", dateTime_sync);
			mMap.put("budgettransfer_amount", budgettransfer_amount);
			mMap.put("budget_tobudget", budget_tobudget);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static String selectBudgetTransferUUidById(Context context, int id) { // BudgetTransfer查询
		
		String uuid = null;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.uuid from BudgetTransfer a where a._id = "+id;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {

			uuid = mCursor.getString(0);

		}
		mCursor.close();
		db.close();

		return uuid;
	}

	public static String selectBudgetItemUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from BudgetItem a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = "";
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(10);
		}
		mCursor.close();
		db.close();
		return uuid;
	}

	public static List<Map<String, Object>> selectCategory(Context context) { // BudgetTransfer查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Category a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String category_iconname = mCursor.getString(9);
			String uuid = mCursor.getString(15);
			int category_isdefault = mCursor.getInt(10);
			String category_categoryname = mCursor.getString(3);
			long dateTime = mCursor.getLong(7);
			String state = mCursor.getString(14);
			int category_issystemrecord = mCursor.getInt(11);
			String category_categorytype = mCursor.getString(5);

			mMap.put("category_iconname", category_iconname);
			mMap.put("uuid", uuid);
			mMap.put("category_isdefault", category_isdefault);
			mMap.put("category_categoryname", category_categoryname);
			mMap.put("dateTime", dateTime);
			mMap.put("state", state);
			mMap.put("category_issystemrecord", category_issystemrecord);
			mMap.put("category_categorytype", category_categorytype);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectEP_BillItem(Context context) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from EP_BillItem a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String billitemhasbillrule = mCursor.getString(20);
			String billitem_ep_billitemstring1 = mCursor.getString(16); // 该字段为父本的UUid加时间
			String uuid = mCursor.getString(19);
			String billitemhascategory = mCursor.getString(21);

			long billitem_ep_billitemduedate = mCursor.getLong(8); // 需要和billitem_ep_billitemduedatenew互换
			double billitem_ep_billitemamount = mCursor.getDouble(3);
			String billitem_ep_billitemname = mCursor.getString(11);
			String billitemhaspayee = mCursor.getString(22);

			long dateTime = mCursor.getLong(1);
			long billitem_ep_billitemenddate = mCursor.getLong(10);
			String state = mCursor.getString(18);

			String billitem_ep_billitemreminderdate = mCursor.getString(14); // 该时间需要转换
																				// 计算并合并两个时间！
																				// 下载的时候再逆运算
			String billitem_ep_billitemremindertime = mCursor.getString(15);

			String billitem_ep_billitemrecurring = mCursor.getString(13);
			long billitem_ep_billitemduedatenew = mCursor.getLong(9); // 需要互换
			String billitem_ep_billisdelete = mCursor.getString(2); // 我设置的为012，对面只有0表示在循环中删除，1表示正常存在
			String billitem_ep_billitemnote = mCursor.getString(12);

			mMap.put("billitemhasbillrule", billitemhasbillrule);
			mMap.put("billitem_ep_billitemstring1", billitem_ep_billitemstring1);
			mMap.put("uuid", uuid);
			mMap.put("billitemhascategory", billitemhascategory);

			mMap.put("billitem_ep_billitemduedate", billitem_ep_billitemduedate);
			mMap.put("billitem_ep_billitemamount", billitem_ep_billitemamount);
			mMap.put("billitem_ep_billitemname", billitem_ep_billitemname);
			mMap.put("billitemhaspayee", billitemhaspayee);

			mMap.put("dateTime", dateTime);
			mMap.put("billitem_ep_billitemenddate", billitem_ep_billitemenddate);
			mMap.put("state", state);
			mMap.put("billitem_ep_billitemreminderdate",
					billitem_ep_billitemreminderdate);

			mMap.put("billitem_ep_billitemremindertime",
					billitem_ep_billitemremindertime);
			mMap.put("billitem_ep_billitemrecurring",
					billitem_ep_billitemrecurring);
			mMap.put("billitem_ep_billitemduedatenew",
					billitem_ep_billitemduedatenew);
			mMap.put("billitem_ep_billisdelete", billitem_ep_billisdelete);
			mMap.put("billitem_ep_billitemnote", billitem_ep_billitemnote);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static String selectBillRuleUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from EP_BillRule a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(17);
		}
		mCursor.close();
		db.close();
		return uuid;
	}
	
	public static String selectBillItemUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from EP_BillItem a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(19);
		}
		mCursor.close();
		db.close();
		return uuid;
	}

	public static String selectPayeeUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Payee a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(12);
		}
		mCursor.close();
		db.close();
		
		return uuid;
	}

	public static List<Map<String, Object>> selectEP_BillRule(Context context) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from EP_BillRule a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String uuid = mCursor.getString(17);
			double billrule_ep_billamount = mCursor.getDouble(2);
			String billrulehascategory = mCursor.getString(19);
			String billrulehaspayee = mCursor.getString(20);

			long dateTime = mCursor.getLong(1);
			String state = mCursor.getString(16);
			String billrule_ep_recurringtype = mCursor.getString(11);
			String billrule_ep_reminderdate = mCursor.getString(12);

			String billrule_ep_billname = mCursor.getString(5);
			long billrule_ep_billduedate = mCursor.getLong(3);
			String billrule_ep_note = mCursor.getString(10);
			long billrule_ep_billenddate = mCursor.getLong(4);
			long billrule_ep_remindertime = mCursor.getLong(13);

			mMap.put("uuid", uuid);
			mMap.put("billrule_ep_billamount", billrule_ep_billamount);
			mMap.put("billrulehascategory", billrulehascategory);
			mMap.put("billrulehaspayee", billrulehaspayee);

			mMap.put("dateTime", dateTime);
			mMap.put("state", state);
			mMap.put("billrule_ep_recurringtype", billrule_ep_recurringtype);
			mMap.put("billrule_ep_reminderdate", billrule_ep_reminderdate);

			mMap.put("billrule_ep_billname", billrule_ep_billname);
			mMap.put("billrule_ep_billduedate", billrule_ep_billduedate);
			mMap.put("billrule_ep_note", billrule_ep_note);
			mMap.put("billrule_ep_billenddate", billrule_ep_billenddate);
			mMap.put("billrule_ep_remindertime", billrule_ep_remindertime);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectPayee(Context context) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Payee a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String uuid = mCursor.getString(12);
			long dateTime = mCursor.getLong(1);
			String state = mCursor.getString(7);
			String payee_category = mCursor.getString(15);

			String payee_name = mCursor.getString(3);
			String payee_memo = mCursor.getString(2);

			mMap.put("uuid", uuid);
			mMap.put("dateTime", dateTime);
			mMap.put("state", state);
			mMap.put("payee_category", payee_category);
			mMap.put("payee_name", payee_name);
			mMap.put("payee_memo", payee_memo);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static List<Map<String, Object>> selectTransaction(Context context) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			String trans_expenseaccount = mCursor.getString(20);
			String trans_incomeaccount = mCursor.getString(21);
			double trans_amount = mCursor.getDouble(1);
			String trans_notes = mCursor.getString(6);

			String trans_payee = mCursor.getString(23);
			String trans_string = mCursor.getString(13);
			String trans_category = mCursor.getString(18);
			long dateTime_sync = mCursor.getLong(3);

			String state = mCursor.getString(11);
			String trans_recurringtype = mCursor.getString(10);
			long trans_datetime = mCursor.getLong(2);
			int trans_isclear = mCursor.getInt(5);

			String uuid = mCursor.getString(16);
			String trans_partransaction = mCursor.getString(22);
			String trans_billitem = mCursor.getString(24);
			String trans_billrule = mCursor.getString(25);
			
			mMap.put("trans_expenseaccount", trans_expenseaccount);
			mMap.put("trans_incomeaccount", trans_incomeaccount);
			mMap.put("trans_amount", trans_amount);
			mMap.put("trans_notes", trans_notes);

			mMap.put("trans_payee", trans_payee);
			mMap.put("trans_string", trans_string);
			mMap.put("trans_category", trans_category);
			mMap.put("dateTime_sync", dateTime_sync);

			mMap.put("state", state);
			mMap.put("trans_recurringtype", trans_recurringtype);
			mMap.put("trans_datetime", trans_datetime);
			mMap.put("trans_isclear", trans_isclear);

			mMap.put("uuid", uuid);
			mMap.put("trans_partransaction", trans_partransaction);
			mMap.put("trans_billitem", trans_billitem);
			mMap.put("trans_billrule", trans_billrule);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	public static String selecAccountsUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from Accounts a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(15);
		}
		mCursor.close();
		db.close();
		
		return uuid;
	}

	public static String selecTransactionUUid(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String uuid = null;
		while (mCursor.moveToNext()) {
			uuid = mCursor.getString(16);
		}
		mCursor.close();
		db.close();
		
		return uuid;
	}
	
	public static String selecTransactionString(Context context, int id) {
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.transactionstring from 'Transaction' a where a._id = " + id;
		Cursor mCursor = db.rawQuery(sql, null);
		String transactionstring = null;
		while (mCursor.moveToNext()) {
			transactionstring = mCursor.getString(0);
		}
		mCursor.close();
		db.close();
		
		return transactionstring;
	}
}
