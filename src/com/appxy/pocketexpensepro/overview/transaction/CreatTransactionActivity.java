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
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.KeyboardUtil;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.keyboard.BasicOnKeyboardActionListener;
import com.appxy.pocketexpensepro.keyboard.CustomKeyboardView;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.accounts.AccountDao;
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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class CreatTransactionActivity extends BaseHomeActivity {
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
	private int gCheckedItem = 0;// 选择位置
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
	private AccountsListViewAdapter accountListViewAdapter;

	private int isCleared = 1;

	private AlertDialog mMemoDialog;

	private AlertDialog mPhotoDialog;
	private AlertDialog mPhotoSeDialog;
	private LinearLayout takePhotoButton;
	private LinearLayout pickPhotoButton;
	private LinearLayout deletePhotoButton;
	private LinearLayout viewPhotoButton;
	private static String picPath = "";
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
	private static final int CAMERA_REQUEST = 1888; 
	private Uri imageuri;
	private KeyboardUtil customKeyBoard;
	private  CustomKeyboardView mKeyboardView;
	private Rect mRect = new Rect();
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		picPath = "";
	}
	
	@Override public void onBackPressed() {
	    if( customKeyBoard.isCustomKeyboardVisible() ){
	    	customKeyBoard.hideKeyboard();
	    }else{
	    	this.finish();
	    }
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		final int action = MotionEventCompat.getActionMasked(ev);

		if (customKeyBoard != null && customKeyBoard.isCustomKeyboardVisible()) {
			
			   int[] location = new int[2];
			    mKeyboardView.getLocationOnScreen(location);
			    mRect.left = location[0];
			    mRect.top = location[1];
			    mRect.right = location[0] + mKeyboardView.getWidth();
			    mRect.bottom = location[1] + mKeyboardView.getHeight();

			    int x = (int) ev.getX();
			    int y = (int) ev.getY();

			    if (action == MotionEvent.ACTION_DOWN && !mRect.contains(x, y)) {
			    	customKeyBoard.hideKeyboard();
			    }
			    
		}
	  
		
		return super.dispatchTouchEvent(ev);
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);

		inflater = LayoutInflater.from(CreatTransactionActivity.this);
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
		
		
		if (picPath != null && picPath.length() > 0) {
	       
			if (camorabitmap != null) {
				  camorabitmap.recycle();
			}
			camorabitmap = MEntity.decodeSampledBitmapFromResource(picPath, 128, 200);
			mImageView.setImageBitmap(camorabitmap);
		}
		
		memoEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		
		payeeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);
				mCursor = TransactionDao.selectPayee(
						CreatTransactionActivity.this, MEntity.sqliteEscape(s.toString()));
				autoListAdapter = new AutoListAdapter(
						CreatTransactionActivity.this, mCursor, true);
				payeeEditText.setAdapter(autoListAdapter);

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

		List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
				CreatTransactionActivity.this, mCategoryType);
		
		if (mDataList != null) {
			filterData(mDataList);
		}

		payeeEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (customKeyBoard.isCustomKeyboardVisible()) {
					customKeyBoard.hideKeyboard();
				}
			}
		});
		
		payeeEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			        imm.hideSoftInputFromWindow(amountEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
				}
				    
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
				categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category"));
				
				int cType = cursor.getInt(cursor
						.getColumnIndexOrThrow("categoryType"));
				
				String categoryName = cursor.getString(cursor
						.getColumnIndexOrThrow("categoryName"));
				categoryButton.setText(categoryName);
				
				mCategoryType = cType;  // 1 income
						
						List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
								CreatTransactionActivity.this, mCategoryType);
						
						if (mDataList != null) {
							filterData(mDataList);
						}

				
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
					gCheckedItem = locationPositionById(groupDataList,categoryId);
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

		List<Map<String, Object>> mAccountList = AccountDao
				.selectAccount(CreatTransactionActivity.this);
		if (mAccountList != null && mAccountList.size() > 0) {
			accountId = (Integer) mAccountList.get(0).get("_id");
			accountButton.setText((String) mAccountList.get(0).get("accName"));
			isCleared = (Integer) mAccountList.get(0).get("autoClear");
		}

		
		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(CreatTransactionActivity.this,
						R.array.on_off,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clearSpinner.setAdapter(adapterSpinner);
		
		if (isCleared == 1) {
			clearSpinner.setSelection(0);
		}else {
			clearSpinner.setSelection(1);
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
				.createFromResource(CreatTransactionActivity.this,
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

		
		amountEditText.setText("0");
//		amountEditText.setInputType(InputType.TYPE_NULL); 
		if (amountEditText != null) {
   		 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   		 imm.hideSoftInputFromWindow(amountEditText.getWindowToken(), 0);
		}
		
		
//		Keyboard mKeyboard = new Keyboard(this, R.xml.keycontent);
		mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
//		mKeyboardView.setKeyboard(mKeyboard);
//		mKeyboardView.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(
//						this));
//				 View view = getWindow().peekDecorView();
//		          if (view != null) {
//		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
//		         }
//				
//				showKeyboardWithAnimation();
				
				        
		
		customKeyBoard = new KeyboardUtil(CreatTransactionActivity.this, CreatTransactionActivity.this, amountEditText); 
//		amountEditText.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				 EditText edittext = (EditText) v;
//			        int inType = edittext.getInputType();       // Backup the input type
//			        edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
//			        edittext.setInputType(inType);              // Restore input type
//		        
//			        View view = getWindow().peekDecorView();
//			          if (view != null) {
//			            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			            inputmanger.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//			         }
//			  
//				if (customKeyBoard != null) {
//					
//					if (!customKeyBoard.isCustomKeyboardVisible()) {
//						 customKeyBoard.showKeyboard();
//					}
//				}
//				
//			}
//		});
		
		amountEditText.setInputType(0);
		amountEditText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
//				  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);    
//			      //得到InputMethodManager的实例  
//			      if (imm.isActive()) {  
//			      //如果开启  
//			      imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);   
//			      //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的  
//			      }  
			      
//				 View view = getWindow().peekDecorView();
//		          if (view != null) {
//		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
//		         }
		          getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		          EditText edittext = (EditText) v;
			        int inType = edittext.getInputType();       // Backup the input type
			        edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
			        edittext.onTouchEvent(event);               // Call native handler
			        edittext.setInputType(inType);              // Restore input type
			        
			        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			        imm.hideSoftInputFromWindow(amountEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			        
				if (customKeyBoard != null) {
					
					if (!customKeyBoard.isCustomKeyboardVisible()) {
						 customKeyBoard.showKeyboard();
					}
					
				}
				
				
				
				return true;
			}
		});
		
		amountEditText.setInputType(InputType.TYPE_NULL); 
		amountEditText.setInputType( amountEditText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
		
		amountEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					
					if (customKeyBoard != null && !customKeyBoard.isCustomKeyboardVisible()) {
						customKeyBoard.showKeyboard(); 
					}
					
				}else {
					if (customKeyBoard != null && customKeyBoard.isCustomKeyboardVisible()) {
						customKeyBoard.hideKeyboard();
					}
					
				}
				
			}
		});
		
		
