package com.appxy.pocketexpensepro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

import android.R.integer;
import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransactionRecurringCheck {
	
	
	public static void recurringCheck(Context context, long today, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		List<Map<String, Object>> mList = AccountDao.selectTransactionRecurringOverToday(context, today);
		
		try {

			for (Map<String, Object> mMap : mList) {

				int id = (Integer) mMap.get("_id");
				String amount = (String) mMap.get("amount");
				long dateTime = (Long) mMap.get("dateTime");
				int isClear = (Integer) mMap.get("isClear");
				String notes = (String) mMap.get("notes");
				String photoName = (String) mMap.get("photoName");
				int recurringTpye = (Integer) mMap.get("recurringType");
				int category = (Integer) mMap.get("category");
				String childTransactions = (String) mMap.get("childTransactions");
				int expenseAccount = (Integer) mMap.get("expenseAccount");
				int incomeAccount = (Integer) mMap.get("incomeAccount");
				int parTransaction = (Integer) mMap.get("parTransaction");
				int payee = (Integer) mMap.get("payee");
				String uuid = (String) mMap.get("uuid");

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(dateTime);

				if (recurringTpye == 1) {
					calendar.add(Calendar.DAY_OF_MONTH, 1);
				} else if (recurringTpye == 2) {
					calendar.add(Calendar.DAY_OF_MONTH, 7);
				} else if (recurringTpye == 3) {
					calendar.add(Calendar.DAY_OF_MONTH, 14);
				} else if (recurringTpye == 4) {
					calendar.add(Calendar.DAY_OF_MONTH, 21);
				} else if (recurringTpye == 5) {
					calendar.add(Calendar.DAY_OF_MONTH, 28);
				} else if (recurringTpye == 6) {
					calendar.add(Calendar.DAY_OF_MONTH, 15);
				} else if (recurringTpye == 7) {
					calendar.add(Calendar.MONTH, 1);
				} else if (recurringTpye == 8) {
					calendar.add(Calendar.MONTH, 2);
				} else if (recurringTpye == 9) {
					calendar.add(Calendar.MONTH, 3);
				} else if (recurringTpye == 10) {
					calendar.add(Calendar.MONTH, 4);
				} else if (recurringTpye == 11) {
					calendar.add(Calendar.MONTH, 5);
				} else if (recurringTpye == 12) {
					calendar.add(Calendar.MONTH, 6);
				} else if (recurringTpye == 13) {
					calendar.add(Calendar.YEAR, 1);
				}
				
				
				if (calendar.getTimeInMillis() < MEntity.getNowMillis()) {
					
					String transactionstring = uuid+ MEntity.turnMilltoDate(dateTime);
					TransactionDao.insertTransactionOne(context,
							amount, dateTime, isClear,
							notes, photoName, 0, category,
							childTransactions + "", expenseAccount,
							incomeAccount, parTransaction, payee,
							transactionstring, mDbxAcctMgr, mDatastore);
					 
				}

					// 批量处理操作
					while (calendar.getTimeInMillis() < MEntity.getNowMillis()) {
						
						String transactionstring = uuid+ MEntity.turnMilltoDate(calendar.getTimeInMillis());
						TransactionDao.insertTransactionOne(context,
								amount, calendar.getTimeInMillis(), isClear,
								notes, photoName, 0, category,
								childTransactions + "", expenseAccount,
								incomeAccount, parTransaction, payee,
								transactionstring, mDbxAcctMgr, mDatastore);

						if (recurringTpye == 1) {
							calendar.add(Calendar.DAY_OF_MONTH, 1);
						} else if (recurringTpye == 2) {
							calendar.add(Calendar.DAY_OF_MONTH, 7);
						} else if (recurringTpye == 3) {
							calendar.add(Calendar.DAY_OF_MONTH, 14);
						} else if (recurringTpye == 4) {
							calendar.add(Calendar.DAY_OF_MONTH, 21);
						} else if (recurringTpye == 5) {
							calendar.add(Calendar.DAY_OF_MONTH, 28);
						} else if (recurringTpye == 6) {
							calendar.add(Calendar.DAY_OF_MONTH, 15);
						} else if (recurringTpye == 7) {
							calendar.add(Calendar.MONTH, 1);
						} else if (recurringTpye == 8) {
							calendar.add(Calendar.MONTH, 2);
						} else if (recurringTpye == 9) {
							calendar.add(Calendar.MONTH, 3);
						} else if (recurringTpye == 10) {
							calendar.add(Calendar.MONTH, 4);
						} else if (recurringTpye == 11) {
							calendar.add(Calendar.MONTH, 5);
						} else if (recurringTpye == 12) {
							calendar.add(Calendar.MONTH, 6);
						} else if (recurringTpye == 13) {
							calendar.add(Calendar.YEAR, 1);
						}
					}
					
					updateTransactionRecurring(context,id,uuid,
							amount, calendar.getTimeInMillis(), isClear,
							notes, photoName, recurringTpye, category,
							childTransactions + "", expenseAccount,
							incomeAccount, parTransaction, payee, mDbxAcctMgr, mDatastore);

			}
			

			
		}catch(Exception e){
			
       }
		
		long end = System.currentTimeMillis();
	}
	
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		return db;
	}
	
	public static long updateTransactionRecurring( Context context, int _id,String uuid, String amount,long dateTime, int isClear, String notes, String photoName, int recurringType, int category, String childTransactions, int expenseAccount , int incomeAccount, int parTransaction, int payee, DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) { // AccountType插入

		
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
		cv.put("state", 1);
		cv.put("uuid", uuid);
		
		SQLiteDatabase db = getConnection(context);
		
		String mId = _id + "";
		
		
		try {
			long id = db.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			db.close();
			if (id > 0) {
				
				if (mDbxAcctMgr.hasLinkedAccount()) { 
					
					
					if (mDatastore == null) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					Transaction transaction = transactionTable.getTransaction();
					
					transaction.setDateTime_sync( MEntity.getMilltoDateFormat(dateTime_sync) );
					transaction.setState("1");
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
					transactionTable.insertRecords(transaction.getFields());
					
					mDatastore.sync();
					
				}
					
			}
			
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("mtag", "Exception in recurring"+e);
			db.close();
			return 0;
		}

	}
	
}
