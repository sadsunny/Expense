package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.appxy.pocketexpensepro.accounts.AccountDao;
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
	private Context context ;

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

		public void setAccountType(int accountType) {
			this.accountType = AccountDao.selectAccountTypeUUidById(context, accountType);
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
		
		public void setIncomingData(DbxRecord iRecord) { //incoming数据初始化

			name = iRecord.getString("name");
			state = iRecord.getString("state");;
			datetime = iRecord.getDate("datetime");
			autoclear = (int) iRecord.getLong("autoclear");
			amount = iRecord.getDouble("amount");
			accountType = iRecord.getString("accountType");
			
			if (iRecord.hasField("orderindex")) {
			  orderindex = (int) iRecord.getLong("orderindex");
			}else {
			 orderindex = 100;
			}
			
			dateTime_sync = iRecord.getDate("dateTime_sync");
			uuid = iRecord.getString("uuid");
			
		}
		
		public void insertOrUpdate() { //根据state操作数据库
			
			if (state.equals("0")) {
				AccountDao.deleteAccountByUUID(context, uuid);
			} else if (state.equals("1")){
				
				List<Map<String, Object>> mList= AccountDao.checkAccountByUUid(context, uuid);
				if ( mList.size() > 0) {
					
				    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
				    if (localDateTime_sync < dateTime_sync.getTime()) {
				      AccountDao.updateAccountAll(context, name, amount+"", datetime.getTime(), autoclear, AccountDao.getAccountTypeIdByUUID(context,accountType),state ,uuid, dateTime_sync.getTime());
					}
					
				}else {
				     AccountDao.insertAccountAll(context, name, amount+"", datetime.getTime(), autoclear, AccountDao.getAccountTypeIdByUUID(context,accountType),state ,uuid, dateTime_sync.getTime());
				}
			}
			
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
			if (accountType != null) {
				accountsFields.set("accountType", accountType);
			}
			if (orderindex >0) {
				accountsFields.set("orderindex", orderindex);
			}
			accountsFields.set("dateTime_sync", dateTime_sync);
			if (uuid != null) {
				accountsFields.set("uuid", uuid);
			}
			
			return accountsFields;
		}

	}
	

	public void updateState(String uuid, String state) throws DbxException {//更改状态
		
		DbxFields queryParams = new DbxFields().set("uuid",uuid);
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord record = it.next();
            DbxFields mUpdateFields = new DbxFields();
            mUpdateFields.set("state", state);
            mUpdateFields.set("dateTime_sync", MEntity.getMilltoDateFormat( System.currentTimeMillis() ));
            record.setAll(mUpdateFields);
		} 

	}

	public Accounts getAccounts() {
		Accounts accounts = new Accounts();
		return accounts;
	}

	public AccountsTable(DbxDatastore datastore, Context context) throws DbxException {

		mDatastore = datastore;
		mTable = datastore.getTable("db_account_table");
		this.context = context;
	}
	
	public AccountsTable( )  {

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
	 
	
	public void insertRecords(DbxFields accountsFields) throws DbxException {

		Log.v("mtag", "accountsFields"+accountsFields);
		
		DbxFields queryParams = new DbxFields();
		if (accountsFields.hasField("uuid") && accountsFields.hasField("accountType")) {
			Log.v("mtag", "insertRecords");
			
		queryParams.set("uuid",accountsFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();
		
		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime_sync").getTime() < accountsFields.getDate("dateTime_sync").getTime()) { //比对同步时间
				
				 firstResult.setAll(accountsFields);
				 while(it.hasNext())
	    			{	
					DbxRecord  r=it.next();
	    			r.deleteRecord();
	    			}
			}

		} else {
			
			mTable.insert(accountsFields);
			
		}
	}

	}

}
