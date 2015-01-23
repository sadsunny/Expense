package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.category.CategoryDao;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class PayeeTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;
	
	public class Payee {

		private String uuid;
		private Date dateTime;
		private String state;
		private String payee_category;
		private String payee_name;
		private String payee_memo;
		
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public void setDatetime(Date dateTime) {
			this.dateTime = dateTime;
		}

		public void setState(String state) {
			this.state = state;
		}

		public void setPayee_category(int payee_category) {
			this.payee_category = PayeeDao.selectCategoryUUidById(context, payee_category);
		}

		public void setPayee_name(String payee_name) {
			this.payee_name = payee_name;
		}

		public void setPayee_memo(String payee_memo) {
			this.payee_memo = payee_memo;
		}

		public Payee() {

		}
		
		public void setIncomingData(DbxRecord iRecord) { 
			
			uuid = iRecord.getString("uuid");
			dateTime = iRecord.getDate("dateTime");
			state = iRecord.getString("state");
			
			if (iRecord.hasField("payee_category")) {
				payee_category = iRecord.getString("payee_category");
			} 
			
			payee_name = iRecord.getString("payee_name");
			if (iRecord.hasField("payee_memo")) {
				payee_memo = iRecord.getString("payee_memo");
			}
			
		}
		
		 public void insertOrUpdate() { //根据state操作数据库，下载后的处理
				
				if (state.equals("0")) {
					PayeeDao.deletePayeeByUUid(context, uuid);
				} else if (state.equals("1")){
					
					List<Map<String, Object>> mList= PayeeDao.checkPayeeByUUid(context, uuid);
					if ( mList.size() > 0) {
						
					    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
					    if (localDateTime_sync < dateTime.getTime()) {
					    PayeeDao.updatePayeeAll(context, payee_name, payee_memo,PayeeDao.selectCategoryIdByUUid(context, payee_category), dateTime.getTime(), state, uuid);
						}
						
					}else {
						PayeeDao.insertPayeeAll(context, payee_name, payee_memo,PayeeDao.selectCategoryIdByUUid(context, payee_category), dateTime.getTime(), state, uuid);
					}
				}
				
			}
		 
		 

		public void setPayeeData(Map<String, Object> mMap) {

			uuid = (String) mMap.get("uuid");
			dateTime = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime"));
			state = (String) mMap.get("state");
			payee_category = SyncDao.selectCategoryUUid(context, Integer.parseInt( (String) mMap.get("payee_category") ) );
			payee_name = (String) mMap.get("payee_name");
			payee_memo = (String) mMap.get("payee_memo");

		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("uuid", uuid);
			accountsFields.set("dateTime",dateTime);
			accountsFields.set("state", state);
			
			if (payee_category !=null) {
				accountsFields.set("payee_category", payee_category);
			}
			
			accountsFields.set("payee_name",payee_name);
			if (payee_memo != null && payee_memo.length() > 0) {
				accountsFields.set("payee_memo",payee_memo);
			}
			
			return accountsFields;
		}

	}

	public void updateState(String uuid, String state) throws DbxException {// 更改状态

		Log.v("mtag", "uuid"+uuid);
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
			return ;
		}

	}

	public Payee getPayee() {
		Payee accounts = new Payee();
		return accounts;
	}

	public PayeeTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_payee_table");
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
		if (thisFields.hasField("uuid") && thisFields.hasField("payee_category")) {
			
		queryParams.set("uuid", thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("dateTime").getTime() <= thisFields.getDate(
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
	

}
