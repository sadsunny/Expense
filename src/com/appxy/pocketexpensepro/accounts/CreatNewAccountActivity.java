package com.appxy.pocketexpensepro.accounts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.ChooseTypeListViewAdapter.ViewHolder;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxRecord;

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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
import android.widget.Toast;

/*
 * 对CreatAccountTypeActivity发出Result code 1
 * 返回到Account主页面的Result code 2
 */
public class CreatNewAccountActivity extends BaseHomeActivity {

	private LayoutInflater inflater;
	private EditText accountEditText;
	private EditText balanceEditText;
	private Button typeButton;
	private Button dateButton;
	private Spinner clearSpinner;
	private ImageView addButton;
	private ImageView subButton;

	private int checkedItem = 2; // choosed position
	private ChooseTypeListViewAdapter mListViewAdapter;
	private ListView mListView;
	private AlertDialog mDialog;
	private List<Map<String, Object>> mTypeList;
	private int accountTypeSize = 0;
	private int typeId = 3; // The type id default checking
	private String balenceAmountString = "0.00";
	private int balenceCheck = 1; // 1 add, 0 sub

	private int mYear;
	private int mMonth;
	private int mDay;
	private String dateString;
	private long dateLong;
	private int clearCheck = 1; // default on 1
	private double balanceDouble = 0.0;
	private int insertId = 0;
	private AlertDialog deleteDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_new_account);
		
		List<Map<String, Object>> mList = AccountDao.selectAccountType(CreatNewAccountActivity.this);
		for (int i = 0; i < mList.size(); i++) {
			if ((Integer)mList.get(i).get("_id") == typeId) {
				checkedItem = i;
			}
		}
		
		inflater = LayoutInflater.from(CreatNewAccountActivity.this);
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

		typeButton.setOnClickListener(mClickListener);
		dateButton.setOnClickListener(mClickListener);
		addButton.setOnClickListener(mClickListener);
		subButton.setOnClickListener(mClickListener);

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(CreatNewAccountActivity.this,
						R.array.on_off,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clearSpinner.setAdapter(adapterSpinner);
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
		typeButton.setText("Checking");

		balanceEditText.setText("0.00");
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
				
				if (accountName == null || accountName.trim().length() == 0
						|| accountName.trim().equals("")) {

					new AlertDialog.Builder(CreatNewAccountActivity.this)
							.setTitle("Warning! ")
							.setMessage("Please make sure the account name is not empty! ")
							.setPositiveButton("Retry",new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();

										}
									}).show();
					
				}else if( AccountDao.selectAccountTypeByID(CreatNewAccountActivity.this, typeId).size() <= 0 ){ 
					
					new AlertDialog.Builder(CreatNewAccountActivity.this)
					.setTitle("Failed to add account! ")
					.setMessage("This account type has been removed by other devices. Please choose another one. ")
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}).show();
					
					
				}else {
					

					if (balenceCheck == 0) {
						balanceDouble = 0 - balanceDouble;
					}
					
					long dateTime_sync =  System.currentTimeMillis();
					String uuid  =  MEntity.getUUID();

					long _id = AccountDao.insertAccount(
							CreatNewAccountActivity.this, accountName,
							balanceDouble+"", dateLong, clearCheck, typeId, 10000, uuid, dateTime_sync, mDbxAcctMgr, mDatastore);
					AccountDao.updateAccountIndex(CreatNewAccountActivity.this,(int)_id, accountName,
							balanceDouble+"", dateLong, clearCheck, typeId, (int)_id, uuid, dateTime_sync, mDbxAcctMgr, mDatastore);
				
					
					insertId = (int)_id;
					
					Intent intent = new Intent();
					intent.putExtra("aName", accountName);
					intent.putExtra("_id", _id);
					setResult(2, intent);
					finish();
					
				}
				break;

			case R.id.type_btn:
				
				View view = inflater.inflate(R.layout.dialog_choose_type, null);
				
				mTypeList = AccountDao.selectAccountType(CreatNewAccountActivity.this);
				accountTypeSize = mTypeList.size();

				mListView = (ListView) view.findViewById(R.id.mListView);
				mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				mListView.setItemsCanFocus(false);
				mListView.setAdapter(mListViewAdapter);
				mListView.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
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
				
				
				mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						final int TtypeId = (Integer) mTypeList.get(position).get("_id");
						final String uuid = (String)mTypeList.get(position).get("uuid");
						
						if(TtypeId  > 9){
							
							View dialogView = inflater.inflate(R.layout.dialog_item_operation,
									null);

							String[] data = {"Delete"};
							ListView diaListView = (ListView) dialogView.findViewById(R.id.dia_listview);
							DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(CreatNewAccountActivity.this , data);
							diaListView.setAdapter(mDialogItemAdapter);
							diaListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1,
										int arg2, long arg3) {
									 AccountDao.deleteAccountTypeByID(CreatNewAccountActivity.this, TtypeId, mDbxAcctMgr, mDatastore, uuid);
									 refreshDialog();
									 deleteDialog.dismiss();
								}
							});
							
							 AlertDialog.Builder builder = new AlertDialog.Builder(CreatNewAccountActivity.this);
							 builder.setView(dialogView);
							 deleteDialog =builder.create();
							 deleteDialog.show();
						}
						
						 
						return true;
					}
				});
				
				
				AlertDialog.Builder mBuilder = new AlertDialog.Builder(
						CreatNewAccountActivity.this);
				mBuilder.setTitle("Choose Account Type");
				mBuilder.setView(view);
				mBuilder.setPositiveButton("New Type",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent intent = new Intent();
								intent.setClass(CreatNewAccountActivity.this,
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
						new ContextThemeWrapper(CreatNewAccountActivity.this,
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
				
				typeButton.setText(data.getStringExtra("typeName"));
				typeId = (Integer) data.getIntExtra("_id", 0);
				
				List<Map<String, Object>> mList = AccountDao.selectAccountType(CreatNewAccountActivity.this);
				for (int i = 0; i < mList.size(); i++) {
					if ((Integer)mList.get(i).get("_id") == typeId) {
						checkedItem = i;
					}
				}
			}
			
			break;

		}

	}
	
	public void refreshDialog() {
		
		mTypeList = AccountDao.selectAccountType(CreatNewAccountActivity.this);
		typeId = 8;
		typeButton.setText("Others");
		for (int i = 0; i < mTypeList.size(); i++) {
			if ((Integer)mTypeList.get(i).get("_id") == typeId) {
				checkedItem = i;
				typeButton.setText(mTypeList.get(i).get("typeName")+ "");
			}
		}
		
		mListView.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
				: 0);
        mListViewAdapter.setItemChecked(checkedItem);
		mListViewAdapter.setAdapterDate(mTypeList);
		mListViewAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		
		Toast.makeText(this, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		if (mMap.containsKey("db_accounttype_table")) {
			
			if (mDialog != null && mDialog.isShowing()) {
				mTypeList = AccountDao.selectAccountType(CreatNewAccountActivity.this);
				for (int i = 0; i < mTypeList.size(); i++) {
					if ((Integer)mTypeList.get(i).get("_id") == typeId) {
						checkedItem = i;
					}
				}
				
				mListView.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
						: 0);
		        mListViewAdapter.setItemChecked(checkedItem);
				mListViewAdapter.setAdapterDate(mTypeList);
				mListViewAdapter.notifyDataSetChanged();
				
			}
			
		}
	}

}