//		amountEditText.setText("0.00");
//		amountEditText.setSelection(4);
//		amountEditText.addTextChangedListener(new TextWatcher() { // 设置保留两位小数
//					private boolean isChanged = false;
//
//					@Override
//					public void onTextChanged(CharSequence s, int start,
//							int before, int count) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void beforeTextChanged(CharSequence s, int start,
//							int count, int after) {
//						// TODO Auto-generated method stub
//					}
//
//					@Override
//					public void afterTextChanged(Editable s) {
//						// TODO Auto-generated method stub
//
//						if (isChanged) {// ----->如果字符未改变则返回
//							return;
//						}
//						String str = s.toString();
//
//						isChanged = true;
//						String cuttedStr = str;
//						/* 删除字符串中的dot */
//						for (int i = str.length() - 1; i >= 0; i--) {
//							char c = str.charAt(i);
//							if ('.' == c) {
//								cuttedStr = str.substring(0, i)
//										+ str.substring(i + 1);
//								break;
//							}
//						}
//						/* 删除前面多余的0 */
//						int NUM = cuttedStr.length();
//						int zeroIndex = -1;
//						for (int i = 0; i < NUM - 2; i++) {
//							char c = cuttedStr.charAt(i);
//							if (c != '0') {
//								zeroIndex = i;
//								break;
//							} else if (i == NUM - 3) {
//								zeroIndex = i;
//								break;
//							}
//						}
//						if (zeroIndex != -1) {
//							cuttedStr = cuttedStr.substring(zeroIndex);
//						}
//						/* 不足3位补0 */
//						if (cuttedStr.length() < 3) {
//							cuttedStr = "0" + cuttedStr;
//						}
//						/* 加上dot，以显示小数点后两位 */
//						cuttedStr = cuttedStr.substring(0,
//								cuttedStr.length() - 2)
//								+ "."
//								+ cuttedStr.substring(cuttedStr.length() - 2);
//
//						amountEditText.setText(cuttedStr);
//						amountString = amountEditText.getText().toString();
//						amountEditText.setSelection(cuttedStr.length());
//						isChanged = false;
//					}
//				});

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(MainActivity.selectedDate);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
	}

	private final View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:

				amountString = amountEditText.getText().toString();
				try {
					amountDouble = Double.parseDouble(amountString);
				} catch (NumberFormatException e) {
					amountDouble = 0.00;
				}
				
				String payeeString = payeeEditText.getText().toString();
				
				if (amountDouble == 0) {

					new AlertDialog.Builder(CreatTransactionActivity.this)
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

					new AlertDialog.Builder(CreatTransactionActivity.this)
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

					new AlertDialog.Builder(CreatTransactionActivity.this)
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
				} else {

					long rId = 0;
					
					if (payeeString != null && payeeString.trim().length() != 0 && !payeeString.trim().equals("")) {

						boolean check = judgMentPayee(payeeString,categoryId );
						
						if (!check) {
							long row = PayeeDao.insertPayee(
									CreatTransactionActivity.this, payeeString,
									new String(), categoryId,mDbxAcctMgr, mDatastore);
							if (row > 0) {
								payeeId = (int) row;
							}
						}
					}else{
						payeeId = 0;
					}

					final String memoString = memoEditText.getText().toString();
					// context, amount, dateTime, isClear, notes, photoName,
					// recurringType, category, childTransactions,
					// expenseAccount , incomeAccount, parTransaction, payee)

					if (mReturnList != null && mReturnList.size() > 1) { //category splite情况

						long row = TransactionDao.insertTransactionAll(
								CreatTransactionActivity.this, amountString,
								dateLong, isCleared, memoString, picPath, 0,
								categoryDefault, childTransactionsDefault + "",
								accountId, incomeAccountDefault, -1, payeeId, new String(), 0, 0 , mDbxAcctMgr, mDatastore); // -1标识其本身为父本,split的父本只会是expense，因为所以的子类只能选择expense

						rId = row;
						String idList = "";

						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							int cId = (Integer) iMap.get("categoryId");
							long id = TransactionDao.insertTransactionAll(
									CreatTransactionActivity.this, amount,
									dateLong, isCleared, memoString, picPath,
									0, cId, 1 + "", accountId,
									incomeAccountDefault, (int) row, payeeId, new String(), 0, 0 , mDbxAcctMgr, mDatastore);
							idList = idList + id + ",";
						}

						long rid = TransactionDao.updateParTransactionChild(
								CreatTransactionActivity.this, row, idList);

					} else {
						
						List<Map<String, Object>> mCategoryList = TransactionDao.selectCategoryAll(CreatTransactionActivity.this);
						final int categoryType = judgeCategoryType(mCategoryList,categoryId);
						
						{
							
							if (categoryType == 0) {
								long row = TransactionDao
										.insertTransactionAll(CreatTransactionActivity.this,amountString, (dateLong+MEntity.getHMSMill()), isCleared,
												memoString, picPath, recurringTpye,
												categoryId,
												childTransactionsDefault + "",
												accountId, incomeAccountDefault, 0,
												payeeId, null, 0, 0 , mDbxAcctMgr, mDatastore);
								rId = row;
							} else {
								long row = TransactionDao.insertTransactionAll(
										CreatTransactionActivity.this,
										amountString, (dateLong+MEntity.getHMSMill()), isCleared,
										memoString, picPath, recurringTpye,
										categoryId, childTransactionsDefault + "",
										expenseAccountDefault, accountId, 0,
										payeeId, null, 0, 0 , mDbxAcctMgr, mDatastore);
								rId = row;
							}
							
						}
						
					}

					Intent intent = new Intent();
					intent.putExtra("done", rId);
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
											CreatTransactionActivity.this, 0);
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
											CreatTransactionActivity.this, 1);
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
							CreatTransactionActivity.this);
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
								.selectCategory(CreatTransactionActivity.this,
										0);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.setAdapterData(
								groupDataList, childrenAllDataList);

						int groupCount = groupDataList.size();

						mDialogExpandableListViewAdapter.notifyDataSetChanged();
						
						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);
						mDialogExpandableListViewAdapter.setSelectedPosition(
								gCheckedItem, cCheckedItem);
						
						mExpandableListView.setSelectedChild(gCheckedItem,
								cCheckedItem, true);
						
						
					} else if (mCategoryType == 1) {
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(CreatTransactionActivity.this,
										1);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.setAdapterData(
								groupDataList, childrenAllDataList);
						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();
						
						mExpandableListView.setSelectedChild(gCheckedItem,
								cCheckedItem, true);
						
						mDialogExpandableListViewAdapter.setSelectedPosition(
								gCheckedItem, cCheckedItem);

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
							CreatTransactionActivity.this);
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
											CreatTransactionActivity.this,
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
					intent.setClass(CreatTransactionActivity.this,
							EditSplitActivity.class);
					startActivityForResult(intent, 10);
				}

				break;
			case R.id.date_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(CreatTransactionActivity.this,
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
						CreatTransactionActivity.this);

				final List<Map<String, Object>> mAccountList = AccountDao
						.selectAccount(CreatTransactionActivity.this);
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
								
								isCleared = (Integer) mAccountList.get(arg2).get("autoClear");
								
								if (isCleared == 1) {
									clearSpinner.setSelection(0);
								}else {
									clearSpinner.setSelection(1);
								}
								
								mAccountDialog.dismiss();
							}
						});

				AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(
						CreatTransactionActivity.this);
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
							Log.v("mtag","picpath设置为空");
							mPhotoSeDialog.dismiss();
						}
					});

					viewPhotoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.putExtra("picPath", picPath);
							intent.setClass(CreatTransactionActivity.this,
									ViewPhotoActivity.class);
							startActivity(intent);
							mPhotoSeDialog.dismiss();
						}
					});

					AlertDialog.Builder mBuilder9 = new AlertDialog.Builder(
							CreatTransactionActivity.this);
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
									System.out.println(e + "Folder Exception");
								}
							}

							String picName = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date());

							String filepath = Environment.getExternalStorageDirectory()+ "/PocketExpense/Images/"+ picName+ ".jpg";
							final File file = new File(filepath);
						    imageuri = Uri.fromFile(file);
							
							
							picPath = filepath;
							Log.v("mtag","创建的 picPath"+picPath);
							Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(
									android.provider.MediaStore.EXTRA_OUTPUT,
									imageuri);
							startActivityForResult(intent, CAMERA_REQUEST);
							mPhotoDialog.dismiss();
						}
					});

					pickPhotoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent openAlbumIntent = new Intent(Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							openAlbumIntent.setType("image/*");
							startActivityForResult(openAlbumIntent, 7);
							mPhotoDialog.dismiss();
						}
					});

					AlertDialog.Builder mBuilder4 = new AlertDialog.Builder(
							CreatTransactionActivity.this);
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
	
	// judege payee
	
    public boolean judgMentPayee(String pName,int category){
		
		boolean tag = false;
		List<Map<String, Object>> mList = PayeeDao.checkPayeeByNameAndCaegory(CreatTransactionActivity.this, pName, category);
		if (mList.size() > 0) {
			tag = true;
			payeeId = (Integer)mList.get(0).get("_id");
		}
		return tag;
	}

	private boolean checkPayee(String pName) {
		boolean check = false;
		List<Map<String, Object>> mList = TransactionDao
				.selectPayee(CreatTransactionActivity.this);

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
		case CAMERA_REQUEST:
			
			if (resultCode == RESULT_OK) {
				
			File file = new File(picPath);
			if (file.exists()) {
				
				if (camorabitmap != null) {
					  camorabitmap.recycle();
				}
				camorabitmap = MEntity.decodeSampledBitmapFromResource(picPath, 128, 200);
				mImageView.setImageBitmap(camorabitmap);
			}
			
			}

			break;
		case 7:

			// 照片的原始资源地址
			if (data != null && resultCode == RESULT_OK) {
				
				Uri originalUri = data.getData();
				picPath = Common.getRealPathFromURI(originalUri,CreatTransactionActivity.this);
				if (camorabitmap != null) {
					  camorabitmap.recycle();
				}
				camorabitmap = MEntity.decodeSampledBitmapFromResource(picPath, 128, 200);
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
						payeeEditText.setFocusable(true);
						amountEditText.setEnabled(false);
						amountEditText.setFocusable(false);
						amountEditText.setFocusableInTouchMode(false);
						BigDecimal b1 = new BigDecimal("0");
						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							BigDecimal b2 = new BigDecimal(amount);
							b1 = b1.add(b2);
						}
						amountString = b1.doubleValue() + "";
						amountEditText.setText(MEntity.doubl2str(amountString));
					}
				} else {
					splitCheck = 0;
					recurringLinearLayout.setVisibility(View.VISIBLE);
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
						payeeEditText.setFocusable(true);
						amountEditText.setEnabled(false);
						amountEditText.setFocusable(false);
						amountEditText.setFocusableInTouchMode(false);
						
						splitCheck = 1;
						BigDecimal b1 = new BigDecimal("0");
						for (Map<String, Object> iMap : mReturnList) {
							String amount = (String) iMap.get("amount");
							BigDecimal b2 = new BigDecimal(amount);
							b1 = b1.add(b2);
						}
						amountString = b1.doubleValue() + "";
						amountEditText.setText(MEntity.doubl2str(amountString));
					}

				} else {
					splitCheck = 0;
					recurringLinearLayout.setVisibility(View.VISIBLE);
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

	
//	@Override
//	public Object onRetainCustomNonConfigurationInstance() {
//		// TODO Auto-generated method stub
//		final String myPath = picPath;
//		Log.v("mtag", "被强制横屏");
//		return myPath;
//	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
	}
	
	
}
