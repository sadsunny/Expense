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

	public ExpenseDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		// TODO Auto-generated method stub
		this.db = paramSQLiteDatabase;
		String CREATE_Accounts_TABLE = "CREATE TABLE Accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accName TEXT, amount TEXT, autoClear INTEGER, checkNumber TEXT, creditLimit Double, dateTime TEXT, dateTime_sync TEXT, dueDate TEXT, iconName TEXT, orderIndex INTEGER, others TEXT, reconcile INTEGER, runningBalance INTEGER, state TEXT, uuid TEXT, accountType INTEGER, expenseTransactions INTEGER, fromBill INTEGER, incomeTransactions INTEGER, toBill INTEGER, transactionRep INTEGER, FOREIGN KEY(accountType) REFERENCES AccountType(_id))";
		paramSQLiteDatabase.execSQL(CREATE_Accounts_TABLE);

		String CREATE_AccountType_TABLE = "CREATE TABLE AccountType ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime TEXT, iconName TEXT, isDefault INTEGER, ordexIndex INTEGER, others TEXT, state TEXT, typeName TEXT, uuid TEXT,accounts INTEGER)";
		paramSQLiteDatabase.execSQL(CREATE_AccountType_TABLE);

		String CREATE_Category_TABLE = "CREATE TABLE Category ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,categoryisExpense INTEGER, categoryisIncome INTEGER, categoryName TEXT, categoryString TEXT, categoryType TEXT, colorName INTEGER, dateTime TEXT, hasBudget INTEGER, iconName TEXT, isDefault INTEGER, isSystemRecord INTEGER, others TEXT, recordIndex INTEGER, state TEXT, uuid TEXT, billItem INTEGER, billRep INTEGER, budgetTemplate INTEGER, categoryHasBillItem INTEGER, categoryHasBillRule INTEGER, payee INTEGER, transactionRep INTEGER, transactions INTEGER)";
		paramSQLiteDatabase.execSQL(CREATE_Category_TABLE);

		String CREATE_Payee_TABLE = "CREATE TABLE Payee ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime TEXT, memo TEXT, name TEXT, orderIndex INTEGER, others TEXT, phone TEXT, state TEXT, tranAmount Double, tranCleared INTEGER, tranMemo TEXT, tranType TEXT, uuid TEXT, website TEXT, billItem INTEGER, category INTEGER, payeeHasBillIRule INTEGER, payeeHasBillItem INTEGER, 'transaction' INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREATE_Payee_TABLE);

		String CREAT_Transaction_TABLE = "CREATE TABLE [Transaction] ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime TEXT, dateTime_sync TEXT, groupByDate TEXT, isClear INTEGER, notes TEXT, orderIndex TEXT, others TEXT, photoName TEXT, recurringType INTEGER, state TEXT, transactionBool INTEGER, transactionstring TEXT, transactionType TEXT, type INTEGER, uuid TEXT, billItem INTEGER, category INTEGER, childTransactions TEXT, expenseAccount INTEGER, incomeAccount INTEGER, parTransaction INTEGER, payee INTEGER, transactionHasBillItem INTEGER, transactionHasBillRule INTEGER, transactionRule INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE, FOREIGN KEY(expenseAccount) REFERENCES Accounts(_id) ON DELETE CASCADE, FOREIGN KEY(transactionHasBillItem) REFERENCES EP_BillItem(_id) ON DELETE CASCADE, FOREIGN KEY(transactionHasBillRule) REFERENCES EP_BillRule(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_Transaction_TABLE);

		String CREAT_EP_BillItem_TABLE = "CREATE TABLE EP_BillItem ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime TEXT, ep_billisDelete INTEGER, ep_billItemAmount Double, ep_billItemBool1 INTEGER, ep_billItemBool2 INTEGER, ep_billItemDate1 TEXT, ep_billItemDate2 TEXT, ep_billItemDueDate TEXT, ep_billItemDueDateNew TEXT, ep_billItemEndDate TEXT, ep_billItemName TEXT, ep_billItemNote TEXT, ep_billItemRecurringType TEXT, ep_billItemReminderDate TEXT, ep_billItemReminderTime TEXT, ep_billItemString1 TEXT, ep_billItemString2 TEXT, state TEXT, uuid TEXT, billItemHasBillRule INTEGER, billItemHasCategory INTEGER, billItemHasPayee INTEGER, billItemHasTransaction INTEGER, FOREIGN KEY(billItemHasCategory) REFERENCES Category(_id) ON DELETE CASCADE,FOREIGN KEY(billItemHasBillRule) REFERENCES EP_BillRule(_id) ON DELETE CASCADE )";
		paramSQLiteDatabase.execSQL(CREAT_EP_BillItem_TABLE);

		String CREAT_EP_BillRule_TABLE = "CREATE TABLE EP_BillRule ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,dateTime TEXT, ep_billAmount Double, ep_billDueDate TEXT, ep_billEndDate TEXT, ep_billName TEXT, ep_bool1 INTEGER, ep_bool2 INTEGER, ep_date1 TEXT, ep_date2 TEXT, ep_note TEXT, ep_recurringType TEXT, ep_reminderDate TEXT, ep_reminderTime TEXT, ep_string1 TEXT, ep_string2 TEXT, state TEXT, uuid TEXT, billRuleHasBillItem INTEGER, billRuleHasCategory INTEGER, billRuleHasPayee INTEGER, billRuleHasTransaction INTEGER, FOREIGN KEY(billRuleHasCategory) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_EP_BillRule_TABLE);

		String CREAT_BudgetItem_TABLE = "CREATE TABLE BudgetItem ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime TEXT, endDate TEXT, isCurrent INTEGER, isRollover INTEGER, orderIndex INTEGER, rolloverAmount Double, startDate TEXT, state TEXT, uuid TEXT, budgetTemplate INTEGER, fromTransfer INTEGER, toTransfer INTEGER, FOREIGN KEY(budgetTemplate) REFERENCES BudgetTemplate(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetItem_TABLE);

		String CREAT_BudgetTemplate_TABLE = "CREATE TABLE BudgetTemplate ( _id INTEGER PRIMARY KEY AUTOINCREMENT , amount TEXT, cycleType TEXT, dateTime TEXT, isNew INTEGER, isRollover INTEGER, orderIndex INTEGER, startDate TEXT, startDateHasChange INTEGER, state TEXT, uuid TEXT, budgetItems INTEGER, budgetRep INTEGER, category INTEGER, FOREIGN KEY(category) REFERENCES Category(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetTemplate_TABLE);

		String CREAT_BudgetTransfer_TABLE = "CREATE TABLE BudgetTransfer ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,amount TEXT, dateTime TEXT, dateTime_sync TEXT, state TEXT, uuid TEXT, fromBudget INTEGER, toBudget INTEGER , FOREIGN KEY(fromBudget) REFERENCES BudgetItem(_id) ON DELETE CASCADE,FOREIGN KEY(toBudget) REFERENCES BudgetItem(_id) ON DELETE CASCADE)";
		paramSQLiteDatabase.execSQL(CREAT_BudgetTransfer_TABLE);

		accountTypeIni(0, 0, "Others");
		accountTypeIni(1, 0, "Asset");
		accountTypeIni(2, 0, "Cash");
		accountTypeIni(3, 0, "Checking");
		accountTypeIni(4, 0, "Credit Card");
		accountTypeIni(5, 0, "Debit Card");
		accountTypeIni(6, 0, "Investing/Retirement");
		accountTypeIni(7, 0, "Loan");
		accountTypeIni(8, 0, "Savings");

		categoryIni("Auto", 0, 0, 0, 0);
		categoryIni("Auto:Gas", 0, 0, 1, 0);
		categoryIni("Auto:Registration", 0, 0, 2, 0);
		categoryIni("Auto:Service", 0, 0, 3, 0);
		categoryIni("Bank Charge", 0, 0, 4, 0);
		categoryIni("Bonus", 1, 0, 4, 0);
		categoryIni("Cash", 0, 0, 5, 0);
		categoryIni("Charity", 0, 0, 6, 0);
		categoryIni("Childcare", 0, 0, 6, 0);
		categoryIni("Clothing", 0, 0, 6, 0);

		categoryIni("Credit Card Payment", 0, 0, 6, 0);
		categoryIni("Eating Out", 0, 0, 6, 0);
		categoryIni("Education", 0, 0, 6, 0);
		categoryIni("Entertainment", 0, 0, 6, 0);
		categoryIni("Gifts", 0, 0, 6, 0);
		categoryIni("Groceries", 0, 0, 6, 0);
		categoryIni("Health & Fitness", 0, 0, 6, 0);
		categoryIni("Home Repair", 0, 0, 6, 0);
		categoryIni("Household", 0, 0, 7, 0);
		categoryIni("Insurance", 0, 0, 7, 0);

		categoryIni("Interest Exp", 0, 0, 6, 0);
		categoryIni("Loan", 0, 0, 6, 0);
		categoryIni("Medical", 0, 0, 6, 0);
		categoryIni("Misc", 0, 0, 6, 0);
		categoryIni("Mortgage Payment", 0, 0, 6, 0);
		categoryIni("Pets", 0, 0, 6, 0);
		categoryIni("Others", 0, 0, 7, 0);
		categoryIni("Others", 1, 0, 7, 0);

		categoryIni("Rent", 0, 0, 6, 0);
		categoryIni("Salary", 1, 0, 6, 0);
		categoryIni("Savings Deposit", 1, 0, 6, 0);
		categoryIni("Tax", 0, 0, 6, 0);
		categoryIni("Tax:Fed", 0, 0, 6, 0);
		categoryIni("Tax:Medicare", 0, 0, 6, 0);
		categoryIni("Tax:Other", 0, 0, 6, 0);
		categoryIni("Tax:Property", 0, 0, 6, 0);
		categoryIni("Tax Refund", 1, 0, 6, 0);
		categoryIni("Tax:SDI", 0, 0, 7, 0);
		categoryIni("Tax:Soc Sec", 0, 0, 7, 0);
		categoryIni("Tax:State", 0, 0, 7, 0);

		categoryIni("Transport", 0, 0, 7, 0);
		categoryIni("Travel", 0, 0, 6, 0);
		categoryIni("Utilities", 0, 0, 6, 0);
		categoryIni("Utilities:Cable TV", 0, 0, 6, 0);
		categoryIni("Utilities:Garbage & Recycling", 0, 0, 6, 0);
		categoryIni("Utilities:Gas& Electric", 0, 0, 6, 0);
		categoryIni("Utilities:Internet", 0, 0, 6, 0);
		categoryIni("Utilities:Telephone", 0, 0, 6, 0);
		categoryIni("Utilities:Water", 0, 0, 6, 0);

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
