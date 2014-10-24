package com.appxy.pocketexpensepro.passcode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.entity.MyApplication;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.appxy.pocketexpensepro.table.AccountTypeTable;
import com.appxy.pocketexpensepro.table.AccountTypeTable.AccountType;
import com.appxy.pocketexpensepro.table.AccountsTable;
import com.appxy.pocketexpensepro.table.AccountsTable.Accounts;
import com.appxy.pocketexpensepro.table.BudgetItemTable;
import com.appxy.pocketexpensepro.table.BudgetItemTable.BudgetItem;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable;
import com.appxy.pocketexpensepro.table.BudgetTemplateTable.BudgetTemplate;
import com.appxy.pocketexpensepro.table.BudgetTransferTable;
import com.appxy.pocketexpensepro.table.BudgetTransferTable.BudgetTransfer;
import com.appxy.pocketexpensepro.table.CategoryTable.Category;
import com.appxy.pocketexpensepro.table.EP_BillItemTable;
import com.appxy.pocketexpensepro.table.EP_BillItemTable.EP_BillItem;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable.EP_BillRule;
import com.appxy.pocketexpensepro.table.PayeeTable.Payee;
import com.appxy.pocketexpensepro.table.CategoryTable;
import com.appxy.pocketexpensepro.table.PayeeTable;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

public abstract class BaseHomeActivity extends FragmentActivity {

	SharedPreferences sharedPreferences;
	// private static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	private String passCode;

	public DbxAccountManager mDbxAcctMgr;
	public DbxDatastore mDatastore;
	private static final String APP_KEY = "6rdffw1lvpr4zuc";
	private static final String APP_SECRET = "gxqx0uiav4744o3";

	Context context;
	HomeKeyEventBroadCastReceiver receiver;

	private DbxDatastore.SyncStatusListener mDatastoreListener = new DbxDatastore.SyncStatusListener() {
		@Override
		public void onDatastoreStatusChange(DbxDatastore ds) {

			if (ds.getSyncStatus().hasIncoming) {
				try {

					Map<String, Set<DbxRecord>> mMap = mDatastore.sync();

					dataHasIncoming(mMap);
					
					mDatastore.sync();
					syncDateChange(mMap);

				} catch (DbxException e) {
					handleException(e);
				}
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		List<Map<String, Object>> mList = SettingDao
				.selectSetting(BaseHomeActivity.this);
		passCode = (String) mList.get(0).get("passcode");
		if (passCode != null && passCode.length() > 2) {
			isPasscode = 1;
		} else {
			isPasscode = 0;
		}

		context = this;
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}

	public abstract void syncDateChange(Map<String, Set<DbxRecord>> mMap);

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();


		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				APP_KEY, APP_SECRET);
		try {

			if (mDbxAcctMgr.hasLinkedAccount()) {
				if (mDatastore == null) {
					mDatastore = DbxDatastore.openDefault(mDbxAcctMgr
							.getLinkedAccount());
				}
				mDatastore.addSyncStatusListener(mDatastoreListener);
			}

		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (MyApplication.isHomePress) {
			MyApplication.isHomePress = false;

			List<Map<String, Object>> mList = SettingDao
					.selectSetting(BaseHomeActivity.this);
			passCode = (String) mList.get(0).get("passcode");
			if (passCode != null && passCode.length() > 2) {
				isPasscode = 1;
			} else {
				isPasscode = 0;
			}

			if (isPasscode == 1) {
				Intent intent = new Intent(this, Activity_HomeBack.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 45);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDatastore != null) {
			mDatastore.removeSyncStatusListener(mDatastoreListener);
			mDatastore.close();
			mDatastore = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						MyApplication.isHomePress = true;

					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						MyApplication.isHomePress = true;
					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void handleException(DbxException e) {
		e.printStackTrace();
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}
	
	private void dataHasIncoming(Map<String, Set<DbxRecord>> mMap) throws DbxException{//处理同步数据incoming
		
		if (mMap.containsKey("db_category_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_category_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					CategoryTable categoryTable = new CategoryTable(mDatastore, this);
					Category category =  categoryTable.getCategory();
					category.setIncomingData(iRecord);
					category.insertOrUpdate();
					
				}
			}
		}
		
		
		
		if (mMap.containsKey("db_accounttype_table")) {
			Set<DbxRecord> incomeDate = mMap.get("db_accounttype_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					AccountTypeTable accountTypeTable = new AccountTypeTable(mDatastore, this);
					AccountType accountType = accountTypeTable.getAccountType();
					accountType.setIncomingData(iRecord);
					accountType.insertOrUpdate(); 
					
				}
			}
		}
		
		if (mMap.containsKey("db_payee_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_payee_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					PayeeTable payeeTable = new PayeeTable(mDatastore, this);
					Payee payee = payeeTable.getPayee();
					payee.setIncomingData(iRecord);
					payee.insertOrUpdate();
					
				}
			}
		}
		
		
		
		if (mMap.containsKey("db_account_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_account_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					AccountsTable accountsTable = new AccountsTable(mDatastore, BaseHomeActivity.this);
					Accounts accounts =accountsTable.getAccounts();
					accounts.setIncomingData(iRecord);
					accounts.insertOrUpdate();
				}
			}
			
		}
		
		
		if (mMap.containsKey("db_budgettemplate_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_budgettemplate_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, context);
					BudgetTemplate budgetTemplate = budgetTemplateTable.getBudgetTemplate();
					budgetTemplate.setIncomingData(iRecord);
					budgetTemplate.insertOrUpdate();
				}
			}
			
		}

		
		if (mMap.containsKey("db_budgetitem_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_budgetitem_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, context);
					BudgetItem budgetItem = budgetItemTable.getBudgetItem();
					budgetItem.setIncomingData(iRecord);
					budgetItem.insertOrUpdate();
					
				}
			}
			
		}

		
		if (mMap.containsKey("db_budgettransfer_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_budgettransfer_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore, context);
					BudgetTransfer budgetTransfer = budgetTransferTable.getBudgetTransfer();
					budgetTransfer.setIncomingData(iRecord);
					budgetTransfer.insertOrUpdate();
					
				}
			}
			
		}
		
		if (mMap.containsKey("db_ep_billrule_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_ep_billrule_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore, context);
					EP_BillRule eP_BillRule = ep_BillRuleTable.getEP_BillRule();
					eP_BillRule.setIncomingData(iRecord);
					eP_BillRule.insertOrUpdate();
					
				}
			}
			
		}
		
		if (mMap.containsKey("db_ep_billitem_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_ep_billitem_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, context);
					EP_BillItem eP_BillItem = ep_BillItemTable.getEP_BillItem();
					eP_BillItem.setIncomingData(iRecord);
					eP_BillItem.insertOrUpdate();
					
				}
			}
			
		}
		
		if (mMap.containsKey("db_transaction_table")) {
			
			Set<DbxRecord> incomeDate = mMap.get("db_transaction_table");
			for (DbxRecord iRecord:incomeDate) {
				if(!iRecord.isDeleted()){
					
					TransactionTable transactionTable = new TransactionTable(mDatastore, context);
					Transaction transaction = transactionTable.getTransaction();
					transaction.setIncomingData(iRecord);
					transaction.insertOrUpdate();
					
				}
			}
			
		}
		
		

		
	}
	
	
	

}
