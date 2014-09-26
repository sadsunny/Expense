package com.appxy.pocketexpensepro.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class EP_BillItemTable {

	private DbxDatastore mDatastore;
	private DbxTable mTable;
	private Context context;
	private final static long day = 24 * 3600 * 1000L;

	public class EP_BillItem {

		private String billitemhasbillrule;
		private String billitem_ep_billitemstring1;
		private String uuid;
		private String billitemhascategory;
		private Date billitem_ep_billitemduedate; // 与ios存入的时间相反
		private double billitem_ep_billitemamount;
		private String billitem_ep_billitemname;
		private String billitemhaspayee;
		private Date dateTime;
		private Date billitem_ep_billitemenddate;
		private String state;
		private String billitem_ep_billitemreminderdate; // 提醒提前时间
		private Date billitem_ep_billitemremindertime; // 提醒具体时间
		private String billitem_ep_billitemrecurring;
		private Date billitem_ep_billitemduedatenew;// 与ios存入的时间相反
		private String billitem_ep_billisdelete; // android存入的为012.
													// ios0表示该特例删除，1表示存在. 01状态相反
		private String billitem_ep_billitemnote;

		public String getBillitemhasbillrule() {
			return billitemhasbillrule;
		}

		public void setBillitemhasbillrule(String billitemhasbillrule) {
			this.billitemhasbillrule = billitemhasbillrule;
		}

		public String getBillitem_ep_billitemstring1() {
			return billitem_ep_billitemstring1;
		}

		public void setBillitem_ep_billitemstring1(
				String billitem_ep_billitemstring1) {
			this.billitem_ep_billitemstring1 = billitem_ep_billitemstring1;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getBillitemhascategory() {
			return billitemhascategory;
		}

		public void setBillitemhascategory(String billitemhascategory) {
			this.billitemhascategory = billitemhascategory;
		}

		public Date getBillitem_ep_billitemduedate() {
			return billitem_ep_billitemduedate;
		}

		public void setBillitem_ep_billitemduedate(
				Date billitem_ep_billitemduedate) {
			this.billitem_ep_billitemduedate = billitem_ep_billitemduedate;
		}

		public double getBillitem_ep_billitemamount() {
			return billitem_ep_billitemamount;
		}

		public void setBillitem_ep_billitemamount(
				double billitem_ep_billitemamount) {
			this.billitem_ep_billitemamount = billitem_ep_billitemamount;
		}

		public String getBillitem_ep_billitemname() {
			return billitem_ep_billitemname;
		}

		public void setBillitem_ep_billitemname(String billitem_ep_billitemname) {
			this.billitem_ep_billitemname = billitem_ep_billitemname;
		}

		public String getBillitemhaspayee() {
			return billitemhaspayee;
		}

		public void setBillitemhaspayee(String billitemhaspayee) {
			this.billitemhaspayee = billitemhaspayee;
		}

		public Date getDateTime() {
			return dateTime;
		}

		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}

		public Date getBillitem_ep_billitemenddate() {
			return billitem_ep_billitemenddate;
		}

		public void setBillitem_ep_billitemenddate(
				Date billitem_ep_billitemenddate) {
			this.billitem_ep_billitemenddate = billitem_ep_billitemenddate;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getBillitem_ep_billitemreminderdate() {
			return billitem_ep_billitemreminderdate;
		}

		public void setBillitem_ep_billitemreminderdate(
				String billitem_ep_billitemreminderdate) {
			this.billitem_ep_billitemreminderdate = billitem_ep_billitemreminderdate;
		}

		public Date getBillitem_ep_billitemremindertime() {
			return billitem_ep_billitemremindertime;
		}

		public void setBillitem_ep_billitemremindertime(
				Date billitem_ep_billitemremindertime) {
			this.billitem_ep_billitemremindertime = billitem_ep_billitemremindertime;
		}

		public String getBillitem_ep_billitemrecurring() {
			return billitem_ep_billitemrecurring;
		}

		public void setBillitem_ep_billitemrecurring(
				String billitem_ep_billitemrecurring) {
			this.billitem_ep_billitemrecurring = billitem_ep_billitemrecurring;
		}

		public Date getBillitem_ep_billitemduedatenew() {
			return billitem_ep_billitemduedatenew;
		}

		public void setBillitem_ep_billitemduedatenew(
				Date billitem_ep_billitemduedatenew) {
			this.billitem_ep_billitemduedatenew = billitem_ep_billitemduedatenew;
		}

		public String getBillitem_ep_billisdelete() {
			return billitem_ep_billisdelete;
		}

		public void setBillitem_ep_billisdelete(String billitem_ep_billisdelete) {
			this.billitem_ep_billisdelete = billitem_ep_billisdelete;
		}

		public String getBillitem_ep_billitemnote() {
			return billitem_ep_billitemnote;
		}

		public void setBillitem_ep_billitemnote(String billitem_ep_billitemnote) {
			this.billitem_ep_billitemnote = billitem_ep_billitemnote;
		}

		public EP_BillItem() {

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

		

		public void setEP_BillItemData(Map<String, Object> mMap) {

			String billRuleUUID = SyncDao.selectBillRuleUUid(context,
					Integer.parseInt( (String) mMap.get("billitemhasbillrule")) ); // 获取UUID
			billitemhasbillrule = billRuleUUID;
			billitem_ep_billitemstring1 = billRuleUUID+" "
					+ ((String) mMap.get("billitem_ep_billitemstring1"))
							.split(" ")[1];
			uuid = (String) mMap.get("uuid");

			long billitemduedatenew = (Long) mMap
					.get("billitem_ep_billitemduedatenew"); // due时间
			long theDateTime = (Long) mMap.get("dateTime");

			billitemhascategory = SyncDao.selectCategoryUUid(context,
					Integer.parseInt( (String) mMap.get("billitemhascategory")) );
			billitem_ep_billitemduedate = MEntity
					.getMilltoDateFormat(billitemduedatenew);
			billitem_ep_billitemamount = (Double) mMap
					.get("billitem_ep_billitemamount");
			billitem_ep_billitemname = (String) mMap
					.get("billitem_ep_billitemname");

			billitemhaspayee = SyncDao.selectPayeeUUid(context, Integer.parseInt( (String) mMap.get("billitemhaspayee") ) );
			dateTime = MEntity.getMilltoDateFormat(theDateTime);
			billitem_ep_billitemenddate = MEntity
					.getMilltoDateFormat((Long) mMap
							.get("billitem_ep_billitemenddate"));
			state = (String) mMap.get("state");

			billitem_ep_billitemreminderdate = MEntity.reminderDate(Integer
					.parseInt((String) mMap
							.get("billitem_ep_billitemreminderdate")));
			billitem_ep_billitemremindertime = MEntity
					.getMilltoDateFormat(getMillis2Int(theDateTime)
							+ Long.parseLong( (String) mMap
									.get("billitem_ep_billitemremindertime")) ); // 需要加上时间转换
			billitem_ep_billitemrecurring = MEntity.turnTorecurring(Integer
					.parseInt((String) mMap
							.get("billitem_ep_billitemrecurring")));
			billitem_ep_billitemduedatenew = MEntity
					.getMilltoDateFormat((Long) mMap
							.get("billitem_ep_billitemduedate"));

			int isdelete = Integer.parseInt((String) mMap
					.get("billitem_ep_billisdelete"));
			if (isdelete == 0 || isdelete == 2) {
				billitem_ep_billisdelete = "1";
			} else {
				billitem_ep_billisdelete = "0";
			}

			billitem_ep_billitemnote = (String) mMap
					.get("billitem_ep_billitemnote");

		}

		public DbxFields getFields() {

			DbxFields accountsFields = new DbxFields();
			accountsFields.set("billitemhasbillrule", billitemhasbillrule);
			accountsFields.set("billitem_ep_billitemstring1",
					billitem_ep_billitemstring1);
			accountsFields.set("uuid", uuid);
			
			if (billitemhascategory != null && billitemhascategory.length() > 0) {
				accountsFields.set("billitemhascategory", billitemhascategory);
			}
			
			accountsFields.set("billitem_ep_billitemduedate",
					billitem_ep_billitemduedate);
			accountsFields.set("billitem_ep_billitemamount",
					billitem_ep_billitemamount);
			accountsFields.set("billitem_ep_billitemname",
					billitem_ep_billitemname);
			if (billitemhaspayee != null && billitemhaspayee.length() > 0) {
				accountsFields.set("billitemhaspayee", billitemhaspayee);
			}
			accountsFields.set("datetime", dateTime);
			accountsFields.set("billitem_ep_billitemenddate",
					billitem_ep_billitemenddate);
			accountsFields.set("state", state);
			accountsFields.set("billitem_ep_billitemreminderdate",
					billitem_ep_billitemreminderdate);
			accountsFields.set("billitem_ep_billitemremindertime",
					billitem_ep_billitemremindertime);
			accountsFields.set("billitem_ep_billitemrecurring",
					billitem_ep_billitemrecurring);
			accountsFields.set("billitem_ep_billitemduedatenew",
					billitem_ep_billitemduedatenew);
			accountsFields.set("billitem_ep_billisdelete",
					billitem_ep_billisdelete);
			if (billitem_ep_billitemnote != null) {
				accountsFields.set("billitem_ep_billitemnote",
						billitem_ep_billitemnote);
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

	public EP_BillItem getEP_BillItem() {
		EP_BillItem accounts = new EP_BillItem();
		return accounts;
	}

	public EP_BillItemTable(DbxDatastore datastore, Context context) {

		mDatastore = datastore;
		mTable = datastore.getTable("db_ep_billitem_table");
		this.context = context;
	}

	public void insertRecords(DbxFields thisFields) throws DbxException {

		DbxFields queryParams = new DbxFields().set("uuid",
				thisFields.getString("uuid"));
		DbxTable.QueryResult results = mTable.query(queryParams);
		Iterator<DbxRecord> it = results.iterator();

		if (it.hasNext()) {
			DbxRecord firstResult = it.next();
			if (firstResult.getDate("datetime").getTime() < thisFields.getDate(
					"datetime").getTime()) { // 比对同步时间

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
