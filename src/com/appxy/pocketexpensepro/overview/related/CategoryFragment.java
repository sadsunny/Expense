package com.appxy.pocketexpensepro.overview.related;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransferActivity;
import com.appxy.pocketexpensepro.bills.BillsDao;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.reports.ReportDao;

public class CategoryFragment extends Fragment implements
		OnSyncFinishedListener {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	
	private Activity mActivity;
	private int categoryId;
	private LayoutInflater mInflater;
	private ListView mListView;
	private ListViewAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mDataList;
	private AlertDialog alertDialog;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				
				if (mDataList != null) {
					mAdapter.setAdapterDate(mDataList);
					mAdapter.notifyDataSetChanged();
				}
				
				break;

			case MSG_FAILURE:
				Toast.makeText(getActivity(), "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHandler.post(mTask);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 Bundle bundle = getArguments(); 
		 if (bundle != null ) {
			categoryId = bundle.getInt("categoryId");
		}else {
			categoryId = 0;
		}
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.mInflater = inflater;
		View view = inflater.inflate(R.layout.related_fragment, container,
				false);
		mListView = (ListView) view.findViewById(R.id.mListView);
		mListView.setDividerHeight(0);
		mAdapter = new ListViewAdapter(mActivity);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(longClickListener);
		mListView.setOnItemClickListener(onClickListener);
		
		
		Thread mThread = new Thread(mTask);
		mThread.start();
		
		return view;
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList = OverViewDao.selectTransactionByCategoryIdJoin(mActivity, categoryId);
			if (mDataList != null) {
				reFillData(mDataList);
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	
	private OnItemClickListener onClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {

			// TODO Auto-generated method stub
			final int tId = (Integer) mDataList.get(paramInt).get("_id");
			int expenseAccount = (Integer) (Integer) mDataList.get(paramInt)
					.get("expenseAccount");
			int incomeAccount = (Integer) mDataList.get(paramInt).get(
					"incomeAccount");

			if (expenseAccount > 0 && incomeAccount > 0) {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(mActivity, EditTransferActivity.class);
				startActivityForResult(intent, 13);

			} else {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(mActivity, EditTransactionActivity.class);
				startActivityForResult(intent, 13);

			}
		}
	};

	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int _id = (Integer) mDataList.get(arg2).get("_id");
			final String uuid = (String) mDataList.get(arg2).get("uuid");
			
			final int theCategory = (Integer) mDataList.get(arg2).get("category");
			final int theExpenseAccount = (Integer) mDataList.get(arg2).get("expenseAccount");
			final int theIncomeAccount = (Integer) mDataList.get(arg2).get("incomeAccount");
			
			final Map<String, Object> mMap = mDataList.get(arg2);
			final int parTransaction = (Integer) mDataList.get(arg2).get(
					"parTransaction");

			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = {"Duplicate", "Delete" };
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

						Calendar c = Calendar.getInstance(); // 处理为当天固定格式时间
						Date date = new Date(c.getTimeInMillis());
						SimpleDateFormat sdf = new SimpleDateFormat(
								"MM-dd-yyyy");
						try {
							c.setTime(new SimpleDateFormat("MM-dd-yyyy")
									.parse(sdf.format(date)));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String amount = (String) mMap.get("amount");
						long dateTime = c.getTimeInMillis();
						int isClear = (Integer) mMap.get("isClear");
						String notes = (String) mMap.get("notes");
						String photoName = (String) mMap.get("photoName");
						int recurringType = (Integer) mMap.get("recurringType");
						int category = (Integer) mMap.get("category");
						String childTransactions = (String) mMap
								.get("childTransactions");
						int expenseAccount = (Integer) mMap
								.get("expenseAccount");
						int incomeAccount = (Integer) mMap.get("incomeAccount");
						int parTransaction = (Integer) mMap
								.get("parTransaction");
						int payee = (Integer) mMap.get("payee");

						long row = TransactionDao.insertTransactionAll(
								mActivity, amount, dateTime, isClear, notes,
								photoName, recurringType, category,
								childTransactions, expenseAccount,
								incomeAccount, parTransaction, payee, null, 0, 0 , MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						alertDialog.dismiss();

						mHandler.post(mTask);

					} else if (arg2 == 1) {

						long row = AccountDao.deleteTransaction(mActivity, _id, uuid, MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						if (parTransaction == -1) {
							AccountDao.deleteTransactionChild(mActivity, _id , MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						}
						alertDialog.dismiss();
						mHandler.post(mTask);
					}
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();
			return true;
		}

	};

	public void reFillData( ArrayList<HashMap<String, Object>> mData) {

		for (Map<String, Object> mMap : mData) {
			
			String payeeName = (String) mMap.get("payeeName");
			int categoryId = (Integer) mMap.get("categoryId");
			if (categoryId <= 0) {
				int expenseAccount = (Integer) mMap.get("expenseAccount");
				int incomeAccount = (Integer) mMap.get("incomeAccount");
				
				if (expenseAccount > 0 && incomeAccount > 0) {
					mMap.put("iconName", 69);
				} else {
					mMap.put("iconName", 56);
				}
				
			}
			
			if (payeeName == null) {
				String memoString = (String) mMap.get("notes");
				if (memoString == null ) {
					mMap.put("payeeName", "--");
				} else {
					
					if (memoString.length() > 0) {
						mMap.put("payeeName", memoString);
					} else {
						mMap.put("payeeName", "--");
					}
					
				}
			} 
		}
		
	}
	
	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		mHandler.post(mTask);
	}

}
