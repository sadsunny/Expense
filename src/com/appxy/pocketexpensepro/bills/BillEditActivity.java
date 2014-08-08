package com.appxy.pocketexpensepro.bills;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.AutoListAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.PayeeListViewAdapter;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.service.NotificationService;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;

import android.R.integer;
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
public class BillEditActivity extends BaseHomeActivity {

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
	private TextView reminderLabel;
	private Map<String, Object> mMap;
	private int indexflag;
	private int _id;

	private String ep_billAmount;
	private long ep_billDueDate;
	private long ep_billEndDate;
	private String ep_billName;
	private String ep_note;
	private int ep_recurringType;
	private int ep_reminderDate;
	private long ep_reminderTime;
	private int billRuleHasCategory;
	private int billRuleHasPayee;
	private int iconName;

	private int edit_status = 0;
	private AlertDialog editDialog;
	private AutoListAdapter autoListAdapter;
	private Cursor mCursor;
	private CharSequence sKey;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bills);

		Intent intent = getIntent();
		if (intent == null) {
			finish();
		}
		mMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
		indexflag = (Integer) mMap.get("indexflag");
		_id = (Integer) mMap.get("_id");
		ep_billAmount = (String) mMap.get("ep_billAmount");
		ep_billDueDate = (Long) mMap.get("ep_billDueDate");
		ep_billEndDate = (Long) mMap.get("ep_billEndDate");
		ep_billName = (String) mMap.get("ep_billName");
		ep_note = (String) mMap.get("ep_note");
		ep_recurringType = (Integer) mMap.get("ep_recurringType");
		ep_reminderDate = (Integer) mMap.get("ep_reminderDate");
		ep_reminderTime = (Long) mMap.get("ep_reminderTime");
		billRuleHasCategory = (Integer) mMap.get("billRuleHasCategory");
		billRuleHasPayee = (Integer) mMap.get("billRuleHasPayee");
		iconName = (Integer) mMap.get("iconName");

		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		inflater = LayoutInflater.from(BillEditActivity.this);
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

		billNameEditText.setText(ep_billName);
		billNameEditText.setSelectAllOnFocus(true);
		amountEditText.setText(MEntity.doubl2str(ep_billAmount));
