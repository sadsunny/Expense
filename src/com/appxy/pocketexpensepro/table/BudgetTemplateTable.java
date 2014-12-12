package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.setting.category.CategoryDao;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class BudgetTemplateTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;

	public class BudgetTemplate {
		
		private String uuid;
		private String budgettemplate_category;
		private int budgettemplate_isrollover = 0;
		
		private int budgettemplate_isnew = 0;
		private Date dateTime;
		private String state;
		
		private Date budgettemplate_startdate;
		private double budgettemplate_amount;
		private String budgettemplate_cycletype;

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getBudgettemplate_category() {
			return budgettemplate_category;
		}

		public void setBudgettemplate_category(int budgettemplate_category) {
			this.budgettemplate_category = SyncDao.selectCategoryUUid(context, budgettemplate_category) ;
		}

		public int getBudgettemplate_isrollover() {
			return budgettemplate_isrollover;
		}

		public void setBudgettemplate_isrollover(int budgettemplate_isrollover) {
			this.budgettemplate_isrollover = budgettemplate_isrollover;
		}

		public int getBudgettemplate_isnew() {
			return budgettemplate_isnew;
		}

		public void setBudgettemplate_isnew(int budgettemplate_isnew) {
			this.budgettemplate_isnew = budgettemplate_isnew;
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

		public Date getBudgettemplate_startdate() {
			return budgettemplate_startdate;
		}

		public void setBudgettemplate_startdate(Date budgettemplate_startdate) {
			this.budgettemplate_startdate = budgettemplate_startdate;
		}

		public double getBudgettemplate_amount() {
			return budgettemplate_amount;
		}

		public void setBudgettemplate_amount(double budgettemplate_amount) {
			this.budgettemplate_amount = budgettemplate_amount;
		}

		public String getBudgettemplate_cycletype() {
			return budgettemplate_cycletype;
		}

		public void setBudgettemplate_cycletype(String budgettemplate_cycletype) {
			this.budgettemplate_cycletype = budgettemplate_cycletype;
		}

		public BudgetTemplate() {

		}
		
		public void setIncomingData(DbxRecord iRecord) { 
			
			uuid = iRecord.getString("uuid");
			budgettemplate_category =  iRecord.getString("budgettemplate_category");
			
			if (iRecord.hasField("budgettemplate_isrollover")) {
				budgettemplate_isrollover = (int) iRecord.getLong("budgettemplate_isrollover");
			}
			
			budgettemplate_isnew = (int) iRecord.getLong("budgettemplate_isnew");
			dateTime = iRecord.getDate("dateTime");
			state = iRecord.getString("state");
			budgettemplate_startdate =iRecord.getDate("budgettemplate_startdate");
			budgettemplate_amount = iRecord.getDouble("budgettemplate_amount");
			budgettemplate_cycletype = iRecord.getString("budgettemplate_cycletype");
			
		}
		
		 public void insertOrUpdate() { //根据state操作数据库，下载后的处理 根据category的唯一性操作
				
			    int categoryId = PayeeDao.selectCategoryIdByUUid(context, budgettemplate_category);
			 
				if (state.equals("0")) {
					
					if (categoryId == 0) {
						BudgetsDao.deleteBudgetTemByUUId(context, uuid);
					}else {
						BudgetsDao.deleteBudgetTemByCategory(context, categoryId+"");
					}
					
				} else if (state.equals("1")){
					
					List<Map<String, Object>> mList ;
					if (categoryId  == 0) {
						mList= BudgetsDao.checkBudgetTemplateByUUid(context, uuid);
					}else {
						mList= BudgetsDao.checkBudgetTemplateByCategory(context, categoryId);
					}
					
					if ( mList.size() > 0) {
						
					    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
					    if (localDateTime_sync < dateTime.getTime()) {
					    	
					    BudgetsDao.updateBudgetTemplateAll(context, budgettemplate_amount+"", PayeeDao.selectCategoryIdByUUid(context, budgettemplate_category), budgettemplate_isrollover, budgettemplate_isnew, budgettemplate_startdate.getTime(), budgettemplate_cycletype, dateTime.getTime(), uuid, state);

						}
						
					}else {
						
						BudgetsDao.insertBudgetTemplateAll(context, budgettemplate_amount+"", PayeeDao.selectCategoryIdByUUid(context, budgettemplate_category), budgettemplate_isrollover, budgettemplate_isnew, budgettemplate_startdate.getTime(), budgettemplate_cycletype, dateTime.getTime(), uuid, state);
					}
				}
				
			}
		 

		public void setBudgetTemplateData(Map<String, Object> mMap) {
			
			uuid = (String) mMap.get("uuid");
			budgettemplate_category =  SyncDao.selectCategoryUUid(context, Integer.parseInt( (String) mMap.get("budgettemplate_category") ) );
			budgettemplate_isrollover = (Integer) mMap.get("budgettemplate_isrollover");
			budgettemplate_isnew = (Integer) mMap.get("budgettemplate_isnew");
			dateTime = MEntity.getMilltoDateFormat((Long) mMap.get("datetime"));
			state = (String) mMap.get("state");
			budgettemplate_startdate = MEntity.getMilltoDateFormat((Long) mMap.get("budgettemplate_startdate"));
			budgettemplate_amount = (Double) mMap.get("budgettemplate_amount");
			budgettemplate_cycletype = (String) mMap
					.get("budgettemplate_cycletype");

		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			if (uuid != null) {
				accountsFields.set("uuid", uuid);
			}
			
			if (budgettemplate_category != null) {
				accountsFields.set("budgettemplate_category",
						budgettemplate_category);
			}
			
			accountsFields.set("budgettemplate_isrollover",
					budgettemplate_isrollover);
			
			accountsFields.set("budgettemplate_isnew", budgettemplate_isnew);
			
			if (dateTime != null) {
				accountsFields.set("dateTime", dateTime);
			}
			
			if (state != null) {
				accountsFields.set("state", state);
			}
			
			if (budgettemplate_startdate !=  null) {
				accountsFields.set("budgettemplate_startdate",
						budgettemplate_startdate);
			}
			if (budgettemplate_amount > 0) {
				accountsFields.set("budgettemplate_amount", budgettemplate_amount);
			}
		
			if (budgettemplate_cycletype != null) {
			accountsFields.set("budgettemplate_cycletype",
					budgettemplate_cycletype);
		
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
			
			while (it.hasNext()) {
				DbxRecord r = it.next();
				r.deleteRecord();
			}
		}

	}

	public BudgetTemplate getBudgetTemplate() {
		BudgetTemplate accounts = new BudgetTemplate();
		return accounts;
	}

	public BudgetTemplateTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_budgettemplate_table");
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
		
		if (thisFields.hasField("budgettemplate_category")) {
			
		queryParams.set("budgettemplate_category", thisFields.getString("budgettemplate_category"));
		
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

}
