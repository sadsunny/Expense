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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.keyboard.CustomKeyboardView;
import com.appxy.pocketexpensepro.overview.transaction.ViewPhotoActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.overview.transaction.AutoListAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.overview.transaction.SplitCategoryActivity;
import com.appxy.pocketexpensepro.overview.transaction.EditSplitActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.KeyboardUtil;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.dropbox.sync.android.DbxRecord;

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
import android.graphics.Rect;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class AccountTransferActivity extends BaseHomeActivity {
	private LayoutInflater inflater;

	private AutoCompleteTextView payeeEditText;
	private EditText amountEditText;
	private Button dateButton;
	private Button toButton;
	private Button fromButton;
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
	private AlertDialog mAccountDialog;
	private int accountCheckItem = 0;

	private int isCleared = 0;

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
	private int clearCheck = 1; // default on 1
	private int recurringTpye = 0; // default on 1
	private Bitmap camorabitmap;
	private LinearLayout recurringLinearLayout;

	private int splitCheck;

	private ChooseListApdater accountListViewAdapter;
	private List<Map<String, Object>> mAccountList;
	private ListView accountListViewFrom;
	private ListView accountListViewTo;
	private int accountCheckItemFrom = 0;
	private int accountCheckItemTo = 0;

	private AlertDialog mAccountDialogFrom;
	private AlertDialog mAccountDialogTo;
	private int fromId;
	private int toId;
	private AutoListAdapter autoListAdapter;
	private Cursor mCursor;
	private CharSequence sKey;
	private KeyboardUtil customKeyBoard;
	
	private  CustomKeyboardView mKeyboardView;
	private Rect mRect = new Rect();
	
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
		setContentView(R.layout.activity_account_transfer);

		inflater = LayoutInflater.from(AccountTransferActivity.this);
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

		mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
		
		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		mImageView = (ImageView) findViewById(R.id.imageView1);
		payeeEditText = (AutoCompleteTextView) findViewById(R.id.payee_edit);
		amountEditText = (EditText) findViewById(R.id.amount_edit);
		dateButton = (Button) findViewById(R.id.date_btn);
		fromButton = (Button) findViewById(R.id.from_btn);
		toButton = (Button) findViewById(R.id.to_btn);
		recurringSpin = (Spinner) findViewById(R.id.recurring_spin);
		photoButton = (Button) findViewById(R.id.photo_btn);
		clearSpinner = (Spinner) findViewById(R.id.clear_spin); // spinner
		memoEditText = (EditText) findViewById(R.id.memo_edit); // spinner
		recurringLinearLayout = (LinearLayout) findViewById(R.id.recurringLinearLayout);

		dateButton.setOnClickListener(mClickListener);
		toButton.setOnClickListener(mClickListener);
		fromButton.setOnClickListener(mClickListener);
		photoButton.setOnClickListener(mClickListener);
		
		payeeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				payeeEditText.setThreshold(1);
				mCursor = TransactionDao.selectPayee(
						AccountTransferActivity.this, MEntity.sqliteEscape(s.toString()));
				autoListAdapter = new AutoListAdapter(
						AccountTransferActivity.this, mCursor, true);
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

		mAccountList = AccountDao.selectAccount(AccountTransferActivity.this);

		List<Map<String, Object>> mAccountList = AccountDao
				.selectAccount(AccountTransferActivity.this);
		if (mAccountList != null && mAccountList.size() > 0) {
			fromId = (Integer) mAccountList.get(0).get("_id");
			fromButton.setText((String) mAccountList.get(0).get("accName"));

			toId = (Integer) mAccountList.get(0).get("_id");
			toButton.setText((String) mAccountList.get(0).get("accName"));
		}

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(AccountTransferActivity.this,
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
				.createFromResource(AccountTransferActivity.this,
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
//		mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
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
				
				        
		
		customKeyBoard = new KeyboardUtil(AccountTransferActivity.this, AccountTransferActivity.this, amountEditText); 
		amountEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 EditText edittext = (EditText) v;
			        int inType = edittext.getInputType();       // Backup the input type
			        edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
			        edittext.setInputType(inType);              // Restore input type
		        
			        View view = getWindow().peekDecorView();
			          if (view != null) {
			            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			            inputmanger.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			         }
			  
				if (customKeyBoard != null) {
					
					if (!customKeyBoard.isCustomKeyboardVisible()) {
						 customKeyBoard.showKeyboard();
					}
				}
				
			}
		});
		
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

				amountString = amountEditText.getText().toString();
				try {
					amountDouble = Double.parseDouble(amountString);
				} catch (NumberFormatException e) {
					amountDouble = 0.00;
				}
				String payeeString = payeeEditText.getText().toString();
				if (amountDouble == 0) {

					new AlertDialog.Builder(AccountTransferActivity.this)
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

				} else if (fromId == 0) {

					new AlertDialog.Builder(AccountTransferActivity.this)
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

				} else if (toId == 0) {

					new AlertDialog.Builder(AccountTransferActivity.this)
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
				}else {

					if (payeeString != null && payeeString.trim().length() != 0
							&& !payeeString.trim().equals("")) {

						boolean check = judgMentPayee(payeeString,categoryId );
						
						if (!check) {
							long row = PayeeDao.insertPayee(
									AccountTransferActivity.this, payeeString,
									new String(), 27,mDbxAcctMgr, mDatastore);//设置默认的category
							if (row > 0) {
								payeeId = (int) row;
							}
						}
					}else{
						payeeId = 0;
					}

					String memoString = memoEditText.getText().toString();

					// context, amount, dateTime, isClear, notes, photoName, recurringType, category, childTransactions, expenseAccount , incomeAccount, parTransaction, payee)
					 if(fromId != toId){
						 long row = TransactionDao.insertTransactionAll( AccountTransferActivity.this,amountString,dateLong,isCleared, memoString, picPath, recurringTpye, 0, 0+"", fromId ,toId , 0 ,payeeId,new String(), 0, 0 , mDbxAcctMgr, mDatastore);
						    Intent intent = new Intent();
							intent.putExtra("_id", row);
							setResult(11, intent);
					 }
					
					finish();
				}

				break;

