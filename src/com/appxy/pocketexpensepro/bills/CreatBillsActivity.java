package com.appxy.pocketexpensepro.bills;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.AutoListAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.PayeeListViewAdapter;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.service.NotificationService;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

@SuppressLint("ResourceAsColor")
public class CreatBillsActivity extends BaseHomeActivity {

	private EditText billNameEditText;
	private EditText amountEditText;
	private Button categoryButton;
	private Button dueDateButton;
	private Button recurringButton;
	private Button reminderButton;
	private AutoCompleteTextView payeeEditText;
	private EditText memoEditText;
	private LayoutInflater inflater;

	private ExpandableListView mExpandableListView;
	private DialogExpandableListViewAdapter mDialogExpandableListViewAdapter;
	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	private int checkedItem;
	private int gCheckedItem;// 选择位置
	private int cCheckedItem;
	private RelativeLayout expenseButton;
	private RelativeLayout incomeButton;
	private View chooseView1;
	private View chooseView2;
	
	private int mCategoryType = 0; // 0 expense 1 income
	private int mCheckCategoryType = 0; // 0 expense 1 income
	private int categoryId; // 初次others的位置
	private AlertDialog mCategoryDialog;

	private String amountString = "0.0";
	private int mYear;
	private int mMonth;
	private int mDay;
	private int uYear;
	private int uMonth;
	private int uDay;
	private int mHour;
	private int mMinute;

	private String dateString;
	private long dateLong;

	private AlertDialog mPayeeDialog;
	private ListView payeeListView;
	private PayeeListViewAdapter payeeListViewAdapter;
	private int payeeCheckItem = 0;
	private int payeeId = 0;
	private double amountDouble;

	private Spinner frequencySpinner;
	private Spinner isForverSpinner;
	private Button untilButton;
	private Spinner remindMeSpinner;
	private Button remindAtButton;

	private int recurringType = 0;// 0 表示没有重复 only once
	private long endDate;
	private int isForever = 1;
	private int recurringTypeSelectPosition = 0;
	private int isForverSelectPosition = 0;
	private int temRecurringType = 0;
	private int temIsForverSelectPosition = 0;

