package com.appxy.pocketexpensepro.overview;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
import com.appxy.pocketexpensepro.CircleView;
import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.RoundProgressBar;
import com.appxy.pocketexpensepro.TransactionRecurringCheck;
import com.appxy.pocketexpensepro.accounts.AccountActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransferActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnBackTimeListener;
import com.appxy.pocketexpensepro.expinterface.OnChangeStateListener;
import com.appxy.pocketexpensepro.expinterface.OnRefreshADS;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateWeekSelectListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;
import com.appxy.pocketexpensepro.expinterface.ReturnFragmentListenter;
import com.appxy.pocketexpensepro.expinterface.TellMainBuyPro;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.overview.budgets.EditBudgetActivity;
import com.appxy.pocketexpensepro.overview.related.RelatedActivity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.reports.ReCashListActivity;
import com.appxy.pocketexpensepro.search.SearchActivity;
import com.appxy.pocketexpensepro.setting.SettingActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.appxy.pocketexpensepro.util.IabHelper;
import com.appxy.pocketexpensepro.util.IabResult;
import com.appxy.pocketexpensepro.util.Inventory;
import com.appxy.pocketexpensepro.util.Purchase;
import com.appxy.pocketexpensepro.util.SkuDetails;
import com.appxy.pocketexpensepro.R;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AccelerateInterpolator;

