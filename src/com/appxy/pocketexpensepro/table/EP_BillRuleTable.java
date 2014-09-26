package com.appxy.pocketexpensepro.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class EP_BillRuleTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;
	
	public class EP_BillRule {

		private String uuid;
		private double billrule_ep_billamount;
		private String billrulehascategory;
		private String billrulehaspayee;
		private Date dateTime;
		private String state;
		private String billrule_ep_recurringtype;
		private String billrule_ep_reminderdate;
		private String billrule_ep_billname;
		private Date billrule_ep_billduedate;
		private String billrule_ep_note;
		private Date billrule_ep_billenddate;
		private Date billrule_ep_remindertime;
		
		
		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public double getBillrule_ep_billamount() {
			return billrule_ep_billamount;
		}

		public void setBillrule_ep_billamount(double billrule_ep_billamount) {
			this.billrule_ep_billamount = billrule_ep_billamount;
		}

		public String getBillrulehascategory() {
			return billrulehascategory;
		}

		public void setBillrulehascategory(String billrulehascategory) {
			this.billrulehascategory = billrulehascategory;
		}

		public String getBillrulehaspayee() {
			return billrulehaspayee;
		}

		public void setBillrulehaspayee(String billrulehaspayee) {
			this.billrulehaspayee = billrulehaspayee;
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

		public String getBillrule_ep_recurringtype() {
			return billrule_ep_recurringtype;
		}

		public void setBillrule_ep_recurringtype(String billrule_ep_recurringtype) {
			this.billrule_ep_recurringtype = billrule_ep_recurringtype;
		}

		public String getBillrule_ep_reminderdate() {
			return billrule_ep_reminderdate;
		}

		public void setBillrule_ep_reminderdate(String billrule_ep_reminderdate) {
			this.billrule_ep_reminderdate = billrule_ep_reminderdate;
		}

		public String getBillrule_ep_billname() {
			return billrule_ep_billname;
		}

		public void setBillrule_ep_billname(String billrule_ep_billname) {
			this.billrule_ep_billname = billrule_ep_billname;
		}

		public Date getBillrule_ep_billduedate() {
			return billrule_ep_billduedate;
		}

		public void setBillrule_ep_billduedate(Date billrule_ep_billduedate) {
			this.billrule_ep_billduedate = billrule_ep_billduedate;
		}

		public String getBillrule_ep_note() {
			return billrule_ep_note;
		}

		public void setBillrule_ep_note(String billrule_ep_note) {
			this.billrule_ep_note = billrule_ep_note;
		}

		public Date getBillrule_ep_billenddate() {
			return billrule_ep_billenddate;
		}

		public void setBillrule_ep_billenddate(Date billrule_ep_billenddate) {
			this.billrule_ep_billenddate = billrule_ep_billenddate;
		}

		public Date getBillrule_ep_remindertime() {
			return billrule_ep_remindertime;
		}

		public void setBillrule_ep_remindertime(Date billrule_ep_remindertime) {
			this.billrule_ep_remindertime = billrule_ep_remindertime;
		}

		public EP_BillRule() {

		}
		
		public long getMillis2Int(long mills) { // 除去时分秒的时间
			Date date1 = new Date();
			date1.setTime(mills);
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			String nowTime = formatter.format(date1);
			Calendar c = Calendar.getInstance();
			try {
				c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long nowMillis = c.getTimeInMillis();

			return nowMillis;
		}

		public void setEP_BillRuleData(Map<String, Object> mMap) {

			long theDuedate = (Long) mMap.get("billrule_ep_billduedate") ;
			uuid = (String) mMap.get("uuid");
			billrule_ep_billamount = (Double) mMap.get("billrule_ep_billamount");
			billrulehascategory = SyncDao.selectCategoryUUid(context, Integer.parseInt( (String) mMap.get("billrulehascategory") ) );
			billrulehaspayee = SyncDao.selectPayeeUUid(context, Integer.parseInt( (String) mMap.get("billrulehaspayee") ) );
			dateTime = MEntity.getMilltoDateFormat((Long) mMap.get("dateTime"));
			state = (String) mMap.get("state");
			billrule_ep_recurringtype = MEntity.turnTorecurring( Integer.parseInt((String) mMap.get("billrule_ep_recurringtype")) );
			billrule_ep_reminderdate = MEntity.reminderDate(Integer.parseInt((String) mMap.get("billrule_ep_reminderdate")));
			billrule_ep_billname = (String) mMap.get("billrule_ep_billname");
			billrule_ep_billduedate = MEntity.getMilltoDateFormat(theDuedate);
			billrule_ep_note = (String) mMap.get("billrule_ep_note");;
			billrule_ep_remindertime = MEntity.getMilltoDateFormat(getMillis2Int(theDuedate)+  (Long) mMap.get("billrule_ep_remindertime")) ;
			
			long theEnddate = (Long) mMap.get("billrule_ep_billenddate");
			if (theEnddate == -1) {
				billrule_ep_billenddate = null;
			}{
				billrule_ep_billenddate = MEntity.getMilltoDateFormat(theEnddate);
			}
			
			
		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("uuid", uuid);
			accountsFields.set("billrule_ep_billamount",billrule_ep_billamount);
			accountsFields.set("billrulehascategory",billrulehascategory);
			if (billrulehaspayee != null && billrulehaspayee.length() > 0) {
				accountsFields.set("billrulehaspayee", billrulehaspayee);
			}
			accountsFields.set("dateTime", dateTime);
			accountsFields.set("state", state);
			accountsFields.set("billrule_ep_recurringtype",billrule_ep_recurringtype);
			accountsFields.set("billrule_ep_reminderdate", billrule_ep_reminderdate);
			accountsFields.set("billrule_ep_billname",billrule_ep_billname);
			accountsFields.set("billrule_ep_billduedate",billrule_ep_billduedate);
			if (billrule_ep_note != null && billrule_ep_note.length() >0) {
				accountsFields.set("billrule_ep_note",billrule_ep_note);
			}
			if (billrule_ep_billenddate !=null) {
				accountsFields.set("billrule_ep_billenddate",billrule_ep_billenddate);
			}
			if (billrule_ep_remindertime != null) {
				accountsFields.set("billrule_ep_remindertime",billrule_ep_remindertime);
			}
			
			
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

	public EP_BillRule getEP_BillRule() {
		EP_BillRule accounts = new EP_BillRule();
		return accounts;
	}

	public EP_BillRuleTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_ep_billrule_table");
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
					"dateTime").getTime()) { 

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
