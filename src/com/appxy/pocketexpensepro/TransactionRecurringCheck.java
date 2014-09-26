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

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransactionRecurringCheck {
	
	
	public static void recurringCheck(Context context, long today) {
		List<Map<String, Object>> mList = AccountDao.selectTransactionRecurringOverToday(context, today);
		
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		
		for (Map<String, Object> mMap:mList) {
			
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
			}else if (recurringTpye == 3) {
				calendar.add(Calendar.DAY_OF_MONTH, 14);
			}else if (recurringTpye == 4) {
				calendar.add(Calendar.DAY_OF_MONTH, 21);
			}else if (recurringTpye == 5) {
				calendar.add(Calendar.DAY_OF_MONTH, 28);
			}else if (recurringTpye == 6) {
				calendar.add(Calendar.DAY_OF_MONTH, 15);
			}else if (recurringTpye == 7) {
				calendar.add(Calendar.MONTH, 1);
			}else if (recurringTpye == 8) {
				calendar.add(Calendar.MONTH, 2);
			}else if (recurringTpye == 9) {
				calendar.add(Calendar.MONTH, 3);
			}else if (recurringTpye == 10) {
				calendar.add(Calendar.MONTH, 4);
			}else if (recurringTpye == 11) {
				calendar.add(Calendar.MONTH, 5);
			}else if (recurringTpye == 12) {
				calendar.add(Calendar.MONTH, 6);
			}else if (recurringTpye == 13) {
				calendar.add(Calendar.YEAR, 1);
			}
			
			if (calendar.getTimeInMillis() <= MEntity.getNowMillis()) {
				
				String transactionstring1 = uuid+MEntity.turnMilltoDate(dateTime);
				updateTransactionRecurring(db, context, id, transactionstring1);
			
//			db.beginTransaction();  //手动设置开始事务
			
	        try{
	            //批量处理操作
	        	while (calendar.getTimeInMillis() < MEntity.getNowMillis()) {
	        		
	        		String transactionstring = uuid+MEntity.turnMilltoDate(calendar.getTimeInMillis());
						 TransactionDao.insertTransactionOne(db,context,amount, calendar.getTimeInMillis(), isClear,
								 notes, photoName, 0,
								 category,
								 childTransactions + "",expenseAccount, incomeAccount, parTransaction, payee,transactionstring);
	        		
					if (recurringTpye == 1) {
						calendar.add(Calendar.DAY_OF_MONTH, 1);
					} else if (recurringTpye == 2) {
						calendar.add(Calendar.DAY_OF_MONTH, 7);
					}else if (recurringTpye == 3) {
						calendar.add(Calendar.DAY_OF_MONTH, 14);
					}else if (recurringTpye == 4) {
						calendar.add(Calendar.DAY_OF_MONTH, 21);
					}else if (recurringTpye == 5) {
						calendar.add(Calendar.DAY_OF_MONTH, 28);
					}else if (recurringTpye == 6) {
						calendar.add(Calendar.DAY_OF_MONTH, 15);
					}else if (recurringTpye == 7) {
						calendar.add(Calendar.MONTH, 1);
					}else if (recurringTpye == 8) {
						calendar.add(Calendar.MONTH, 2);
					}else if (recurringTpye == 9) {
						calendar.add(Calendar.MONTH, 3);
					}else if (recurringTpye == 10) {
						calendar.add(Calendar.MONTH, 4);
					}else if (recurringTpye == 11) {
						calendar.add(Calendar.MONTH, 5);
					}else if (recurringTpye == 12) {
						calendar.add(Calendar.MONTH, 6);
					}else if (recurringTpye == 13) {
						calendar.add(Calendar.YEAR, 1);
					}
				}
        		
	        	 TransactionDao.insertTransactionOne(db,context,amount, calendar.getTimeInMillis(), isClear,
						 notes, photoName, recurringTpye,
						 category,
						 childTransactions + "",expenseAccount, incomeAccount, parTransaction, payee,"");
	        	 
//	            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
	           
	           }catch(Exception e){
	              Log.v("mtest", "******Exception******"+e);
	              
	           }finally{
//	               db.endTransaction(); //处理完成
	               db.close();
	           }
			}
			
		
		}
	}
	
	public static long updateTransactionRecurring(SQLiteDatabase db, Context context, int _id, String transactionstring) { // AccountType插入

		ContentValues cv = new ContentValues();
		cv.put("recurringType", 0);
		cv.put("transactionstring",transactionstring);
		
		String mId = _id + "";
		try {
			long id = db
					.update("'Transaction'", cv, "_id = ?", new String[] { mId });
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	
}
