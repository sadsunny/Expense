package com.appxy.pocketexpensepro.accounts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.ChooseTypeListViewAdapter.ViewHolder;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/*
 * 对CreatAccountTypeActivity发出Result code 1
 * 返回到Account主页面的Result code 2
 */
public class EditAccountActivity extends Activity {

	private LayoutInflater inflater;
	private EditText accountEditText;
	private EditText balanceEditText;
	private Button typeButton;
	private Button dateButton;
	private Spinner clearSpinner;
	private ImageView addButton;
	private ImageView subButton;

	private int checkedItem = 0; // choosed position
	private ChooseTypeListViewAdapter mListViewAdapter;
	private ListView mListView;
	private AlertDialog mDialog;
	private List<Map<String, Object>> mTypeList;
	private int accountTypeSize = 0;
	private int typeId; // the type id default 1
	private String balenceAmountString = "0.00";
	private int balenceCheck = 1; // 1 add, 0 sub

	private int mYear;
	private int mMonth;
	private int mDay;
	private String dateString;
	private long dateLong;
	private int clearCheck = 1; // default on 1
	private double balanceDouble = 0.0;

	private int _id;
	private List<Map<String, Object>> mDataList;
	private String seAcccountName;
	private String seAmount;
	private long seDate;
	private String seTypeName;
	private int seTypeId;
	private int seIsclear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_new_account);

		Intent intent = getIntent();
		if (intent != null) {
			_id = intent.getIntExtra("_id", 0);
		}

		if (_id <= 0) {
			finish();
		}

		mDataList = AccountDao.selectAccountById(this, _id);

		if (mDataList == null || mDataList.size() == 0) {
			finish();
		}
		seAcccountName = (String) mDataList.get(0).get("accName");
		seAmount = (String) mDataList.get(0).get("amount");
		seDate = (Long) mDataList.get(0).get("dateTime");
		seTypeName = (String) mDataList.get(0).get("typeName");
		seTypeId = (Integer) mDataList.get(0).get("tpye_id");
		seIsclear = (Integer) mDataList.get(0).get("autoClear");
		typeId = seTypeId;
		dateLong = seDate;
		checkedItem = locationTypePosition(AccountDao.selectAccountType(EditAccountActivity.this), seTypeId);
		balenceAmountString = seAmount;
		
		inflater = LayoutInflater.from(EditAccountActivity.this);
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

		accountEditText = (EditText) findViewById(R.id.account_edit);
		balanceEditText = (EditText) findViewById(R.id.balance_edit);
		typeButton = (Button) findViewById(R.id.type_btn);
		dateButton = (Button) findViewById(R.id.date_btn);
		clearSpinner = (Spinner) findViewById(R.id.clear_spin); // spinner
		addButton = (ImageView) findViewById(R.id.add_btn);
		subButton = (ImageView) findViewById(R.id.sub_btn);
		addButton.setImageResource(R.drawable.b_add_sel);
		subButton.setImageResource(R.drawable.b_sub);

		accountEditText.setText(seAcccountName);
		accountEditText.setSelection(seAcccountName.length());
		accountEditText.setSelectAllOnFocus(true);

		balanceEditText.setText(MEntity.doubl2str(seAmount));

		typeButton.setOnClickListener(mClickListener);
		dateButton.setOnClickListener(mClickListener);
		addButton.setOnClickListener(mClickListener);
		subButton.setOnClickListener(mClickListener);

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(EditAccountActivity.this, R.array.on_off,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clearSpinner.setAdapter(adapterSpinner);
		if (seIsclear == 1) {
			clearSpinner.setSelection(0);
			clearCheck = 1;
		} else {
			clearSpinner.setSelection(1);
			clearCheck = 0;
		}
		clearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					clearCheck = 1;
				} else if (arg2 == 1) {
					clearCheck = 0;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		mListViewAdapter = new ChooseTypeListViewAdapter(this);
		typeButton.setText(seTypeName);

		balanceEditText.addTextChangedListener(new TextWatcher() { // 设置保留两位小数
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

						balanceEditText.setText(cuttedStr);
						balenceAmountString = balanceEditText.getText()
								.toString();
						balanceEditText.setSelection(cuttedStr.length());
						isChanged = false;
					}
				});

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(seDate);
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

				String accountName = accountEditText.getText().toString();

				try {
					balanceDouble = Double.parseDouble(balenceAmountString);
				} catch (NumberFormatException e) {
					balanceDouble = 0.00;
				}

				Log.v("mtest", "1balenceAmountString" + balenceAmountString);
				Log.v("mtest", "2balanceDouble" + balanceDouble);

				if (accountName == null || accountName.trim().length() == 0
						|| accountName.trim().equals("")) {

					new AlertDialog.Builder(EditAccountActivity.this)
							.setTitle("Warning! ")
							.setMessage(
									"Please make sure the account name is not empty! ")
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
				} else if (balanceDouble == 0) {

					new AlertDialog.Builder(EditAccountActivity.this)
							.setTitle("Warning! ")
							.setMessage(
									"Please make sure the balance amount is not zero! ")
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
				} else {

					if (balenceCheck == 0) {
						balanceDouble = 0 - balanceDouble;
					}

					long row = AccountDao.updateAccount(EditAccountActivity.this,_id,accountName,balenceAmountString, dateLong, clearCheck, typeId);

					Intent intent = new Intent();
					intent.putExtra("aName", accountName);
					intent.putExtra("_id", row);
					setResult(8, intent);
					finish();
				}
				break;

			case R.id.type_btn:

				View view = inflater.inflate(R.layout.dialog_choose_type, null);

				mTypeList = AccountDao
						.selectAccountType(EditAccountActivity.this);
				accountTypeSize = mTypeList.size();

				mListView = (ListView) view.findViewById(R.id.mListView);
				mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				mListView.setItemsCanFocus(false);
				mListView.setAdapter(mListViewAdapter);
				mListView
						.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
								: 0);
				mListViewAdapter.setItemChecked(checkedItem);
				mListViewAdapter.setAdapterDate(mTypeList);
				mListViewAdapter.notifyDataSetChanged();
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						checkedItem = arg2;
						typeId = (Integer) mTypeList.get(arg2).get("_id");
						typeButton.setText(mTypeList.get(arg2).get("typeName")
								+ "");
						mListViewAdapter.setItemChecked(checkedItem);
						mListViewAdapter.notifyDataSetChanged();
						mDialog.dismiss();
					}
				});

				AlertDialog.Builder mBuilder = new AlertDialog.Builder(
						EditAccountActivity.this);
				mBuilder.setTitle("Choose Account Type");
				mBuilder.setView(view);
				mBuilder.setPositiveButton("New Tpye",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent intent = new Intent();
								intent.setClass(EditAccountActivity.this,
										CreatAccountTypeActivity.class);
								startActivityForResult(intent, 1);
							}
						});
				mBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mDialog = mBuilder.create();
				mDialog.show();
				break;

			case R.id.date_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(EditAccountActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Due Date");
				DPD.show();
				break;

			case R.id.add_btn:
				balenceCheck = 1;
				addButton.setImageResource(R.drawable.b_add_sel);
				subButton.setImageResource(R.drawable.b_sub);

				break;

			case R.id.sub_btn:
				balenceCheck = 0;
				addButton.setImageResource(R.drawable.b_add);
				subButton.setImageResource(R.drawable.b_sub_sel);

				break;

			default:
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
	
	private int locationTypePosition(List<Map<String, Object>> mData, int id) {
		int i = 0;
		int position = 0;
		for (Map<String, Object> mMap : mData) {
			int tId = (Integer) mMap.get("_id");
			if (tId == id) {
				position = i;
			}
			i = i + 1;
		}

		return position;
		
	}

	private void updateDisplay() {
		// TODO Auto-generated method stub

		dateString = (new StringBuilder().append(mMonth + 1).append("-")
				.append(mDay).append("-").append(mYear)).toString();
		dateButton.setText(dateString);

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dateLong = c.getTimeInMillis();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case 1:

			if (data != null) {
				checkedItem = accountTypeSize;
				typeButton.setText(data.getStringExtra("typeName"));
				typeId = (Integer) data.getIntExtra("_id", 0);
			}
			break;

		}

	}

}
