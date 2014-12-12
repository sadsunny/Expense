package com.appxy.pocketexpensepro.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpenseDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ExpenseDatabase";
	private static final int DATABASE_VERSION = 2; //更新数据库，之前版本为1。本次更新主要添加同步字段数据
	private SQLiteDatabase db;
	private static ExpenseDBHelper instance;
	
	public ExpenseDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	

    public static synchronized ExpenseDBHelper getHelper(Context context)
    {
        if (instance == null)
            instance = new ExpenseDBHelper(context);

        return instance;
    }

//	private volatile static ExpenseDBHelper uniqueInstance;  
//    public static ExpenseDBHelper getInstance(Context context) {  
//      if (uniqueInstance == null) {  
//        synchronized (ExpenseDBHelper.class) {  
//          if (uniqueInstance == null) {  
//            uniqueInstance = new ExpenseDBHelper(context);  
//          }  
//        }  
//      }  
//      return uniqueInstance;  
//    }  
  
    
	@Override
	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		// TODO Auto-generated method stub
		this.db = paramSQLiteDatabase;
		String CREATE_Accounts_TABLE = "CREATE TABLE IF NOT EXISTS Accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accName TEXT, amount TEXT, autoClear INTEGER, checkNumber TEXT, creditLimit Double, dateTime INTEGER, dateTime_sync INTEGER, dueDate INTEGER, iconName TEXT, orderIndex INTEGER, others TEXT, reconcile INTEGER, runningBalance INTEGER, state TEXT, uuid TEXT, accountType INTEGER, expenseTransactions INTEGER, fromBill INTEGER, incomeTransactions INTEGER, toBill INTEGER, transactionRep INTEGER, FOREIGN KEY(accountType) REFERENCES AccountType(_id))";
		paramSQLiteDatabase.execSQL(CREATE_Accounts_TABLE);

		String CREATE_AccountType_TABLE = "CREATE TABLE  IF NOT EXISTS AccountType ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, iconName TEXT, isDefault INTEGER, ordexIndex INTEGER, others TEXT, state TEXT, typeName TEXT, uuid TEXT,accounts INTEGER)";
		paramSQLiteDatabase.execSQL(CREATE_AccountType_TABLE);

		String CREATE_Category_TABLE = "CREATE TABLE  IF NOT EXISTS Category ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,categoryisExpense INTEGER, categoryisIncome INTEGER, categoryName TEXT, categoryString TEXT, categoryType TEXT, colorName INTEGER, dateTime INTEGER, hasBudget INTEGER, iconName TEXT, isDefault INTEGER, isSystemRecord INTEGER, others TEXT, recordIndex INTEGER, state TEXT, uuid TEXT, billItem INTEGER, billRep INTEGER, budgetTemplate INTEGER, categoryHasBillItem INTEGER, categoryHasBillRule INTEGER, payee INTEGER, transactionRep INTEGER, transactions INTEGER)";
		paramSQLiteDatabase.execSQL(CREATE_Category_TABLE);

		String CREATE_Payee_TABLE = "CREATE TABLE  IF NOT EXISTS Payee ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, memo TEXT, name TEXT, orderIndex INTEGER, others TEXT, phone TEXT, state TEXT, tranAmount Double, tranCleared INTEGER, tranMemo TEXT, tranType TEXT, uuid TEXT, website TEXT, billItem INTEGER, category INTEGER, payeeHasBillIRule INTEGER, payeeHasBillItem INTEGER, 'transaction' INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREATE_Payee_TABLE);

		String CREAT_Transaction_TABLE = "CREATE TABLE  IF NOT EXISTS [Transaction] ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime INTEGER, dateTime_sync INTEGER, groupByDate TEXT, isClear INTEGER, notes TEXT, orderIndex TEXT, others TEXT, photoName TEXT, recurringType INTEGER, state TEXT, transactionBool INTEGER, transactionstring TEXT, transactionType TEXT, type INTEGER, uuid TEXT, billItem INTEGER, category INTEGER, childTransactions TEXT, expenseAccount INTEGER, incomeAccount INTEGER, parTransaction INTEGER, payee INTEGER, transactionHasBillItem INTEGER, transactionHasBillRule INTEGER, transactionRule INTEGER, FOREIGN KEY(payee) REFERENCES Payee(_id) ON DELETE CASCADE ,FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE, FOREIGN KEY(expenseAccount) REFERENCES Accounts(_id) ON DELETE CASCADE, FOREIGN KEY(incomeAccount) REFERENCES Accounts(_id) ON DELETE CASCADE, FOREIGN KEY(transactionHasBillItem) REFERENCES EP_BillItem(_id) ON DELETE CASCADE, FOREIGN KEY(transactionHasBillRule) REFERENCES EP_BillRule(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_Transaction_TABLE);

		String CREAT_EP_BillItem_TABLE = "CREATE TABLE  IF NOT EXISTS EP_BillItem ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, ep_billisDelete INTEGER, ep_billItemAmount Double, ep_billItemBool1 INTEGER, ep_billItemBool2 INTEGER, ep_billItemDate1 TEXT, ep_billItemDate2 TEXT, ep_billItemDueDate INTEGER, ep_billItemDueDateNew INTEGER, ep_billItemEndDate INTEGER, ep_billItemName TEXT, ep_billItemNote TEXT, ep_billItemRecurringType TEXT, ep_billItemReminderDate TEXT, ep_billItemReminderTime INTEGER, ep_billItemString1 TEXT, ep_billItemString2 TEXT, state TEXT, uuid TEXT, billItemHasBillRule INTEGER, billItemHasCategory INTEGER, billItemHasPayee INTEGER, billItemHasTransaction INTEGER, FOREIGN KEY(billItemHasCategory) REFERENCES Category(_id) ON DELETE CASCADE,FOREIGN KEY(billItemHasBillRule) REFERENCES EP_BillRule(_id) ON DELETE CASCADE )";
		paramSQLiteDatabase.execSQL(CREAT_EP_BillItem_TABLE);

		String CREAT_EP_BillRule_TABLE = "CREATE TABLE  IF NOT EXISTS EP_BillRule ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, ep_billAmount Double, ep_billDueDate INTEGER, ep_billEndDate INTEGER, ep_billName TEXT, ep_bool1 INTEGER, ep_bool2 INTEGER, ep_date1 TEXT, ep_date2 TEXT, ep_note TEXT, ep_recurringType TEXT, ep_reminderDate TEXT, ep_reminderTime INTEGER, ep_string1 TEXT, ep_string2 TEXT, state TEXT, uuid TEXT, billRuleHasBillItem INTEGER, billRuleHasCategory INTEGER, billRuleHasPayee INTEGER, billRuleHasTransaction INTEGER, FOREIGN KEY(billRuleHasCategory) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_EP_BillRule_TABLE);

		String CREAT_BudgetItem_TABLE = "CREATE TABLE  IF NOT EXISTS BudgetItem ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime INTEGER, endDate INTEGER, isCurrent INTEGER, isRollover INTEGER, orderIndex INTEGER, rolloverAmount Double, startDate INTEGER, state TEXT, uuid TEXT, budgetTemplate INTEGER, fromTransfer INTEGER, toTransfer INTEGER, FOREIGN KEY(budgetTemplate) REFERENCES BudgetTemplate(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetItem_TABLE);

		String CREAT_BudgetTemplate_TABLE = "CREATE TABLE IF NOT EXISTS BudgetTemplate ( _id INTEGER PRIMARY KEY AUTOINCREMENT , amount TEXT, cycleType TEXT, dateTime INTEGER, isNew INTEGER, isRollover INTEGER, orderIndex INTEGER, startDate INTEGER, startDateHasChange INTEGER, state TEXT, uuid TEXT, budgetItems INTEGER, budgetRep INTEGER, category INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetTemplate_TABLE);

		String CREAT_BudgetTransfer_TABLE = "CREATE TABLE IF NOT EXISTS BudgetTransfer ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime INTEGER, dateTime_sync INTEGER, state TEXT, uuid TEXT, fromBudget INTEGER, toBudget INTEGER , FOREIGN KEY(fromBudget) REFERENCES BudgetItem(_id) ON DELETE CASCADE,FOREIGN KEY(toBudget) REFERENCES BudgetItem(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetTransfer_TABLE);
		
		String CREAT_Setting_TABLE = "CREATE TABLE IF NOT EXISTS Setting ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,accDRendDate TEXT, accDRstartDate TEXT, accDRstring TEXT, budgetNewStyle TEXT, budgetNewStyleCycle TEXT, cateDRendDate TEXT, cateDRstartDate TEXT, cateDRstring TEXT, currency TEXT, dateTime TEXT, expDRString TEXT, expenseLastView TEXT, isBefore TEXT, otherBool TEXT, otherBool1 TEXT, otherBool2 TEXT, otherBool3 TEXT, otherBool4 TEXT, otherBool5 TEXT, otherBool6 TEXT, otherBool7 TEXT, otherBool8 TEXT, otherBool9 TEXT, otherBool10 TEXT, otherBool11 TEXT, otherBool12 TEXT, otherBool13 TEXT, otherBool14 TEXT, otherBool15 TEXT, otherBool16 TEXT, otherBool17 TEXT, otherBool18 TEXT, otherBool19 TEXT, otherBool20 TEXT, others TEXT, others1 TEXT, others2 TEXT, others3 TEXT, others4 TEXT, others5 TEXT, others6 TEXT, others7 TEXT, others8 TEXT, others9 TEXT, others10 TEXT, others11 TEXT, others12 TEXT, others13 TEXT, others14 TEXT, others15 TEXT, others16 TEXT, others17 TEXT, others18 TEXT, others19 TEXT, others20 TEXT, passcode TEXT, payeeCategory INTEGER, payeeCfged INTEGER, payeeMemo INTEGER, payeeName INTEGER, payeeTranAmount INTEGER, payeeTranClear INTEGER, payeeTranMemo INTEGER, payeeTranType INTEGER, playorder INTEGER, sortType TEXT, state TEXT, uuid TEXT, weekstartday TEXT)" ;
	    paramSQLiteDatabase.execSQL(CREAT_Setting_TABLE);

	
		accountTypeIni(0, 1, "Asset", "F2243FC7-6E01-4CD8-8A03-6AE56E7B20E1");
		accountTypeIni(1, 1, "Cash", "9832B8FA-537C-4963-8CA9-19385E9732E5");
		accountTypeIni(2, 1, "Checking","9C4251B9-5B57-4472-8B6E-BAF1A4D60650");
		accountTypeIni(3, 1, "Credit Card", "4C9ACC13-D22D-4A7F-ABB3-7A5A7C94EAA2");
		accountTypeIni(4, 1, "Debit Card", "A54BB0EF-17DF-4BA5-BB1E-A24AC31DA138");
		accountTypeIni(5, 1, "Investing/Retirement", "A8D6FFD2-602B-4E23-AA86-44751A2234C6");
		accountTypeIni(6, 1, "Loan", "B10A95AC-6BA2-401A-9A67-AF667313872F");
		accountTypeIni(8, 1, "Others", "EB77B173-7BE4-458E-B1DD-0309EBF3A12C");
		accountTypeIni(7, 1, "Savings", "3E3BEB88-153A-4ACB-AE15-3B2B7935D56E");

		categoryIni("Auto", 0, 0, 15, 0, "0F6FD33B-E575-448D-9DFC-FBDD46BB244F");
		categoryIni("Auto:Gas", 0, 0, 15, 0,"33F11CA3-50C8-491D-823A-66F8DA2632D9");
		categoryIni("Auto:Registration", 0, 0, 15, 0,"5760538F-A7CF-4EEC-873C-03EF9A7D6FE0");
		categoryIni("Auto:Service", 0, 0, 15, 0, "1D1C1F8D-4CE7-4467-AA1C-8C99DFFE64F8");
		categoryIni("Bank Charge", 0, 0, 8, 0, "EBBE58EC-32F7-49FB-8C26-5E49572D0355");
		categoryIni("Bonus", 1, 0, 10, 0, "65E255EE-B01C-498A-8A6D-980787DBB16B");
		categoryIni("Cash", 0, 0, 16, 0, "CBB79C68-B40A-4FBC-A89D-714EEF0C610C");
		categoryIni("Charity", 0, 0, 18, 0, "E1A3532A-77E6-4E72-A16F-3E9A7C36E81E");
		categoryIni("Childcare", 0, 0, 19, 0, "79AA0903-0A7A-4923-A92D-5EA59F71A43C");
		categoryIni("Clothing", 0, 0, 20, 0, "F6C83821-FCEF-492C-B4EE-940C80457546");
		
		categoryIni("Credit Card Payment", 0, 0, 25, 0, "03D897A0-DD0F-4F4D-A72A-03DA70348BFE");
		categoryIni("Eating Out", 0, 0, 28, 0, "CA6C55B4-2B95-4921-9AE4-74E76A253426");
		categoryIni("Education", 0, 0, 29, 0, "B7AD59FE-13C4-4476-8DE0-D8CC94F7A20D");
		categoryIni("Entertainment", 0, 0, 30, 0,"9344BA4C-7B9E-40D5-8A05-9FECD60C63AB");
		categoryIni("Gifts", 0, 0, 35, 0, "A515223D-318F-420F-B366-CE207EC2D512");
		categoryIni("Groceries", 0, 0, 36, 0, "41FC8F1F-CBEF-48F9-B5CC-BB47956D79B7");
		categoryIni("Health & Fitness", 0, 0, 37, 0, "8997A036-74B9-4140-BF62-0ABC349C0E08");
		categoryIni("Home Repair", 0, 0, 59, 0, "1DF5FB17-EAA2-4890-81CD-15DFCCDEB1A2");
		categoryIni("Household", 0, 0, 2, 0, "283C5A28-D76A-4EF5-9713-31FB37B6D963");
		categoryIni("Insurance", 0, 0, 43, 0, "87F4F37D-1373-4404-ADFD-6AB23823853E");
		
		categoryIni("Interest Exp", 0, 0, 61, 0, "D4AB78F5-32CE-434F-9A1D-F25FE681E71A");
		categoryIni("Loan", 0, 0, 45, 0, "1E32DE55-5EA4-4519-A3F2-AB21861F9B03");
		categoryIni("Medical", 0, 0, 47, 0, "C8A54F8C-F440-4FE8-9526-B650928F02D4");
		categoryIni("Misc", 0, 0, 49, 0, "7960FFF8-874B-4CD7-B0DF-02A8D7CA7756");
		categoryIni("Mortgage Payment", 0, 0, 3, 0, "45643F47-6105-4E21-BD54-9C1E74105D71");
		categoryIni("Others", 0, 0, 56, 0, "E15F57E7-E976-449D-831F-BCD4631C73C5");
		categoryIni("Others", 1, 0, 57, 0, "6CFF80C6-6080-4263-80D9-E0ED8DC4E606");
		categoryIni("Pets", 0, 0, 52, 0,"0206FC4C-9291-40F6-9242-16A673588515");
		categoryIni("Rent", 0, 0, 58, 0, "CD0F4454-38FA-40C3-8651-CAAD7238773E");
		categoryIni("Salary", 1, 0, 60, 0, "553E1F3C-2121-43FA-B6EF-9673C9C79F1B");
		
		categoryIni("Savings Deposit", 1, 0, 62, 0, "3944081E-93F6-4541-965E-F3077FD9695E");
		categoryIni("Tax", 0, 0, 64, 0, "5C7368FA-1E98-4777-8E0E-9D470283AE8C");
		categoryIni("Tax:Fed", 0, 0, 64, 0, "0D997AF9-3C18-45A4-AB44-C005D10CC12D");
		categoryIni("Tax:Medicare", 0, 0, 64, 0, "8DF4ACB4-B4E9-4AD1-8B6E-C670CF0E1B50");
		categoryIni("Tax:Other", 0, 0, 64, 0, "1E85D7CF-2263-4F01-839B-510CED8D5147");
		categoryIni("Tax:Property", 0, 0, 64, 0, "E1FB0951-9A62-4801-954C-99B0C87B1BDE");
		categoryIni("Tax Refund", 1, 0, 65, 0, "38B27F70-2C9F-4705-AF3B-929BD2711D21");
		categoryIni("Tax:SDI", 0, 0, 64, 0, "88D34AA2-A3F0-4A2B-8C47-B6BB26A043FB");
		categoryIni("Tax:Soc Sec", 0, 0, 64, 0, "779F4166-A755-4D91-AB87-B3328BC32B9E");
		categoryIni("Tax:State", 0, 0, 64, 0, "0B171B72-1C7A-4C2F-A7AC-A4FA50B385BB");
		
		categoryIni("Transport", 0, 0, 12, 0, "E3EB044D-F2BA-4239-8DA5-4C84D4F87616");
		categoryIni("Travel", 0, 0, 0, 0, "8002FBE6-DE5A-4C60-937E-4904204FB17C");
		categoryIni("Utilities", 0, 0, 42, 0, "9482F32A-6EFF-42CF-90B8-1C5F18C9E851");
		categoryIni("Utilities:Cable TV", 0, 0, 14, 0, "DA648D8C-C1C2-4824-8D06-6CA1D0C534E7");
		categoryIni("Utilities:Garbage & Recycling", 0, 0, 26, 0, "FA8FD9FE-B71A-40C9-A648-99F002EDC005");
		categoryIni("Utilities:Gas& Electric", 0, 0, 70, 0, "8AED613B-DC33-4A1D-9284-48EAB43744A5");
		categoryIni("Utilities:Internet", 0, 0, 44, 0, "04CDA8B1-E0E8-4B39-8143-BE47DC963EE9");
		categoryIni("Utilities:Telephone", 0, 0, 1, 0, "9810BE73-F5A8-49BC-85DE-5C1E5E8F8333");
		categoryIni("Utilities:Water", 0, 0, 73, 0, "1FD9D0CA-D3DF-4640-A541-58FA1C5338E1");
		
		settingIni(148,"22");

		accountIni( paramSQLiteDatabase , "Default Account", 0+"", System.currentTimeMillis(), 1, 8, "1", "E0552410-9082-4B31-96D3-7A777F046AB4", System.currentTimeMillis(), 1);
		
	}
	
	public  long accountIni(SQLiteDatabase db, String accName,
			String amount, long dateTime, int autoClear, int accountType,
			String state, String uuid, long dateTime_sync, int orderIndex) { // Account插入
		ContentValues cv = new ContentValues();

		cv.put("accName", accName);
		cv.put("amount", amount + "");
		cv.put("dateTime", dateTime);
		cv.put("autoClear", autoClear);
		cv.put("accountType", accountType);
		cv.put("orderIndex", orderIndex);
		
		cv.put("state", state);
		cv.put("uuid", uuid);
		cv.put("dateTime_sync", dateTime_sync);
		long id = db.insert("Accounts", null, cv);
		return id;
		

	}

	public long categoryIni(String categoryName, int categoryType,
			int hasBudget, int iconName, int isDefault, String uuid) {

		ContentValues cv = new ContentValues();
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("hasBudget", hasBudget);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("isSystemRecord", 1);
		cv.put("dateTime", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("uuid", uuid);
		
		long row = db.insert("Category", null, cv);
		return row;
	}

	public long accountTypeIni(int iconName, int isDefault, String typeName, String uuid) {

		ContentValues cv = new ContentValues();
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("typeName", typeName);
		cv.put("dateTime", System.currentTimeMillis());
		cv.put("state", 1);
		cv.put("uuid", uuid);
		
		long row = db.insert("AccountType", null, cv);
		return row;
	}

	public long settingIni(int currency, String passcode) {

		ContentValues cv = new ContentValues();
		cv.put("currency", currency);
		cv.put("passcode", passcode);
		long row = db.insert("Setting", null, cv);
		return row;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2) {
		// TODO Auto-generated method stub
		
		if (paramInt1 == 1) { // 处理老版本的数据，更新同步字段
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			long syncTime = calendar.getTimeInMillis();
			
			upgradeCategory(paramSQLiteDatabase, syncTime);
			upgradeAccountType(paramSQLiteDatabase, syncTime);
			upgradePayee(paramSQLiteDatabase, syncTime);
			upgradeAccount(paramSQLiteDatabase, syncTime);
			upgradeBudgetTemplate(paramSQLiteDatabase, syncTime);
			upgradeBudgetItem(paramSQLiteDatabase, syncTime);
			upgradeBudgetTransfer(paramSQLiteDatabase, syncTime);
			upgradeEP_BillRule(paramSQLiteDatabase, syncTime);
			upgradeEP_BillItem(paramSQLiteDatabase, syncTime);
			upgradeTransaction(paramSQLiteDatabase, syncTime);
			
//			long accountOrder = accountIni(paramSQLiteDatabase, "Default Account", 0+"", syncTime, 1, 8, 1+"", "E0552410-9082-4B31-96D3-7A777F046AB4", syncTime, 10000);
//			DbDao.updateAccountOrder(paramSQLiteDatabase, (int)accountOrder , (int)accountOrder);
		}
		
	}
	
	public void upgradeCategory(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectCategory(db);
		for (int i:mMap.values()) {
			if (i > 0 && i< 50) {
				DbDao.updateCategory(db, i, 1+"", DbEntity.categoryUUID[i-1], syncTime);
			} else {
				DbDao.updateCategory(db, i, 1+"", MEntity.getUUID(), syncTime);
			}
		}
		
	}
	
	public void upgradeAccountType(SQLiteDatabase db, long syncTime){
		Map<String, Integer> mMap = DbDao.selectAccountType(db);
		for (int i:mMap.values()) {
			if (i > 0 && i<10) {
				DbDao.updateAccountType(db, i, 1+"", DbEntity.accountTypeUUID[i-1], syncTime);
			} else {
				DbDao.updateAccountType(db, i, 1+"", MEntity.getUUID(), syncTime);
			}
		}
		
	}
	
	
	public void upgradePayee(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectPayee(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updatePayee(db, i, 1+"", MEntity.getUUID(), syncTime);
			} 
		}
		
	}
	
	public void upgradeAccount(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectAccount(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updateAccount(db, i, 1+"", MEntity.getUUID(), syncTime);
			} 
		}
		
	}
	
	public void upgradeBudgetTemplate(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectBudgetTemplate(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updateBudgetTemplate(db, i, 1+"", MEntity.getUUID(),syncTime);
			} 
		}
		
	}

	
	public void upgradeBudgetItem(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectBudgetItem(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updateBudgetItem(db, i, 1+"", MEntity.getUUID(), syncTime);
			} 
		}
		
	}
	
	public void upgradeBudgetTransfer(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectBudgetTransfer(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updateBudgetTransfer(db, i, 1+"", MEntity.getUUID(), syncTime);
			} 
		}
		
	}
	
	public void upgradeEP_BillRule(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectEP_BillRule(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updateEP_BillRule(db, i, 1+"", MEntity.getUUID(), syncTime);
			} 
		}
		
	}
	
	
	public static String turnMilltoDate(long milliSeconds) {// 将毫秒转化成固定格式的月日年
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	public void upgradeEP_BillItem(SQLiteDatabase db, long syncTime) {
		 List<Map<String, Object>> mList = DbDao.selectBillItemBy(db);
		
		for (Map<String, Object> iMap:mList) {
			
			int _id = (Integer)iMap.get("_id");
			long ep_billDueDate = (Long)iMap.get("ep_billDueDate");
			int billItemHasBillRule = (Integer)iMap.get("billItemHasBillRule");
			
			String ruleUUId = DbDao.selectBillRuleUUid(db, billItemHasBillRule);
			String ep_billItemString1 = null ;
			if (ruleUUId != null) {
				  ep_billItemString1 = DbDao.selectBillRuleUUid(db, billItemHasBillRule)+" "+turnMilltoDate(ep_billDueDate) ;
			}
			
			if (_id > 0) {
				Log.e("mtag", "ep_billItemString1"+ep_billItemString1);
				DbDao.updateEP_BillItem(db, _id, 1+"", MEntity.getUUID(), syncTime, ep_billItemString1);
			} 
		}
		
	}
	
	public void upgradeTransaction(SQLiteDatabase db, long syncTime) {
		Map<String, Integer> mMap = DbDao.selectTransaction(db);
		for (int i:mMap.values()) {
			if (i > 0) {
				DbDao.updateTransaction(db, i, 1+"", MEntity.getUUID(), syncTime);
			} 
		}
		
	}
	

}
