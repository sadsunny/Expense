package com.appxy.pocketexpensepro.accounts;

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
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.overview.transaction.AutoListAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.overview.transaction.SplitCategoryActivity;
import com.appxy.pocketexpensepro.overview.transaction.EditSplitActivity;
import com.appxy.pocketexpensepro.overview.transaction.ViewPhotoActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxRecord;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class EditTransactionActivity extends BaseHomeActivity {
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
	private RelativeLayout expenseButton;
	private RelativeLayout incomeButton;
	private View chooseView1;
	private View chooseView2;
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
	private AccountsChooseAdapter accountListViewAdapter;

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
	private int _id;
	private AutoListAdapter autoListAdapter;
	private Cursor mCursor;
	private CharSequence sKey;
	private List<Map<String, Object>> mDataList1;
	private int transactionHasBillItem  ;
	private int transactionHasBillRule  ;
	private String uuid ;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);

		inflater = LayoutInflater.from(EditTransactionActivity.this);
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
		_id = intent.getIntExtra("_id", 0);
		if (_id <= 0) {
			finish();
		}
		uuid = SyncDao.selecTransactionUUid(this, _id);
		Log.v("mtag", "编辑uuid"+uuid);
		
		List<Map<String, Object>> mInitializeDataList = AccountDao
				.selectTransactionByID(this, _id);
		Log.v("mtest", "mInitializeDataList"+mInitializeDataList);
		
		Map<String, Object> mMap = mInitializeDataList.get(0);

		String amount = (String) mMap.get("amount");
		long dateTime = (Long) mMap.get("dateTime");
		int isClear = (Integer) mMap.get("isClear");
		String notes = (String) mMap.get("notes");
		String photoName = (String) mMap.get("photoName");
		int recurringType = (Integer) mMap.get("recurringType");
		int category = (Integer) mMap.get("category");
		String childTransactions = (String) mMap.get("childTransactions");
		int expenseAccount = (Integer) mMap.get("expenseAccount");
		int incomeAccount = (Integer) mMap.get("incomeAccount");
		int parTransaction = (Integer) mMap.get("parTransaction");
		int payee = (Integer) mMap.get("payee");
		 transactionHasBillItem = (Integer) mMap.get("transactionHasBillItem");
		 transactionHasBillRule = (Integer) mMap.get("transactionHasBillRule");
		
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

		if (payee > 0) {
			List<Map<String, Object>> mPayeeDataList = AccountDao
					.selectPayeeById(this, payee);
			if (mPayeeDataList != null && mPayeeDataList.size()  >0) {
				String pNameString = (String) mPayeeDataList.get(0).get("name");
				payeeEditText.setText(pNameString);
				payeeId = payee;
				List<Map<String, Object>> mPList = TransactionDao
						.selectPayee(EditTransactionActivity.this);
				payeeCheckItem = locationIdPosition(mPList, payee);
			}else {
				finish();
			}
		
		}
		
		payeeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);
				mCursor = TransactionDao.selectPayee(
						EditTransactionActivity.this, MEntity.sqliteEscape(s.toString()));
				autoListAdapter = new AutoListAdapter(
						EditTransactionActivity.this, mCursor, true);
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
		
		amountEditText.setText(MEntity.doubl2str(amount));
		amountString = amount;

		if (expenseAccount <= 0 || incomeAccount <= 0) {

			List<Map<String, Object>> mAccountList = AccountDao
					.selectAccount(EditTransactionActivity.this);
			if (expenseAccount > 0) {
				accountId = expenseAccount;
				accountCheckItem = locationIdPosition(mAccountList,
						expenseAccount);
				List<Map<String, Object>> mAccountListById = AccountDao
						.selectAccountById(EditTransactionActivity.this,
								expenseAccount);
				accountButton.setText((String) mAccountListById.get(0).get(
						"accName"));
			} else if (incomeAccount > 0) {
				accountId = incomeAccount;
				accountCheckItem = locationIdPosition(mAccountList,
						incomeAccount);
				List<Map<String, Object>> mAccountListById = AccountDao
						.selectAccountById(EditTransactionActivity.this,
								incomeAccount);
				accountButton.setText((String) mAccountListById.get(0).get(
						"accName"));
			}

		}

		isCleared = isClear;
		this.recurringTpye = recurringType;

		if (photoName != null && photoName.length() > 0) {
			picPath = photoName;
			File file = new File(photoName);
			if (file.exists()) {
				camorabitmap = BitmapFactory.decodeFile(photoName);
				mImageView.setImageBitmap(camorabitmap);
			}
		}

		if (notes != null && notes.length() > 0) {
			memoEditText.setText(notes);
		}

		categoryButton.setOnClickListener(mClickListener);
		dateButton.setOnClickListener(mClickListener);
		accountButton.setOnClickListener(mClickListener);
		photoButton.setOnClickListener(mClickListener);

		int childType = 0;
		try {
			childType = Integer.parseInt(childTransactions);
		} catch (Exception e) {
			// TODO: handle exception
			childType = 0;
		}

	   mDataList1 = new ArrayList<Map<String, Object>>();
		if (expenseAccount > 0 && incomeAccount <= 0) {
			mDataList1 = PayeeDao
					.selectCategory(EditTransactionActivity.this, 0);
			mCategoryType = 0;

		} else if (incomeAccount > 0 && expenseAccount <= 0) {
			mDataList1 = PayeeDao
					.selectCategory(EditTransactionActivity.this, 1);
			mCategoryType = 1;

		}

		if (mDataList1 != null) {
			filterData(mDataList1);
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

				int cType = cursor.getInt(cursor
						.getColumnIndexOrThrow("categoryType"));
				mCategoryType = cType ; // 1 income
				
				if (categoryName.contains(":")) {
					String parentString[] = categoryName.split(":");
					String groupString = parentString[0];
					gCheckedItem = locationPositionByName(groupDataList,
							groupString);

					int i = 0;

					for (Map<String, Object> iMap : mDataList1) {

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

		if (parTransaction == -1 && childTransactions.length() > 1) {// 表示该项是父本（这里不会出现子类，也就是parTransaction
																		// >0,childTransactions//
																		// =1的情况）
			categoryButton.setText("-Split-");
			splitCheck = 1;
			categoryId = 0;
			amountEditText.setFocusable(false);
			amountEditText.setEnabled(false);
			payeeEditText.setFocusable(true);
			
			recurringLinearLayout.setVisibility(View.GONE);
			List<Map<String, Object>> mChildList = AccountDao
					.selectTransactionChildById(EditTransactionActivity.this,
							_id);
			mReturnList = mChildList;

		} else {
			if (category > 0) {
				categoryId = category;
				splitCheck = 0;
				amountEditText.setEnabled(true);

				if (( transactionHasBillItem >0) || (transactionHasBillRule >0)) {
					recurringLinearLayout.setVisibility(View.GONE);
				} else {
					recurringLinearLayout.setVisibility(View.VISIBLE);
				}
				
				if (groupDataList != null && groupDataList.size() > 0) {
					List<Map<String, Object>> tList = locationPosition(
							mDataList1, groupDataList, childrenAllDataList,
							categoryId);
					gCheckedItem = (Integer) tList.get(0).get("gPosition");
					cCheckedItem = (Integer) tList.get(0).get("cPosition");
					List<Map<String, Object>> mCategoryList = AccountDao
							.selectCategoryById(EditTransactionActivity.this,
									category);
					categoryButton.setText((String) mCategoryList.get(0).get(
							"categoryName"));
				}
			}

		}

		// if (groupDataList != null && groupDataList.size() > 0) {
		// categoryId = locationOthersId(groupDataList);
		// gCheckedItem = locationOthersPosition(groupDataList);
		// cCheckedItem = -1;
		// categoryButton.setText("Others");
		// }

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(EditTransactionActivity.this,
						R.array.on_off,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clearSpinner.setAdapter(adapterSpinner);

		if (isClear == 0) {
			clearSpinner.setSelection(1);
		} else if (isClear == 1) {
			clearSpinner.setSelection(0);
		}
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
				.createFromResource(EditTransactionActivity.this,
						R.array.transaction_recurring,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recurringSpin.setAdapter(adapterSpinner1);
		recurringSpin.setSelection(recurringType);
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

		amountEditText.setSelection(MEntity.doubl2str(amount).length());
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
		c.setTimeInMillis(dateTime);
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

					new AlertDialog.Builder(EditTransactionActivity.this)
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

					new AlertDialog.Builder(EditTransactionActivity.this)
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

				} else if (categoryId == 0 && splitCheck != 1) {

					new AlertDialog.Builder(EditTransactionActivity.this)
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
				}else if(payeeString == null || payeeString.trim().length() == 0 || payeeString.trim().equals("")){
					
					new AlertDialog.Builder(EditTransactionActivity.this)
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
									
				} else {

					if (payeeString != null && payeeString.trim().length() != 0
							&& !payeeString.trim().equals("")) {

						boolean check = checkPayee(payeeString);
						if (!check) {
							long row = PayeeDao.insertPayee(
									EditTransactionActivity.this, payeeString,
									new String(), categoryId, mDbxAcctMgr, mDatastore);
							if (row > 0) {
								payeeId = (int) row;
							}
						}
					}

					String memoString = memoEditText.getText().toString();

					// context, amount, dateTime, isClear, notes, photoName,
					// recurringType, category, childTransactions,
					// expenseAccount , incomeAccount, parTransaction, payee)

					if (mReturnList != null && mReturnList.size() > 1) {

						Log.v("mtest", "mReturnList" + mReturnList);
						long row = AccountDao.updateTransactionAll(_id,
								EditTransactionActivity.this, amountString,
								dateLong, isCleared, memoString, picPath, 0,
								categoryDefault, childTransactionsDefault + "",
								accountId, incomeAccountDefault, -1, payeeId, uuid, mDbxAcctMgr,mDatastore); // -1标识其本身为父本,split的父本只会是expense，因为所以的子类只能选择expense

						String idList = "";
						AccountDao.deleteTransactionChildById(
								EditTransactionActivity.this, _id,  mDbxAcctMgr, mDatastore);
						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							int cId = (Integer) iMap.get("categoryId");
							long id = TransactionDao.insertTransactionAll(
									EditTransactionActivity.this, amount,
									dateLong, isCleared, memoString, picPath,
									0, cId, 1 + "", accountId,
									incomeAccountDefault, _id, payeeId, new String(), 0, 0 , mDbxAcctMgr, mDatastore);
							idList = idList + id + ",";
						}
						Log.v("mtest", "row" + row);
						Log.v("mtest", "idList" + idList);

						long rid = TransactionDao.updateParTransactionChild(
								EditTransactionActivity.this, _id, idList);
						Log.v("mtest", "rid" + rid);

					} else {
						List<Map<String, Object>> mCategoryList = TransactionDao
								.selectCategoryAll(EditTransactionActivity.this);
						int categoryType = judgeCategoryType(mCategoryList,
								categoryId);
						if (categoryType == 0) {
							long row = AccountDao.updateTransactionAll(_id,
									EditTransactionActivity.this, amountString,
									dateLong, isCleared, memoString, picPath,
									recurringTpye, categoryId,
									childTransactionsDefault + "", accountId,
									incomeAccountDefault, 0, payeeId, uuid, mDbxAcctMgr,mDatastore);
						} else {
							long row = AccountDao.updateTransactionAll(_id,
									EditTransactionActivity.this, amountString,
									dateLong, isCleared, memoString, picPath,
									recurringTpye, categoryId,
									childTransactionsDefault + "",
									expenseAccountDefault, accountId, 0,
									payeeId, uuid, mDbxAcctMgr,mDatastore);
						}
					}
					    Intent resultintent = new Intent();
						resultintent.putExtra("row", 1);
						setResult(13, resultintent);

					finish();
				}

				break;

//			case R.id.add_payee_btn:
//				View view = inflater.inflate(R.layout.dialog_choose_type, null);
//				payeeListView = (ListView) view.findViewById(R.id.mListView);
//				payeeListViewAdapter = new PayeeListViewAdapter(
//						EditTransactionActivity.this);
//
//				final List<Map<String, Object>> mList = TransactionDao
//						.selectPayee(EditTransactionActivity.this);
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
//						payeeEditText.setText(nameString);
//						payeeEditText.setSelection(nameString.length());
//
//						payeeId = (Integer) mList.get(arg2).get("_id");
//						mDialog.dismiss();
//					}
//				});
//
//				AlertDialog.Builder mBuilder = new AlertDialog.Builder(
//						EditTransactionActivity.this);
//				mBuilder.setTitle("Choose Payee");
//				mBuilder.setView(view);
//				mBuilder.setNegativeButton("Cancel",
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//							}
//						});
//
//				mDialog = mBuilder.create();
//				mDialog.show();
//				break;

			case R.id.category_btn:
				if (splitCheck == 0) {

					View view1 = inflater.inflate(
							R.layout.dialog_choose_category, null);
					expenseButton = (RelativeLayout) view1
							.findViewById(R.id.expense_btn);
					incomeButton = (RelativeLayout) view1.findViewById(R.id.income_btn);
					chooseView1  = (View) view1.findViewById(R.id.view1);
					chooseView2  = (View) view1.findViewById(R.id.view2);
					
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
									.selectCategory(
											EditTransactionActivity.this, 0);
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

							chooseView1.setVisibility(View.INVISIBLE);
							chooseView2.setVisibility(View.VISIBLE);
							
							List<Map<String, Object>> mDataList = PayeeDao
									.selectCategory(
											EditTransactionActivity.this, 1);
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
							EditTransactionActivity.this);
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
								.selectCategory(EditTransactionActivity.this, 0);
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
								.selectCategory(EditTransactionActivity.this, 1);
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
							EditTransactionActivity.this);
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
											EditTransactionActivity.this,
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
					intent.setClass(EditTransactionActivity.this,
							EditSplitActivity.class);
					startActivityForResult(intent, 10);
				}

				break;
			case R.id.date_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(EditTransactionActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Date");
				DPD.show();

				break;
			case R.id.account_btn:

				View view2 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				accountListView = (ListView) view2.findViewById(R.id.mListView);
				accountListViewAdapter = new AccountsChooseAdapter(
						EditTransactionActivity.this);

				final List<Map<String, Object>> mAccountList = AccountDao
						.selectAccount(EditTransactionActivity.this);
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
						EditTransactionActivity.this);
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

				if (picPath != null && picPath.length() > 0 && camorabitmap != null) {

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
							intent.setClass(EditTransactionActivity.this,
									ViewPhotoActivity.class);
							startActivity(intent);
							mPhotoSeDialog.dismiss();
						}
					});

					AlertDialog.Builder mBuilder9 = new AlertDialog.Builder(
							EditTransactionActivity.this);
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
							EditTransactionActivity.this);
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
				.selectPayee(EditTransactionActivity.this);

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 6:
			File file = new File(picPath);
			if (file.exists()) {
				camorabitmap = BitmapFactory.decodeFile(picPath);
				mImageView.setImageBitmap(camorabitmap);
			}

			break;
		case 7:

			// 照片的原始资源地址
			if (data != null) {
				Uri originalUri = data.getData();
				picPath = Common.getRealPathFromURI(originalUri,
						EditTransactionActivity.this);
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
						
						if (( transactionHasBillItem >0) || (transactionHasBillRule >0)) {
							recurringLinearLayout.setVisibility(View.GONE);
							recurringTpye = 0;
						} else {
							recurringLinearLayout.setVisibility(View.VISIBLE);
						}
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
					if (( transactionHasBillItem >0) || (transactionHasBillRule >0)) {
						recurringLinearLayout.setVisibility(View.GONE);
						recurringTpye = 0;
					} else {
						recurringLinearLayout.setVisibility(View.VISIBLE);
					}
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
						if (( transactionHasBillItem >0) || (transactionHasBillRule >0)) {
							recurringLinearLayout.setVisibility(View.GONE);
							recurringTpye = 0;
						} else {
							recurringLinearLayout.setVisibility(View.VISIBLE);
						}
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
						recurringTpye = 0;
						categoryButton.setText("-Split-");
						splitCheck = 1;
						amountEditText.setEnabled(false);
						amountEditText.setFocusable(false);
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
					if (( transactionHasBillItem >0) || (transactionHasBillRule >0)) {
						recurringLinearLayout.setVisibility(View.GONE);
						recurringTpye = 0;
					} else {
						recurringLinearLayout.setVisibility(View.VISIBLE);
					}
					amountEditText.setEnabled(true);
					amountEditText.requestFocus();
					amountEditText.setFocusable(true);
					amountEditText.setFocusableInTouchMode(true);
					
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

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
	}
}
