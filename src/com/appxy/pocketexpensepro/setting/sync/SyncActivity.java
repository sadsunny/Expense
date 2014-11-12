package com.appxy.pocketexpensepro.setting.sync;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeSyncActivity;
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
import com.appxy.pocketexpensepro.table.CategoryTable;
import com.appxy.pocketexpensepro.table.EP_BillItemTable;
import com.appxy.pocketexpensepro.table.EP_BillItemTable.EP_BillItem;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable;
import com.appxy.pocketexpensepro.table.EP_BillRuleTable.EP_BillRule;
import com.appxy.pocketexpensepro.table.PayeeTable;
import com.appxy.pocketexpensepro.table.PayeeTable.Payee;
import com.appxy.pocketexpensepro.table.TransactionTable;
import com.appxy.pocketexpensepro.table.TransactionTable.Transaction;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

import android.R.bool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SyncActivity extends BaseHomeSyncActivity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private static final String APP_KEY = "6rdffw1lvpr4zuc";
	private static final String APP_SECRET = "gxqx0uiav4744o3";
	private static final int REQUEST_LINK_TO_DBX = 0;

	private DbxAccountManager mDbxAcctMgr;
	private DbxDatastore mDatastore;
	private DbxDatastoreManager mLocalManager;
	private DbxDatastoreManager mDatastoreManager;
	private DbxAccount mDbxAcct;

	private Switch syncSwitch;
	private TextView accountNameTextView;
	private SharedPreferences mPreferences;
	private boolean isSync;
	private boolean isUpload = false;
	private ProgressDialog progressDialog;
	
	private DbxDatastore.SyncStatusListener mDatastoreListener = new DbxDatastore.SyncStatusListener() {
        @Override
        public void onDatastoreStatusChange(DbxDatastore ds) {
        	
        	if (!ds.getSyncStatus().isDownloading) {
        		if (isUpload) {
        			justSync(true);
				}
			}
        	
        	
            if (ds.getSyncStatus().hasIncoming) {
                try {
                	
                    mDatastore.sync();
                   
                } catch (DbxException e) {
                    handleException(e);
                }
            }
            
            
        }
    };
    
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				Toast.makeText(SyncActivity.this, "Dropbox sync successed",
						Toast.LENGTH_SHORT).show();

				 if(progressDialog!=null && progressDialog.isShowing()){
		    			progressDialog.dismiss();
		    		}
				 isUpload = false;
				break;

			case MSG_FAILURE:
				Toast.makeText(SyncActivity.this, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	
	DbxAccount.Listener mAccountListener = new DbxAccount.Listener() {

		@Override
		public void onAccountChange(DbxAccount arg0) {
			// TODO Auto-generated method stub
			if (isSync) {

				String name = mDbxAcct.getAccountInfo().displayName;
				accountNameTextView.setVisibility(View.VISIBLE);
				accountNameTextView.setText(name);

				SharedPreferences.Editor meditor = mPreferences.edit();
				meditor.putString("accountName", name);
				meditor.commit();
			} else {

				accountNameTextView.setVisibility(View.GONE);
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				APP_KEY, APP_SECRET);
		setContentView(R.layout.activity_sync);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mPreferences = getSharedPreferences("ExpenseSync", MODE_PRIVATE);
		isSync = mPreferences.getBoolean("isSync", false);
		
		accountNameTextView = (TextView) findViewById(R.id.account_name);
		syncSwitch = (Switch) findViewById(R.id.sync_switch);
		syncSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {

					if (!mDbxAcctMgr.hasLinkedAccount()) { 
						mDbxAcctMgr.startLink(SyncActivity.this,
								REQUEST_LINK_TO_DBX);
					}
						
				} else {

					accountNameTextView.setVisibility(View.INVISIBLE);
					isSync = false;
					SharedPreferences.Editor meditor = mPreferences.edit();
					meditor.putBoolean("isSync", isSync);
					meditor.commit();
					if (mDbxAcctMgr != null) {
						
						try {
							mDbxAcctMgr.unlink();
						} catch (Exception e) {
							// TODO: handle exception
						}
						
					}

				}

			}
		});

	}
	
	public void deleteAllRecod() throws DbxException {
		AccountsTable accountsTable = new AccountsTable(mDatastore,this);
		accountsTable.deleteAll();
		
		AccountTypeTable accountTypeTable = new AccountTypeTable(mDatastore, this);
		accountTypeTable.deleteAll();
		
		BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, this);
		budgetItemTable.deleteAll();
		
		BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, this);
		budgetTemplateTable.deleteAll();
		
		BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore,this);
		budgetTransferTable.deleteAll();
		
		CategoryTable categoryTable = new CategoryTable(mDatastore, this);
		categoryTable.deleteAll();
		
		EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, this);
		ep_BillItemTable.deleteAll();
		
		EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore,this);
		ep_BillRuleTable.deleteAll();
		
		PayeeTable payeeTable = new PayeeTable(mDatastore, this);
		payeeTable.deleteAll();
		
		TransactionTable transactionTable = new TransactionTable(mDatastore,this);
		transactionTable.deleteAll();
		
		mDatastore.sync();
		mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
	}
	
	public void upLoadAllDate() throws DbxException { //上传所有数据
		
		List<Map<String, Object>> AccountsList = SyncDao.selectAccount(SyncActivity.this);
		for (Map<String, Object> iMap:AccountsList) {
			AccountsTable accountsTable = new AccountsTable(mDatastore,this);
			Accounts accounts = accountsTable.getAccounts();
			accounts.setAccountsData(iMap);
			accountsTable.insertRecords(accounts.getFields());
		}
		
		List<Map<String, Object>> AccountTypeList = SyncDao.selectAccountType(SyncActivity.this);
		for (Map<String, Object> iMap:AccountTypeList) {
			AccountTypeTable accountTypeTable = new AccountTypeTable(mDatastore, this);
			AccountType accountType = accountTypeTable.getAccountType();
			accountType.setAccountTypeData(iMap);
			accountTypeTable.insertRecords(accountType.getFields());
		}
		
		List<Map<String, Object>> BudgetItemList = SyncDao.selectBudgetItem(SyncActivity.this);
		for (Map<String, Object> iMap:BudgetItemList) {
			BudgetItemTable budgetItemTable = new BudgetItemTable(mDatastore, this);
			BudgetItem budgetItem = budgetItemTable.getBudgetItem();
			budgetItem.setBudgetItemData(iMap);
			budgetItemTable.insertRecords(budgetItem.getFields());
		}
		
		List<Map<String, Object>> BudgetTemplateList = SyncDao.selectBudgetTemplate(SyncActivity.this);
		for (Map<String, Object> iMap:BudgetTemplateList) {
			BudgetTemplateTable budgetTemplateTable = new BudgetTemplateTable(mDatastore, this);
			BudgetTemplate budgetTemplate = budgetTemplateTable.getBudgetTemplate();
			budgetTemplate.setBudgetTemplateData(iMap);
			budgetTemplateTable.insertRecords(budgetTemplate.getFields());
		}
		
		List<Map<String, Object>> BudgetTransferList = SyncDao.selectBudgetTransfer(SyncActivity.this);
		for (Map<String, Object> iMap:BudgetTransferList) {
			BudgetTransferTable budgetTransferTable = new BudgetTransferTable(mDatastore,this);
			BudgetTransfer budgetTransfer = budgetTransferTable.getBudgetTransfer();
			budgetTransfer.setBudgetTransferData(iMap);
			budgetTransferTable.insertRecords(budgetTransfer.getFields());
		}
		
		List<Map<String, Object>> CategoryList = SyncDao.selectCategory(SyncActivity.this);
		for (Map<String, Object> iMap:CategoryList) {
			CategoryTable categoryTable = new CategoryTable(mDatastore, this);
			Category category = categoryTable.getCategory();
			category.setCategoryData(iMap);
			categoryTable.insertRecords(category.getFields());
		}
		
		List<Map<String, Object>> EP_BillItemList = SyncDao.selectEP_BillItem(SyncActivity.this);
		for (Map<String, Object> iMap:EP_BillItemList) {
			
			EP_BillItemTable ep_BillItemTable = new EP_BillItemTable(mDatastore, this);
			EP_BillItem ep_BillItem = ep_BillItemTable.getEP_BillItem();
			ep_BillItem.setEP_BillItemData(iMap);
			ep_BillItemTable.insertRecords(ep_BillItem.getFields());
		}
		
		List<Map<String, Object>> EP_BillRuleList = SyncDao.selectEP_BillRule(SyncActivity.this);
		for (Map<String, Object> iMap:EP_BillRuleList) {
			EP_BillRuleTable ep_BillRuleTable = new EP_BillRuleTable(mDatastore,this);
			EP_BillRule ep_BillRule = ep_BillRuleTable.getEP_BillRule();
			ep_BillRule.setEP_BillRuleData(iMap);
			ep_BillRuleTable.insertRecords(ep_BillRule.getFields());
		}
		
		List<Map<String, Object>> PayeeList = SyncDao.selectPayee(SyncActivity.this);
		for (Map<String, Object> iMap:PayeeList) {
			PayeeTable payeeTable = new PayeeTable(mDatastore, this);
			Payee payee = payeeTable.getPayee();
			payee.setPayeeData(iMap);
			payeeTable.insertRecords(payee.getFields());
		}
		
		List<Map<String, Object>> TransactionList = SyncDao.selectTransaction(SyncActivity.this);
		for (Map<String, Object> iMap:TransactionList) {
			TransactionTable transactionTable = new TransactionTable(mDatastore,this);
			Transaction transaction = transactionTable.getTransaction();
			transaction.setTransactionData(iMap);
			transactionTable.insertRecords(transaction.getFields());
		}
		
		mDatastore.sync();
		mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		
	}
	
	public void checkSync() { //check状态
		
		
		if (isSync) {
			if (mDbxAcctMgr.hasLinkedAccount()) {

				try {
					
					if (null == mDbxAcct) {
						 mDbxAcct=mDbxAcctMgr.getLinkedAccount();
			         }
					
					if (null == mDatastore) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					 String accName = mPreferences.getString("accountName", "");
					 accountNameTextView.setVisibility(View.VISIBLE);
					 accountNameTextView.setText(accName);
					 syncSwitch.setChecked(true);
					 
					 mDatastore.addSyncStatusListener(mDatastoreListener);
					 mDbxAcct.addListener(mAccountListener);
					 
				} catch (DbxException e) {
					handleException(e);
				}

				
			}
		} else {
           accountNameTextView.setVisibility(View.INVISIBLE);
           syncSwitch.setChecked(false);
		}
		
	}
	
	public void justSync(boolean justLinked) { // 同步并上传数据库
		
		if (isSync) {
			if (mDbxAcctMgr.hasLinkedAccount()) {

				try {
					
					if (null == mDbxAcct) {
						 mDbxAcct=mDbxAcctMgr.getLinkedAccount();
			         }
					
					if (null == mDatastore) {
						mDatastore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
					}
					
					 if (isUpload) {
						 Thread mThread = new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										upLoadAllDate();
//										deleteAllRecod();
									} catch (DbxException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
							mThread.start();
					}
					
				} catch (DbxException e) {
					handleException(e);
				}

				
			}
		} else {
           accountNameTextView.setVisibility(View.INVISIBLE);
           syncSwitch.setChecked(false);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkSync();
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mDatastore != null) {
	        mDatastore.removeSyncStatusListener(mDatastoreListener);
	        mDatastore.close();
	        mDatastore = null;
	    }
		if (mDbxAcct != null) {
			mDbxAcct.removeListener(mAccountListener);
			mDbxAcct = null;
	    }


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == RESULT_OK) {
				isSync = true;
				isUpload = true;
				

				progressDialog = ProgressDialog.show(SyncActivity.this, null, "Syncing....");
				
				if (mDbxAcctMgr != null && mDbxAcctMgr.hasLinkedAccount()) {

					SharedPreferences.Editor meditor = mPreferences.edit();	
					meditor.putBoolean("isSync", isSync);
					meditor.commit();
				}
				
				
			} else {
				// ... Link failed or was cancelled by the user.
				Toast.makeText(this, "Link to Dropbox failed.",Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void handleException(DbxException e) {
		e.printStackTrace();
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void syncDateChange() {
		// TODO Auto-generated method stub
	}

}
