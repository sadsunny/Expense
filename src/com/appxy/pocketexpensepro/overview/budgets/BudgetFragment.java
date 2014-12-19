package com.appxy.pocketexpensepro.overview.budgets;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.accounts.EditAccountActivity;
import com.appxy.pocketexpensepro.accounts.AccountsFragment.SectionController;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.overview.BudgetListApdater;
import com.appxy.pocketexpensepro.overview.BudgetToTransactionActivity;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.overview.budgets.BudgetListActivity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetTransferActivity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.overview.budgets.EditBudgetActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class BudgetFragment extends Fragment implements OnSyncFinishedListener{

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private LayoutInflater mInflater;
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
	private TextView notiTextView;

	private Activity mActivity ;
	
	public BudgetFragment() {

	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = activity;
	}

	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				mProgressBar.setMax((int) budgetAmount);
				mProgressBar.setProgress((int) transactionAmount);
				
				if (mBudgetList != null && mBudgetList.size() > 0) {
					mListView.setVisibility(View.VISIBLE);
					notiTextView.setVisibility(View.INVISIBLE);
					budgetListApdater.setAdapterDate(mBudgetList);
					budgetListApdater.notifyDataSetChanged();
				} else {
					mListView.setVisibility(View.INVISIBLE);
					notiTextView.setVisibility(View.VISIBLE);
				}

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
				Toast.makeText(mActivity, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setTitle("Budget");
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		mPreferences = mActivity.getSharedPreferences("Expense", mActivity.MODE_PRIVATE);
		BdgetSetting = mPreferences.getInt("BdgetSetting", 0);

		month = Calendar.getInstance();
		month.setTimeInMillis(MainActivity.selectedDate);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_budget, container,
				false);
		
		mInflater = inflater;

		dateTextView = (TextView) view.findViewById(R.id.date_txt);
		previous = (RelativeLayout) view.findViewById(R.id.previous);
		next = (RelativeLayout) view.findViewById(R.id.next);
		notiTextView= (TextView)view.findViewById(R.id.notice_txt);
		
		currencyTextView1 = (TextView) view.findViewById(R.id.currency_label1);
		totalTextView = (TextView) view.findViewById(R.id.total_txt);
		currencyTextView2 = (TextView) view.findViewById(R.id.currency_label2);
		leftTextLabel = (TextView) view.findViewById(R.id.left_label);

		currencyTextView1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currencyTextView2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);

		mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
		leftTextView = (TextView) view.findViewById(R.id.budget_amount);
		setBudgetLayout = (LinearLayout) view.findViewById(R.id.setbudget_linearLayout);
		transferLayout = (LinearLayout) view.findViewById(R.id.transfer_linearLayout);
		mListView = (ListView) view.findViewById(R.id.budget_listview);
		budgetListApdater = new BudgetListApdater(mActivity);
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
				int item_id = (Integer) mBudgetList.get(arg2).get(
						"_id");
				
				Intent intent = new Intent();
				intent.putExtra("_id", category_id);
				intent.putExtra("item_id", item_id);
				intent.putExtra("categoryName", categoryName);
				intent.putExtra("date", month.getTimeInMillis());
				intent.setClass(mActivity,
						BudgetToTransactionActivity.class);
				startActivityForResult(intent, 17);
			}
		});

		setBudgetLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mActivity, BudgetListActivity.class);
				startActivityForResult(intent, 4);
			}
		});

		transferLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mActivity,
						BudgetTransferActivity.class);
				startActivityForResult(intent, 15);

			}
		});

		Thread mThread = new Thread(mTask);
		mThread.start();
		
		return view;
	}


	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mBudgetList = OverViewDao.selectBudget(mActivity);

			Calendar calendar = Calendar.getInstance();
			long firstDay = MEntity.getFirstDayOfMonthMillis(month
					.getTimeInMillis());
			long lastDay = MEntity.getLastDayOfMonthMillis(month
					.getTimeInMillis());
			List<Map<String, Object>> mTransferList = OverViewDao
					.selectBudgetTransfer(mActivity, firstDay,
							lastDay);
			
			BigDecimal b0 = new BigDecimal("0");
			BigDecimal bt0 = new BigDecimal("0");
			for (Map<String, Object> iMap : mBudgetList) {
				int _id = (Integer) iMap.get("_id");
				String catrgoryName = (String) iMap.get("categoryName");
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
								mActivity, catrgoryName, firstDay,
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
			final String uuid = (String) mBudgetList.get(arg2).get("uuid");
			
			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = { "Edit", "Delete" };

			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(
					mActivity, data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (arg2 == 0) {

						Intent intent = new Intent();
						intent.putExtra("_id", id);
						intent.putExtra("uuid", uuid);
						
						intent.setClass(mActivity,
								EditBudgetActivity.class);
						startActivityForResult(intent, 16);
						alertDialog.dismiss();

					} else if (arg2 == 1) {
						long row = BudgetsDao.deleteBudget(mActivity,
								id, uuid, MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						mHandler.post(mTask);
						alertDialog.dismiss();

					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(
					mActivity);
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

		case 12:

			if (data != null) {

				mHandler.post(mTask);
			}
			break;
		}
	}

	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		Toast.makeText(mActivity, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
	}

}