	private int remindDate;
	private long remindTime;
	private int remindDateSelectPosition;
	private int temRemindDateSelectPosition;
	private String remindTimeString = "";
	private TextView reminderLabel ;
	private long selectDate;
	private AutoListAdapter autoListAdapter;
	private Cursor mCursor;
	private CharSequence sKey;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bills);

		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();
		Intent intent = getIntent();
		selectDate = intent.getLongExtra("selectDate", 0); 

		inflater = LayoutInflater.from(CreatBillsActivity.this);
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

		billNameEditText = (EditText) findViewById(R.id.bill_edit);
		amountEditText = (EditText) findViewById(R.id.amount_edit);
		categoryButton = (Button) findViewById(R.id.category_btn);
		dueDateButton = (Button) findViewById(R.id.duedate_btn);
		recurringButton = (Button) findViewById(R.id.recurring_btn);
		reminderButton = (Button) findViewById(R.id.reminder_btn);
		payeeEditText = (AutoCompleteTextView) findViewById(R.id.payee_edit);
		memoEditText = (EditText) findViewById(R.id.memo_edit);

		categoryButton.setOnClickListener(mClickListener);
		dueDateButton.setOnClickListener(mClickListener);
		recurringButton.setOnClickListener(mClickListener);
		reminderButton.setOnClickListener(mClickListener);
		
		payeeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);
				mCursor = TransactionDao.selectPayee(
						CreatBillsActivity.this, MEntity.sqliteEscape(s.toString()));
				autoListAdapter = new AutoListAdapter(
						CreatBillsActivity.this, mCursor, true);
				payeeEditText.setAdapter(autoListAdapter);
				Log.v("mtest", "mCursor11" + mCursor);
				sKey = s;

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);

			}

		});
		
		payeeEditText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				Cursor cursor = (Cursor) arg0.getItemAtPosition(0);

				// Get the state's capital from this row in the database.
				payeeId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			}
		});
		
		

		List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
				CreatBillsActivity.this, 0);
		filterData(mDataList);
		categoryId = locationOthersId(groupDataList);
		gCheckedItem = locationOthersPosition(groupDataList);
		cCheckedItem = -1;
		categoryButton.setText("Others");

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

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(selectDate);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();
		
		Calendar c1 = Calendar.getInstance();
		uYear = c1.get(Calendar.YEAR);
		uMonth = c1.get(Calendar.MONTH);
		uDay = c1.get(Calendar.DAY_OF_MONTH);
		mHour = c1.get(Calendar.HOUR_OF_DAY);
		mMinute = c1.get(Calendar.MINUTE);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
				
				String ep_billName = billNameEditText.getText().toString();
				double ep_billAmount = 0;
				String ep_note = memoEditText.getText().toString();
				try {
					ep_billAmount = Float.parseFloat(amountString);
                } catch (NumberFormatException e) {
                	ep_billAmount = 0;
                }
				
				
				if (ep_billName == null
						|| ep_billName.trim().length() == 0
						|| ep_billName.trim().equals("")) {
					
					new AlertDialog.Builder(CreatBillsActivity.this)
					.setTitle("Warning! ")
					.setMessage("Please make sure the bill name is not empty! ")
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								@Override	
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									
								}
							}).show();
					
				} else if(ep_billAmount == 0){
					
					new AlertDialog.Builder(CreatBillsActivity.this)
					.setTitle("Warning! ")
					.setMessage("Please make sure the amount is not zero! ")
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								@Override	
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									
								}
							}).show();
					
				}else{
					long lastDate = -1;
					if(isForever == 1){
						lastDate = -1;
					}else{
						lastDate = endDate;
					}
					
					long row = BillsDao.insertBillRule(CreatBillsActivity.this, ep_billAmount,  dateLong, lastDate,  ep_billName,ep_note, recurringType,  remindDateSelectPosition,  remindTime,  categoryId,  payeeId);
					
					Intent intent = new Intent();
					intent.putExtra("_id", row);
					setResult(8, intent);
					
					if(remindDateSelectPosition > 0){
						 Intent service=new Intent(CreatBillsActivity.this, NotificationService.class);  
						 CreatBillsActivity.this.startService(service);  
					}
					
					finish();
				}
				break;

			case R.id.category_btn:

				View view = inflater.inflate(R.layout.dialog_choose_category,
						null);
				expenseButton = (RelativeLayout) view
						.findViewById(R.id.expense_btn);
				incomeButton = (RelativeLayout) view.findViewById(R.id.income_btn);
				chooseView1  = (View) view.findViewById(R.id.view1);
				chooseView2  = (View) view.findViewById(R.id.view2);
				
				if(mCategoryType == 0){
					chooseView1.setVisibility(View.VISIBLE);
					chooseView2.setVisibility(View.INVISIBLE);
				}else{
					chooseView1.setVisibility(View.INVISIBLE);
					chooseView2.setVisibility(View.VISIBLE);
				}
				

				expenseButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						
						chooseView1.setVisibility(View.VISIBLE);
						chooseView2.setVisibility(View.INVISIBLE);
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(CreatBillsActivity.this, 0);
						filterData(mDataList);

						mDialogExpandableListViewAdapter.notifyDataSetChanged();

						mExpandableListView
								.setOnChildClickListener(new OnChildClickListener() {

									@Override
									public boolean onChildClick(
											ExpandableListView parent, View v,
											int groupPosition,
											int childPosition, long id) {
										// TODO Auto-generated method stub
										mCategoryType = 0;

										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition,
																childPosition));

										gCheckedItem = groupPosition;
										cCheckedItem = childPosition;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();

										categoryId = (Integer) childrenAllDataList
												.get(groupPosition)
												.get(childPosition).get("_id");
										String cName = (String) childrenAllDataList
												.get(groupPosition)
												.get(childPosition)
												.get("categoryName");
										categoryButton.setText(cName);
										mCategoryDialog.dismiss();

										return true;
									}
								});

						mExpandableListView
								.setOnGroupClickListener(new OnGroupClickListener() {

									@Override
									public boolean onGroupClick(
											ExpandableListView parent, View v,
											int groupPosition, long id) {
										// TODO Auto-generated method stub
										mCategoryType = 0;
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition,
																0));

										gCheckedItem = groupPosition;
										cCheckedItem = -1;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();
										mCategoryDialog.dismiss();

										categoryId = (Integer) groupDataList
												.get(groupPosition).get("_id");
										String cName = (String) groupDataList
												.get(groupPosition).get(
														"categoryName");
										categoryButton.setText(cName);
										return true;
									}
								});

						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);

					}
				});

				incomeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						chooseView1.setVisibility(View.INVISIBLE);
						chooseView2.setVisibility(View.VISIBLE);
						
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(CreatBillsActivity.this, 1);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();

						mExpandableListView
								.setOnChildClickListener(new OnChildClickListener() {

									@Override
									public boolean onChildClick(
											ExpandableListView parent, View v,
											int groupPosition,
											int childPosition, long id) {
										// TODO Auto-generated method stub
										mCategoryType = 1;
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition,
																childPosition));

										gCheckedItem = groupPosition;
										cCheckedItem = childPosition;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();

										categoryId = (Integer) childrenAllDataList
												.get(groupPosition)
												.get(childPosition).get("_id");
										String cName = (String) childrenAllDataList
												.get(groupPosition)
												.get(childPosition)
												.get("categoryName");
										categoryButton.setText(cName);

										mCategoryDialog.dismiss();

										return true;
									}
								});

						mExpandableListView
								.setOnGroupClickListener(new OnGroupClickListener() {

									@Override
									public boolean onGroupClick(
											ExpandableListView parent, View v,
											int groupPosition, long id) {
										// TODO Auto-generated method stub
										mCategoryType = 1;
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition,
																0));

										gCheckedItem = groupPosition;
										cCheckedItem = -1;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();
										mCategoryDialog.dismiss();

										categoryId = (Integer) groupDataList
												.get(groupPosition).get("_id");
										String cName = (String) groupDataList
												.get(groupPosition).get(
														"categoryName");
										categoryButton.setText(cName);

										return true;
									}
								});

						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);

					}
				});

				mExpandableListView = (ExpandableListView) view
						.findViewById(R.id.mExpandableListView);
				mDialogExpandableListViewAdapter = new DialogExpandableListViewAdapter(
						CreatBillsActivity.this);
				mExpandableListView
						.setAdapter(mDialogExpandableListViewAdapter);
				mExpandableListView
						.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
				mExpandableListView.setItemsCanFocus(false);
				mExpandableListView.setGroupIndicator(null);

				checkedItem = mExpandableListView
						.getFlatListPosition(ExpandableListView
								.getPackedPositionForChild(gCheckedItem, 0)); // 根据group和child找到绝对位置

				if (mCategoryType == 0) {
					List<Map<String, Object>> mDataList = PayeeDao
							.selectCategory(CreatBillsActivity.this, 0);
					filterData(mDataList);

					mExpandableListView
							.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
									: 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(
							gCheckedItem, cCheckedItem);

				} else if (mCategoryType == 1) {
					List<Map<String, Object>> mDataList = PayeeDao
							.selectCategory(CreatBillsActivity.this, 1);
					filterData(mDataList);

					mExpandableListView
							.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
									: 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(
							gCheckedItem, cCheckedItem);

				}

				mDialogExpandableListViewAdapter.setAdapterData(groupDataList,
						childrenAllDataList);
				mDialogExpandableListViewAdapter.notifyDataSetChanged();

				mExpandableListView
						.setOnChildClickListener(new OnChildClickListener() {

							@Override
							public boolean onChildClick(
									ExpandableListView parent, View v,
									int groupPosition, int childPosition,
									long id) {
								// TODO Auto-generated method stub

								checkedItem = mExpandableListView
										.getFlatListPosition(ExpandableListView
												.getPackedPositionForChild(
														groupPosition,
														childPosition));

								gCheckedItem = groupPosition;
								cCheckedItem = childPosition;

								mDialogExpandableListViewAdapter
										.setSelectedPosition(gCheckedItem,
												cCheckedItem);
								mDialogExpandableListViewAdapter
										.notifyDataSetChanged();

								categoryId = (Integer) childrenAllDataList
										.get(groupPosition).get(childPosition)
										.get("_id");
								String cName = (String) childrenAllDataList
										.get(groupPosition).get(childPosition)
										.get("categoryName");
								categoryButton.setText(cName);
								mCategoryDialog.dismiss();

								return true;
							}
						});

				mExpandableListView
						.setOnGroupClickListener(new OnGroupClickListener() {

							@Override
							public boolean onGroupClick(
									ExpandableListView parent, View v,
									int groupPosition, long id) {
								// TODO Auto-generated method stub

								checkedItem = mExpandableListView
										.getFlatListPosition(ExpandableListView
												.getPackedPositionForChild(
														groupPosition, 0));

								gCheckedItem = groupPosition;
								cCheckedItem = -1;

								mDialogExpandableListViewAdapter
										.setSelectedPosition(gCheckedItem,
												cCheckedItem);
								mDialogExpandableListViewAdapter
										.notifyDataSetChanged();
								mCategoryDialog.dismiss();

								categoryId = (Integer) groupDataList.get(
										groupPosition).get("_id");
								String cName = (String) groupDataList.get(
										groupPosition).get("categoryName");
								categoryButton.setText(cName);
								return true;
							}
						});

				int groupCount = groupDataList.size();

				for (int i = 0; i < groupCount; i++) {
					mExpandableListView.expandGroup(i);
				}
				mExpandableListView.setCacheColorHint(0);

				AlertDialog.Builder mBuilder = new AlertDialog.Builder(
						CreatBillsActivity.this);
				mBuilder.setTitle("Choose Category");
				mBuilder.setView(view);
				mBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mCategoryDialog = mBuilder.create();
				mCategoryDialog.show();

				break;

			case R.id.duedate_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(CreatBillsActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Date");
				DPD.show();

				break;

			case R.id.recurring_btn:

				View view5 = inflater.inflate(R.layout.dialog_recurring, null);
				frequencySpinner = (Spinner) view5
						.findViewById(R.id.repeat_frequency_spinner);
				isForverSpinner = (Spinner) view5
						.findViewById(R.id.is_forever_spinner);
				untilButton = (Button) view5.findViewById(R.id.repeating_until);

				ArrayAdapter<CharSequence> adapter1 = ArrayAdapter
						.createFromResource(CreatBillsActivity.this,
								R.array.recurring,
								android.R.layout.simple_spinner_dropdown_item);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				frequencySpinner.setAdapter(adapter1);
				frequencySpinner.setSelection(recurringTypeSelectPosition);
				frequencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								temRecurringType = arg2;
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}

						});

				if (isForverSelectPosition == 0) {
					untilButton.setVisibility(View.INVISIBLE);
				} else {
					untilButton.setVisibility(View.VISIBLE);
				}

				ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
						.createFromResource(CreatBillsActivity.this,
								R.array.is_forver,
								android.R.layout.simple_spinner_dropdown_item);
				adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				isForverSpinner.setAdapter(adapter2);
				isForverSpinner.setSelection(isForverSelectPosition);
				isForverSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								temIsForverSelectPosition = arg2;
								if (arg2 == 0) {
									untilButton.setVisibility(View.INVISIBLE);
									isForever = 1;
								} else {
									untilButton.setVisibility(View.VISIBLE);
									isForever = 0;
								}

							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}

						});

				untilButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DatePickerDialog DPD = new DatePickerDialog( // 改变theme
								new ContextThemeWrapper(
										CreatBillsActivity.this,
										android.R.style.Theme_Holo_Light),
								mDateSetListenerUntil, uYear, uMonth, uDay);
						DPD.setTitle("EndDate");
						DPD.show();

					}
				});

				AlertDialog.Builder mBuilder5 = new AlertDialog.Builder(
						CreatBillsActivity.this);
				mBuilder5.setTitle("Recurring");
				mBuilder5.setView(view5);
				mBuilder5.setPositiveButton("Done",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								recurringType = temRecurringType;
								recurringTypeSelectPosition = temRecurringType;
								isForverSelectPosition = temIsForverSelectPosition;
								recurringButton.setText(frequencySpinner.getSelectedItem().toString());
							}
						});
				mBuilder5.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mBuilder5.create().show();
				updateDisplayUntil();

				break;

			case R.id.reminder_btn:

				View view4 = inflater.inflate(R.layout.dialog_remindme, null);

				remindMeSpinner = (Spinner) view4.findViewById(R.id.remind_spn);
				remindAtButton = (Button) view4.findViewById(R.id.remindAt_btn);
				reminderLabel = (TextView) view4.findViewById(R.id.textView2);
				
				ArrayAdapter<CharSequence> adapter3 = ArrayAdapter
						.createFromResource(CreatBillsActivity.this,
								R.array.reminder,
								android.R.layout.simple_spinner_dropdown_item);
				adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				remindMeSpinner.setAdapter(adapter3);
				remindMeSpinner.setSelection(remindDateSelectPosition);
				remindMeSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								temRemindDateSelectPosition = arg2;
								
								if(arg2 == 0){
									remindAtButton.setTextColor(R.color.lightgray);
									remindAtButton.setClickable(false);
									reminderLabel.setTextColor(R.color.lightgray);
								}else{
									remindAtButton.setTextColor(Color.BLACK);
									remindAtButton.setClickable(true);
									reminderLabel.setTextColor(Color.BLACK);
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}

						});

				if(remindDateSelectPosition == 0){
					remindAtButton.setTextColor(R.color.lightgray);
					remindAtButton.setClickable(false);
					reminderLabel.setTextColor(R.color.lightgray);
				}else{
					remindAtButton.setTextColor(Color.BLACK);
					remindAtButton.setClickable(true);
					reminderLabel.setTextColor(Color.BLACK);
				}
				
				
				remindAtButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						TimePickerDialog DPD1 = new TimePickerDialog(
						// 改变theme
								new ContextThemeWrapper(
										CreatBillsActivity.this,
										android.R.style.Theme_Holo_Light),
								mTimeSetListener, mHour, mMinute, false);
						DPD1.setTitle("Reminder");
						DPD1.show();

					}
				});

				AlertDialog.Builder mBuilder4 = new AlertDialog.Builder(
						CreatBillsActivity.this);
				mBuilder4.setTitle("Recurring");
				mBuilder4.setView(view4);
				mBuilder4.setPositiveButton("Done",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								remindDateSelectPosition = temRemindDateSelectPosition;
								String mRemindString = remindMeSpinner.getSelectedItem().toString();
								if(remindDateSelectPosition > 0){
									reminderButton.setText(mRemindString+", "+remindTimeString);
								}else{
									reminderButton.setText(mRemindString);
								}
								remindTime = mHour*60*60*1000 + mMinute*60*1000;
								
							}
						});
				mBuilder4.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mBuilder4.create().show();
				updateDisplayTime();

				break;

