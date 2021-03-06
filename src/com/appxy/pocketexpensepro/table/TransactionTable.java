package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.bills.BillsDao;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.dropbox.sync.android.DbxFields.ValueType;

public class TransactionTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;

	public class Transaction {

		private String trans_expenseaccount;
		private String trans_incomeaccount;
		private double trans_amount;
		private String trans_notes;
		private String trans_payee;
		private String trans_string;
		private String trans_category;
		private Date dateTime_sync;
		private String state;
		private String  trans_recurringtype;
		private Date trans_datetime;
		private int trans_isclear;
		private String uuid;
		private String trans_partransaction;
		private String trans_billitem;
		private String trans_billrule;
		
		public void setTrans_billitem(int trans_billitem) {
			this.trans_billitem = SyncDao.selectBillItemUUid(context, trans_billitem);
		}

		public void setTrans_billrule(int trans_billrule) {
			this.trans_billrule = SyncDao.selectBillRuleUUid(context, trans_billrule);
		}

		public void setTrans_expenseaccount(int trans_expenseaccount) {
			this.trans_expenseaccount = SyncDao.selecAccountsUUid(context, trans_expenseaccount);
		}

		public void setTrans_incomeaccount(int trans_incomeaccount) {
			this.trans_incomeaccount = SyncDao.selecAccountsUUid(context, trans_incomeaccount);;
		}

		public void setTrans_amount(double trans_amount) {
			this.trans_amount = trans_amount;
		}

		public void setTrans_notes(String trans_notes) {
			this.trans_notes = trans_notes;
		}

		public void setTrans_payee(int trans_payee) {
			this.trans_payee = PayeeDao.selectPayeeUUidById(context, trans_payee);
		}

		public void setTrans_string(String trans_string) {
			this.trans_string = trans_string;
		}

		public void setTrans_category(int trans_category) {
			this.trans_category = PayeeDao.selectCategoryUUidById(context, trans_category);
		}

		public void setDateTime_sync(Date dateTime_sync) {
			this.dateTime_sync = dateTime_sync;
		}

		public void setState(String state) {
			this.state = state;
		}

		public void setTrans_recurringtype(int trans_recurringtype) {
			this.trans_recurringtype = MEntity.TransactionRecurring[trans_recurringtype];;
		}

		public void setTrans_datetime(Date trans_datetime) {
			this.trans_datetime = trans_datetime;
		}

		public void setTrans_isclear(int trans_isclear) {
			this.trans_isclear = trans_isclear;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public void setTrans_partransaction(int trans_partransaction) {
			this.trans_partransaction = SyncDao.selecTransactionUUid(context, trans_partransaction);
		}

		public Transaction() {

		}
		
		
		public void setIncomingData(DbxRecord iRecord) { 
			if (iRecord.hasField("trans_expenseaccount")) {
				trans_expenseaccount = iRecord.getString("trans_expenseaccount");
			}
			if (iRecord.hasField("trans_incomeaccount")) {
				trans_incomeaccount = iRecord.getString("trans_incomeaccount");
			}
			
			if (iRecord.hasField("trans_amount")) {
				trans_amount = iRecord.getDouble("trans_amount");
			}else {
				trans_amount =  0;
			}
			
			if (iRecord.hasField("trans_notes")) {
				trans_notes = iRecord.getString("trans_notes");
			}
			
			if (iRecord.hasField("trans_payee")) {
				trans_payee = iRecord.getString("trans_payee");
			}
			
			if (iRecord.hasField("trans_string")) {
				trans_string = iRecord.getString("trans_string");
			}
			if (iRecord.hasField("trans_category")) {
				trans_category = iRecord.getString("trans_category");
			}
			
			if (iRecord.hasField("dateTime_sync")) {
				dateTime_sync = iRecord.getDate("dateTime_sync");
			}else {
				dateTime_sync = new Date();
			}
			
			if (iRecord.hasField("uuid")) {
				uuid = iRecord.getString("uuid");
			}else {
				uuid = null;
			}
			
			if (iRecord.hasField("state")) {
				state = iRecord.getString("state");
			}else{
				state = "1";
			}
			
			if (iRecord.hasField("trans_recurringtype")) {
				
				trans_recurringtype = iRecord.getString("trans_recurringtype");
			}
			
			if (iRecord.hasField("trans_datetime")) {
				trans_datetime = iRecord.getDate("trans_datetime");
			}else{
				trans_datetime = new Date();
			}
			
				if (iRecord.hasField("trans_isclear")) {
					
					if (iRecord.getFieldType("trans_isclear") == ValueType.LONG) {
						trans_isclear = (int) iRecord
								.getLong("trans_isclear");
					} else if (iRecord.getFieldType("trans_isclear") == ValueType.BOOLEAN) {

						boolean mBool = iRecord
								.getBoolean("trans_isclear");

						if (mBool) {
							trans_isclear = 1;
						} else {
							trans_isclear = 0;
						}

					} else {
						trans_isclear = 0;
					}
					
					
				}else {
					trans_isclear = 0;
				}
				
			
			if (iRecord.hasField("trans_partransaction")) {
				trans_partransaction = iRecord.getString("trans_partransaction");
			}
			if (iRecord.hasField("trans_billitem")) {
				trans_billitem = iRecord.getString("trans_billitem");
			}
	
			if (iRecord.hasField("trans_billrule")) {
				trans_billrule = iRecord.getString("trans_billrule");
			}
			
		}
		
		 public void insertOrUpdate() { 
				
			 if (uuid != null) {
				
				if (state.equals("0")) {
					
					TransactionDao.deleteTransactionByUUId(context, uuid);
					
				} else if (state.equals("1")){
					
					String isChild = "0";
					if (trans_partransaction != null && trans_partransaction.length() > 0) {
						isChild = "1";
					}
					
					int transExpenseAcc = TransactionDao.selectAccountsIdByUUid(context, trans_expenseaccount);
					int transIncomeAcc = TransactionDao.selectAccountsIdByUUid(context, trans_incomeaccount);
					
					if (transExpenseAcc <=0 && transIncomeAcc <= 0) {
						
					}else{
						
					
					List<Map<String, Object>> mList= TransactionDao.checkTransactionByUUid(context, uuid);
					if ( mList.size() > 0) {
						
					    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
					    if (localDateTime_sync < dateTime_sync.getTime()) {
					    	
					    	 TransactionDao.updateTransactionAllData(context, trans_amount+"", trans_datetime.getTime(), trans_isclear,
										trans_notes, MEntity.positionTransactionRecurring(trans_recurringtype), PayeeDao.selectCategoryIdByUUid(context, trans_category),
										isChild, TransactionDao.selectAccountsIdByUUid(context, trans_expenseaccount), 
										TransactionDao.selectAccountsIdByUUid(context, trans_incomeaccount), TransactionDao.selectTransactionIdByUUid(context, trans_partransaction),
										PayeeDao.selectPayeeIdByUUid(context, trans_payee), trans_string,
										dateTime_sync.getTime(), state, uuid, TransactionDao.selectEP_BillRuleIdByUUid(context, trans_billrule), TransactionDao.selectEP_BillItemIdByUUid(context, trans_billitem));
										

						}
						
					}else {
						
						
						List<Map<String, Object>> mListTran = TransactionDao.checkTransactionByTransString(context, trans_string);
						 
						if (mListTran.size() > 0) {
							  long localDateTime_sync = (Long) mListTran.get(0).get("dateTime_sync");
							   
							  if (localDateTime_sync < dateTime_sync.getTime()) {
							    	
							    	 TransactionDao.updateTransactionAllData(context, trans_amount+"", trans_datetime.getTime(), trans_isclear,
												trans_notes, MEntity.positionTransactionRecurring(trans_recurringtype), PayeeDao.selectCategoryIdByUUid(context, trans_category),
												isChild, TransactionDao.selectAccountsIdByUUid(context, trans_expenseaccount), 
												TransactionDao.selectAccountsIdByUUid(context, trans_incomeaccount), TransactionDao.selectTransactionIdByUUid(context, trans_partransaction),
												PayeeDao.selectPayeeIdByUUid(context, trans_payee), trans_string,
												dateTime_sync.getTime(), state, uuid, TransactionDao.selectEP_BillRuleIdByUUid(context, trans_billrule), TransactionDao.selectEP_BillItemIdByUUid(context, trans_billitem));
												

								}
						} else {

					    TransactionDao.insertTransactionAllData(context, trans_amount+"", trans_datetime.getTime(), trans_isclear,
						trans_notes, MEntity.positionTransactionRecurring(trans_recurringtype), PayeeDao.selectCategoryIdByUUid(context, trans_category),
						isChild, TransactionDao.selectAccountsIdByUUid(context, trans_expenseaccount), 
						TransactionDao.selectAccountsIdByUUid(context, trans_incomeaccount), TransactionDao.selectTransactionIdByUUid(context, trans_partransaction),
						PayeeDao.selectPayeeIdByUUid(context, trans_payee), trans_string,
						dateTime_sync.getTime(), state, uuid, TransactionDao.selectEP_BillRuleIdByUUid(context, trans_billrule), TransactionDao.selectEP_BillItemIdByUUid(context, trans_billitem));
						}
					}
					
				}
					
					
				}
			  }
			}
		 

		public void setTransactionData(Map<String, Object> mMap) {

			trans_expenseaccount = SyncDao.selecAccountsUUid(context, Integer.parseInt((String) mMap.get("trans_expenseaccount")) );
			trans_incomeaccount = SyncDao.selecAccountsUUid(context, Integer.parseInt((String) mMap.get("trans_incomeaccount")) );
			trans_amount = (Double) mMap.get("trans_amount");
			trans_notes = (String) mMap.get("trans_notes");
			
			trans_payee = SyncDao.selectPayeeUUid(context, Integer.parseInt((String) mMap.get("trans_payee")) );
			trans_string = (String) mMap.get("trans_string");
			trans_category =SyncDao.selectCategoryUUid(context, Integer.parseInt( (String) mMap.get("trans_category") ));
			
			dateTime_sync = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime_sync"));
			
			state = (String) mMap.get("state");
			
			int transaction_recurring = Integer.parseInt( ((String) mMap.get("trans_recurringtype")) );
			trans_recurringtype = MEntity.TransactionRecurring[transaction_recurring];
			
			trans_datetime = MEntity.getMilltoDateFormat((Long) mMap.get("trans_datetime"));
			trans_isclear = (Integer) mMap.get("trans_isclear");
			
			uuid = (String) mMap.get("uuid");
			trans_partransaction = SyncDao.selecTransactionUUid(context, Integer.parseInt((String) mMap.get("trans_partransaction")) );
			
			String trans_billitemID = (String) mMap.get("trans_billitem");
			if (trans_billitemID != null) {
				trans_billitem = SyncDao.selectBillItemUUid(context, Integer.parseInt(trans_billitemID) );
			}
			
			String trans_billruleID = (String) mMap.get("trans_billrule");
			if (trans_billruleID != null) {
				trans_billrule = SyncDao.selectBillRuleUUid(context, Integer.parseInt(trans_billruleID) );
			}
			
		}
		
		public DbxFields getFieldsApart() {

			DbxFields accountsFields = new DbxFields();
			
			if (trans_expenseaccount != null && trans_expenseaccount.length() > 0) {
				accountsFields.set("trans_expenseaccount", trans_expenseaccount);
			}
			accountsFields.set("trans_amount",trans_amount);
			
			accountsFields.set("dateTime_sync",dateTime_sync);
		
			accountsFields.set("trans_datetime",trans_datetime);
			
			if (uuid != null) {
				accountsFields.set("uuid",uuid);
			}
			return accountsFields;
		}

		public DbxFields getFieldsClear() {
			
			DbxFields accountsFields = new DbxFields();
			
			accountsFields.set("dateTime_sync",dateTime_sync);
			accountsFields.set("trans_isclear",trans_isclear);
			
			if (uuid != null) {
				accountsFields.set("uuid",uuid);
			}
			
			return accountsFields;
			
		}


		
		public DbxFields getFieldsApart1() {
			
			DbxFields accountsFields = new DbxFields();
			
			accountsFields.set("dateTime_sync",dateTime_sync);
			
			if (trans_recurringtype != null && trans_recurringtype.length() > 0) {
				accountsFields.set("trans_recurringtype",trans_recurringtype);
			}
			
			if (trans_string != null && trans_string.length()  > 0) {
				accountsFields.set("trans_string", trans_string);
			}
			
			if (uuid != null) {
				accountsFields.set("uuid",uuid);
			}
			
			return accountsFields;
			
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			
			
			if (trans_expenseaccount != null && trans_expenseaccount.length() > 0) {
				accountsFields.set("trans_expenseaccount", trans_expenseaccount);
			}
			if (trans_incomeaccount != null && trans_incomeaccount.length() > 0) {
				accountsFields.set("trans_incomeaccount", trans_incomeaccount);
			}
			accountsFields.set("trans_amount",trans_amount);
			if (trans_notes != null) {
				accountsFields.set("trans_notes",trans_notes);
			}
			
			if (trans_payee != null) {
				accountsFields.set("trans_payee", trans_payee);
			}
			
			if (trans_string != null && trans_string.length()  > 0) {
				accountsFields.set("trans_string", trans_string);
			}
			
			if (trans_category != null && trans_category.length() > 0) {
				accountsFields.set("trans_category", trans_category);
			}
			
			if (dateTime_sync != null) {
				accountsFields.set("dateTime_sync",dateTime_sync);
			}else {
				accountsFields.set("dateTime_sync",new Date());
			}
			
			if (state !=  null) {
				accountsFields.set("state", state);
			}
			
			if (trans_recurringtype != null && trans_recurringtype.length() > 0) {
				accountsFields.set("trans_recurringtype",trans_recurringtype);
			}
		
			if (trans_datetime != null) {
				accountsFields.set("trans_datetime",trans_datetime);
			}
			
			accountsFields.set("trans_isclear",trans_isclear);
			
			if (uuid != null) {
				accountsFields.set("uuid",uuid);
			}
			
			if (trans_partransaction != null && trans_partransaction.length()  > 0) {
				accountsFields.set("trans_partransaction",trans_partransaction);
			}
			if (trans_billitem != null && trans_billitem.length() > 0) {
				accountsFields.set("trans_billitem",trans_billitem);
			}
			if (trans_billrule != null  && trans_billrule.length() >0) {
				accountsFields.set("trans_billrule",trans_billrule);
			}
			
			return accountsFields;
		}
		
		public DbxFields getFieldsUpdate() {

			DbxFields accountsFields = new DbxFields();
			
			if (dateTime_sync != null) {
				accountsFields.set("dateTime_sync",dateTime_sync);
			}else {
				accountsFields.set("dateTime_sync",new Date());
			}
			
			if (trans_recurringtype != null && trans_recurringtype.length() > 0) {
				accountsFields.set("trans_recurringtype",trans_recurringtype);
			}
			
			if (uuid != null) {
				accountsFields.set("uuid",uuid);
			}
			
			return accountsFields;
		}


}

	public void updateState(String uuid, String trans_string, String state) throws DbxException {// ��存�圭�舵��

		DbxFields queryParams = new DbxFields().set("uuid", uuid);
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			
			DbxRecord record = it.next();
			DbxFields mUpdateFields = new DbxFields();
			mUpdateFields.set("state", state);
			mUpdateFields.set("dateTime_sync", MEntity.getMilltoDateFormat(System.currentTimeMillis()));
			record.setAll(mUpdateFields);
			
		}else{
			
			if (trans_string != null) {
				
			DbxFields queryParams1 = new DbxFields().set("trans_string", trans_string);
			DbxTable.QueryResult results1 = mTable.query(queryParams1);
			Iterator<DbxRecord> it1 = results1.iterator();

			if (it1.hasNext()) {
				
				DbxRecord record = it1.next();
				DbxFields mUpdateFields = new DbxFields();
				mUpdateFields.set("state", state);
				mUpdateFields.set("dateTime_sync", MEntity.getMilltoDateFormat(System.currentTimeMillis()));
				record.setAll(mUpdateFields);
				
			}
			
		 }
			
	  }
	}
	

	public Transaction getTransaction() {
		Transaction accounts = new Transaction();
		return accounts;
	}

	public TransactionTable(DbxDatastore datastore,Context context) {

		mDatastore = datastore;
		try {
			
			if (mDatastore != null) {
				mTable = datastore.getTable("db_transaction_table");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.context = context;
	}
	
	public void deleteAll() throws DbxException{
		 DbxTable.QueryResult results = mTable.query();
		 Iterator<DbxRecord> it = results.iterator();
	    	while(it.hasNext())
	    		
	    	{
	    		DbxRecord firstResult= it.next();
	    		firstResult.deleteRecord(); 
	    		mDatastore.sync();
	    	}
	    }

	public void insertRecords(DbxFields thisFields) throws DbxException {

		DbxFields queryParams = new DbxFields();
		
		if (thisFields.hasField("uuid")) {
			
		queryParams.set("uuid", thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();
		
		if (it.hasNext()) {
			
			DbxRecord firstResult = it.next();
			
			Date firstDate ;
			if (firstResult.hasField("dateTime_sync")) {
				firstDate = firstResult.getDate("dateTime_sync");
			}else {
				firstDate = new Date();
			}
			
			if (firstDate.getTime() <= thisFields.getDate(
					"dateTime_sync").getTime()) { 

				
				firstResult.setAll(thisFields);
				while (it.hasNext()) {
					DbxRecord r = it.next();
					r.deleteRecord();
				}
			}

		} else {
			
			DbxFields queryParams1 = new DbxFields();
			if (thisFields.hasField("trans_string")) {
				
			queryParams1.set("trans_string", thisFields.getString("trans_string"));
			DbxTable.QueryResult results1 = mTable.query(queryParams1);
			Iterator<DbxRecord> it1 = results1.iterator();
			
			if (it1.hasNext()) {
				
				DbxRecord firstResult = it1.next();
				
				Date firstDate ;
				if (firstResult.hasField("dateTime_sync")) {
					firstDate = firstResult.getDate("dateTime_sync");
				}else {
					firstDate = new Date();
				}
				
				
				if (firstDate.getTime() <= thisFields.getDate(
						"dateTime_sync").getTime()) {

					firstResult.setAll(thisFields);
					while (it.hasNext()) {
						DbxRecord r = it.next();
						r.deleteRecord();
					}
				}

			} else {
				
				mTable.insert(thisFields);
			}
			
		}else {
			
			mTable.insert(thisFields);
		}
			
	}
		
	}
		
}

}
