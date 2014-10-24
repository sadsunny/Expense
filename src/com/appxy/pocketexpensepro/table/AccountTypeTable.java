package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class AccountTypeTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;
	
	public class AccountType {

		private String accounttype_typename;
		private String accounttype_iconname;
		private Date dateTime;
		private String state;
		private int accounttype_isdefault;
		private String uuid;

		public String getAccounttype_typename() {
			return accounttype_typename;
		}

		public void setAccounttype_typename(String accounttype_typename) {
			this.accounttype_typename = accounttype_typename;
		}

		public String getAccounttype_iconname() {
			return accounttype_iconname;
		}

		public void setAccounttype_iconname(int accounttype_iconname) {
			this.accounttype_iconname = Common.ACCOUNTTYPESYNCNAME[accounttype_iconname];
		}

		public Date getDatetime() {
			return dateTime;
		}

		public void setDatetime(long datetime) {
			this.dateTime = MEntity.getMilltoDateFormat(datetime);;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public int getAccounttype_isdefault() {
			return accounttype_isdefault;
		}

		public void setAccounttype_isdefault(int accounttype_isdefault) {
			this.accounttype_isdefault = accounttype_isdefault;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		
		public AccountType() {

		}

		public void insertOrUpdate() { //根据state操作数据库，下载后的处理
			
			if (state.equals("0")) {
				AccountDao.deleteAccountTypeByUUID(context, uuid);
			} else if (state.equals("1")){
				
				List<Map<String, Object>> mList= AccountDao.checkAccountTypeByUUid(context, uuid);
				if ( mList.size() > 0) {
					
				    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
				    if (localDateTime_sync < dateTime.getTime()) {
				    AccountDao.updateAccountTypeAll(context, Common.positionAccountType(accounttype_iconname), accounttype_isdefault, accounttype_typename, dateTime.getTime(), state, uuid);
					}
					
				}else {
					
					AccountDao.insertAccountTypeAll(context, Common.positionAccountType(accounttype_iconname), accounttype_isdefault, accounttype_typename, dateTime.getTime(), state, uuid);

				}
			}
			
		}

		public void setAccountTypeData(Map<String, Object> mMap) {
			
			accounttype_typename = (String) mMap.get("accounttype_typename");
			accounttype_iconname =  Common.ACCOUNTTYPESYNCNAME [ Integer.parseInt((String) mMap.get("accounttype_iconname")) ] ;
			dateTime = MEntity.getMilltoDateFormat((Long) mMap.get("datetime"));
			state = (String) mMap.get("state");
			accounttype_isdefault = (Integer) mMap.get("accounttype_isdefault");
			uuid = (String) mMap.get("uuid");
			
		}
		
		public void setIncomingData(DbxRecord iRecord) { 
			
			accounttype_typename = iRecord.getString("accounttype_typename");
			accounttype_iconname = iRecord.getString("accounttype_iconname");
			dateTime = iRecord.getDate("dateTime");
			state = iRecord.getString("state");
			accounttype_isdefault = (int)iRecord.getLong("accounttype_isdefault");
			uuid = iRecord.getString("uuid");
			
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("accounttype_typename", accounttype_typename);
			accountsFields.set("accounttype_iconname", accounttype_iconname);
			accountsFields.set("dateTime", dateTime);
			accountsFields.set("state", state);
			accountsFields.set("accounttype_isdefault", accounttype_isdefault);
			accountsFields.set("uuid", uuid);
			return accountsFields;
		}

	}

	public void updateState(String uuid, String state) throws DbxException {// 更改状态,删除同步数据

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

	public AccountType getAccountType() {
		AccountType accounts = new AccountType();
		return accounts;
	}

	public AccountTypeTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_accounttype_table");
		this.context  = context;
		
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
		queryParams.set("uuid", thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();
		
		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime").getTime() < thisFields
					.getDate("dateTime").getTime()) { // 比对同步时间

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
