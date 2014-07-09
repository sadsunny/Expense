package com.appxy.pocketexpensepro.reports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransferActivity;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ReCashListActivity extends Activity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private Thread mThread;

	private ListView mListView;
	private long startDate;
	private long endDate;
	private List<Map<String, Object>> mDataList;
	private ReCashListAdapter mAdapter;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				mAdapter.setAdapterDate(mDataList);
				mAdapter.notifyDataSetChanged();

				break;

			case MSG_FAILURE:
				Toast.makeText(ReCashListActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recashlist);
		mInflater = LayoutInflater.from(this);
		mListView = (ListView) findViewById(R.id.mListView);
		mListView.setDividerHeight(0);

		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		int rangType = intent.getIntExtra("rangType", 0);
		long dateLong = intent.getLongExtra("dateLong", 0);

		if (rangType == 4 || rangType == 5) {
			startDate = MEntity.getFirstDayOfMonthMillis(dateLong);
			endDate = MEntity.getLastDayOfMonthMillis(dateLong);
		} else {
			startDate = dateLong;
			endDate = dateLong;
		}

		mAdapter = new ReCashListAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(onClickListener);
		mListView.setOnItemLongClickListener(onLongClickListener);

		mThread = new Thread(mTask);
		mThread.start();

		Intent resultintent = new Intent();
		resultintent.putExtra("row", 1);
		setResult(1, resultintent);

	}

	private OnItemClickListener onClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			final int tId = (Integer) mDataList.get(arg2).get("_id");
			int expenseAccount = (Integer) (Integer) mDataList.get(arg2).get(
					"expenseAccount");
			int incomeAccount = (Integer) mDataList.get(arg2).get(
					"incomeAccount");
			String childTransactionstring = (String) mDataList.get(arg2).get(
					"childTransactions");
			int childTransactions = 0;
			try {
				childTransactions = Integer.parseInt(childTransactionstring);
			} catch (Exception e) {
				// TODO: handle exception
				childTransactions = 1;
			}
			if (childTransactions == 1) {

				new AlertDialog.Builder(ReCashListActivity.this)
						.setTitle("Warning! ")
						.setMessage(
								"This is a part of a transaction splite, and ot can not be edit alone! ")
						.setPositiveButton("Retry",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();

									}
								}).show();

			} else {

				if (expenseAccount > 0 && incomeAccount > 0) {

					Intent intent = new Intent();
					intent.putExtra("_id", tId);
					intent.setClass(ReCashListActivity.this,
							EditTransferActivity.class);
					startActivityForResult(intent, 13);

				} else {

					Intent intent = new Intent();
					intent.putExtra("_id", tId);
					intent.setClass(ReCashListActivity.this,
							EditTransactionActivity.class);
					startActivityForResult(intent, 13);

				}
			}

		}
	};

	private OnItemLongClickListener onLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub

			final int _id = (Integer) (Integer) mDataList.get(arg2).get("_id");
			String childTransactionstring = (String) mDataList.get(arg2).get(
					"childTransactions");
			int childTransactions = 0;
			try {
				childTransactions = Integer.parseInt(childTransactionstring);
			} catch (Exception e) {
				// TODO: handle exception
				childTransactions = 1;
			}

			if (childTransactions == 1) {

				new AlertDialog.Builder(ReCashListActivity.this)
						.setTitle("Warning! ")
						.setMessage(
								"This is a part of a transaction splite, and ot can not be edit alone! ")
						.setPositiveButton("Retry",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();

									}
								}).show();
			} else {

				View dialogView = mInflater.inflate(
						R.layout.dialog_item_operation, null);

				String[] data = { "Delete" };
				ListView diaListView = (ListView) dialogView
						.findViewById(R.id.dia_listview);
				DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(
						ReCashListActivity.this, data);
				diaListView.setAdapter(mDialogItemAdapter);
				diaListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						if (arg2 == 0) {

							long row = AccountDao.deleteTransaction(
									ReCashListActivity.this, _id);
							alertDialog.dismiss();
							mHandler.post(mTask);

						}

					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ReCashListActivity.this);
				builder.setView(dialogView);
				alertDialog = builder.create();
				alertDialog.show();
			}
			return true;
		}
	};

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList = ReportDao.selectTransactionByTimeBEPay(
					ReCashListActivity.this, startDate, endDate);
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

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
		case 13:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;
		}
	}

}
