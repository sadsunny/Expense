package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.category.CategoryDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class CategoryTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;

	public class Category {

		private String category_iconname;
		private String uuid;
		private int category_isdefault;
		private String category_categoryname;
		private Date dateTime;
		private String state;
		private int category_issystemrecord;
		private String category_categorytype;

		public String getCategory_iconname() {
			return category_iconname;
		}

		public void setCategory_iconname(String category_iconname) {
			
			int categoryIcon = Integer.parseInt( category_iconname );
			String iconString =  "uncategorized"; 
			iconString = Common.CATEGORYSYNCNAME[categoryIcon];
			this.category_iconname = iconString;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public int getCategory_isdefault() {
			return category_isdefault;
		}

		public void setCategory_isdefault(int category_isdefault) {
			this.category_isdefault = category_isdefault;
		}

		public String getCategory_categoryname() {
			return category_categoryname;
		}

		public void setCategory_categoryname(String category_categoryname) {
			this.category_categoryname = category_categoryname;
		}

		public Date getDatetime() {
			return dateTime;
		}

		public void setDatetime(Date dateTime) {
			this.dateTime = dateTime;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public int getCategory_issystemrecord() {
			return category_issystemrecord;
		}

		public void setCategory_issystemrecord(int category_issystemrecord) {
			this.category_issystemrecord = category_issystemrecord;
		}

		public String getCategory_categorytype() {
			return category_categorytype;
		}

		public void setCategory_categorytype(String category_categorytype) {
			
			int categoryTypeInt = Integer.parseInt( category_categorytype );
			String mCategoryType = "EXPENSE";
			if (categoryTypeInt == 0) {
				 mCategoryType = "EXPENSE";
			}else {
				 mCategoryType = "INCOME";
			}
			this.category_categorytype = mCategoryType;
			
		}

		public Category() {

		}
		
	 public void insertOrUpdate() { //根据state操作数据库，下载后的处理
			
			if (state.equals("0")) {
				CategoryDao.deleteCategoryByUUid(context, uuid);
			} else if (state.equals("1")){
				
				List<Map<String, Object>> mList= CategoryDao.checkCategoryByUUid(context, uuid);
				if ( mList.size() > 0) {
					
				    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
				    if (localDateTime_sync < dateTime.getTime()) {
				    	CategoryDao.updateCategoryAll(context, category_categoryname, ConversionType(category_categorytype), Common.positionCategory(category_iconname), category_issystemrecord, category_isdefault, dateTime.getTime(), state, uuid);
					}
					
				}else {
					
					   List<Map<String, Object>> mNameList= CategoryDao.checkCategoryByName(context, category_categoryname);
					   if (mNameList.size() <= 0) {
					   CategoryDao.insertCategoryAll(context, category_categoryname, ConversionType(category_categorytype), Common.positionCategory(category_iconname), category_issystemrecord, category_isdefault, dateTime.getTime(), state, uuid);
					  }
				  }
			}
			
		}
	 
	 public int ConversionType(String aType) {
		int type = 0;
		if (aType.equals("EXPENSE")) {
			type = 0;
		}else if (aType.equals("INCOME")) {
			type = 1;
		}
		return type;
	}
	
		
		public void setIncomingData(DbxRecord iRecord) { 
			
			category_iconname = iRecord.getString("category_iconname") ;
			uuid = iRecord.getString("uuid") ;
			category_isdefault = (int)iRecord.getLong("category_isdefault");
			category_categoryname = iRecord.getString("category_categoryname");
			dateTime = iRecord.getDate("dateTime");
			state = iRecord.getString("state");
			category_issystemrecord = (int)iRecord.getLong("category_issystemrecord");
			category_categorytype = iRecord.getString("category_categorytype");
			
		}

		public void setCategoryData(Map<String, Object> mMap) {
			
			category_iconname = Common.CATEGORYSYNCNAME [ Integer.parseInt((String) mMap.get("category_iconname") ) ] ;
			uuid = (String) mMap.get("uuid");
			category_isdefault = (Integer) mMap.get("category_isdefault");
			category_categoryname = (String) mMap.get("category_categoryname");
			dateTime = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime"));
			state = (String) mMap.get("state");
			category_issystemrecord = (Integer) mMap.get("category_issystemrecord");
			
			int categoryTypeInt = Integer.parseInt( (String) mMap.get("category_categorytype") );
			String mCategoryType = "EXPENSE";
			if (categoryTypeInt == 0) {
				 mCategoryType = "EXPENSE";
			}else {
				 mCategoryType = "INCOME";
			}
			category_categorytype = mCategoryType;
			
		}
		
		public DbxFields getFieldsName() {

			DbxFields accountsFields = new DbxFields();
			
		
			if (uuid != null) {
				accountsFields.set("uuid", uuid);
			}
			
			if (category_categoryname != null) {
				accountsFields.set("category_categoryname", category_categoryname);
			}
			
			if (dateTime != null) {
				accountsFields.set("dateTime", dateTime);
			}
			
			return accountsFields;
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			
			if (category_iconname != null) {
				accountsFields.set("category_iconname",category_iconname);
			}
		
			if (uuid != null) {
				accountsFields.set("uuid", uuid);
			}
			accountsFields.set("category_isdefault",category_isdefault);
			
			if (category_categoryname != null) {
				accountsFields.set("category_categoryname", category_categoryname);
			}
			
			if (dateTime != null) {
				accountsFields.set("dateTime", dateTime);
			}
			
			if (state != null) {
				accountsFields.set("state", state);
			}
			accountsFields.set("category_issystemrecord",category_issystemrecord);
			
			if (category_categorytype != null) {
				accountsFields.set("category_categorytype", category_categorytype);
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

	public Category getCategory() {
		Category accounts = new Category();
		return accounts;
	}

	public CategoryTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_category_table");
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

//		if (thisFields.hasField("category_categoryname")) {
//			DbxFields queryCaName = new DbxFields(); // 判断改名字的Category的个数
//			queryCaName.set("category_categoryname", thisFields.getString("category_categoryname"));
//			DbxTable.QueryResult nameResults = mTable.query(queryCaName);
//			int  resultsCount = nameResults.count();
//			if (resultsCount >= 2) {
//				
//			}
//		}
//		
		
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
