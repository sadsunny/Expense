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

public class BudgetItemTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;
	
	public class BudgetItem {

		private String uuid;
		private Date budgetitem_startdate;
		private double budgetitem_amount;
		private Date budgetitem_enddate;
		private double budgetitem_rolloveramount;
		private Date dateTime;
		private String state;
		private String budgetitem_budgettemplate;
		private int budgetitem_isrollover;

		public BudgetItem() {

		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public Date getBudgetitem_startdate() {
			return budgetitem_startdate;
		}

		public void setBudgetitem_startdate(Date budgetitem_startdate) {
			this.budgetitem_startdate = budgetitem_startdate;
		}

		public double getBudgetitem_amount() {
			return budgetitem_amount;
		}

		public void setBudgetitem_amount(double budgetitem_amount) {
			this.budgetitem_amount = budgetitem_amount;
		}

		public Date getBudgetitem_enddate() {
			return budgetitem_enddate;
		}

		public void setBudgetitem_enddate(Date budgetitem_enddate) {
			this.budgetitem_enddate = budgetitem_enddate;
		}

		public double getBudgetitem_rolloveramou() {
			return budgetitem_rolloveramount;
		}

		public void setBudgetitem_rolloveramou(double budgetitem_rolloveramou) {
			this.budgetitem_rolloveramount = budgetitem_rolloveramou;
		}

		public Date getDateTime() {
			return dateTime;
		}

		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getBudgetitem_budgettemplat() {
			return budgetitem_budgettemplate;
		}

		public void setBudgetitem_budgettemplate(String budgetitem_budgettemplate) {
			this.budgetitem_budgettemplate = SyncDao.selectBudgetTemplateUUid(context, Integer.parseInt(budgetitem_budgettemplate) );
		}

		public int getBudgetitem_isrollover() {
			return budgetitem_isrollover;
		}

		public void setBudgetitem_isrollover(int budgetitem_isrollover) {
			this.budgetitem_isrollover = budgetitem_isrollover;
		}

		public void setBudgetItemData(Map<String, Object> mMap) {

			uuid = (String) mMap.get("uuid");
			budgetitem_startdate = MEntity.getMilltoDateFormat( (Long) mMap.get("budgetitem_startdate") );
			budgetitem_amount = (Double) mMap.get("budgetitem_amount");
			budgetitem_enddate = MEntity.getMilltoDateFormat( (Long) mMap.get("budgetitem_enddate") );
			budgetitem_rolloveramount = (Double) mMap.get("budgetitem_rolloveramount");
			
			dateTime = MEntity.getMilltoDateFormat( (Long) mMap.get("dateTime") );
			state = (String) mMap.get("state");
			budgetitem_budgettemplate = SyncDao.selectBudgetTemplateUUid(context, Integer.parseInt((String) mMap.get("budgetitem_budgettemplate")) );
			budgetitem_isrollover = (Integer) mMap.get("budgetitem_isrollover");
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("uuid", uuid);
			accountsFields.set("budgetitem_startdate", budgetitem_startdate);
			accountsFields.set("budgetitem_amount", budgetitem_amount);
			accountsFields.set("budgetitem_enddate", budgetitem_enddate);
			accountsFields.set("budgetitem_rolloveramount", budgetitem_rolloveramount);
			
			accountsFields.set("dateTime", dateTime);
			accountsFields.set("state", state);
			accountsFields.set("budgetitem_budgettemplate", budgetitem_budgettemplate);
			accountsFields.set("budgetitem_isrollover", budgetitem_isrollover);
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

	public BudgetItem getBudgetItemType() {
		BudgetItem accounts = new BudgetItem();
		return accounts;
	}

	public BudgetItemTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_budgetitem_table");
		this.context = context;
	}

	public void insertRecords(DbxFields thisFields) throws DbxException {

		DbxFields queryParams = new DbxFields();
		queryParams.set("uuid", thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime").getTime() < thisFields.getDate(
					"dateTime").getTime()) { // 比对同步时间

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
