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
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.BudgetActivity;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.overview.transaction.AccountsListViewAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

public class BillPayActivity extends Activity{
	private ActionBar actionBar;
	private Map<String, Object> mMap;
	private int indexflag;
	private int _id;
	private EditText amountEditText;
	private Button accountButton;
	private Button dateButton;
	private String amountString = "0.0";
	private int accountId = 0;
	private AlertDialog mAccountDialog;
	private LayoutInflater inflater;
	private ListView accountListView ;
	private AccountsListViewAdapter accountListViewAdapter ;
	private int accountCheckItem = 0;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String dateString;
	private long dateLong;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_pay);
		
		inflater = LayoutInflater.from(BillPayActivity.this);
		Intent intent= getIntent();
		if (intent == null) {
			finish();
		} 
		mMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
		indexflag = (Integer) mMap.get("indexflag");
		_id = (Integer) mMap.get("_id");
		String billName = (String) mMap.get("ep_billName");
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(billName);
		
		amountEditText = (EditText) findViewById(R.id.amount_edit);
		accountButton = (Button) findViewById(R.id.account_btn);
		dateButton = (Button) findViewById(R.id.paydate_btn);
		
		amountEditText.setText("0.00");
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
		
		List<Map<String, Object>> mAccountList = AccountDao.selectAccount(BillPayActivity.this);
		if (mAccountList != null && mAccountList.size() > 0) {
			accountId = (Integer) mAccountList.get(0).get("_id");
			accountButton.setText((String) mAccountList.get(0).get("accName"));
		}
		
		accountButton.setOnClickListener(mClickListener);
		dateButton.setOnClickListener(mClickListener);
		
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();
		
	}
	
	private final View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.account_btn:
				
				View view2 = inflater.inflate(R.layout.dialog_choose_type, null);
				 accountListView = (ListView) view2.findViewById(R.id.mListView);
				 accountListViewAdapter = new AccountsListViewAdapter(BillPayActivity.this);

		final List<Map<String, Object>> mAccountList = AccountDao
				.selectAccount(BillPayActivity.this);
		accountListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		accountListView.setAdapter(accountListViewAdapter);
		accountListView.setSelection((accountCheckItem - 1) > 0 ? (accountCheckItem - 1)
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
						accountListViewAdapter.setItemChecked(accountCheckItem);
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
				BillPayActivity.this);
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
						new ContextThemeWrapper(BillPayActivity.this,
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
			
			if (indexflag == 0 || indexflag ==1) {
				long row = BillsDao.insertTransactionRule(BillPayActivity.this, amountString, dateLong, accountId, _id);
				Intent intent = new Intent();
				intent.putExtra("_id", row);
				setResult(17, intent);
			} else if (indexflag == 2) {
				
				String ep_billItemAmount = (String) mMap.get("ep_billAmount");
				int ep_billisDelete = 0;
				long ep_billItemDueDate = (Long) mMap.get("ep_billDueDate");
				long ep_billItemDueDateNew = ep_billItemDueDate;
				long ep_billItemEndDate = (Long) mMap.get("ep_billEndDate");
				String ep_billItemName = (String) mMap.get("ep_billName");
				String ep_billItemNote = (String) mMap.get("ep_note");
				int ep_billItemRecurringType = (Integer) mMap.get("ep_recurringType");
				int ep_billItemReminderDate = (Integer) mMap.get("ep_reminderDate");
				long ep_billItemReminderTime = (Long) mMap.get("ep_reminderTime");
				int billItemHasBillRule = _id;
				int billItemHasCategory = (Integer) mMap.get("billRuleHasCategory");
				int billItemHasPayee = (Integer) mMap.get("billRuleHasPayee");
				
				long row = BillsDao.insertBillItem(BillPayActivity.this, ep_billisDelete, ep_billItemAmount, ep_billItemDueDate, ep_billItemDueDateNew, ep_billItemEndDate, ep_billItemName, ep_billItemNote, ep_billItemRecurringType, ep_billItemReminderDate, ep_billItemReminderTime, billItemHasBillRule, billItemHasCategory, billItemHasPayee);
				mMap.put("_id", row);
				mMap.put("ep_billisDelete", 0);
				mMap.put("ep_billItemDueDateNew",ep_billItemDueDateNew);
				mMap.put("billItemHasBillRule", _id);
				
				long row1 = BillsDao.insertTransactionItem(BillPayActivity.this, amountString, dateLong, accountId, (int)row);
				
				Intent intent = new Intent();
				intent.putExtra("_id", row);
				intent.putExtra("dataMap",(Serializable)mMap);
				setResult(18, intent);
				
			} else if (indexflag == 3) {
				
				long row = BillsDao.insertTransactionItem(BillPayActivity.this, amountString, dateLong, accountId, _id);
				Intent intent = new Intent();
				intent.putExtra("_id", row);
				setResult(17, intent);
			}
			
			finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}