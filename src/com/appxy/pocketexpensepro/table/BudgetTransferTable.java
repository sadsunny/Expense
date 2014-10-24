package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;

import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class BudgetTransferTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;

	public class BudgetTransfer {

		private String uuid;
		private Date budgettransfer_datetime;
		private String budget_frombudget;
		private String state;
		private Date dateTime_sync;
		private double budgettransfer_amount;
		private String budget_tobudget;

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public Date getBudgettransfer_datetime() {
			return budgettransfer_datetime;
		}

		public void setBudgettransfer_datetime(Date budgettransfer_datetime) {
			this.budgettransfer_datetime = budgettransfer_datetime;
		}

		public String getBudget_frombudget() {
			return budget_frombudget;
		}

		public void setBudget_frombudget(int budget_frombudget) {
			this.budget_frombudget = SyncDao.selectBudgetItemUUid(context, budget_frombudget);;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public Date getDateTime_sync() {
			return dateTime_sync;
		}

		public void setDateTime_sync(Date dateTime_sync) {
			this.dateTime_sync = dateTime_sync;
		}

		public double getBudgettransfer_amount() {
			return budgettransfer_amount;
		}

		public void setBudgettransfer_amount(double budgettransfer_amount) {
			this.budgettransfer_amount = budgettransfer_amount;
		}

		public String getBudget_tobudget() {
			return budget_tobudget;
		}

		public void setBudget_tobudget(int budget_tobudget) {
			this.budget_tobudget = SyncDao.selectBudgetItemUUid(context, budget_tobudget);
		}

		public BudgetTransfer() {

		}
		
		public void setIncomingData(DbxRecord iRecord) { 
			
			uuid = iRecord.getString("uuid");
			budgettransfer_datetime = iRecord.getDate("budgettransfer_datetime");
			budget_frombudget = iRecord.getString("budget_frombudget");
			state =iRecord.getString("state");
			dateTime_sync = iRecord.getDate("dateTime_sync");
			budgettransfer_amount = iRecord.getDouble("budgettransfer_amount");
			budget_tobudget = iRecord.getString("budget_tobudget");
		}
		
		 public void insertOrUpdate() { //根据state操作数据库，下载后的处理
				
				if (state.equals("0")) {
					
					BudgetsDao.deleteBudgetTemByUUId(context, uuid);
					
				} else if (state.equals("1")){
					
					List<Map<String, Object>> mList= BudgetsDao.checkBudgetTransferByUUid(context, uuid);
					if ( mList.size() > 0) {
						
					    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
					    if (localDateTime_sync < dateTime_sync.getTime()) {
					    	
					    BudgetsDao.updateBudgetTransferAll(context, budgettransfer_amount+"", budgettransfer_datetime.getTime(), BudgetsDao.selectBudgetItemIdByUUid(context, budget_frombudget), BudgetsDao.selectBudgetItemIdByUUid(context, budget_tobudget), dateTime_sync.getTime(), state, uuid)	;

						}
						
					}else {
						
						BudgetsDao.insertBudgetTransferAll(context, budgettransfer_amount+"", budgettransfer_datetime.getTime(), BudgetsDao.selectBudgetItemIdByUUid(context, budget_frombudget), BudgetsDao.selectBudgetItemIdByUUid(context, budget_tobudget), dateTime_sync.getTime(), state, uuid)	;
					}
				}
				
			}
		 

		public void setBudgetTransferData(Map<String, Object> mMap) {

			uuid = (String) mMap.get("uuid");
			budgettransfer_datetime = MEntity.getMilltoDateFormat((Long) mMap.get("budgettransfer_datetime"));
			budget_frombudget = SyncDao.selectBudgetItemUUid(context, Integer.parseInt( (String) mMap.get("budget_frombudget")) );
			state = (String) mMap.get("state");
			dateTime_sync = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime_sync"));
			budgettransfer_amount = (Double) mMap.get("budgettransfer_amount");
			budget_tobudget = SyncDao.selectBudgetItemUUid(context, Integer.parseInt( (String) mMap.get("budget_tobudget") ));
			
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("uuid", uuid);
			accountsFields.set("budgettransfer_datetime", budgettransfer_datetime);
			
			if (budget_frombudget != null) {
				accountsFields.set("budget_frombudget", budget_frombudget);
			}
		
			accountsFields.set("state", state);
			accountsFields.set("dateTime_sync", dateTime_sync);
			accountsFields.set("budgettransfer_amount", budgettransfer_amount);
			
			if (budget_tobudget != null) {
				accountsFields.set("budget_tobudget", budget_tobudget);
			}
			
			return accountsFields;
		}

	}

	public void updateState(String uuid, String state) throws DbxException {// 更改状态

		DbxFields queryParams = new DbxFields().set("uuid", uuid);
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord record = it.next();
			DbxFields mUpdateFields = new DbxFields();
			mUpdateFields.set("state", state);
			mUpdateFields.set("dateTime", MEntity.getMilltoDateFormat(System.currentTimeMillis()));
			record.setAll(mUpdateFields);
			mDatastore.sync();
		}

	}

	public BudgetTransfer getBudgetTransfer() {
		BudgetTransfer accounts = new BudgetTransfer();
		return accounts;
	}

	public BudgetTransferTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_budgettransfer_table");
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
		if (thisFields.hasField("budget_tobudget") && thisFields.hasField("budget_frombudget")) {
			
		queryParams.set("uuid", thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime_sync").getTime() < thisFields.getDate(
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

}
