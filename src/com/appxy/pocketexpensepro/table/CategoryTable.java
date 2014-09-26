package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
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
			this.category_iconname = category_iconname;
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
			this.category_categorytype = category_categorytype;
		}

		public Category() {

		}

		public void setCategoryData(Map<String, Object> mMap) {
			
			category_iconname = Common.getID2Name(context, Common.CATEGORY_ICON [ Integer.parseInt((String) mMap.get("category_iconname")) ] );
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

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("category_iconname",category_iconname);
			accountsFields.set("uuid", uuid);
			accountsFields.set("category_isdefault",category_isdefault);
			accountsFields.set("category_categoryname", category_categoryname);
			accountsFields.set("dateTime", dateTime);
			accountsFields.set("state", state);
			accountsFields.set("category_issystemrecord",category_issystemrecord);
			accountsFields.set("category_categorytype", category_categorytype);

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

	public Category getCategory() {
		Category accounts = new Category();
		return accounts;
	}

	public CategoryTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_category_table");
		this.context = context;
	}

	public void insertRecords(DbxFields thisFields) throws DbxException {

		DbxFields queryParams = new DbxFields();
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