//			case R.id.add_payee_btn:
//
//				View view = inflater.inflate(R.layout.dialog_choose_type, null);
//				payeeListView = (ListView) view.findViewById(R.id.mListView);
//				payeeListViewAdapter = new PayeeListViewAdapter(
//						AccountTransferActivity.this);
//
//				final List<Map<String, Object>> mList = TransactionDao
//						.selectPayee(AccountTransferActivity.this);
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
//						AccountTransferActivity.this);
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

			case R.id.from_btn:

				View view1 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				accountListViewFrom = (ListView) view1
						.findViewById(R.id.mListView);
				accountListViewAdapter = new ChooseListApdater(
						AccountTransferActivity.this);

				accountListViewFrom.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				accountListViewFrom.setAdapter(accountListViewAdapter);
				accountListViewFrom
						.setSelection((accountCheckItemFrom - 1) > 0 ? (accountCheckItemFrom - 1)
								: 0);
				accountListViewAdapter.setItemChecked(accountCheckItemFrom);
				accountListViewAdapter.setAdapterDate(mAccountList);
				accountListViewAdapter.notifyDataSetChanged();
				accountListViewFrom
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								accountCheckItemFrom = arg2;
								accountListViewAdapter
										.setItemChecked(accountCheckItemFrom);
								accountListViewAdapter.notifyDataSetChanged();
								String nameString = (String) mAccountList.get(
										arg2).get("accName");
								fromButton.setText(nameString);

								fromId = (Integer) mAccountList.get(arg2).get(
										"_id");
								mAccountDialogFrom.dismiss();
							}
						});

				AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(
						AccountTransferActivity.this);
				mBuilder1.setTitle("From Account");
				mBuilder1.setView(view1);
				mBuilder1.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mAccountDialogFrom = mBuilder1.create();
				mAccountDialogFrom.show();

				break;
			case R.id.date_btn:

				DatePickerDialog DPD = new DatePickerDialog( // 改变theme
						new ContextThemeWrapper(AccountTransferActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Date");
				DPD.show();

				break;
			case R.id.to_btn:

				View view2 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				accountListViewTo = (ListView) view2
						.findViewById(R.id.mListView);
				accountListViewAdapter = new ChooseListApdater(
						AccountTransferActivity.this);

				accountListViewTo.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				accountListViewTo.setAdapter(accountListViewAdapter);
				accountListViewTo
						.setSelection((accountCheckItemTo - 1) > 0 ? (accountCheckItemTo - 1)
								: 0);
				accountListViewAdapter.setItemChecked(accountCheckItemTo);
				accountListViewAdapter.setAdapterDate(mAccountList);
				accountListViewAdapter.notifyDataSetChanged();
				accountListViewTo
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								accountCheckItemTo = arg2;
								accountListViewAdapter
										.setItemChecked(accountCheckItemTo);
								accountListViewAdapter.notifyDataSetChanged();
								String nameString = (String) mAccountList.get(
										arg2).get("accName");
								toButton.setText(nameString);

								toId = (Integer) mAccountList.get(arg2).get(
										"_id");
								mAccountDialogTo.dismiss();
							}
						});

				AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(
						AccountTransferActivity.this);
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

				mAccountDialogTo = mBuilder2.create();
				mAccountDialogTo.show();

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
							intent.setClass(AccountTransferActivity.this, ViewPhotoActivity.class);
							startActivity(intent);
							mPhotoSeDialog.dismiss();
						}
					});

					AlertDialog.Builder mBuilder9 = new AlertDialog.Builder(
							AccountTransferActivity.this);
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
							AccountTransferActivity.this);
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

public boolean judgMentPayee(String pName,int category){
		
		boolean tag = false;
		List<Map<String, Object>> mList = PayeeDao.checkPayeeByNameAndCaegory(AccountTransferActivity.this, pName, category);
		if (mList.size() > 0) {
			tag = true;
			payeeId = (Integer)mList.get(0).get("_id");
		}
		return tag;
	}

	private boolean checkPayee(String pName) {
		boolean check = false;
		List<Map<String, Object>> mList = TransactionDao
				.selectPayee(AccountTransferActivity.this);

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
					mImageView.setImageBitmap(camorabitmap);
				}
			break;
		case 7:

			// 照片的原始资源地址
			if (data != null) {
				Uri originalUri = data.getData();
				picPath = Common.getRealPathFromURI(originalUri,
						AccountTransferActivity.this);
				camorabitmap = BitmapFactory.decodeFile(picPath);
				mImageView.setImageBitmap(camorabitmap);
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
