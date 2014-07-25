package com.appxy.pocketexpensepro.bills;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.BudgetActivity;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.overview.transaction.AccountsListViewAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactonByAccountActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EditBillPayActivity extends BaseHomeActivity {
	private ActionBar actionBar;
	private Map<String, Object> mMap;
	private int _id;
	private EditText amountEditText;
	private Button accountButton;
	private Button dateButton;
	private String amountString = "0.0";
	private int accountId = 0;
	private AlertDialog mAccountDialog;
	private LayoutInflater inflater;
	private ListView accountListView;
	private AccountsListViewAdapter accountListViewAdapter;
	private int accountCheckItem = 0;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String dateString;
	private long dateLong;
	private int  payId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_pay);

		inflater = LayoutInflater.from(EditBillPayActivity.this);

		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(
				R.layout.activity_custom_actionbar, null, false);

		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView, lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		View cancelActionView = customActionBarView
				.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(mClickListener);
		View doneActionView = customActionBarView
				.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mClickListener);

		Intent intent = getIntent();
		if (intent == null) {
			finish();
		}
		
		payId = (Integer) intent.getIntExtra("_id", 0) ;
		if (payId <= 0) {
			finish();
		}
		
		amountEditText = (EditText) findViewById(R.id.amount_edit);
		accountButton = (Button) findViewById(R.id.account_btn);
		dateButton = (Button) findViewById(R.id.paydate_btn);
		
		List<Map<String, Object>> mList = BillsDao.selectTransactionById(this, payId);
		String amount1 = (String) mList.get(0).get("amount");
		long dateTime1 = (Long) mList.get(0).get("dateTime");
		int expenseAccount1 = (Integer) mList.get(0).get("expenseAccount");

		amountEditText.setText(MEntity.doubl2str(amount1));
		amountEditText.setSelection(MEntity.doubl2str(amount1).length());
		amountString = amount1;
		amountEditText.addTextChangedListener(new TextWatcher() { // 设置保留两位小数
					private boolean isChanged = false;

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

						if (isChanged) {// ----->如果字符未改变则返回
							return;
						}
						String str = s.toString();

						isChanged = true;
						String cuttedStr = str;
						/* 删除字符串中的dot */
						for (int i = str.length() - 1; i >= 0; i--) {
							char c = str.charAt(i);
							if ('.' == c) {
								cuttedStr = str.substring(0, i)
										+ str.substring(i + 1);
								break;
							}
						}
						/* 删除前面多余的0 */
						int NUM = cuttedStr.length();
						int zeroIndex = -1;
						for (int i = 0; i < NUM - 2; i++) {
							char c = cuttedStr.charAt(i);
							if (c != '0') {
								zeroIndex = i;
								break;
							} else if (i == NUM - 3) {
								zeroIndex = i;
								break;
							}
						}
						if (zeroIndex != -1) {
							cuttedStr = cuttedStr.substring(zeroIndex);
						}
						/* 不足3位补0 */
						if (cuttedStr.length() < 3) {
							cuttedStr = "0" + cuttedStr;
						}
						/* 加上dot，以显示小数点后两位 */
						cuttedStr = cuttedStr.substring(0,
								cuttedStr.length() - 2)
								+ "."
								+ cuttedStr.substring(cuttedStr.length() - 2);

						amountEditText.setText(cuttedStr);
						amountString = amountEditText.getText().toString();
						amountEditText.setSelection(cuttedStr.length());
						isChanged = false;
					}
				});

		List<Map<String, Object>> mAccountList = AccountDao
				.selectAccount(EditBillPayActivity.this);
		accountId = expenseAccount1;
		if (mAccountList != null && mAccountList.size() > 0) {
			
			for (int i = 0; i < mAccountList.size(); i++) {
				int aid = (Integer) mAccountList.get(i).get("_id");
				if (aid == accountId) {
					accountCheckItem = i;
					accountButton.setText((String) mAccountList.get(i).get("accName"));
				}
			}
		}
		

		accountButton.setOnClickListener(mClickListener);
		dateButton.setOnClickListener(mClickListener);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dateTime1);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();

	}

	private final View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
				
				double amountDouble = 0;
				
				try {
					amountDouble = Double.parseDouble(amountString);
				} catch (NumberFormatException e) {
					amountDouble = 0.00;
				}
				
				if (amountDouble == 0) {
					
					new AlertDialog.Builder(EditBillPayActivity.this)
					.setTitle("Warning! ")
					.setMessage(
							"Please make sure the amount is not zero! ")
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}).show();
				} else if(accountId == 0){
					new AlertDialog.Builder(EditBillPayActivity.this)
					.setTitle("Warning! ")
					.setMessage("Please choose an Account! ")
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}).show();
				}else{

					long row = BillsDao.updateBillPay(EditBillPayActivity.this, payId, amountString, dateLong ,accountId);
					Intent intent = new Intent();
					intent.putExtra("_id", row);
					setResult(17, intent);
					finish();
			}
				
				break;
			case R.id.account_btn:

				View view2 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				accountListView = (ListView) view2.findViewById(R.id.mListView);
				accountListViewAdapter = new AccountsListViewAdapter(
						EditBillPayActivity.this);

				final List<Map<String, Object>> mAccountList = AccountDao
						.selectAccount(EditBillPayActivity.this);
				accountListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				accountListView.setAdapter(accountListViewAdapter);
				accountListView
						.setSelection((accountCheckItem - 1) > 0 ? (accountCheckItem - 1)
								: 0);
				accountListViewAdapter.setItemChecked(accountCheckItem);
				accountListViewAdapter.setAdapterDate(mAccountList);
				accountListViewAdapter.notifyDataSetChanged();
				accountListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								accountCheckItem = arg2;
								accountListViewAdapter
										.setItemChecked(accountCheckItem);
								accountListViewAdapter.notifyDataSetChanged();
								String nameString = (String) mAccountList.get(
										arg2).get("accName");
								accountButton.setText(nameString);

								accountId = (Integer) mAccountList.get(arg2)
										.get("_id");
								mAccountDialog.dismiss();
							}
						});

				AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(
						EditBillPayActivity.this);
				mBuilder2.setTitle("Choose Account");
				mBuilder2.setView(view2);
				mBuilder2.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mAccountDialog = mBuilder2.create();
				mAccountDialog.show();

				break;

			case R.id.paydate_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(EditBillPayActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Date");
				DPD.show();

				break;
			}
		}
	};

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}

	};

	private void updateDisplay() {
		// TODO Auto-generated method stub

		dateString = (new StringBuilder().append(mMonth + 1).append("-")
				.append(mDay).append("-").append(mYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dateLong = c.getTimeInMillis();

		Date date = new Date(dateLong);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
		dateButton.setText(sdf.format(date));

	}


}
