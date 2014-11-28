package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.util.Log;

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

public class BudgetItemTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;
	
	public class BudgetItem {

		private String uuid;
		private Date budgetitem_startdate;
		private double budgetitem_amount;
		private Date budgetitem_enddate;
		private double budgetitem_rolloveramount = 0;
		private Date dateTime;
		private String state;
		private String budgetitem_budgettemplate;
		private int budgetitem_isrollover = 0;

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

		public void setBudgetitem_budgettemplate(int budgetitem_budgettemplate) {
			this.budgetitem_budgettemplate = SyncDao.selectBudgetTemplateUUid(context, budgetitem_budgettemplate );
		}

		public int getBudgetitem_isrollover() {
			return budgetitem_isrollover;
		}

		public void setBudgetitem_isrollover(int budgetitem_isrollover) {
			this.budgetitem_isrollover = budgetitem_isrollover;
		}
		
		public void setIncomingData(DbxRecord iRecord) { 
			
			if (iRecord.hasField("uuid")) {
				uuid =  iRecord.getString("uuid");
			}
		
			if (iRecord.hasField("budgetitem_startdate") ) {
				budgetitem_startdate = iRecord.getDate("budgetitem_startdate");
			}
			
			budgetitem_amount = iRecord.getDouble("budgetitem_amount");
			
			if ( iRecord.hasField("budgetitem_enddate")) {
			budgetitem_enddate = iRecord.getDate("budgetitem_enddate");
			}
			
			budgetitem_rolloveramount = iRecord.getDouble("budgetitem_rolloveramount");
			dateTime = iRecord.getDate("dateTime");
			state = iRecord.getString("state");
			
			if (iRecord.hasField("budgetitem_budgettemplate")) {
				budgetitem_budgettemplate = iRecord.getString("budgetitem_budgettemplate");
			}
			
			if (iRecord.hasField("budgetitem_isrollover")) {
				budgetitem_isrollover = (int)iRecord.getLong("budgetitem_isrollover");
			}
			
			
		}
		
		public void insertOrUpdate() { //根据state操作数据库，下载后的处理
			
			int budgetTemId = BudgetsDao.selectBudgetTemplateIdByUUid(context, budgetitem_budgettemplate);
			
			if (state.equals("0")) {
				
				if (budgetTemId == 0) {
					BudgetsDao.deleteBudgetItemByUUId(context, uuid);
				}else {
					BudgetsDao.deleteBudgetItemByTemp(context, budgetTemId+"");
				}
				
			} else if (state.equals("1")){
				
				List<Map<String, Object>> mList ;
				if (budgetTemId == 0) {
					mList= BudgetsDao.checkBudgetItemByUUid(context, uuid);
				}else {
					mList= BudgetsDao.checkBudgetItemByTemp(context, budgetTemId);
				}
				
				if ( mList.size() > 0) {
					
				    long localDateTime_sync = (Long) mList.get(0).get("dateTime_sync");
				    if (localDateTime_sync < dateTime.getTime()) {
				    	
				     BudgetsDao.updateBudgetItemAll(context, budgetitem_amount+"", BudgetsDao.selectBudgetTemplateIdByUUid(context, budgetitem_budgettemplate), budgetitem_startdate.getTime(), budgetitem_isrollover, budgetitem_enddate.getTime(), budgetitem_rolloveramount, dateTime.getTime(), state, uuid);
					}
					
				}else {
					Log.v("mtag", "budgetitem_amount "+budgetitem_amount);
					Log.v("mtag", "budgetitem_startdate "+budgetitem_startdate);
					Log.v("mtag", "budgetitem_isrollover "+budgetitem_isrollover);
					Log.v("mtag", "budgetitem_enddate "+budgetitem_enddate);
					
					Log.v("mtag", "budgetitem_rolloveramount "+budgetitem_rolloveramount);
					Log.v("mtag", "dateTime "+dateTime);
					Log.v("mtag", "state "+state);
					Log.v("mtag", "uuid "+uuid);
					
					
					BudgetsDao.insertBudgetItemAll(context, budgetitem_amount+"", BudgetsDao.selectBudgetTemplateIdByUUid(context, budgetitem_budgettemplate), budgetitem_startdate.getTime(), budgetitem_isrollover, budgetitem_enddate.getTime(), budgetitem_rolloveramount, dateTime.getTime(), state, uuid);
				}
			}
			
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
			if (uuid != null) {
				accountsFields.set("uuid", uuid);
			}
			
			if (budgetitem_startdate != null) {
				accountsFields.set("budgetitem_startdate", budgetitem_startdate);
			}
			
			if (budgetitem_amount > 0 ) {
				accountsFields.set("budgetitem_amount", budgetitem_amount);
			}
			
			if (budgetitem_enddate != null) {
				accountsFields.set("budgetitem_enddate", budgetitem_enddate);
			}
			
			accountsFields.set("budgetitem_rolloveramount", budgetitem_rolloveramount);
			
			if (dateTime != null) {
				accountsFields.set("dateTime", dateTime);
			}
			
			if (state != null) {
				accountsFields.set("state", state);
			}
		
			if (budgetitem_budgettemplate != null) {
				accountsFields.set("budgetitem_budgettemplate", budgetitem_budgettemplate);
			}
			
			accountsFields.set("budgetitem_isrollover", budgetitem_isrollover);
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

	public BudgetItem getBudgetItem() {
		BudgetItem accounts = new BudgetItem();
		return accounts;
	}

	public BudgetItemTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_budgetitem_table");
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
		if (thisFields.hasField("budgetitem_budgettemplate")) {
			
		queryParams.set("budgetitem_budgettemplate", thisFields.getString("budgetitem_budgettemplate"));
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
