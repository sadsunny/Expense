package com.appxy.pocketexpensepro.report;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.appxy.pocketexpensepro.db.ExpenseDBHelper;

public class ReportDao{
	
	public static SQLiteDatabase getConnection(Context context) {
		ExpenseDBHelper helper = new ExpenseDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}
	
}
