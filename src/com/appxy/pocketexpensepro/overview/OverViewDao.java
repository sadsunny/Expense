package com.appxy.pocketexpensepro.overview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appxy.pocketexpensepro.R.id;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class OverViewDao {

	private final static long DAYMILLIS = 86400000L - 1L;
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}

	public static List<Map<String, Object>> selectTransactionByTime(
			Context context, long time) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a.dateTime >= "+ time +" and a.dateTime <  "+(time+DAYMILLIS)+ " and a.childTransactions != 1 order by a.dateTime DESC , a._id DESC ";
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
			String uuid =  mCursor.getString(16);
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("uuid", uuid);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static  ArrayList<HashMap<String, Object>> selectTransactionByDayTime(
			Context context, long time) { // Account查询
		 ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		 HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* , Payee.name, Category.iconName, Category._id from 'Transaction' a left join Payee on a.payee = Payee._id left join Category on a.category = Category._id where a.dateTime >= "+ time +" and a.dateTime <  "+(time+DAYMILLIS)+ " and a.childTransactions != 1 order by a.dateTime DESC , a._id DESC ";
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
			String uuid =  mCursor.getString(16); 
			String payeeName = mCursor.getString(27); 
			int iconName = mCursor.getInt(28);                                                                               
			int categoryId = mCursor.getInt(29);    
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("uuid", uuid);
			mMap.put("payeeName", payeeName);
			mMap.put("iconName", iconName);
			mMap.put("categoryId", categoryId);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static  ArrayList<HashMap<String, Object>> selectTransactionByAccountId(
			Context context, int accountId) { // Account查询
		 ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		 HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* , Payee.name, Category.iconName, Category._id from 'Transaction' a left join Payee on a.payee = Payee._id left join Category on a.category = Category._id where (a.expenseAccount = " + accountId + " or a.incomeAccount = "+ accountId + ") and a.childTransactions != 1 order by a.dateTime DESC , a._id DESC ";
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
			String uuid =  mCursor.getString(16); 
			String payeeName = mCursor.getString(27); 
			int iconName = mCursor.getInt(28);                                                                               
			int categoryId = mCursor.getInt(29);    
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("uuid", uuid);
			mMap.put("payeeName", payeeName);
			mMap.put("iconName", iconName);
			mMap.put("categoryId", categoryId);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static  ArrayList<HashMap<String, Object>> selectTransactionByCategoryIdJoin(
			Context context, int categoryId) { // Account查询
		 ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		 HashMap<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* , Payee.name, b.iconName, b._id from 'Transaction' a left join Payee on a.payee = Payee._id ,Category b where a.category = b._id and a.category = "+categoryId+" and a.childTransactions != 1 order by a.dateTime DESC , a._id DESC ";
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
			String uuid =  mCursor.getString(16); 
			String payeeName = mCursor.getString(27); 
			int iconName = mCursor.getInt(28);                                                                               
			int mCategoryId = mCursor.getInt(29);    
			
			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("uuid", uuid);
			mMap.put("payeeName", payeeName);
			mMap.put("iconName", iconName);
			mMap.put("categoryId", mCategoryId);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	

	public static List<Map<String, Object>> selectTransactionByTimeBE(
			Context context, long beginTime, long endTime) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and a.parTransaction != -1 and  (a.expenseAccount <= 0 or a.incomeAccount <= 0) order by a.dateTime DESC , a._id DESC ";
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
			mMap.put("notes", notes);
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

	public static List<Map<String, Object>> selectTransactionByTimeBEWithCategory(
			Context context, long beginTime, long endTime) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a , Category b where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and a.parTransaction != -1 and a.category = b._id order by a.dateTime DESC , a._id DESC ";
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
			int categoryType = mCursor.getInt(32);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("categoryType", categoryType);
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
	
	public static List<Map<String, Object>> selectSumCategory(
			Context context, long beginTime, long endTime, int type) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select b.categoryType ,b.categoryName, sum(a.amount) from 'Transaction' a , Category b where b.categoryType = "+type+"  and a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS) + " and a.parTransaction != -1 and a.category = b._id and (a.expenseAccount <= 0  or a.incomeAccount <= 0)  group by b._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			int categoryType = mCursor.getInt(0);
			String categoryName = mCursor.getString(1);
			double sum = mCursor.getDouble(2);
			
			mMap.put("categoryType", categoryType);
			mMap.put("categoryName", categoryName);
			mMap.put("sum", sum);

			mList.add(mMap);
		}
		
		
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static List<Map<String, Object>> selectBudget(Context context) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetItem a, BudgetTemplate b,Category c where a.budgetTemplate = b._id and  b.category = c._id order by c.categoryName ASC ";
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
	public static List<Map<String, Object>> selectBudgetTransferById(Context context , int id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetTransfer a where a._id = "+id+" ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int fromBudget = mCursor.getInt(6);
			int toBudget = mCursor.getInt(7);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("fromBudget", fromBudget);
			mMap.put("toBudget", toBudget);
			mMap.put("payeeName", "--");
			mMap.put("index", 1);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectBudgetCategoryNameByItem(Context context , int itemId) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.categoryName from Category a,BudgetTemplate b, BudgetItem c where a._id = b.category and b._id = c.budgetTemplate and c._id = "+itemId;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();
			
			String categoryName = mCursor.getString(0);
			mMap.put("categoryName", categoryName);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}


	public static List<Map<String, Object>> selectBudgetTransferByTimeItem(Context context , long beginTime , long endTime, int item_id) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetTransfer a where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and (a.fromBudget = "+item_id+" or a.toBudget  =  " + item_id +")";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int fromBudget = mCursor.getInt(6);
			int toBudget = mCursor.getInt(7);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("fromBudget", fromBudget);
			mMap.put("toBudget", toBudget);
		
			mMap.put("index", 1);
			
			String sql1 = "select a.categoryName from Category a,BudgetTemplate b, BudgetItem c where a._id = b.category and b._id = c.budgetTemplate and c._id = "+fromBudget;
			Cursor mCursor1 = db.rawQuery(sql1, null);
			String fromNameString = "";
			while (mCursor1.moveToNext()) {
				
			  fromNameString = mCursor1.getString(0);
				
			}
			
			String sql2 = "select a.categoryName from Category a,BudgetTemplate b, BudgetItem c where a._id = b.category and b._id = c.budgetTemplate and c._id = "+toBudget;
			Cursor mCursor2 = db.rawQuery(sql2, null);
			String toNameString = "";
			while (mCursor2.moveToNext()) {
				
				toNameString = mCursor2.getString(0);
				
			}
			
			mMap.put("payeeName", fromNameString +" > "+toNameString);
			mList.add(mMap);
			
		}
		
		
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static List<Map<String, Object>> selectBudgetTransfer(Context context , long beginTime , long endTime) { // 查询Category
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select * from BudgetTransfer a where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			
			mMap = new HashMap<String, Object>();
			int _id = mCursor.getInt(0);
			String amount = mCursor.getString(1);
			long dateTime = mCursor.getLong(2);
			int fromBudget = mCursor.getInt(6);
			int toBudget = mCursor.getInt(7);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("fromBudget", fromBudget);
			mMap.put("toBudget", toBudget);
			mMap.put("payeeName", "--");
			mMap.put("index", 1);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
	public static List<Map<String, Object>> selectBudgetAllSum(Context context) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select total(a.amount) from BudgetItem a, BudgetTemplate b, Category c where a.budgetTemplate = b._id and b.category = c._id";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			double allAmount = mCursor.getDouble(0);
			mMap.put("allAmount", allAmount);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	public static List<Map<String, Object>> selectBudgetSumSpentByTime(Context context, long beginTime , long endTime) { // Account查询
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select total(d.amount) from 'Transaction' d, BudgetItem a, BudgetTemplate b, Category c where a.budgetTemplate = b._id and b.category = c._id and d.category = c._id and d.dateTime >= "+ beginTime+ " and d.dateTime <= "+ (endTime+DAYMILLIS) ;
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			double allAmount = mCursor.getDouble(0);
			mMap.put("allAmount", allAmount);

			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
	
public static List<Map<String, Object>> selectTransactionSumByCategoryIdAndTime(Context context, String categorName,long beginTime, long endTime) { // 根据category的名字查询transaction
		
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select total(a.amount) from 'Transaction' a , Category b where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and a.category = b._id and b.categoryName like '"+ categorName +"%' and a.parTransaction != -1 ";
		Cursor mCursor = db.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			mMap = new HashMap<String, Object>();

			double allAmount = mCursor.getDouble(0);
			mMap.put("allAmount", allAmount);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

	
	public static List<Map<String, Object>> selectTransactionByCategoryIdAndTime(Context context, String categorName,long beginTime, long endTime) { // 根据category的名字查询transaction
		
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a , Category b where a.dateTime >= "+ beginTime+ " and a.dateTime <= "+ (endTime+DAYMILLIS)+ " and a.category = b._id and b.categoryName like '"+ categorName +"%' and a.parTransaction != -1 order by a.dateTime DESC , a._id DESC ";
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
			String uuid = mCursor.getString(16);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("index", 0);
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("uuid", uuid);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}
	
public static List<Map<String, Object>> selectTransactionByCategoryId(Context context, int categoryId ) { // 根据category的Id查询transaction
		
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		SQLiteDatabase db = getConnection(context);
		String sql = "select a.* from 'Transaction' a , Category b where a.category = b._id and b._id = "+ categoryId +" and a.parTransaction != -1 order by a.dateTime DESC , a._id DESC ";
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
			String uuid = mCursor.getString(16);

			mMap.put("_id", _id);
			mMap.put("amount", amount);
			mMap.put("dateTime", dateTime);
			mMap.put("isClear", isClear);
			mMap.put("photoName", photoName);
			mMap.put("notes", notes);
			mMap.put("recurringType", recurringType);
			mMap.put("category", category);
			mMap.put("childTransactions", childTransactions);
			mMap.put("parTransaction", parTransaction);
			mMap.put("expenseAccount", expenseAccount);
			mMap.put("incomeAccount", incomeAccount);
			mMap.put("payee", payee);
			mMap.put("index", 0);
			int transactionHasBillItem = mCursor.getInt(24);
			int transactionHasBillRule = mCursor.getInt(25);
			mMap.put("transactionHasBillItem", transactionHasBillItem);
			mMap.put("transactionHasBillRule", transactionHasBillRule);
			mMap.put("uuid", uuid);
			
			mList.add(mMap);
		}
		mCursor.close();
		db.close();

		return mList;
	}

}
