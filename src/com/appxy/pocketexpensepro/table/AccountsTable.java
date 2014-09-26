package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.appxy.pocketexpensepro.entity.MEntity;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class AccountsTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;

	public class Accounts {

		private String name;
		private String state;
		private Date datetime;
		private int autoclear;
		private double amount;
		private String accountType;
		private int orderindex;
		private Date dateTime_sync;
		private String uuid;

		public String getName() { // Get Set used in insert and update
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public Date getDatetime() {
			return datetime;
		}

		public void setDatetime(Date datetime) {
			this.datetime = datetime;
		}

		public int getAutoclear() {
			return autoclear;
		}

		public void setAutoclear(int autoclear) {
			this.autoclear = autoclear;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public String getAccountType() {
			return accountType;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}

		public int getOrderindex() {
			return orderindex;
		}

		public void setOrderindex(int orderindex) {
			this.orderindex = orderindex;
		}

		public Date getDateTime_sync() {
			return dateTime_sync;
		}

		public void setDateTime_sync(Date dateTime_sync) {
			this.dateTime_sync = dateTime_sync;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public Accounts() {

		}

		public void setAccountsData(Map<String, Object> mMap) {

			name = (String) mMap.get("name");
			state = (String) mMap.get("state");
			datetime = MEntity.getMilltoDateFormat((Long) mMap.get("datetime"));
			autoclear = (Integer) mMap.get("autoclear");
			amount = (Double) mMap.get("amount");
			accountType = (String) mMap.get("accountType");
			orderindex = (Integer) mMap.get("orderindex");
			dateTime_sync = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime_sync"));
			uuid = (String) mMap.get("uuid");
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("name", name);
			accountsFields.set("state", state);
			accountsFields.set("datetime", datetime);
			accountsFields.set("autoclear", autoclear);
			accountsFields.set("amount", amount);
			accountsFields.set("accountType", accountType);
			accountsFields.set("orderindex", orderindex);
			accountsFields.set("dateTime_sync", dateTime_sync);
			accountsFields.set("uuid", uuid);
			return accountsFields;
		}

	}

	public void updateState(String uuid, int state) throws DbxException {//更改状态
		
		DbxFields queryParams = new DbxFields().set("uuid",uuid);
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

	public Accounts getAccounts() {
		Accounts accounts = new Accounts();
		return accounts;
	}

	public AccountsTable(DbxDatastore datastore) throws DbxException {

		mDatastore = datastore;
		mTable = datastore.getTable("db_account_table");
		
	}
	
	 public void deleteall(Iterator<DbxRecord> it) throws DbxException{
	    	
	    	while(it.hasNext())
	    		
	    	{
	    		DbxRecord firstResult= it.next();
	    		firstResult.deleteRecord(); 
	    		mDatastore.sync();
	    	}
	    }
	 
	
	public void insertRecords(DbxFields accountsFields) throws DbxException {

		DbxFields queryParams = new DbxFields();
		queryParams.set("uuid",accountsFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);;
		Iterator<DbxRecord> it = results.iterator();
		
		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime_sync").getTime() < accountsFields.getDate("dateTime_sync").getTime()) { //比对同步时间
				
				 firstResult.setAll(accountsFields);
				 while(it.hasNext())
	    			{	DbxRecord  r=it.next();
	    			r.deleteRecord();
	    			}
			}

		} else {
			mTable.insert(accountsFields);
		}

	}

}
