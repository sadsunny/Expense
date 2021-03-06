package com.appxy.pocketexpensepro.bills;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;

public class BillDetailsActivity extends BaseHomeActivity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private Thread mThread;
	private Button payButton;
	private Map<String, Object> mMap;
	private ActionBar actionBar;

	private ImageView categoryImageView;
	private TextView nameTextView;
	private TextView dueDaTextView;
	private TextView currencyTextView1;
	private TextView currencyTextView2;
	private TextView currencyTextView3;
	private TextView totalTextView;
	private TextView paidTextView;
	private TextView remainTextView;
	private ListView mListView;
	private View line_label;
	private LinearLayout paidLayout;
	
	private int _id;
	private int indexflag;
	private String ep_billAmount;
	private String ep_billName;
	private long ep_billDueDate;
	private int iconName;
	private double paidAmount;
	private double remain;
	private List<Map<String, Object>> pDataList;
	private BillPayListViewAdapter billPayListViewAdapter;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				categoryImageView
						.setImageResource(Common.CATEGORY_ICON[iconName]);
				nameTextView.setText(ep_billName);
				dueDaTextView.setText(MEntity.turnToDateString(ep_billDueDate));
				currencyTextView1
						.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
				currencyTextView2
						.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
				currencyTextView3
						.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
				totalTextView.setText(MEntity.doublepoint2str(ep_billAmount));
				paidTextView.setText(MEntity.doublepoint2str(paidAmount + ""));

				if (remain <= 0) {
//					payButton.setEnabled(false);
//					payButton.setText("Paid");
//					payButton.setTextColor(Color.parseColor("#947156"));
//					payButton.setBackgroundResource(R.drawable.paid_bag);
					remainTextView.setText(MEntity.doublepoint2str(0 + ""));

					payButton.setVisibility(View.GONE);
					paidLayout.setVisibility(View.VISIBLE);
				} else {
					payButton.setVisibility(View.VISIBLE);
					paidLayout.setVisibility(View.GONE);
					payButton.setEnabled(true);
					payButton.setText("Pay Bill");
					payButton.setTextColor(Color.parseColor("#45211c"));
					payButton
							.setBackgroundResource(R.drawable.add_payment_selector);
					remainTextView
							.setText(MEntity.doublepoint2str(remain + ""));
				}

				billPayListViewAdapter.setAdapterDate(pDataList);
				billPayListViewAdapter.notifyDataSetChanged();
				if (pDataList.size() > 0) {
					line_label.setVisibility(View.VISIBLE);
				} else {
					line_label.setVisibility(View.GONE);
				}

				break;

			case MSG_FAILURE:
				Toast.makeText(BillDetailsActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_details);
		mInflater = LayoutInflater.from(this);

		Intent intent = getIntent();
		if (intent == null) {
			finish();
		}
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
		if (mMap == null) {
			finish();
		}

		line_label = (View)findViewById(R.id.line_label);
		paidLayout = (LinearLayout) findViewById(R.id.PaidLinearLayout);
		categoryImageView = (ImageView) findViewById(R.id.category_imageView);
		nameTextView = (TextView) findViewById(R.id.account_textView);
		dueDaTextView = (TextView) findViewById(R.id.date_textView);
		currencyTextView1 = (TextView) findViewById(R.id.currency_textView1);
		currencyTextView2 = (TextView) findViewById(R.id.currency_textView2);
		currencyTextView3 = (TextView) findViewById(R.id.currency_textView3);
		totalTextView = (TextView) findViewById(R.id.Total_textView);
		paidTextView = (TextView) findViewById(R.id.Paid_textView);
		remainTextView = (TextView) findViewById(R.id.remain_textView);
		mListView = (ListView) findViewById(R.id.mListview);
		billPayListViewAdapter = new BillPayListViewAdapter(
				BillDetailsActivity.this);
		mListView.setDividerHeight(0);
		mListView.setAdapter(billPayListViewAdapter);
		mListView.setOnItemClickListener(mClickListener);
		mListView.setOnItemLongClickListener(mLongClickListener);
		//
		payButton = (Button) findViewById(R.id.paybill_btn);
		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("dataMap", (Serializable) mMap);
				intent.putExtra("remain", remain);
				intent.setClass(BillDetailsActivity.this, BillPayActivity.class);
				startActivityForResult(intent, 17);
			}
		});

		mThread = new Thread(mTask);
		mThread.start();

	}

	private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int _id = (Integer) pDataList.get(arg2).get("_id");
			Intent intent = new Intent();
			intent.putExtra("_id", _id);
			intent.setClass(BillDetailsActivity.this, EditBillPayActivity.class);
			startActivityForResult(intent, 17);

		}
	};

	private OnItemLongClickListener mLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int _id = (Integer) pDataList.get(arg2).get("_id");

			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = { "Delete" };
			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(
					BillDetailsActivity.this, data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					if (arg2 == 0) {
						BillsDao.deleteBillPayById(BillDetailsActivity.this,
								_id);
						Intent intent = new Intent();
						intent.putExtra("_id", 1);
						setResult(16, intent);
						alertDialog.dismiss();
						mHandler.post(mTask);
					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(
					BillDetailsActivity.this);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
		}
	};

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			_id = (Integer) mMap.get("_id");
			indexflag = (Integer) mMap.get("indexflag");
			ep_billAmount = (String) mMap.get("ep_billAmount");
			ep_billDueDate = (Long) mMap.get("ep_billDueDate");
			long ep_billEndDate = (Long) mMap.get("ep_billEndDate");
			ep_billName = (String) mMap.get("ep_billName");
			String ep_note = (String) mMap.get("ep_note");
			int ep_recurringType = (Integer) mMap.get("ep_recurringType");
			int ep_reminderDate = (Integer) mMap.get("ep_reminderDate");
			long ep_reminderTime = (Long) mMap.get("ep_reminderTime");
			int billRuleHasCategory = (Integer) mMap.get("billRuleHasCategory");
			int billRuleHasPayee = (Integer) mMap.get("billRuleHasPayee");
			iconName = (Integer) mMap.get("iconName");

			// int ep_billisDelete = 0;
			// int billItemHasBillRule = _id;
			// long ep_billItemDueDateNew = 1;

			paidAmount = 0;
			BigDecimal b0 = new BigDecimal(ep_billAmount);
			remain = b0.doubleValue();

			if (indexflag == 0 || indexflag == 1) {

				pDataList = BillsDao.selectTransactionByBillRuleId(
						BillDetailsActivity.this, _id);

				BigDecimal b1 = new BigDecimal(0.0); // pay的总额
				for (Map<String, Object> pMap : pDataList) {
					String pAmount = (String) pMap.get("amount");
					BigDecimal b2 = new BigDecimal(pAmount);
					b1 = b1.add(b2);
				}
				remain = (b0.subtract(b1)).doubleValue();
				paidAmount = b1.doubleValue();

			} else if (indexflag == 2) {
				pDataList = new ArrayList<Map<String, Object>>();
			} else if (indexflag == 3) {

				pDataList = BillsDao.selectTransactionByBillItemId(
						BillDetailsActivity.this, _id);
				Log.v("mtest", "pDataList" + pDataList);

				BigDecimal b1 = new BigDecimal(0.0); // pay的总额
				for (Map<String, Object> pMap : pDataList) {
					String pAmount = (String) pMap.get("amount");
					BigDecimal b2 = new BigDecimal(pAmount);
					b1 = b1.add(b2);
				}
				remain = (b0.subtract(b1)).doubleValue();
				paidAmount = b1.doubleValue();
			}

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 17:

			if (data != null) {
				Intent intent = new Intent();
				intent.putExtra("_id", 1);
				setResult(16, intent);
				mHandler.post(mTask);
			}else {
				finish();
			}
			break;
		case 16:

			if (data != null) {
				Intent intent = new Intent();
				intent.putExtra("_id", 1);
				setResult(16, intent);
				mHandler.post(mTask);
			}else {
				finish();
			}
			break;

		case 18:

			if (data != null) {
				Intent intent = new Intent();
				intent.putExtra("_id", 1);
				setResult(16, intent);

				mMap = (Map<String, Object>) data
						.getSerializableExtra("dataMap");
				mHandler.post(mTask);
			}else {
				finish();
			}
			break;

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

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
	}
	

}
