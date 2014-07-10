package com.appxy.pocketexpensepro.overview.transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.accounts.AccountDao;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class CreatTransactonByAccountActivity extends BaseHomeActivity {
	private static final int categoryDefault = 0;
	private static final int childTransactionsDefault = 0;
	private static final int expenseAccountDefault = 0;
	private static final int incomeAccountDefault = 0;
	private static final int parTransactionDefault = 0;

	private LayoutInflater inflater;

	private AutoCompleteTextView payeeEditText;
	private Button categoryButton;
	private EditText amountEditText;
	private Button dateButton;
	private Button accountButton;
	private Spinner recurringSpin;
	private Button photoButton;

	private AlertDialog mDialog;
	private ListView payeeListView;
	private PayeeListViewAdapter payeeListViewAdapter;
	private int payeeCheckItem = 0;
	private int payeeId = 0;//
	private double amountDouble;

	private AlertDialog mCategoryDialog;
	private int checkedItem;
	private int gCheckedItem;// 选择位置
	private int cCheckedItem;
	private Button expenseButton;
	private Button incomeButton;
	private int mCategoryType = 0; // 0 expense 1 income
	private ExpandableListView mExpandableListView;
	private DialogExpandableListViewAdapter mDialogExpandableListViewAdapter;
	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	private int categoryId; // 初次others的位置

	private String amountString = "0.0";
	private int mYear;
	private int mMonth;
	private int mDay;
	private String dateString;
	private long dateLong;

	private ListView accountListView;
	private int accountId = 0;
	private AlertDialog mAccountDialog;
	private int accountCheckItem = 0;
	private AccountsListViewAdapter accountListViewAdapter;

	private int isCleared = 1;

	private AlertDialog mMemoDialog;

	private AlertDialog mPhotoDialog;
	private AlertDialog mPhotoSeDialog;
	private LinearLayout takePhotoButton;
	private LinearLayout pickPhotoButton;
	private LinearLayout deletePhotoButton;
	private LinearLayout viewPhotoButton;
	private String picPath = "";
	private ImageView mImageView;
	private EditText memoEditText;
	private Spinner clearSpinner;
	private int recurringTpye = 0; // default on 1
	private Bitmap camorabitmap;
	private LinearLayout recurringLinearLayout;

	private int splitCheck;
	private List<Map<String, Object>> mReturnList;
	private AutoListAdapter autoListAdapter;
	private Cursor mCursor;
	private CharSequence sKey;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		
		Intent intent = getIntent();
		int account_id = intent.getIntExtra("acount_id", 0);
	    if (account_id <= 0) {
			finish();
		}

		inflater = LayoutInflater.from(CreatTransactonByAccountActivity.this);
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

		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();
		mReturnList = new ArrayList<Map<String, Object>>();

		mImageView = (ImageView) findViewById(R.id.imageView1);
		payeeEditText = (AutoCompleteTextView) findViewById(R.id.payee_edit);
		categoryButton = (Button) findViewById(R.id.category_btn);
		amountEditText = (EditText) findViewById(R.id.amount_edit);
		dateButton = (Button) findViewById(R.id.date_btn);
		accountButton = (Button) findViewById(R.id.account_btn);
		recurringSpin = (Spinner) findViewById(R.id.recurring_spin);
		photoButton = (Button) findViewById(R.id.photo_btn);
		clearSpinner = (Spinner) findViewById(R.id.clear_spin); // spinner
		memoEditText = (EditText) findViewById(R.id.memo_edit); // spinner
		recurringLinearLayout = (LinearLayout) findViewById(R.id.recurringLinearLayout);

		payeeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);
				mCursor = TransactionDao.selectPayee(
						CreatTransactonByAccountActivity.this, MEntity.sqliteEscape(s.toString()));
				autoListAdapter = new AutoListAdapter(
						CreatTransactonByAccountActivity.this, mCursor, true);
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

		final List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
				CreatTransactonByAccountActivity.this, 0);
		if (mDataList != null) {
			filterData(mDataList);
		}

		payeeEditText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				Cursor cursor = (Cursor) arg0.getItemAtPosition(0);

				// Get the state's capital from this row in the database.
				payeeId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				categoryId = cursor.getInt(cursor
						.getColumnIndexOrThrow("category"));
				String categoryName = cursor.getString(cursor
						.getColumnIndexOrThrow("categoryName"));
				categoryButton.setText(categoryName);

				if (categoryName.contains(":")) {
					String parentString[] = categoryName.split(":");
					String groupString = parentString[0];
					gCheckedItem = locationPositionByName(groupDataList,
							groupString);

					int i = 0;

					for (Map<String, Object> iMap : mDataList) {

						String cName = (String) iMap.get("categoryName");
						int cId = (Integer) iMap.get("_id");
						String temp[] = cName.split(":");
						if (temp[0].equals(groupString) && cName.contains(":")) {
							
							if (cId == categoryId) {
								cCheckedItem = i;
							}
							i = i + 1;
						}

					}

				} else {
					gCheckedItem = locationPositionById(groupDataList,
							categoryId);
					cCheckedItem = -1;
				}
			}
		});

		categoryButton.setOnClickListener(mClickListener);
		dateButton.setOnClickListener(mClickListener);
		accountButton.setOnClickListener(mClickListener);
		photoButton.setOnClickListener(mClickListener);

		if (groupDataList != null && groupDataList.size() > 0) {
			categoryId = locationOthersId(groupDataList);
			gCheckedItem = locationOthersPosition(groupDataList);
			cCheckedItem = -1;
			categoryButton.setText("Others");
		}

		List<Map<String, Object>> mAccountList = AccountDao.selectAccount(CreatTransactonByAccountActivity.this);
		if (mAccountList != null && mAccountList.size() > 0) {
			int i =0;
			for (Map<String, Object> iMap: mAccountList) {
				int a_id = (Integer) iMap.get("_id");
				if (a_id == account_id) {
					accountCheckItem = i;
					accountId = account_id;
					accountButton.setText((String) iMap.get("accName"));
				} 
				i++;
			}
			
			
		}

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(CreatTransactonByAccountActivity.this,
						R.array.on_off,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clearSpinner.setAdapter(adapterSpinner);
		clearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					isCleared = 1;
				} else if (arg2 == 1) {
					isCleared = 0;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		ArrayAdapter<CharSequence> adapterSpinner1 = ArrayAdapter
				.createFromResource(CreatTransactonByAccountActivity.this,
						R.array.transaction_recurring,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recurringSpin.setAdapter(adapterSpinner1);
		recurringSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				recurringTpye = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		amountEditText.setText("0.00");
		amountEditText.setSelection(4);
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
		c.setTimeInMillis(MainActivity.selectedDate);
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

				try {
					amountDouble = Double.parseDouble(amountString);
				} catch (NumberFormatException e) {
					amountDouble = 0.00;
				}
				
				String payeeString = payeeEditText.getText().toString();
				
				if (amountDouble == 0) {

					new AlertDialog.Builder(CreatTransactonByAccountActivity.this)
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

				} else if (accountId == 0) {

					new AlertDialog.Builder(CreatTransactonByAccountActivity.this)
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

				} else if (categoryId == 0) {

					new AlertDialog.Builder(CreatTransactonByAccountActivity.this)
							.setTitle("Warning! ")
							.setMessage("Please choose an Category! ")
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
				} else if(payeeString == null || payeeString.trim().length() == 0 || payeeString.trim().equals("")){
					
					new AlertDialog.Builder(CreatTransactonByAccountActivity.this)
					.setTitle("Warning! ")
					.setMessage(" Payee is required! ")
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
									
				}else {

					if (payeeString != null && payeeString.trim().length() != 0 && !payeeString.trim().equals("")) {

						boolean check = checkPayee(payeeString);
						if (!check) {
							long row = PayeeDao.insertPayee(
									CreatTransactonByAccountActivity.this, payeeString,
									new String(), categoryId);
							if (row > 0) {
								payeeId = (int) row;
							}
						}
					}

					final String memoString = memoEditText.getText().toString();
					// context, amount, dateTime, isClear, notes, photoName,
					// recurringType, category, childTransactions,
					// expenseAccount , incomeAccount, parTransaction, payee)

					if (mReturnList != null && mReturnList.size() > 1) { //category splite情况

						Log.v("mtest", "mReturnList" + mReturnList);
						long row = TransactionDao.insertTransactionAll(
								CreatTransactonByAccountActivity.this, amountString,
								dateLong, isCleared, memoString, picPath, 0,
								categoryDefault, childTransactionsDefault + "",
								accountId, incomeAccountDefault, -1, payeeId); // -1标识其本身为父本,split的父本只会是expense，因为所以的子类只能选择expense

						String idList = "";

						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							int cId = (Integer) iMap.get("categoryId");
							long id = TransactionDao.insertTransactionAll(
									CreatTransactonByAccountActivity.this, amount,
									dateLong, isCleared, memoString, picPath,
									0, cId, 1 + "", accountId,
									incomeAccountDefault, (int) row, payeeId);
							idList = idList + id + ",";
						}
						Log.v("mtest", "row" + row);
						Log.v("mtest", "idList" + idList);

						long rid = TransactionDao.updateParTransactionChild(
								CreatTransactonByAccountActivity.this, row, idList);
						Log.v("mtest", "rid" + rid);

					} else {
						
						List<Map<String, Object>> mCategoryList = TransactionDao.selectCategoryAll(CreatTransactonByAccountActivity.this);
						final int categoryType = judgeCategoryType(mCategoryList,categoryId);
						
//						if(recurringTpye > 0 && recurringTpye < 14){
//							
////							if( dateLong <= MEntity.getNowMillis() ){ //交易重复事件
////								
////								Thread mThread = new Thread(new Runnable() {
////									
////									@Override
////									public void run() {
////										// TODO Auto-generated method stub
////										Calendar calendar = Calendar.getInstance();
////										calendar.setTimeInMillis(dateLong);
////										
////										ExpenseDBHelper helper = new ExpenseDBHelper(CreatTransactionActivity.this);
////										SQLiteDatabase db = helper.getWritableDatabase();
////										db.beginTransaction();  //手动设置开始事务
////										
////										Calendar calendar1 = Calendar.getInstance();
////										calendar1.setTimeInMillis(dateLong);
////										Log.v("mtest", "recurringTpye"+recurringTpye);
////										if (recurringTpye == 1) {
////											calendar1.add(Calendar.DAY_OF_MONTH, 1);
////										} else if (recurringTpye == 2) {
////											calendar1.add(Calendar.DAY_OF_MONTH, 7);
////										}else if (recurringTpye == 3) {
////											calendar1.add(Calendar.DAY_OF_MONTH, 14);
////										}else if (recurringTpye == 4) {
////											calendar1.add(Calendar.DAY_OF_MONTH, 21);
////										}else if (recurringTpye == 5) {
////											calendar1.add(Calendar.DAY_OF_MONTH, 28);
////										}else if (recurringTpye == 6) {
////											calendar1.add(Calendar.DAY_OF_MONTH, 15);
////										}else if (recurringTpye == 7) {
////											calendar1.add(Calendar.MONTH, 1);
////										}else if (recurringTpye == 8) {
////											calendar1.add(Calendar.MONTH, 2);
////										}else if (recurringTpye == 9) {
////											calendar1.add(Calendar.MONTH, 3);
////										}else if (recurringTpye == 10) {
////											calendar1.add(Calendar.MONTH, 4);
////										}else if (recurringTpye == 11) {
////											calendar1.add(Calendar.MONTH, 5);
////										}else if (recurringTpye == 12) {
////											calendar1.add(Calendar.MONTH, 6);
////										}else if (recurringTpye == 13) {
////											calendar1.add(Calendar.YEAR, 1);
////										}
////										
////										if (calendar1.getTimeInMillis() <= MEntity.getNowMillis()) {
////											
////								        try{
////								            //批量处理操作
////								        	while (calendar.getTimeInMillis() < MEntity.getNowMillis()) {
////								        		
////								        		if (categoryType == 0) {
////													 TransactionDao.insertTransactionOne(db,CreatTransactionActivity.this,amountString, calendar.getTimeInMillis(), isCleared,
////																	memoString, picPath, 0,
////																	categoryId,
////																	childTransactionsDefault + "",
////																	accountId, incomeAccountDefault, 0,
////																	payeeId);
////												} else {
////													TransactionDao.insertTransactionOne(db,CreatTransactionActivity.this,amountString, calendar.getTimeInMillis(), isCleared,
////															memoString, picPath, 0,
////															categoryId, childTransactionsDefault + "",
////															expenseAccountDefault, accountId, 0,
////															payeeId);
////												}
////								        		
////												if (recurringTpye == 1) {
////													calendar.add(Calendar.DAY_OF_MONTH, 1);
////												} else if (recurringTpye == 2) {
////													calendar.add(Calendar.DAY_OF_MONTH, 7);
////												}else if (recurringTpye == 3) {
////													calendar.add(Calendar.DAY_OF_MONTH, 14);
////												}else if (recurringTpye == 4) {
////													calendar.add(Calendar.DAY_OF_MONTH, 21);
////												}else if (recurringTpye == 5) {
////													calendar.add(Calendar.DAY_OF_MONTH, 28);
////												}else if (recurringTpye == 6) {
////													calendar.add(Calendar.DAY_OF_MONTH, 15);
////												}else if (recurringTpye == 7) {
////													calendar.add(Calendar.MONTH, 1);
////												}else if (recurringTpye == 8) {
////													calendar.add(Calendar.MONTH, 2);
////												}else if (recurringTpye == 9) {
////													calendar.add(Calendar.MONTH, 3);
////												}else if (recurringTpye == 10) {
////													calendar.add(Calendar.MONTH, 4);
////												}else if (recurringTpye == 11) {
////													calendar.add(Calendar.MONTH, 5);
////												}else if (recurringTpye == 12) {
////													calendar.add(Calendar.MONTH, 6);
////												}else if (recurringTpye == 13) {
////													calendar.add(Calendar.YEAR, 1);
////												}
////											}
////							        		
////											
////								        	if (categoryType == 0) {
////												 TransactionDao.insertTransactionOne(db,CreatTransactionActivity.this,amountString, calendar.getTimeInMillis(), isCleared,
////																memoString, picPath, recurringTpye,
////																categoryId,
////																childTransactionsDefault + "",
////																accountId, incomeAccountDefault, 0,
////																payeeId);
////											} else {
////												TransactionDao.insertTransactionOne(db,CreatTransactionActivity.this,amountString, calendar.getTimeInMillis(), isCleared,
////														memoString, picPath, recurringTpye,
////														categoryId, childTransactionsDefault + "",
////														expenseAccountDefault, accountId, 0,
////														payeeId);
////											}
////								        	
////								            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
////								           
////								           }catch(Exception e){
////								              Log.v("mtest", "******Exception******"+e);
////								              
////								           }finally{
////								               db.endTransaction(); //处理完成
////								               db.close();
////								           }
////								        
////										}
////										
////									}
////								});
////								
////								mThread.start();
////								
////							}
//							
//						}else
						{
							
							if (categoryType == 0) {
								long row = TransactionDao
										.insertTransactionAll(CreatTransactonByAccountActivity.this,amountString, dateLong, isCleared,
												memoString, picPath, recurringTpye,
												categoryId,
												childTransactionsDefault + "",
												accountId, incomeAccountDefault, 0,
												payeeId);
							} else {
								long row = TransactionDao.insertTransactionAll(
										CreatTransactonByAccountActivity.this,
										amountString, dateLong, isCleared,
										memoString, picPath, recurringTpye,
										categoryId, childTransactionsDefault + "",
										expenseAccountDefault, accountId, 0,
										payeeId);
							}
							
						}
						
					}

					Intent intent = new Intent();
					intent.putExtra("done", 1);
					setResult(6, intent);
					finish();

					finish();
				}

				break;

			// case R.id.add_payee_btn:
			//
			// View view = inflater.inflate(R.layout.dialog_choose_type, null);
			// payeeListView = (ListView) view.findViewById(R.id.mListView);
			// payeeListViewAdapter = new PayeeListViewAdapter(
			// CreatTransactionActivity.this);
			//
			// final List<Map<String, Object>> mList = TransactionDao
			// .selectPayee(CreatTransactionActivity.this);
			//
			// payeeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// payeeListView.setAdapter(payeeListViewAdapter);
			// payeeListView
			// .setSelection((payeeCheckItem - 1) > 0 ? (payeeCheckItem - 1)
			// : 0);
			// payeeListViewAdapter.setItemChecked(payeeCheckItem);
			// payeeListViewAdapter.setAdapterDate(mList);
			// payeeListViewAdapter.notifyDataSetChanged();
			// payeeListView.setOnItemClickListener(new OnItemClickListener() {
			//
			// @Override
			// public void onItemClick(AdapterView<?> arg0, View arg1,
			// int arg2, long arg3) {
			// // TODO Auto-generated method stub
			// payeeCheckItem = arg2;
			// payeeListViewAdapter.setItemChecked(payeeCheckItem);
			// payeeListViewAdapter.notifyDataSetChanged();
			// String nameString = (String) mList.get(arg2)
			// .get("name");
			// payeeEditText.setText(nameString);
			// payeeEditText.setSelection(nameString.length());
			//
			// payeeId = (Integer) mList.get(arg2).get("_id");
			// mDialog.dismiss();
			// }
			// });
			//
			// AlertDialog.Builder mBuilder = new AlertDialog.Builder(
			// CreatTransactionActivity.this);
			// mBuilder.setTitle("Choose Payee");
			// mBuilder.setView(view);
			// mBuilder.setNegativeButton("Cancel",
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// // TODO Auto-generated method stub
			// }
			// });
			//
			// mDialog = mBuilder.create();
			// mDialog.show();
			// break;

			case R.id.category_btn:
				if (splitCheck == 0) {

					View view1 = inflater.inflate(
							R.layout.dialog_choose_category, null);
					expenseButton = (Button) view1
							.findViewById(R.id.expense_btn);
					incomeButton = (Button) view1.findViewById(R.id.income_btn);

					expenseButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View paramView) {
							// TODO Auto-generated method stub
							List<Map<String, Object>> mDataList = PayeeDao
									.selectCategory(
											CreatTransactonByAccountActivity.this, 0);
							filterData(mDataList);

							mDialogExpandableListViewAdapter
									.notifyDataSetChanged();

							mExpandableListView
									.setOnChildClickListener(new OnChildClickListener() {

										@Override
										public boolean onChildClick(
												ExpandableListView parent,
												View v, int groupPosition,
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
													.get(childPosition)
													.get("_id");
											String categoryName = (String) childrenAllDataList
													.get(groupPosition)
													.get(childPosition)
													.get("categoryName");
											categoryButton
													.setText(categoryName);
											mCategoryDialog.dismiss();

											return true;
										}
									});

							mExpandableListView
									.setOnGroupClickListener(new OnGroupClickListener() {

										@Override
										public boolean onGroupClick(
												ExpandableListView parent,
												View v, int groupPosition,
												long id) {
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
													.get(groupPosition).get(
															"_id");
											String categoryName = (String) groupDataList
													.get(groupPosition).get(
															"categoryName");
											categoryButton
													.setText(categoryName);
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

							List<Map<String, Object>> mDataList = PayeeDao
									.selectCategory(
											CreatTransactonByAccountActivity.this, 1);
							filterData(mDataList);
							mDialogExpandableListViewAdapter
									.notifyDataSetChanged();

							mExpandableListView
									.setOnChildClickListener(new OnChildClickListener() {

										@Override
										public boolean onChildClick(
												ExpandableListView parent,
												View v, int groupPosition,
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
													.get(childPosition)
													.get("_id");
											String categoryName = (String) childrenAllDataList
													.get(groupPosition)
													.get(childPosition)
													.get("categoryName");
											categoryButton
													.setText(categoryName);

											mCategoryDialog.dismiss();

											return true;
										}
									});

							mExpandableListView
									.setOnGroupClickListener(new OnGroupClickListener() {

										@Override
										public boolean onGroupClick(
												ExpandableListView parent,
												View v, int groupPosition,
												long id) {
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
													.get(groupPosition).get(
															"_id");

											String categoryName = (String) groupDataList
													.get(groupPosition).get(
															"categoryName");
											categoryButton
													.setText(categoryName);

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

					mExpandableListView = (ExpandableListView) view1
							.findViewById(R.id.mExpandableListView);
					mDialogExpandableListViewAdapter = new DialogExpandableListViewAdapter(
							CreatTransactonByAccountActivity.this);
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
								.selectCategory(CreatTransactonByAccountActivity.this,
										0);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.setAdapterData(
								groupDataList, childrenAllDataList);

						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);

						mExpandableListView.setSelectedChild(gCheckedItem,
								cCheckedItem, true);
						mDialogExpandableListViewAdapter.setSelectedPosition(
								gCheckedItem, cCheckedItem);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();
					} else if (mCategoryType == 1) {
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(CreatTransactonByAccountActivity.this,
										1);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.setAdapterData(
								groupDataList, childrenAllDataList);
						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);

						mExpandableListView.setSelectedChild(gCheckedItem,
								cCheckedItem, true);
						mDialogExpandableListViewAdapter.setSelectedPosition(
								gCheckedItem, cCheckedItem);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();

					}

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
											.get(groupPosition)
											.get(childPosition).get("_id");
									String categoryName = (String) childrenAllDataList
											.get(groupPosition)
											.get(childPosition)
											.get("categoryName");
									categoryButton.setText(categoryName);
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
									String categoryName = (String) groupDataList
											.get(groupPosition).get(
													"categoryName");
									categoryButton.setText(categoryName);

									return true;
								}
							});

					AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(
							CreatTransactonByAccountActivity.this);
					mBuilder1.setTitle("Choose Category");
					mBuilder1.setView(view1);
					mBuilder1.setPositiveButton("Split",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.setClass(
											CreatTransactonByAccountActivity.this,
											SplitCategoryActivity.class);
									startActivityForResult(intent, 9);
									dialog.dismiss();
								}

							});
					mBuilder1.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							});

					mCategoryDialog = mBuilder1.create();
					mCategoryDialog.show();

				} else if (splitCheck == 1) {

					Intent intent = new Intent();
					intent.putExtra("returnList", (Serializable) mReturnList);
					intent.setClass(CreatTransactonByAccountActivity.this,
							EditSplitActivity.class);
					startActivityForResult(intent, 10);
				}

				break;
			case R.id.date_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(CreatTransactonByAccountActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Date");
				DPD.show();

				break;
			case R.id.account_btn:

				View view2 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				accountListView = (ListView) view2.findViewById(R.id.mListView);
				accountListViewAdapter = new AccountsListViewAdapter(
						CreatTransactonByAccountActivity.this);

				final List<Map<String, Object>> mAccountList = AccountDao
						.selectAccount(CreatTransactonByAccountActivity.this);
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
						CreatTransactonByAccountActivity.this);
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
			case R.id.photo_btn:

				if (picPath != null && picPath.length() > 0
						&& camorabitmap != null) {

					View view9 = inflater.inflate(R.layout.dialog_photo_second,
							null);
					deletePhotoButton = (LinearLayout) view9
							.findViewById(R.id.delete_btn);
					viewPhotoButton = (LinearLayout) view9
							.findViewById(R.id.view_btn);
					deletePhotoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							File file = new File(picPath);
							try {
								file.delete();
							} catch (Exception e) {
								// TODO: handle exception
								System.out.println(e + "Exception");
							}
							mImageView.setImageBitmap(null);
							picPath = "";
							mPhotoSeDialog.dismiss();
						}
					});

					viewPhotoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.putExtra("picPath", picPath);
							intent.setClass(CreatTransactonByAccountActivity.this,
									ViewPhotoActivity.class);
							startActivity(intent);
							mPhotoSeDialog.dismiss();
						}
					});

					AlertDialog.Builder mBuilder9 = new AlertDialog.Builder(
							CreatTransactonByAccountActivity.this);
					mBuilder9.setTitle("Take Photo");
					mBuilder9.setView(view9);
					mBuilder9.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							});

					mPhotoSeDialog = mBuilder9.create();
					mPhotoSeDialog.show();

				} else {

					View view4 = inflater.inflate(R.layout.dialog_photo, null);
					takePhotoButton = (LinearLayout) view4
							.findViewById(R.id.take_btn);
					pickPhotoButton = (LinearLayout) view4
							.findViewById(R.id.pick_btn);
					takePhotoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							File fileFolder = new File(Environment
									.getExternalStorageDirectory()
									+ "/PocketExpense/Images/");
							if (!fileFolder.exists()) {
								try {
									fileFolder.mkdirs();
								} catch (Exception e) {
									// TODO: handle exception
									System.out.println(e + "Exception");
								}
							}

							String picName = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date());

							String filepath = Environment
									.getExternalStorageDirectory()
									+ "/PocketExpense/Images/"
									+ picName
									+ ".jpg";
							final File file = new File(filepath);
							final Uri imageuri = Uri.fromFile(file);
							picPath = filepath;
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(
									android.provider.MediaStore.EXTRA_OUTPUT,
									imageuri);
							startActivityForResult(intent, 6);
							mPhotoDialog.dismiss();
						}
					});

					pickPhotoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent openAlbumIntent = new Intent(
									Intent.ACTION_GET_CONTENT);
							openAlbumIntent.setType("image/*");
							startActivityForResult(openAlbumIntent, 7);
							mPhotoDialog.dismiss();
						}
					});

					AlertDialog.Builder mBuilder4 = new AlertDialog.Builder(
							CreatTransactonByAccountActivity.this);
					mBuilder4.setTitle("Take Photo");
					mBuilder4.setView(view4);
					mBuilder4.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							});

					mPhotoDialog = mBuilder4.create();
					mPhotoDialog.show();

				}

				break;
			}
		}
	};

	private boolean checkPayee(String pName) {
		boolean check = false;
		List<Map<String, Object>> mList = TransactionDao
				.selectPayee(CreatTransactonByAccountActivity.this);

		for (Map<String, Object> iMap : mList) {
			String payeeNameString = (String) iMap.get("name");
			if (payeeNameString.equals(pName)) {
				check = true;
				payeeId = (Integer) iMap.get("_id");
			}
		}
		return check;
	}

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

	public int locationPositionById(List<Map<String, Object>> mData, int id) { // 定位的位置
		int i = 0;
		int position = -1;
		for (Map<String, Object> mMap : mData) {
			int mId = (Integer) mMap.get("_id");
			if (mId == id) {
				position = i;
			}
			i = i + 1;
		}

		return position;
	}

	public int locationPositionByName(List<Map<String, Object>> mData,
			String name) { // 定位的位置
		int i = 0;
		int position = 0;
		for (Map<String, Object> mMap : mData) {
			String categoryName = (String) mMap.get("categoryName");
			if (categoryName.equals(name)) {
				position = i;
			}
			i = i + 1;
		}

		return position;
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

	public int judgeCategoryType(List<Map<String, Object>> mList, int cId) { // 判断该transaction，category是属于expense还是income

		int rId = 0;
		for (Map<String, Object> iMap : mList) {
			int id = (Integer) iMap.get("_id");
			if (id == cId) {
				rId = (Integer) iMap.get("categoryType");
			}
		}
		return rId;
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 6:
			File file = new File(picPath);
			if (file.exists()) {
				camorabitmap = BitmapFactory.decodeFile(picPath);
				Log.v("mtest", "camorabitmap" + camorabitmap);
				mImageView.setImageBitmap(camorabitmap);
			}

			break;
		case 7:

			// 照片的原始资源地址
			if (data != null) {
				Uri originalUri = data.getData();
				picPath = Common.getRealPathFromURI(originalUri,
						CreatTransactonByAccountActivity.this);
				camorabitmap = BitmapFactory.decodeFile(picPath);
				mImageView.setImageBitmap(camorabitmap);
			}
			break;

		case 9:
			if (data != null) {
				mReturnList.clear();
				mReturnList = (List<Map<String, Object>>) data
						.getSerializableExtra("returnList");

				if (mReturnList != null && mReturnList.size() > 0) {

					if (mReturnList.size() == 1) {
						splitCheck = 0;
						recurringLinearLayout.setVisibility(View.VISIBLE);
						amountEditText.setEnabled(true);
						amountString = (String) mReturnList.get(0)
								.get("amount");
						amountEditText.setText(MEntity.doubl2str(amountString));
						categoryId = (Integer) mReturnList.get(0).get(
								"categoryId");
						categoryButton.setText((String) mReturnList.get(0).get(
								"categoryName"));
					} else if (mReturnList.size() > 1) {
						categoryButton.setText("-Split-");
						splitCheck = 1;
						recurringLinearLayout.setVisibility(View.GONE);
						amountEditText.setEnabled(false);
						BigDecimal b1 = new BigDecimal("0");
						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							BigDecimal b2 = new BigDecimal(amount);
							b1 = b1.add(b2);
						}
						amountString = b1.doubleValue() + "";
						Log.v("mtest", "amountString" + amountString);
						amountEditText.setText(MEntity.doubl2str(amountString));
					}
				} else {
					splitCheck = 0;
					recurringLinearLayout.setVisibility(View.VISIBLE);
					amountEditText.setEnabled(true);

					mReturnList.clear();
					if (groupDataList != null && groupDataList.size() > 0) {
						categoryId = locationOthersId(groupDataList);
						gCheckedItem = locationOthersPosition(groupDataList);
						cCheckedItem = -1;
						categoryButton.setText("Others");
					}
				}
			}
			break;
		case 10:
			if (data != null) {
				mReturnList.clear();
				mReturnList = (List<Map<String, Object>>) data
						.getSerializableExtra("returnList");
				if (mReturnList != null && mReturnList.size() > 0) {

					if (mReturnList.size() == 1) {
						splitCheck = 0;
						recurringLinearLayout.setVisibility(View.VISIBLE);
						amountEditText.setEnabled(true);

						amountString = (String) mReturnList.get(0)
								.get("amount");
						amountEditText.setText(MEntity.doubl2str(amountString));
						categoryId = (Integer) mReturnList.get(0).get(
								"categoryId");
						categoryButton.setText((String) mReturnList.get(0).get(
								"categoryName"));
					} else if (mReturnList.size() > 1) {
						recurringLinearLayout.setVisibility(View.GONE);
						categoryButton.setText("-Split-");
						amountEditText.setEnabled(false);

						splitCheck = 1;
						BigDecimal b1 = new BigDecimal("0");
						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							BigDecimal b2 = new BigDecimal(amount);
							b1 = b1.add(b2);
						}
						amountString = b1.doubleValue() + "";
						Log.v("mtest", "amountString" + amountString);
						amountEditText.setText(MEntity.doubl2str(amountString));
					}

				} else {
					splitCheck = 0;
					recurringLinearLayout.setVisibility(View.VISIBLE);
					amountEditText.setEnabled(true);

					mReturnList.clear();
					if (groupDataList != null && groupDataList.size() > 0) {
						categoryId = locationOthersId(groupDataList);
						gCheckedItem = locationOthersPosition(groupDataList);
						cCheckedItem = -1;
						categoryButton.setText("Others");
					}
				}
			}

			break;
		}
	}
}
