package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetTransferActivity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BudgetActivity extends Activity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private LayoutInflater mInflater;
	private ActionBar actionBar;
	private ProgressBar mProgressBar;
	private TextView leftTextView;
	private LinearLayout setBudgetLayout;
	private LinearLayout transferLayout;
	private ListView mListView;
	
	private double budgetAmount;
	private double transactionAmount;
    private List<Map<String, Object>> mBudgetList;
    private BudgetListApdater budgetListApdater;
    
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				mProgressBar.setMax((int) budgetAmount);
				mProgressBar.setProgress((int) transactionAmount);
				budgetListApdater.setAdapterDate(mBudgetList);
				budgetListApdater.notifyDataSetChanged();
				leftTextView.setText((budgetAmount-transactionAmount)+"");
				
				break;

			case MSG_FAILURE:
				Toast.makeText(BudgetActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget);
		mInflater = LayoutInflater.from(this);
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Calendar calendar = Calendar.getInstance();
		actionBar.setSubtitle(MEntity.turnMilltoMonthYear(calendar.getTimeInMillis()));

		mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
		leftTextView = (TextView) findViewById(R.id.budget_amount);
		setBudgetLayout = (LinearLayout) findViewById(R.id.setbudget_linearLayout);
		transferLayout = (LinearLayout) findViewById(R.id.transfer_linearLayout);
		mListView = (ListView) findViewById(R.id.budget_listview);
		budgetListApdater = new BudgetListApdater(this);
		mListView.setAdapter(budgetListApdater);
		mListView.setDividerHeight(0);
		
		setBudgetLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BudgetActivity.this, CreatBudgetsActivity.class);
				startActivityForResult(intent, 4);
			}
		});
		
		transferLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BudgetActivity.this, BudgetTransferActivity.class);
				startActivityForResult(intent, 15);
				
			}
		});
		
		Thread mThread = new Thread(mTask);
		mThread.start();
		
		Intent intent = new Intent();
		intent.putExtra("done", 1);
		setResult(14, intent);
		
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mBudgetList = OverViewDao.selectBudget(BudgetActivity.this);
			List<Map<String, Object>> mTransferList = OverViewDao.selectBudgetTransfer(BudgetActivity.this);
			Calendar calendar = Calendar.getInstance();
			long firstDay = MEntity.getFirstDayOfMonthMillis(calendar.getTimeInMillis());
			long lastDay = MEntity.getLastDayOfMonthMillis(calendar.getTimeInMillis());
			
			BigDecimal b0 = new BigDecimal("0");
			BigDecimal bt0 = new BigDecimal("0");
			for (Map<String, Object> iMap: mBudgetList) {
				int _id = (Integer) iMap.get("_id");
				String amount = (String) iMap.get("amount");
				int category_id = (Integer) iMap.get("category");
				
				BigDecimal b1 = new BigDecimal(amount);
				b0 = b0.add(b1);
				for (Map<String, Object> mMap: mTransferList) {
					
					int fromBudget = (Integer) mMap.get("fromBudget");
					int toBudget = (Integer) mMap.get("toBudget");
					String amountTransfer = (String) mMap.get("amount");
					
					BigDecimal b2 = new BigDecimal(amountTransfer);
					if (_id == fromBudget) {
						b1 = b1.subtract(b2);
					} else if (_id == toBudget) {
						b1 = b1.add(b2);
					}
				}
				iMap.put("amount", b1.doubleValue()+"");
				
				BigDecimal bz = new BigDecimal("0");
				List<Map<String, Object>> mTransactionList = OverViewDao.selectTransactionByCategoryIdAndTime(BudgetActivity.this, category_id, firstDay, lastDay) ;
				for (Map<String, Object> tMap: mTransactionList){
					
					String tAmount = (String) tMap.get("amount");
					int expenseAccount = (Integer) tMap.get("expenseAccount");
					int incomeAccount = (Integer) tMap.get("incomeAccount");
					BigDecimal b3 = new BigDecimal(tAmount);

					if (expenseAccount > 0 && incomeAccount <= 0) {
						bz = bz.add(b3);
					} 
				}
				bt0 = bt0.add(bz);
				double tAmount = bz.doubleValue();
				iMap.put("tAmount", tAmount+"");
				
			}
			
			budgetAmount = b0.doubleValue();
			transactionAmount = bt0.doubleValue();
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
		//
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.add_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;

		case R.id.action_add:
			Intent intent = new Intent();
			intent.setClass(BudgetActivity.this, CreatBudgetsActivity.class);
			startActivityForResult(intent, 4);
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 4:

			if (data != null) {

				mHandler.post(mTask);
			}
			break;
		case 15:

			if (data != null) {

				mHandler.post(mTask);
			}
			break;
		}
	}
}
