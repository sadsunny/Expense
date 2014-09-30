package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

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
		
		public void setTrans_billitem(String trans_billitem) {
			this.trans_billitem = trans_billitem;
		}

		public void setTrans_billrule(String trans_billrule) {
			this.trans_billrule = trans_billrule;
		}

		public void setTrans_expenseaccount(String trans_expenseaccount) {
			this.trans_expenseaccount = trans_expenseaccount;
		}

		public void setTrans_incomeaccount(String trans_incomeaccount) {
			this.trans_incomeaccount = trans_incomeaccount;
		}

		public void setTrans_amount(double trans_amount) {
			this.trans_amount = trans_amount;
		}

		public void setTrans_notes(String trans_notes) {
			this.trans_notes = trans_notes;
		}

		public void setTrans_payee(String trans_payee) {
			this.trans_payee = trans_payee;
		}

		public void setTrans_string(String trans_string) {
			this.trans_string = trans_string;
		}

		public void setTrans_category(String trans_category) {
			this.trans_category = trans_category;
		}

		public void setDateTime_sync(Date dateTime_sync) {
			this.dateTime_sync = dateTime_sync;
		}

		public void setState(String state) {
			this.state = state;
		}

		public void setTrans_recurringtype(String trans_recurringtype) {
			this.trans_recurringtype = trans_recurringtype;
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

		public void setTrans_partransaction(String trans_partransaction) {
			this.trans_partransaction = trans_partransaction;
		}

		public Transaction() {

		}

		public void setTransactionData(Map<String, Object> mMap) {

			trans_expenseaccount = SyncDao.selecAccountsUUid(context, Integer.parseInt((String) mMap.get("trans_expenseaccount")) );
			trans_incomeaccount = SyncDao.selecAccountsUUid(context, Integer.parseInt((String) mMap.get("trans_incomeaccount")) );
			trans_amount = (Double) mMap.get("trans_amount");
			trans_notes = (String) mMap.get("trans_notes");
			
			trans_payee = (String) mMap.get("trans_payee");
			trans_string = (String) mMap.get("trans_string");
			trans_category =SyncDao.selectCategoryUUid(context, Integer.parseInt( (String) mMap.get("trans_category") ));
			dateTime_sync = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime_sync"));
			
			state = (String) mMap.get("state");
			
			int transaction_recurring = Integer.parseInt( ((String) mMap.get("trans_recurringtype")) );
			trans_recurringtype = context.getResources().getStringArray(R.array.transaction_recurring)[transaction_recurring];
			
			
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
			
			
			accountsFields.set("trans_payee", trans_payee);
			if (trans_string != null && trans_string.length()  > 0) {
				accountsFields.set("trans_string", trans_string);
			}
			if (trans_category != null && trans_category.length() > 0) {
				accountsFields.set("trans_category", trans_category);
			}
			accountsFields.set("dateTime_sync",dateTime_sync);
			
			accountsFields.set("state", state);
			if (trans_recurringtype != null && trans_recurringtype.length() > 0) {
				accountsFields.set("trans_recurringtype",trans_recurringtype);
			}
		
			accountsFields.set("trans_datetime",trans_datetime);
			accountsFields.set("trans_isclear",trans_isclear);
			
			accountsFields.set("uuid",uuid);
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

	}

	public void updateState(String uuid, int state) throws DbxException {// 更改状态

		DbxFields queryParams = new DbxFields().set("uuid", uuid);
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord record = it.next();
			DbxFields mUpdateFields = new DbxFields();
			mUpdateFields.set("state", state);
			record.setAll(mUpdateFields);
			mDatastore.sync();
		}

	}

	public Transaction getTransaction() {
		Transaction accounts = new Transaction();
		return accounts;
	}

	public TransactionTable(DbxDatastore datastore,Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_transaction_table");
		this.context = context;
	}

	public void insertRecords(DbxFields thisFields) throws DbxException {

		DbxFields queryParams = new DbxFields();
		queryParams.set("uuid", thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime_sync").getTime() <= thisFields.getDate(
					"dateTime_sync").getTime()) { // 比对同步时间

				firstResult.setAll(thisFields);
				while (it.hasNext()) {
					DbxRecord r = it.next();
					r.deleteRecord();
				}
			}

		} else {
			mTable.insert(thisFields);
		}

	}

}