//		amountEditText.setSelection(ep_billAmount.length());
		amountString = ep_billAmount;

		categoryId = billRuleHasCategory;

		List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
				BillEditActivity.this, 0);
		filterData(mDataList);
		if (groupDataList != null && groupDataList.size() > 0) {
			List<Map<String, Object>> tList = locationPosition(mDataList,
					groupDataList, childrenAllDataList, categoryId);
			gCheckedItem = (Integer) tList.get(0).get("gPosition");
			cCheckedItem = (Integer) tList.get(0).get("cPosition");
			List<Map<String, Object>> mCategoryList = AccountDao
					.selectCategoryById(BillEditActivity.this, categoryId);
			categoryButton.setText((String) mCategoryList.get(0).get(
					"categoryName"));
		}

		Calendar untilCalendar = Calendar.getInstance();
		uYear = untilCalendar.get(Calendar.YEAR);
		uMonth = untilCalendar.get(Calendar.MONTH);
		uDay = untilCalendar.get(Calendar.DAY_OF_MONTH);

		recurringTypeSelectPosition = ep_recurringType;
		temRecurringType = ep_recurringType;
		recurringType = ep_recurringType;
		endDate = ep_billEndDate;
		if (ep_recurringType > 0) {

			if (ep_billEndDate == -1) {
				isForever = 1;
				isForverSelectPosition = 0;
				temIsForverSelectPosition = 0;
			} else {
				temIsForverSelectPosition = 1;
				isForverSelectPosition = 1;
				isForever = 0;
				untilCalendar.setTimeInMillis(ep_billEndDate);
				uYear = untilCalendar.get(Calendar.YEAR);
				uMonth = untilCalendar.get(Calendar.MONTH);
				uDay = untilCalendar.get(Calendar.DAY_OF_MONTH);
			}
			String recurringName = this.getResources().getStringArray(
					R.array.recurring)[ep_recurringType];
			recurringButton.setText(recurringName);
		} else {
			recurringButton.setText("No Recurring");
		}

		remindDateSelectPosition = ep_reminderDate;
		temRemindDateSelectPosition = ep_reminderDate;
		Calendar timeCalendar = Calendar.getInstance();
		mHour = timeCalendar.get(Calendar.HOUR_OF_DAY);
		mMinute = timeCalendar.get(Calendar.MINUTE);
		String mRemindString = this.getResources().getStringArray(
				R.array.reminder)[ep_reminderDate];
		if (ep_reminderDate > 0) {
			timeCalendar.setTimeInMillis(MEntity.getNowMillis()
					+ ep_reminderTime);
			mHour = timeCalendar.get(Calendar.HOUR_OF_DAY);
			mMinute = timeCalendar.get(Calendar.MINUTE);
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

			String remindTime = (new StringBuilder().append(pad(Hour))
					.append(":").append(pad(mMinute)).append(" ")
					.append(AmOrPm)).toString();

			reminderButton.setText(mRemindString + ", " + remindTime);
		} else {
			reminderButton.setText(mRemindString);
		}

		if (billRuleHasPayee > 0) {
			List<Map<String, Object>> mPayeeDataList = AccountDao
					.selectPayeeById(this, billRuleHasPayee);
			String pNameString = (String) mPayeeDataList.get(0).get("name");
			payeeEditText.setText(pNameString);
			payeeId = billRuleHasPayee;
			List<Map<String, Object>> mPList = TransactionDao
					.selectPayee(BillEditActivity.this);
//			payeeCheckItem = locationIdPosition(mPList, payeeId);
		}
		
		payeeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);
				mCursor = TransactionDao.selectPayee(
						BillEditActivity.this, MEntity.sqliteEscape(s.toString()));
				autoListAdapter = new AutoListAdapter(
						BillEditActivity.this, mCursor, true);
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
		
		
		memoEditText.setText(ep_note);

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
		c.setTimeInMillis(ep_billDueDate);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();

		judgementDialog(indexflag, _id);

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

				if (ep_billName == null || ep_billName.trim().length() == 0
						|| ep_billName.trim().equals("")) {

					new AlertDialog.Builder(BillEditActivity.this)
							.setTitle("Warning! ")
							.setMessage(
									"Please make sure the bill name is not empty! ")
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

				} else if (ep_billAmount == 0) {

					new AlertDialog.Builder(BillEditActivity.this)
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

				} else {
					long lastDate = -1;
					if (isForever == 1) {
						lastDate = -1;
					} else {
						lastDate = endDate;
					}
					// int edit_status = 0; 编辑状态，1表示change this ，2表示this and
					// future
					if (indexflag == 0) {
						long row = BillsDao.updateBillRule(
								BillEditActivity.this, _id, ep_billAmount,
								dateLong, lastDate, ep_billName, ep_note,
								recurringType, remindDateSelectPosition,
								remindTime, categoryId, payeeId);
						
					} else if (indexflag == 1) {
						
						if (edit_status == 1) {
							int ep_billisDelete =0;
							long ep_billItemDueDateNew  = 1;
							
							if(dateLong == ep_billDueDate){
								ep_billisDelete =0;
								ep_billItemDueDateNew = dateLong;
							}else{
								ep_billisDelete =2;
								ep_billItemDueDateNew = ep_billDueDate;
							}
							
							long row = BillsDao.insertBillItem(BillEditActivity.this, ep_billisDelete, amountString, dateLong, ep_billItemDueDateNew, 
									lastDate, ep_billName, ep_note, recurringType, remindDateSelectPosition, remindTime, _id, categoryId, payeeId);
							
						} else if (edit_status == 2) {
							Log.v("mtest","lastDate"+MEntity.turnToDateString(lastDate));
							
							long row = BillsDao.updateBillRule(
									BillEditActivity.this, _id, ep_billAmount,
									dateLong, lastDate, ep_billName, ep_note,
									recurringType, remindDateSelectPosition,
									remindTime, categoryId, payeeId);
							BillsDao.deleteBillObjectByParId(BillEditActivity.this, _id) ;
							
						}
					} else if (indexflag == 2) {
						if (edit_status == 1) {
							
							int ep_billisDelete =0;
							long ep_billItemDueDateNew  = 1;
							if(dateLong == ep_billDueDate){
								ep_billisDelete =0;
								ep_billItemDueDateNew = dateLong;
							}else{
								ep_billisDelete =2;
								ep_billItemDueDateNew = ep_billDueDate;
							}
							
							long row = BillsDao.insertBillItem(BillEditActivity.this, ep_billisDelete, amountString, dateLong, ep_billItemDueDateNew, 
									lastDate, ep_billName, ep_note, recurringType, remindDateSelectPosition, remindTime, _id, categoryId, payeeId);
							
						} else if (edit_status == 2) {
							long row = BillsDao.insertBillRule(
									BillEditActivity.this, ep_billAmount,
									dateLong, lastDate, ep_billName, ep_note,
									recurringType, remindDateSelectPosition,
									remindTime, categoryId, payeeId);
							
							BillsDao.updateBillDateRule(BillEditActivity.this, _id, getPreDate(ep_recurringType, dateLong));
						}
					} else if (indexflag == 3) {
						
						if (edit_status == 1) {
							int ep_billisDelete =0;
							long ep_billItemDueDateNew  = 1;
							if(dateLong == ep_billDueDate){
								ep_billisDelete =0;
								ep_billItemDueDateNew = dateLong;
							}else{
								ep_billisDelete =2;
								ep_billItemDueDateNew = ep_billDueDate;
							}
							int p_id = (Integer)mMap.get("billItemHasBillRule");
							long row = BillsDao.updateBillItem(BillEditActivity.this, _id, ep_billisDelete, amountString, dateLong, ep_billItemDueDateNew, 
									lastDate, ep_billName, ep_note, recurringType, remindDateSelectPosition, remindTime, p_id, categoryId, payeeId) ;
						} else if (edit_status == 2) {
							long row = BillsDao.insertBillRule(
									BillEditActivity.this, ep_billAmount,
									dateLong, lastDate, ep_billName, ep_note,
									recurringType, remindDateSelectPosition,
									remindTime, categoryId, payeeId);
                            int p_id = (Integer)mMap.get("billItemHasBillRule");
							BillsDao.updateBillDateRule(BillEditActivity.this, p_id, getPreDate(ep_recurringType, dateLong));
							BillsDao.deleteBillObjectByAfterDate(BillEditActivity.this, dateLong);
							
							List<Map<String, Object>> mList = BillsDao.selectBillRuleById(BillEditActivity.this, p_id);
							long parentDuedate = (Long)mList.get(0).get("ep_billDueDate");
							if(dateLong == parentDuedate){
								BillsDao.deleteBill(BillEditActivity.this, p_id);
							}
						}
					}

					Intent intent = new Intent();
					intent.putExtra("_id", 1);
					setResult(19, intent);

					if(remindDateSelectPosition > 0 || ep_reminderDate > 0){
						 Intent service=new Intent(BillEditActivity.this, NotificationService.class);  
						 BillEditActivity.this.startService(service);  
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
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(BillEditActivity.this, 0);
						filterData(mDataList);

						chooseView1.setVisibility(View.VISIBLE);
						chooseView2.setVisibility(View.INVISIBLE);
						
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
								.selectCategory(BillEditActivity.this, 1);
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
						BillEditActivity.this);
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
							.selectCategory(BillEditActivity.this, 0);
					filterData(mDataList);

					mExpandableListView
							.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
									: 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(
							gCheckedItem, cCheckedItem);

				} else if (mCategoryType == 1) {
					List<Map<String, Object>> mDataList = PayeeDao
							.selectCategory(BillEditActivity.this, 1);
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
						BillEditActivity.this);
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
						new ContextThemeWrapper(BillEditActivity.this,
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
						.createFromResource(BillEditActivity.this,
								R.array.recurring,
								android.R.layout.simple_spinner_dropdown_item);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				frequencySpinner.setAdapter(adapter1);
				frequencySpinner.setSelection(recurringTypeSelectPosition);
				frequencySpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {

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
						.createFromResource(BillEditActivity.this,
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
								new ContextThemeWrapper(BillEditActivity.this,
										android.R.style.Theme_Holo_Light),
								mDateSetListenerUntil, uYear, uMonth, uDay);
						DPD.setTitle("EndDate");
						DPD.show();

					}
				});

				AlertDialog.Builder mBuilder5 = new AlertDialog.Builder(
						BillEditActivity.this);
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
								recurringButton.setText(frequencySpinner
										.getSelectedItem().toString());
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
						.createFromResource(BillEditActivity.this,
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

								if (arg2 == 0) {
									remindAtButton
											.setTextColor(R.color.lightgray);
									remindAtButton.setClickable(false);
									reminderLabel
											.setTextColor(R.color.lightgray);
								} else {
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

				if (remindDateSelectPosition == 0) {
					remindAtButton.setTextColor(R.color.lightgray);
					remindAtButton.setClickable(false);
					reminderLabel.setTextColor(R.color.lightgray);
				} else {
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
								new ContextThemeWrapper(BillEditActivity.this,
										android.R.style.Theme_Holo_Light),
								mTimeSetListener, mHour, mMinute, false);
						DPD1.setTitle("Reminder");
						DPD1.show();

					}
				});

				AlertDialog.Builder mBuilder4 = new AlertDialog.Builder(
						BillEditActivity.this);
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
								String mRemindString = remindMeSpinner
										.getSelectedItem().toString();
								if (remindDateSelectPosition > 0) {
									reminderButton.setText(mRemindString + ", "
											+ remindTimeString);
								} else {
									reminderButton.setText(mRemindString);
								}
								remindTime = mHour * 60 * 60 * 1000 + mMinute
										* 60 * 1000;

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
//						BillEditActivity.this);
//
//				final List<Map<String, Object>> mList = TransactionDao
//						.selectPayee(BillEditActivity.this);
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
//						BillEditActivity.this);
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

	public List<Map<String, Object>> locationPosition(
			List<Map<String, Object>> allData, List<Map<String, Object>> gData,
			List<List<Map<String, Object>>> cData, int id) { // 没有在group中找到，就定位id在Group和child的位置，返回map<Group,Child>
		List<Map<String, Object>> rData = new ArrayList<Map<String, Object>>();
		String categoryName = "";
		int gPosition = -2;
		int cPosition = -2;
		for (Map<String, Object> aMap : allData) {
			int _id = (Integer) aMap.get("_id");
			String cNameString = (String) aMap.get("categoryName");
			if (_id == id) {
				categoryName = cNameString;
			}
		}

		if (categoryName.length() > 0) {

			if (categoryName.contains(":")) {
				String temp[] = categoryName.split(":");
				gPosition = locationgGroupByName(gData, temp[0]);

				List<Map<String, Object>> temChildDataList = cData
						.get(gPosition);
				int i = 0;
				for (Map<String, Object> iMap : temChildDataList) {
					String childString = (String) iMap.get("categoryName");
					int _id = (Integer) iMap.get("_id");
					if (childString.equals(categoryName) && _id == id) {
						cPosition = i;
					}
					i++;
				}

			} else {
				gPosition = locationGroupPosition(gData, id);
				cPosition = -1;
			}

		}
		Map<String, Object> rMap = new HashMap<String, Object>();
		rMap.put("gPosition", gPosition);
		rMap.put("cPosition", cPosition);
		rData.add(rMap);
		return rData;
	}

	public int locationGroupPosition(List<Map<String, Object>> mData, int id) { // 定位id在Group位置
		int position = -1;
		int i = 0;

		for (Map<String, Object> mMap : mData) {
			int _id = (Integer) mMap.get("_id");
			if (_id == id) {
				position = i;
			}
			i = i + 1;
		}
		return position;
	}

	public int locationgGroupByName(List<Map<String, Object>> mData,
			String cName) { // 定位others的位置
		int position = 0;
		int i = 0;

		for (Map<String, Object> mMap : mData) {
			String cNameString = (String) mMap.get("categoryName");
			if (cNameString.equals(cName)) {
				position = i;
			}
			i = i + 1;
		}
		return position;
	}

	public int locationIdPosition(List<Map<String, Object>> mData, int pId) { // 定位ID的位置
		int i = 0;
		int position = 0;
		for (Map<String, Object> mMap : mData) {
			int _id = (Integer) mMap.get("_id");
			if (_id == pId) {
				position = i;
			}
			i = i + 1;
		}

		return position;
	}

	public void judgementDialog(int mFlag, int mId) { // 弹出dialog和之前的判断

		if (mFlag == 0) {

			edit_status = 1; // 1表示只修改当前，2表示修改当前和all future

		} else if (mFlag == 1) {

			int temPaydate = judgeTemPayDate(mId); // 父本pay状态
			long firstPayDate = judgePayDate(mId);// 返回第一笔pay的时间，如果为0表没有pay的bill

			if (temPaydate > 0) {
				edit_status = 1;
				recurringButton.setEnabled(false);
				recurringButton.setTextColor(R.color.bill_gray);
				billNameEditText.setEnabled(false);
				billNameEditText.setTextColor(R.color.bill_gray);
			} else {

				if (firstPayDate == 0) {

					editDialogShow();

				} else {

					edit_status = 1;
					recurringButton.setEnabled(false);
					recurringButton.setTextColor(R.color.bill_gray);
					billNameEditText.setEnabled(false);
					billNameEditText.setTextColor(R.color.bill_gray);
				}
			}

		} else if (mFlag == 2) {
			long firstPayDate = judgePayDate(mId);

			if (firstPayDate == 0) {
				editDialogShow();
			} else {

				if (firstPayDate < ep_billDueDate) {

					editDialogShow();

				} else {
					edit_status = 1;
					recurringButton.setEnabled(false);
					recurringButton.setTextColor(R.color.bill_gray);
					billNameEditText.setEnabled(false);
					billNameEditText.setTextColor(R.color.bill_gray);
				}

			}

		} else if (mFlag == 3) {

			if (mMap.containsKey("billItemHasBillRule")) {
				int billo_id = (Integer) mMap.get("billItemHasBillRule");
				long firstPayDate = judgePayDate(billo_id);

				if (firstPayDate == 0) {
					editDialogShow();
				} else {

					if (firstPayDate < ep_billDueDate) {

						editDialogShow();

					} else {
						edit_status = 1;
						recurringButton.setEnabled(false);
						recurringButton.setTextColor(R.color.bill_gray);
						billNameEditText.setEnabled(false);
						billNameEditText.setTextColor(R.color.bill_gray);
					}

				}

			} else {

				edit_status = 1;
				recurringButton.setEnabled(false);
				recurringButton.setTextColor(R.color.bill_gray);
				billNameEditText.setEnabled(false);
				billNameEditText.setTextColor(R.color.bill_gray);
			}

		}

	}

	public void editDialogShow() { // 对话框的弹出方法

		View dialogview = inflater.inflate(
				R.layout.dialog_upcoming_item_operation, null);
		ListView diaListView = (ListView) dialogview
				.findViewById(R.id.dia_listview);
		DialogEditBillAdapter dialogEditBillAdapter = new DialogEditBillAdapter(
				this);

		diaListView.setAdapter(dialogEditBillAdapter);
		diaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					edit_status = 1;
					recurringButton.setEnabled(false);
					recurringButton.setTextColor(R.color.bill_gray);
					billNameEditText.setEnabled(false);
					billNameEditText.setTextColor(R.color.bill_gray);
					editDialog.dismiss();

				} else if (arg2 == 1) {
					edit_status = 2;
					editDialog.dismiss();
				}

			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(
				BillEditActivity.this);
		builder.setTitle("Details");
		builder.setView(dialogview);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		editDialog = builder.create();
		editDialog.show();

	}

	public int judgeTemPayDate(int b_id) {

		List<Map<String, Object>> mList = BillsDao
				.selectTransactionByBillRuleId(this, b_id);
		if (mList.size() > 0) {
			return 1;
		} else {
			return 0;
		}

	}

	public long judgePayDate(int b_id) {

		List<Map<String, Object>> dataList = BillsDao.selectBillItemByRuleId(
				this, b_id);

		if (dataList.size() > 0) {
			ArrayList<Long> OPayList = new ArrayList<Long>();
			long reData = 0;
			for (Map<String, Object> oMap : dataList) {
				int Object_id = (Integer) oMap.get("_id");
				long bk_billODueDate = (Long) oMap.get("ep_billDueDate");
				List<Map<String, Object>> mList = BillsDao
						.selectTransactionByBillItemId(this, Object_id);

				if (mList.size() > 0) {
					OPayList.add(bk_billODueDate);
				}
			}

			if (OPayList.size() > 0) {

				long max = OPayList.get(0);
				for (int i = 0; i < OPayList.size(); i++) {

					if (OPayList.get(i) > max) {
						max = OPayList.get(i);
					}

				}
				reData = max;
			} else {
				reData = 0;
			}

			return reData;

		} else {
			return 0;
		}

	}
	
	public long getPreDate(int bk_billRepeatType, long bk_billDuedate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(bk_billDuedate);
		
		if (bk_billRepeatType == 1) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-7);

		} else if (bk_billRepeatType == 2) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-14);

		} else if (bk_billRepeatType == 3) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-28);

		} else if (bk_billRepeatType == 4) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-15);

		} else if (bk_billRepeatType == 5) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, -1);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, -1 - 1);
				} else {
					calendar.add(Calendar.MONTH, -1);
				}


		} else if (bk_billRepeatType == 6) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, -2);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, -2 - 2);
				} else {
					calendar.add(Calendar.MONTH, -2);
				}


		} else if (bk_billRepeatType == 7) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, -3);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, -3 - 3);
				} else {
					calendar.add(Calendar.MONTH, -3);
				}


		} else if (bk_billRepeatType == 8) {

				calendar.add(Calendar.YEAR, -1);

		}
		long preDuedate = calendar.getTimeInMillis();
		return preDuedate;
	}

}
