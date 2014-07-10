package com.appxy.pocketexpensepro.db;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpenseDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ExpenseDatabase";
	private static final int DATABASE_VERSION = 1;
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
		String CREATE_Accounts_TABLE = "CREATE TABLE Accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accName TEXT, amount TEXT, autoClear INTEGER, checkNumber TEXT, creditLimit Double, dateTime INTEGER, dateTime_sync INTEGER, dueDate INTEGER, iconName TEXT, orderIndex INTEGER, others TEXT, reconcile INTEGER, runningBalance INTEGER, state TEXT, uuid TEXT, accountType INTEGER, expenseTransactions INTEGER, fromBill INTEGER, incomeTransactions INTEGER, toBill INTEGER, transactionRep INTEGER, FOREIGN KEY(accountType) REFERENCES AccountType(_id))";
		paramSQLiteDatabase.execSQL(CREATE_Accounts_TABLE);

		String CREATE_AccountType_TABLE = "CREATE TABLE AccountType ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, iconName TEXT, isDefault INTEGER, ordexIndex INTEGER, others TEXT, state TEXT, typeName TEXT, uuid TEXT,accounts INTEGER)";
		paramSQLiteDatabase.execSQL(CREATE_AccountType_TABLE);

		String CREATE_Category_TABLE = "CREATE TABLE Category ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,categoryisExpense INTEGER, categoryisIncome INTEGER, categoryName TEXT, categoryString TEXT, categoryType TEXT, colorName INTEGER, dateTime INTEGER, hasBudget INTEGER, iconName TEXT, isDefault INTEGER, isSystemRecord INTEGER, others TEXT, recordIndex INTEGER, state TEXT, uuid TEXT, billItem INTEGER, billRep INTEGER, budgetTemplate INTEGER, categoryHasBillItem INTEGER, categoryHasBillRule INTEGER, payee INTEGER, transactionRep INTEGER, transactions INTEGER)";
		paramSQLiteDatabase.execSQL(CREATE_Category_TABLE);

		String CREATE_Payee_TABLE = "CREATE TABLE Payee ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, memo TEXT, name TEXT, orderIndex INTEGER, others TEXT, phone TEXT, state TEXT, tranAmount Double, tranCleared INTEGER, tranMemo TEXT, tranType TEXT, uuid TEXT, website TEXT, billItem INTEGER, category INTEGER, payeeHasBillIRule INTEGER, payeeHasBillItem INTEGER, 'transaction' INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREATE_Payee_TABLE);

		String CREAT_Transaction_TABLE = "CREATE TABLE [Transaction] ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime INTEGER, dateTime_sync INTEGER, groupByDate TEXT, isClear INTEGER, notes TEXT, orderIndex TEXT, others TEXT, photoName TEXT, recurringType INTEGER, state TEXT, transactionBool INTEGER, transactionstring TEXT, transactionType TEXT, type INTEGER, uuid TEXT, billItem INTEGER, category INTEGER, childTransactions TEXT, expenseAccount INTEGER, incomeAccount INTEGER, parTransaction INTEGER, payee INTEGER, transactionHasBillItem INTEGER, transactionHasBillRule INTEGER, transactionRule INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE, FOREIGN KEY(expenseAccount) REFERENCES Accounts(_id) ON DELETE CASCADE, FOREIGN KEY(transactionHasBillItem) REFERENCES EP_BillItem(_id) ON DELETE CASCADE, FOREIGN KEY(transactionHasBillRule) REFERENCES EP_BillRule(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_Transaction_TABLE);

		String CREAT_EP_BillItem_TABLE = "CREATE TABLE EP_BillItem ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, ep_billisDelete INTEGER, ep_billItemAmount Double, ep_billItemBool1 INTEGER, ep_billItemBool2 INTEGER, ep_billItemDate1 TEXT, ep_billItemDate2 TEXT, ep_billItemDueDate INTEGER, ep_billItemDueDateNew INTEGER, ep_billItemEndDate INTEGER, ep_billItemName TEXT, ep_billItemNote TEXT, ep_billItemRecurringType TEXT, ep_billItemReminderDate TEXT, ep_billItemReminderTime INTEGER, ep_billItemString1 TEXT, ep_billItemString2 TEXT, state TEXT, uuid TEXT, billItemHasBillRule INTEGER, billItemHasCategory INTEGER, billItemHasPayee INTEGER, billItemHasTransaction INTEGER, FOREIGN KEY(billItemHasCategory) REFERENCES Category(_id) ON DELETE CASCADE,FOREIGN KEY(billItemHasBillRule) REFERENCES EP_BillRule(_id) ON DELETE CASCADE )";
		paramSQLiteDatabase.execSQL(CREAT_EP_BillItem_TABLE);

		String CREAT_EP_BillRule_TABLE = "CREATE TABLE EP_BillRule ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime INTEGER, ep_billAmount Double, ep_billDueDate INTEGER, ep_billEndDate INTEGER, ep_billName TEXT, ep_bool1 INTEGER, ep_bool2 INTEGER, ep_date1 TEXT, ep_date2 TEXT, ep_note TEXT, ep_recurringType TEXT, ep_reminderDate TEXT, ep_reminderTime INTEGER, ep_string1 TEXT, ep_string2 TEXT, state TEXT, uuid TEXT, billRuleHasBillItem INTEGER, billRuleHasCategory INTEGER, billRuleHasPayee INTEGER, billRuleHasTransaction INTEGER, FOREIGN KEY(billRuleHasCategory) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_EP_BillRule_TABLE);

		String CREAT_BudgetItem_TABLE = "CREATE TABLE BudgetItem ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime INTEGER, endDate INTEGER, isCurrent INTEGER, isRollover INTEGER, orderIndex INTEGER, rolloverAmount Double, startDate INTEGER, state TEXT, uuid TEXT, budgetTemplate INTEGER, fromTransfer INTEGER, toTransfer INTEGER, FOREIGN KEY(budgetTemplate) REFERENCES BudgetTemplate(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetItem_TABLE);

		String CREAT_BudgetTemplate_TABLE = "CREATE TABLE BudgetTemplate ( _id INTEGER PRIMARY KEY AUTOINCREMENT , amount TEXT, cycleType TEXT, dateTime INTEGER, isNew INTEGER, isRollover INTEGER, orderIndex INTEGER, startDate INTEGER, startDateHasChange INTEGER, state TEXT, uuid TEXT, budgetItems INTEGER, budgetRep INTEGER, category INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetTemplate_TABLE);

		String CREAT_BudgetTransfer_TABLE = "CREATE TABLE BudgetTransfer ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime INTEGER, dateTime_sync INTEGER, state TEXT, uuid TEXT, fromBudget INTEGER, toBudget INTEGER , FOREIGN KEY(fromBudget) REFERENCES BudgetItem(_id) ON DELETE CASCADE,FOREIGN KEY(toBudget) REFERENCES BudgetItem(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetTransfer_TABLE);
		
		String CREAT_Setting_TABLE = "CREATE TABLE Setting ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,accDRendDate TEXT, accDRstartDate TEXT, accDRstring TEXT, budgetNewStyle TEXT, budgetNewStyleCycle TEXT, cateDRendDate TEXT, cateDRstartDate TEXT, cateDRstring TEXT, currency TEXT, dateTime TEXT, expDRString TEXT, expenseLastView TEXT, isBefore TEXT, otherBool TEXT, otherBool1 TEXT, otherBool2 TEXT, otherBool3 TEXT, otherBool4 TEXT, otherBool5 TEXT, otherBool6 TEXT, otherBool7 TEXT, otherBool8 TEXT, otherBool9 TEXT, otherBool10 TEXT, otherBool11 TEXT, otherBool12 TEXT, otherBool13 TEXT, otherBool14 TEXT, otherBool15 TEXT, otherBool16 TEXT, otherBool17 TEXT, otherBool18 TEXT, otherBool19 TEXT, otherBool20 TEXT, others TEXT, others1 TEXT, others2 TEXT, others3 TEXT, others4 TEXT, others5 TEXT, others6 TEXT, others7 TEXT, others8 TEXT, others9 TEXT, others10 TEXT, others11 TEXT, others12 TEXT, others13 TEXT, others14 TEXT, others15 TEXT, others16 TEXT, others17 TEXT, others18 TEXT, others19 TEXT, others20 TEXT, passcode TEXT, payeeCategory INTEGER, payeeCfged INTEGER, payeeMemo INTEGER, payeeName INTEGER, payeeTranAmount INTEGER, payeeTranClear INTEGER, payeeTranMemo INTEGER, payeeTranType INTEGER, playorder INTEGER, sortType TEXT, state TEXT, uuid TEXT, weekstartday TEXT)" ;
	    paramSQLiteDatabase.execSQL(CREAT_Setting_TABLE);

	
		accountTypeIni(0, 1, "Asset");
		accountTypeIni(1, 1, "Cash");
		accountTypeIni(2, 1, "Checking");
		accountTypeIni(3, 1, "Credit Card");
		accountTypeIni(4, 1, "Debit Card");
		accountTypeIni(5, 1, "Investing/Retirement");
		accountTypeIni(6, 1, "Loan");
		accountTypeIni(8, 1, "Others");
		accountTypeIni(7, 1, "Savings");

		categoryIni("Auto", 0, 0, 15, 0);
		categoryIni("Auto:Gas", 0, 0, 15, 0);
		categoryIni("Auto:Registration", 0, 0, 15, 0);
		categoryIni("Auto:Service", 0, 0, 15, 0);
		categoryIni("Bank Charge", 0, 0, 8, 0);
		categoryIni("Bonus", 1, 0, 10, 0);
		categoryIni("Cash", 0, 0, 16, 0);
		categoryIni("Charity", 0, 0, 18, 0);
		categoryIni("Childcare", 0, 0, 19, 0);
		categoryIni("Clothing", 0, 0, 20, 0);

		categoryIni("Credit Card Payment", 0, 0, 25, 0);
		categoryIni("Eating Out", 0, 0, 28, 0);
		categoryIni("Education", 0, 0, 29, 0);
		categoryIni("Entertainment", 0, 0, 30, 0);
		categoryIni("Gifts", 0, 0, 35, 0);
		categoryIni("Groceries", 0, 0, 36, 0);
		categoryIni("Health & Fitness", 0, 0, 37, 0);
		categoryIni("Home Repair", 0, 0, 59, 0);
		categoryIni("Household", 0, 0, 2, 0);
		categoryIni("Insurance", 0, 0, 43, 0);

		categoryIni("Interest Exp", 0, 0, 61, 0);
		categoryIni("Loan", 0, 0, 45, 0);
		categoryIni("Medical", 0, 0, 47, 0);
		categoryIni("Misc", 0, 0, 49, 0);
		categoryIni("Mortgage Payment", 0, 0, 3, 0);
		categoryIni("Others", 0, 0, 56, 0);
		categoryIni("Others", 1, 0, 57, 0);
		categoryIni("Pets", 0, 0, 52, 0);
		
		categoryIni("Rent", 0, 0, 58, 0);
		categoryIni("Salary", 1, 0, 60, 0);
		categoryIni("Savings Deposit", 1, 0, 62, 0);
		categoryIni("Tax", 0, 0, 64, 0);
		categoryIni("Tax:Fed", 0, 0, 64, 0);
		categoryIni("Tax:Medicare", 0, 0, 64, 0);
		categoryIni("Tax:Other", 0, 0, 64, 0);
		categoryIni("Tax:Property", 0, 0, 64, 0);
		categoryIni("Tax Refund", 1, 0, 65, 0);
		categoryIni("Tax:SDI", 0, 0, 64, 0);
		categoryIni("Tax:Soc Sec", 0, 0, 64, 0);
		categoryIni("Tax:State", 0, 0, 64, 0);

		categoryIni("Transport", 0, 0, 12, 0);
		categoryIni("Travel", 0, 0, 0, 0);
		categoryIni("Utilities", 0, 0, 42, 0);
		categoryIni("Utilities:Cable TV", 0, 0, 14, 0);
		categoryIni("Utilities:Garbage & Recycling", 0, 0, 26, 0);
		categoryIni("Utilities:Gas& Electric", 0, 0, 70, 0);
		categoryIni("Utilities:Internet", 0, 0, 44, 0);
		categoryIni("Utilities:Telephone", 0, 0, 1, 0);
		categoryIni("Utilities:Water", 0, 0, 73, 0);
		
		settingIni(148,"22");

	}

	public long categoryIni(String categoryName, int categoryType,
			int hasBudget, int iconName, int isDefault) {

		ContentValues cv = new ContentValues();
		cv.put("categoryName", categoryName);
		cv.put("categoryType", categoryType);
		cv.put("hasBudget", hasBudget);
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		long row = db.insert("Category", null, cv);
		return row;
	}

	public long accountTypeIni(int iconName, int isDefault, String typeName) {

		ContentValues cv = new ContentValues();
		cv.put("iconName", iconName);
		cv.put("isDefault", isDefault);
		cv.put("typeName", typeName);
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
		// Drop older table if existed
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Accounts");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS AccountType");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Category");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Payee");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Transaction");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS EP_BillItem");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS EP_BillRule");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS BudgetItem");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS BudgetTemplate");
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS BudgetTransfer");

		// create fresh books table
		this.onCreate(paramSQLiteDatabase);
	}

}