//			case R.id.payee_btn:
//
//				View view6 = inflater
//						.inflate(R.layout.dialog_choose_type, null);
//				payeeListView = (ListView) view6.findViewById(R.id.mListView);
//				payeeListViewAdapter = new PayeeListViewAdapter(
//						CreatBillsActivity.this);
//
//				final List<Map<String, Object>> mList = TransactionDao
//						.selectPayee(CreatBillsActivity.this);
//
//				payeeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//				payeeListView.setAdapter(payeeListViewAdapter);
//				payeeListView
//						.setSelection((payeeCheckItem - 1) > 0 ? (payeeCheckItem - 1)
//								: 0);
//				payeeListViewAdapter.setItemChecked(payeeCheckItem);
//				payeeListViewAdapter.setAdapterDate(mList);
//				payeeListViewAdapter.notifyDataSetChanged();
//				payeeListView.setOnItemClickListener(new OnItemClickListener() {
//
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1,
//							int arg2, long arg3) {
//						// TODO Auto-generated method stub
//						payeeCheckItem = arg2;
//						payeeListViewAdapter.setItemChecked(payeeCheckItem);
//						payeeListViewAdapter.notifyDataSetChanged();
//						String nameString = (String) mList.get(arg2)
//								.get("name");
//						payeeButton.setText(nameString);
//
//						payeeId = (Integer) mList.get(arg2).get("_id");
//						mPayeeDialog.dismiss();
//					}
//				});
//
//				AlertDialog.Builder mBuilder6 = new AlertDialog.Builder(
//						CreatBillsActivity.this);
//				mBuilder6.setTitle("Choose Payee");
//				mBuilder6.setView(view6);
//				mBuilder6.setNegativeButton("Cancel",
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//							}
//						});
//
//				mPayeeDialog = mBuilder6.create();
//				mPayeeDialog.show();
//
//				break;
			}
		}
	};

	/* 设置时间相关方法如下 */
	private void updateDisplayTime() {
		String AmOrPm = "";
		int Hour;
		if (mHour >= 12) {
			AmOrPm = "PM";
		} else {
			AmOrPm = "AM";
		}

		if (13 <= mHour) {
			Hour = mHour % 12;
		} else {
			Hour = mHour;
		}

		remindAtButton.setText(new StringBuilder().append(pad(Hour))
				.append(":").append(pad(mMinute)).append(" ").append(AmOrPm)
				.append(" "));
		remindTimeString = (new StringBuilder().append(pad(Hour)).append(":")
				.append(pad(mMinute)).append(" ").append(AmOrPm)).toString();

	}

	private static String pad(int c) { // 设置分钟和小时是否需要加0
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			updateDisplayTime();
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
		dueDateButton.setText(sdf.format(date));

	}

	private DatePickerDialog.OnDateSetListener mDateSetListenerUntil = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			uYear = year;
			uMonth = monthOfYear;
			uDay = dayOfMonth;
			updateDisplayUntil();
		}
	};

	private void updateDisplayUntil() {
		// TODO Auto-generated method stub

		String mdateString = (new StringBuilder().append(uMonth + 1)
				.append("-").append(uDay).append("-").append(uYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(mdateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		endDate = c.getTimeInMillis();

		Date date = new Date(endDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
		untilButton.setText(sdf.format(date));
	}

	public void filterData(List<Map<String, Object>> mData) {// 过滤子类和父类

		List<Map<String, Object>> temChildDataList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList;

		groupDataList.clear();
		childrenAllDataList.clear();

		for (Map<String, Object> mMap : mData) { // 分离父类和子类
			String categoryName = (String) mMap.get("categoryName");

			if (categoryName.contains(":")) {
				temChildDataList.add(mMap);
			} else {
				groupDataList.add(mMap);
			}

		}

		for (Map<String, Object> mMap : groupDataList) {

			String categoryName = (String) mMap.get("categoryName");
			tempList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> iMap : temChildDataList) {

				String cName = (String) iMap.get("categoryName");
				String temp[] = cName.split(":");
				if (temp[0].equals(categoryName)) {
					tempList.add(iMap);
				}

			}
			childrenAllDataList.add(tempList);
		}
	}

	public int locationOthersPosition(List<Map<String, Object>> mData) { // 定位others的位置
		int i = 0;
		int position = 0;
		for (Map<String, Object> mMap : mData) {
			String categoryName = (String) mMap.get("categoryName");
			if (categoryName.equals("Others")) {
				position = i;
			}
			i = i + 1;
		}

		return position;
	}

	public int locationOthersId(List<Map<String, Object>> mData) { // 定位others的位置
		int id = 0;
		for (Map<String, Object> mMap : mData) {
			String categoryName = (String) mMap.get("categoryName");
			String temp[] = categoryName.split(":");
			if (temp[0].equals("Others")) {
				id = (Integer) mMap.get("_id");
			}
		}
		return id;
	}

	@Override
	public void syncDateChange() {
		// TODO Auto-generated method stub
		
	}

}
