package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.accounts.EditAccountActivity;
import com.appxy.pocketexpensepro.accounts.AccountsFragment.SectionController;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetListActivity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetTransferActivity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.overview.budgets.EditBudgetActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class BudgetActivity extends BaseHomeActivity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private LayoutInflater mInflater;
	private ActionBar actionBar;
	private ProgressBar mProgressBar;

	private LinearLayout setBudgetLayout;
	private LinearLayout transferLayout;
	private ListView mListView;

	private double budgetAmount;
	private double transactionAmount;
	private List<Map<String, Object>> mBudgetList;
	private BudgetListApdater budgetListApdater;
	private AlertDialog alertDialog;
	private RelativeLayout previous;
	private RelativeLayout next;
	private TextView dateTextView;
	private Calendar month;
	private SharedPreferences mPreferences;
	private int BdgetSetting;
	private TextView currencyTextView1;
	private TextView totalTextView;
	private TextView currencyTextView2;
	private TextView leftTextLabel;
	private TextView leftTextView;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				mProgressBar.setMax((int) budgetAmount);
				mProgressBar.setProgress((int) transactionAmount);

				budgetListApdater.setAdapterDate(mBudgetList);
				budgetListApdater.notifyDataSetChanged();

				totalTextView.setText(budgetAmount + "");
				if (BdgetSetting == 0) {
					leftTextView.setText(MEntity
							.doublepoint2str((budgetAmount - transactionAmount)
									+ ""));
					leftTextLabel.setText("LEFT");
				} else {
					leftTextView.setText(MEntity
							.doublepoint2str(transactionAmount + ""));
					leftTextLabel.setText("SPENT");
				}

				break;

			case MSG_FAILURE:
				Toast.makeText(BudgetActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget);

		mPreferences = getSharedPreferences("Expense", MODE_PRIVATE);
		BdgetSetting = mPreferences.getInt("BdgetSetting", 0);

		month = Calendar.getInstance();
		month.setTimeInMillis(MainActivity.selectedDate);

		mInflater = LayoutInflater.from(this);
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		dateTextView = (TextView) findViewById(R.id.date_txt);
		previous = (RelativeLayout) findViewById(R.id.previous);
		next = (RelativeLayout) findViewById(R.id.next);

		currencyTextView1 = (TextView) findViewById(R.id.currency_label1);
		totalTextView = (TextView) findViewById(R.id.total_txt);
		currencyTextView2 = (TextView) findViewById(R.id.currency_label2);
		leftTextLabel = (TextView) findViewById(R.id.left_label);

		currencyTextView1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currencyTextView2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);

		mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
		leftTextView = (TextView) findViewById(R.id.budget_amount);
		setBudgetLayout = (LinearLayout) findViewById(R.id.setbudget_linearLayout);
		transferLayout = (LinearLayout) findViewById(R.id.transfer_linearLayout);
		mListView = (ListView) findViewById(R.id.budget_listview);
		budgetListApdater = new BudgetListApdater(this);
		mListView.setAdapter(budgetListApdater);
		mListView.setDividerHeight(0);

		dateTextView.setText(android.text.format.DateFormat.format("MMM yyyy",
				month));
		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				mHandler.post(mTask);
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				mHandler.post(mTask);
			}
		});

		mListView.setOnItemLongClickListener(longClickListener);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int category_id = (Integer) mBudgetList.get(arg2).get(
						"category");
				String categoryName = (String) mBudgetList.get(arg2).get(
						"categoryName");
				Intent intent = new Intent();
				intent.putExtra("_id", category_id);
				intent.putExtra("categoryName", categoryName);
				intent.putExtra("date", month.getTimeInMillis());
				intent.setClass(BudgetActivity.this,
						BudgetToTransactionActivity.class);
				startActivityForResult(intent, 17);
			}
		});

		setBudgetLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BudgetActivity.this, BudgetListActivity.class);
				startActivityForResult(intent, 4);
			}
		});

		transferLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BudgetActivity.this,
						BudgetTransferActivity.class);
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
			List<Map<String, Object>> mTransferList = OverViewDao
					.selectBudgetTransfer(BudgetActivity.this);
			Calendar calendar = Calendar.getInstance();
			long firstDay = MEntity.getFirstDayOfMonthMillis(month
					.getTimeInMillis());
			long lastDay = MEntity.getLastDayOfMonthMillis(month
					.getTimeInMillis());

			BigDecimal b0 = new BigDecimal("0");
			BigDecimal bt0 = new BigDecimal("0");
			for (Map<String, Object> iMap : mBudgetList) {
				int _id = (Integer) iMap.get("_id");
				String  catrgoryName = (String) iMap.get("categoryName");
				String amount = (String) iMap.get("amount");
				int category_id = (Integer) iMap.get("category");

				BigDecimal b1 = new BigDecimal(amount);
				b0 = b0.add(b1);
				for (Map<String, Object> mMap : mTransferList) {

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
				iMap.put("amount", b1.doubleValue() + "");

				BigDecimal bz = new BigDecimal("0");
				List<Map<String, Object>> mTransactionList = OverViewDao
						.selectTransactionByCategoryIdAndTime(
								BudgetActivity.this, catrgoryName, firstDay,
								lastDay);
				for (Map<String, Object> tMap : mTransactionList) {

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
				iMap.put("tAmount", tAmount + "");

			}

			budgetAmount = b0.doubleValue();
			transactionAmount = bt0.doubleValue();

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
		//
	};

	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int id = (Integer) mBudgetList.get(arg2).get("_id");
			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = { "Edit", "Delete" };

			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(
					BudgetActivity.this, data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (arg2 == 0) {

						Intent intent = new Intent();
						intent.putExtra("_id", id);
						intent.setClass(BudgetActivity.this,
								EditBudgetActivity.class);
						startActivityForResult(intent, 16);
						alertDialog.dismiss();

					} else if (arg2 == 1) {
						long row = BudgetsDao.deleteBudget(BudgetActivity.this,
								id);
						mHandler.post(mTask);
						alertDialog.dismiss();

					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(
					BudgetActivity.this);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
		}

	};

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		dateTextView.setText(android.text.format.DateFormat.format("MMM yyyy",
				month));
	}

	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}
		dateTextView.setText(android.text.format.DateFormat.format("MMM yyyy",
				month));
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

		case 16:

			if (data != null) {

				mHandler.post(mTask);
			}
			break;
		}
	}
}