public class OverviewFragment extends Fragment implements
		OnChangeStateListener, OnUpdateListListener, OnRefreshADS, OnSyncFinishedListener {

	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private static final int MSG_FAILURE = 0;
	private static final int MSG_SUCCESS = 1;
	private String currecyLable ;

	private ViewPager mViewPager;

	private FragmentActivity mActivity;
	public  ViewPagerAdapter mViewPagerAdapter;
	private WeekFragment weekFragment;
	private ListView mListView;
	private ArrayList<HashMap<String, Object>> mDataList;
	private ListViewAdapter mListViewAdapter;
	private Thread mThread;
	private long selectedDate;
	private int viewPagerPosition;
	private RelativeLayout weekLayout;
	private OnBackTimeListener onBackTimeListener;

	public static SparseArray<Fragment> registeredFragments;

	private LinearLayout budgetRelativeLayout;
	private TextView leftTextView;
	private FixedSpeedScroller mScroller;

	private double budgetAmount;
	private double transactionAmount;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;
	private long argumentsDate;
	private int currentPosition;

	private ImageView addImageView;
	private LinearLayout accountRelativeLayout;

	private OnUpdateWeekSelectListener onUpdateWeekSelectListener;
	private OnUpdateNavigationListener onUpdateNavigationListener;
	public static MenuItem item;
	private Button addView;
	private RoundProgressBar mProgressBar;

	private SharedPreferences mPreferences;
	private int BdgetSetting;
	private TextView left_label;
	private TextView currency_label1;
	private TextView currency_label2;
	private TextView networthTextView;
	private View adsLine;
	private List<Map<String, Object>> mBudgetList;
	private RelativeLayout adsLayout;
	private Button adsButton;
	private TextView notiTextView;
	private TellMainBuyPro tellMainBuyPro;
	
	private Typeface typeFace ;
	
	private double networthDoubel = 0;
	/*
	 * 
	 */
	private IabHelper mHelper;
	private boolean iap_is_ok = false;
	private IInAppBillingService billingservice;
	private static final String PREFS_NAME = "SAVE_INFO";
	public static final String Paid_Id_VF = "upgrade";
	static final String TAG = "Expense";
	static final int RC_REQUEST = 10001;
	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi803lugKTJdERpN++BDhRYY5hr0CpTsuj+g3fIZGBLn+LkZ+me0it3lP375tXqMlL0NLNlasu9vWli3QkCFBbERf+KysqUCsrqqcoq3hUini6LSiKkyuISM2Y4gWUqSVT+vkLP4psshnwJTbF6ii2jZfXFxLVoT5P30+y4rgCwncgRsX14x2bCpJlEdxrNfoxL4EqlHAt9/9vsc0PoW8QH/ChKJFkTDOsB9/42aur4zF9ua568ny1K6vlE/lnkffBP6DvsHFrIdpctRyUdrBVnUyMl+1k2ufUHJudfeGpKuExLcNOxuryCTolIFj44dB2TugNFzQwOE4xoRyCfJ7bQIDAQAB";
	private ArrayList<String> sku_list;
	private ArrayList<String> price_list;
	/*
	 * 
	 */
	
	public OverviewFragment() {

	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				
				if (mDataList != null && mDataList.size() > 0 ) {
					
					notiTextView.setVisibility(View.INVISIBLE);
					mListView.setVisibility(View.VISIBLE);
					
					mListViewAdapter.setAdapterDate(mDataList);
					mListViewAdapter.notifyDataSetChanged();
					
				}else{
					notiTextView.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.INVISIBLE);
				}
					

				if (BdgetSetting == 0) {
					left_label.setText("LEFT");
					leftTextView.setText(MEntity
							.doublepoint2str((budgetAmount - transactionAmount)
									+ ""));
				} else {
					left_label.setText("SPENT");
					leftTextView.setText(MEntity
							.doublepoint2str((transactionAmount) + ""));
				}


				networthTextView.setText(MEntity.doublepoint2str(String
						.valueOf(networthDoubel)));

				if ((mBudgetList == null) || mBudgetList.size() == 0) {
					mProgressBar.setProgress(0);
					mProgressBar.setMax((int) 1);
					mProgressBar.setSecondaryProgress((int) 100);
				} else {
					mProgressBar.setProgress((int) (transactionAmount * 0.8));
					mProgressBar.setMax((int) budgetAmount);
					mProgressBar
							.setSecondaryProgress((int) (budgetAmount * 10));
				}

				if ((budgetAmount - transactionAmount) < 0) {
					mProgressBar.setPaintColor(Color.rgb(246, 48, 48));
				} else {
					mProgressBar.setPaintColor(Color.rgb(12, 164, 227));
				}

				
				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	
	private Handler iapHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				adsButton.setText(price_list.get(0));
				break;
			}
		};
	};
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (FragmentActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		currecyLable = Common.CURRENCY_SIGN[Common.CURRENCY];
		typeFace = Typeface.createFromAsset(mActivity.getAssets(),"fonts/ROBOTO-REGULAR.TTF");
				
		onBackTimeListener = (OnBackTimeListener) mActivity;
		onUpdateNavigationListener = (OnUpdateNavigationListener) mActivity;
		mViewPagerAdapter = new ViewPagerAdapter(mActivity.getSupportFragmentManager());

		mPreferences = mActivity.getSharedPreferences("Expense",mActivity.MODE_PRIVATE); 
		BdgetSetting = mPreferences.getInt("BdgetSetting", 0);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			argumentsDate = bundle.getLong("selectedDate");
		}
		Calendar todaCalendar = Calendar.getInstance();
		int mOffset = MEntity.getWeekOffsetByDay(argumentsDate,
				todaCalendar.getTimeInMillis());
		currentPosition = MID_VALUE + mOffset;
		selectedDate = argumentsDate;

	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_overview, container,
				false);
		mInflater = inflater;
		
		mProgressBar = (RoundProgressBar) view.findViewById(R.id.roundBar);
		currency_label1 = (TextView) view.findViewById(R.id.currency_label1);
		currency_label2 = (TextView) view.findViewById(R.id.currency_label2);
		notiTextView = (TextView) view.findViewById(R.id.notice_txt);
		currency_label1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currency_label2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		networthTextView = (TextView) view.findViewById(R.id.net_amount);
		budgetRelativeLayout = (LinearLayout) view.findViewById(R.id.budget_relativeLayout);
		leftTextView = (TextView) view.findViewById(R.id.left_amount);
		left_label = (TextView) view.findViewById(R.id.left_label);
		addView = (Button) view.findViewById(R.id.add_btn);
		adsLine = (View) view.findViewById(R.id.ads_line);
		
		sku_list = new ArrayList<String>();
		price_list = new ArrayList<String>();
		sku_list.add(Paid_Id_VF);
		price_list.add("N/A");
		
		 adsLayout = (RelativeLayout) view.findViewById(R.id.ads_layout);
		 adsButton = (Button) view.findViewById(R.id.ads_button);
		 
		    if (Common.mIsPaid) {
				adsLayout.setVisibility(View.GONE);
				adsLine.setVisibility(View.GONE);
			} else {
				 adsLayout.setVisibility(View.VISIBLE);
				 adsLine.setVisibility(View.VISIBLE);
				 try {
					
				 mHelper = new IabHelper(mActivity, base64EncodedPublicKey);
				 mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
					
					@Override
					public void onIabSetupFinished(IabResult result) {
						// TODO Auto-generated method stub
						if (!result.isSuccess()) {
		                   // Oh noes, there was a problem.
		                   return;
		               }
						
						 if (mHelper == null) return;
						 iap_is_ok = true;
						 
						 new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									getPrice();
								}
							}).start();
						 
						try {
							mHelper.queryInventoryAsync(mGotInventoryListener);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				 } catch (Exception e) {
						// TODO: handle exception
					}
				 
			}
		    
		    adsLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub
					
					if (iap_is_ok && mHelper != null) {
						mHelper.flagEndAsync();
						String payload = "";
						mHelper.launchPurchaseFlow(mActivity, Paid_Id_VF, RC_REQUEST, mPurchaseFinishedListener);
					}
					
				}
			});
			
		
		 
		accountRelativeLayout = (LinearLayout) view.findViewById(R.id.relativeLayout2);

		accountRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), AccountActivity.class);
				startActivityForResult(intent, 14);

			}
		});

		addView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
						
				List<Map<String, Object>> mAccountList1 = AccountDao
						.selectAccount(mActivity);
				if (mAccountList1.size() == 0) {

					new AlertDialog.Builder(mActivity)
							.setTitle("No Accounts! ")
							.setMessage(
									"In order to add a transaction please add an account first! ")
							.setPositiveButton("Add Account",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent();
											intent.setClass(
													getActivity(),
													CreatNewAccountActivity.class);
											startActivityForResult(intent, 6);
											dialog.dismiss();

										}
									}).show();

				} else {

					Intent intent = new Intent();
					intent.setClass(getActivity(),
							CreatTransactionActivity.class);
					startActivityForResult(intent, 6);

				}

			}
		});

		budgetRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), BudgetActivity.class);
				startActivityForResult(intent, 14);

			}
		});

		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(currentPosition);
		viewPagerPosition = currentPosition;

		final OnUpdateNavigationListener onUpdateNavigationListener;
		onUpdateNavigationListener = (OnUpdateNavigationListener) mActivity;
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						viewPagerPosition = position;

						if (position == MID_VALUE) {
							Calendar calendar1 = Calendar.getInstance();
							calendar1.set(Calendar.HOUR_OF_DAY, 0);
							calendar1.set(Calendar.MINUTE, 0);
							calendar1.set(Calendar.SECOND, 0);
							calendar1.set(Calendar.MILLISECOND, 0);
							onUpdateNavigationListener
									.OnUpdateNavigation(calendar1
											.getTimeInMillis());

						} else {
							long theSelectedDate = MEntity
									.getFirstDayByOffset(position - MID_VALUE);
							onUpdateNavigationListener
									.OnUpdateNavigation(theSelectedDate);
						}

					}

					@Override
					public void onPageScrollStateChanged(int state) {
						switch (state) {
						case ViewPager.SCROLL_STATE_IDLE:
							// TODO
							break;
						case ViewPager.SCROLL_STATE_DRAGGING:
							// TODO
							break;
						case ViewPager.SCROLL_STATE_SETTLING:
							// TODO
							break;
						default:
							// TODO
							break;
						}
					}
				});

		mListView = (ListView) view.findViewById(R.id.mListView);
		mListView.setDividerHeight(0);
		mListViewAdapter = new ListViewAdapter(mActivity);
		mListView.setAdapter(mListViewAdapter);
		mListView.setOnItemLongClickListener(longClickListener);
		mListView.setOnItemClickListener(onClickListener);

		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}
		
		return view;
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
 
			mDataList = OverViewDao.selectTransactionByDayTime(mActivity,
					selectedDate);
			reFillData(mDataList);


			long firstDay = MEntity
					.getFirstDayOfMonthMillis(MainActivity.selectedDate);
			long lastDay = MEntity
					.getLastDayOfMonthMillis(MainActivity.selectedDate);
			
			
			mBudgetList = OverViewDao.selectBudget(mActivity);
			
			BigDecimal b0 = new BigDecimal("0");
			BigDecimal bt0 = new BigDecimal("0");
			
			for (Map<String, Object> iMap : mBudgetList) {
				
				String catrgoryName = (String) iMap.get("categoryName");
				String amount = (String) iMap.get("amount");

				BigDecimal b1 = new BigDecimal(amount);
				b0 = b0.add(b1);
				
				List<Map<String, Object>> mTransactionList = OverViewDao
						.selectTransactionSumByCategoryIdAndTime(
								mActivity, catrgoryName, firstDay,
								lastDay);
				
				if (mTransactionList.size() > 0) {
					double sumSpent = (Double) mTransactionList.get(0).get("allAmount");
					BigDecimal bz = new BigDecimal(sumSpent);
					bt0 = bt0.add(bz);
				}

			}
			
			budgetAmount = b0.doubleValue();
			transactionAmount = bt0.doubleValue();

			List<Map<String, Object>> newwothList = AccountDao.selectAccountNetworth(mActivity);
			 if (newwothList!= null && newwothList.size() >0) {
				 networthDoubel = (Double) newwothList.get(0).get("allAmount");
			}else {
				networthDoubel = 0;
			}
			 
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
			
		}
	};
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		loadIsPaid();
		if (Common.mIsPaid && adsLayout != null) {
			adsLayout.setVisibility(View.GONE);
			adsLine.setVisibility(View.GONE);
		}

		BdgetSetting = mPreferences.getInt("BdgetSetting", 0);

		if (BdgetSetting == 0) {
			left_label.setText("LEFT");
			leftTextView.setText(MEntity
					.doublepoint2str((budgetAmount - transactionAmount) + ""));
		} else {
			left_label.setText("SPENT");
			leftTextView.setText(MEntity.doublepoint2str((transactionAmount)
					+ ""));
		}

		if ((budgetAmount - transactionAmount) < 0) {
			mProgressBar.setPaintColor(Color.rgb(246, 48, 48));
		} else {
			mProgressBar.setPaintColor(Color.rgb(12, 164, 227));
		}

		currency_label1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currency_label2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);

		if (MainActivity.sqlChange == 1 || MainActivity.isFirstSync) {
			
			onBackTimeListener.OnBackTime(MainActivity.selectedDate,
					viewPagerPosition);// viewPagerPosition用于判断具体的fragment
			
			if(MainActivity.sqlChange == 1){
				MainActivity.sqlChange = 0;
			}
			
			if (MainActivity.isFirstSync) {
				MainActivity.isFirstSync = false;
			}
			
		}
		
		
		if (Common.CURRENCY_SIGN[Common.CURRENCY] != currecyLable) {
			mListViewAdapter.notifyDataSetChanged();
		}
		Log.e("mtag", "fragment onResume" +  MainActivity.mDbxAcctMgr1);
		
		TransactionRecurringCheck.recurringCheck(mActivity,
				MEntity.getNowMillis(), MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1); //  need find a To find a suitable place
		
		
	}

	private OnItemClickListener onClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {

			// TODO Auto-generated method stub
			final int tId = (Integer) mDataList.get(paramInt).get("_id");
			int expenseAccount = (Integer) (Integer) mDataList.get(paramInt)
					.get("expenseAccount");
			int incomeAccount = (Integer) mDataList.get(paramInt).get(
					"incomeAccount");

			if (expenseAccount > 0 && incomeAccount > 0) {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(mActivity, EditTransferActivity.class);
				startActivityForResult(intent, 13);

			} else {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(mActivity, EditTransactionActivity.class);
				startActivityForResult(intent, 13);

			}
		}
	};

	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int _id = (Integer) mDataList.get(arg2).get("_id");
			final String uuid = (String) mDataList.get(arg2).get("uuid");
			
			final int theCategory = (Integer) mDataList.get(arg2).get("category");
			final int theExpenseAccount = (Integer) mDataList.get(arg2).get("expenseAccount");
			final int theIncomeAccount = (Integer) mDataList.get(arg2).get("incomeAccount");
			
			final Map<String, Object> mMap = mDataList.get(arg2);
			final int parTransaction = (Integer) mDataList.get(arg2).get(
					"parTransaction");

			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = { "Related" ,"Duplicate", "Delete" };
			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(
					mActivity, data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (arg2 == 0) {
						Intent intent = new Intent();
						intent.putExtra("categoryId", theCategory);
						intent.putExtra("expenseAccountId", theExpenseAccount);
						intent.putExtra("incomeAccountId", theIncomeAccount);
						intent.setClass(mActivity, RelatedActivity.class);
						startActivityForResult(intent, 15);
						alertDialog.dismiss();
					}else if (arg2 == 1) {

						Calendar c = Calendar.getInstance(); // 处理为当天固定格式时间
						Date date = new Date(c.getTimeInMillis());
						SimpleDateFormat sdf = new SimpleDateFormat(
								"MM-dd-yyyy");
						try {
							c.setTime(new SimpleDateFormat("MM-dd-yyyy")
									.parse(sdf.format(date)));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String amount = (String) mMap.get("amount");
						long dateTime = c.getTimeInMillis();
						int isClear = (Integer) mMap.get("isClear");
						String notes = (String) mMap.get("notes");
						String photoName = (String) mMap.get("photoName");
						int recurringType = (Integer) mMap.get("recurringType");
						int category = (Integer) mMap.get("category");
						String childTransactions = (String) mMap
								.get("childTransactions");
						int expenseAccount = (Integer) mMap
								.get("expenseAccount");
						int incomeAccount = (Integer) mMap.get("incomeAccount");
						int parTransaction = (Integer) mMap
								.get("parTransaction");
						int payee = (Integer) mMap.get("payee");

						long row = TransactionDao.insertTransactionAll(
								mActivity, amount, dateTime, isClear, notes,
								photoName, recurringType, category,
								childTransactions, expenseAccount,
								incomeAccount, parTransaction, payee, null, 0, 0 , MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						alertDialog.dismiss();

						mHandler.post(mTask);
						onBackTimeListener.OnBackTime(
								MainActivity.selectedDate, viewPagerPosition);// viewPagerPosition用于判断具体的fragment

					} else if (arg2 == 2) {

						long row = AccountDao.deleteTransaction(mActivity, _id, uuid, MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						if (parTransaction == -1) {
							AccountDao.deleteTransactionChild(mActivity, _id , MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
						}
						alertDialog.dismiss();
						mHandler.post(mTask);
						onBackTimeListener.OnBackTime(
								MainActivity.selectedDate, viewPagerPosition);// viewPagerPosition用于判断具体的fragment
					}
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();
			return true;
		}

	};

	public long getMilltoDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return calendar.getTimeInMillis();
		}
		return calendar.getTimeInMillis();
	}
	
	public void reFillData( ArrayList<HashMap<String, Object>> mData) {

		long s = System.currentTimeMillis();
		
		for (Map<String, Object> mMap : mData) {
			
			String payeeName = (String) mMap.get("payeeName");
			int categoryId = (Integer) mMap.get("categoryId");
			if (categoryId <= 0) {
				int expenseAccount = (Integer) mMap.get("expenseAccount");
				int incomeAccount = (Integer) mMap.get("incomeAccount");
				
				if (expenseAccount > 0 && incomeAccount > 0) {
					mMap.put("iconName", 69);
				} else {
					mMap.put("iconName", 56);
				}
				
			}
			
			if (payeeName == null) {
				String memoString = (String) mMap.get("notes");
				if (memoString == null ) {
					mMap.put("payeeName", "--");
				} else {
					
					if (memoString.length() > 0) {
						mMap.put("payeeName", memoString);
					} else {
						mMap.put("payeeName", "--");
					}
					
				}
			} 
		}
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.today_menu, menu);
		item = menu.findItem(R.id.today);

		menu.findItem(R.id.today).setIcon(getTodayIcon());
	}

	private Drawable getTodayIcon() {
		// 初始化画布
		Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.today).copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(originalBitmap);

		int size = MEntity.dip2px(mActivity, 32);
		int px = MEntity.dip2px(mActivity, 14.25f);
		int textSize = MEntity.dip2px(mActivity, 13f);

		Calendar calendar = Calendar.getInstance();
		int contacyCount = calendar.get(Calendar.DAY_OF_MONTH);
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		
		// 应用字体
		countPaint.setTypeface(typeFace);
		
		countPaint.setColor(Color.WHITE);
		countPaint.setTextSize(textSize);
		countPaint.setTextAlign(Paint.Align.CENTER);
		FontMetrics fontMetrics = countPaint.getFontMetrics();
		float ascentY = fontMetrics.ascent;
		float descentY = fontMetrics.descent;

		canvas.drawText(String.valueOf(contacyCount), size / 2, size - px
				- (ascentY + descentY) / 2, countPaint);
		BitmapDrawable bd = new BitmapDrawable(mActivity.getResources(),
				originalBitmap);
		return bd;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.today:
			
			if (MainActivity.selectedDate != MEntity.getNowMillis()) {
				onUpdateNavigationListener.OnUpdateNavigation();
			}
			
			return true;

		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (resultCode) {
		
		case 6:

			if (data != null) {

				onBackTimeListener.OnBackTime(MainActivity.selectedDate,
						viewPagerPosition);
			}
			break;
		case 14:

			if (data != null) {
//				mHandler.post(mTask);
				onBackTimeListener.OnBackTime(MainActivity.selectedDate,
						viewPagerPosition);
			}
			break;

		case 13:

			if (data != null) {

				onBackTimeListener.OnBackTime(MainActivity.selectedDate,
						viewPagerPosition);// viewPagerPosition用于判断具体的fragment
			}
			break;
			
		case 2:

			if (data != null) {

				onBackTimeListener.OnBackTime(MainActivity.selectedDate,
						viewPagerPosition);// viewPagerPosition用于判断具体的fragment
			}
			break;
			
		case 15:

			if (data != null) {

				onBackTimeListener.OnBackTime(MainActivity.selectedDate,
						viewPagerPosition);// viewPagerPosition用于判断具体的fragment
			}
			break;
			
		}
		
		if (mHelper == null) return;
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			Log.v("mtest", "fragment result edn");
			super.onActivityResult(requestCode, resultCode, data);
			 if (requestCode == RC_REQUEST) {     
			      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			      String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
			        
			      if (resultCode == mActivity.RESULT_OK) {
			         try {
			            JSONObject jo = new JSONObject(purchaseData);
			            String sku = jo.getString("productId");
			            alert("Thank you for upgrading to pro! ");
			            adsLayout.setVisibility(View.GONE);
			            adsLine.setVisibility(View.GONE);
			             SharedPreferences sharedPreferences = mActivity.getSharedPreferences(PREFS_NAME,0);   //已经设置密码 
					     SharedPreferences.Editor meditor = sharedPreferences.edit();  
						 meditor.putBoolean("isPaid",true ); 
						 meditor.commit();
			          }
			          catch (JSONException e) {
			             alert("Failed to parse purchase data.");
			             e.printStackTrace();
			          }
			      }
			   }
			 
        }
        else {
        	
        }

	}

	
	//获取价格
			private void getPrice(){
				ArrayList<String> skus = new ArrayList<String>();
				skus.add(Paid_Id_VF);
				billingservice = mHelper.getService();
				
				Bundle querySkus = new Bundle();
			    querySkus.putStringArrayList("ITEM_ID_LIST", skus);
				try {
					Bundle skuDetails = billingservice.getSkuDetails(3, mActivity.getPackageName(),"inapp", querySkus);
					Log.v("mtest", "skuDetails"+skuDetails);
					ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
					Log.v("mtest", "responseList"+responseList);
					if (null!=responseList) {
						for (String thisResponse : responseList) {
				            try {
								SkuDetails d = new SkuDetails(thisResponse);
								
								for (int i = 0; i < sku_list.size(); i++) {
									if (sku_list.get(i).equals(d.getSku())) {
										price_list.set(i, d.getPrice());
									}
								}
								iapHandler.sendEmptyMessage(0);
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				            
				        }
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		 IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
				
				@Override
				public void onQueryInventoryFinished(IabResult result, Inventory inv) {
					// TODO Auto-generated method stub
					 if (mHelper == null) return;
					 
					 if (!result.isSuccess()) {
			                return;
			            }
					 
					 Purchase premiumPurchase = inv.getPurchase(Paid_Id_VF);
					
					 Common.mIsPaid = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
				     SharedPreferences sharedPreferences = mActivity.getSharedPreferences(PREFS_NAME,0);  
				     SharedPreferences.Editor meditor = sharedPreferences.edit();  
					 meditor.putBoolean("isPaid",Common.mIsPaid ); 
					 meditor.commit();
					 
					 if (Common.mIsPaid) {
						adsLayout.setVisibility(View.GONE);
						adsLine.setVisibility(View.GONE);
					}
					 
				}
			};
			
	        void loadIsPaid() { //查询是否支付
	        	
				    SharedPreferences sharedPreferences = mActivity.getSharedPreferences(PREFS_NAME, 0);  
			        Common.mIsPaid = sharedPreferences.getBoolean("isPaid", false);
			}
	        
		    boolean verifyDeveloperPayload(Purchase p) {
		        String payload = p.getDeveloperPayload();
		        return true;
		    }
		    
		   public void alert(String message) {
		        AlertDialog.Builder bld = new AlertDialog.Builder(mActivity);
		        bld.setMessage(message);
		        bld.setNeutralButton("OK", null);
		        bld.create().show();
		    }
//		    void complain(String message) {
//		        Log.e(TAG, "**** Expense Error: " + message);
//		        alert("Error: " + message);
//		    }
		    
		    // Callback for when a purchase is finished
		    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		    	
		    	@Override
		        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

		            // if we were disposed of in the meantime, quit.
		            if (mHelper == null) return;
		   			
		            Log.v("mtest", "*****调用查询queryInventoryAsync*****"+result);
		             mHelper.queryInventoryAsync(mGotInventoryListener);
		            
		            if (!result.isSuccess()) {
		            	Log.v("mtest", "1结果"+result);
//		            	complain("Error purchasing: " + result);
		                return;
		            }
		            if (!verifyDeveloperPayload(purchase)) {
		            	Log.v("mtest", "2结果"+result);
		                return;
		            }

		            if (purchase.getSku().equals(Paid_Id_VF)) {
		                // bought the premium upgrade!
		                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
		            	Log.v("mtest", "3结果"+result);
		                alert("Thank you for upgrading to pro!");
		             Common.mIsPaid =true;
		   		     SharedPreferences sharedPreferences = mActivity.getSharedPreferences(PREFS_NAME,0);   //已经设置密码 
		   		     SharedPreferences.Editor meditor = sharedPreferences.edit();  
		   			 meditor.putBoolean("isPaid",Common.mIsPaid ); 
		   			 meditor.commit();
		   			 adsLayout.setVisibility(View.GONE);
		   			 adsLine.setVisibility(View.GONE);
		            }
		        }

			
		    };

		   @Override
		    public void onDestroy() {
		        super.onDestroy();
		        // very important:
		        
		        if (mHelper != null){
		            try {
		            	mHelper.dispose();
		            }catch (IllegalArgumentException ex){
		                ex.printStackTrace();
		            }finally{}
		        }
		        mHelper = null;
		        
		    }

		    
		    
	@Override
	public void OnChangeState(int state) {
		// TODO Auto-generated method stub
		if (state == 0) {
			weekLayout.setVisibility(View.VISIBLE);
			// calendarLayout.setVisibility(View.GONE);
		} else {
			weekLayout.setVisibility(View.GONE);
			// calendarLayout.setVisibility(View.VISIBLE);
		}

	}

	public String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy, HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}

	@Override
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mHandler.post(mTask);

	}

	@Override
	public void refreshADS() {
		// TODO Auto-generated method stub
		if (Common.mIsPaid && adsLayout != null) {
			adsLayout.setVisibility(View.GONE);
			adsLine.setVisibility(View.GONE);
		}
	}

	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		Toast.makeText(mActivity, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
	}

}
