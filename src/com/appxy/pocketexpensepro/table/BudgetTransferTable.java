package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.appxy.pocketexpensepro.entity.MEntity;
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

		public void setBudget_frombudget(String budget_frombudget) {
			this.budget_frombudget = budget_frombudget;
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

		public void setBudget_tobudget(String budget_tobudget) {
			this.budget_tobudget = budget_tobudget;
		}

		public BudgetTransfer() {

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
			accountsFields.set("budget_frombudget", budget_frombudget);
			accountsFields.set("state", state);
			accountsFields.set("dateTime_sync", dateTime_sync);
			accountsFields.set("budgettransfer_amount", budgettransfer_amount);
			accountsFields.set("budget_tobudget", budget_tobudget);
			
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

	public BudgetTransfer getBudgetTransfer() {
		BudgetTransfer accounts = new BudgetTransfer();
		return accounts;
	}

	public BudgetTransferTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_budgettransfer_table");
		this.context = context;
	}

	public void insertRecords(DbxFields thisFields) throws DbxException {

		DbxFields queryParams = new DbxFields();
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
