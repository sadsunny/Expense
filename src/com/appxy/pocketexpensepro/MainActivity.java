package com.appxy.pocketexpensepro;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.accounts.AccountsFragment;
import com.appxy.pocketexpensepro.bills.BillsFragment;
import com.appxy.pocketexpensepro.bills.BillsFragmentMonth;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnActivityToBillListener;
import com.appxy.pocketexpensepro.expinterface.OnBackTimeListener;
import com.appxy.pocketexpensepro.expinterface.OnBillToActivityListener;
import com.appxy.pocketexpensepro.expinterface.OnChangeStateListener;
import com.appxy.pocketexpensepro.expinterface.OnRefreshADS;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.expinterface.OnTellUpdateMonthListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateMonthListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateWeekSelectListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;
import com.appxy.pocketexpensepro.expinterface.ReturnFragmentListenter;
import com.appxy.pocketexpensepro.expinterface.TellMainBuyPro;
import com.appxy.pocketexpensepro.overview.MonthViewPagerAdapter;
import com.appxy.pocketexpensepro.overview.OverViewFragmentMonth;
import com.appxy.pocketexpensepro.overview.OverviewFragment;
import com.appxy.pocketexpensepro.overview.ViewPagerAdapter;
import com.appxy.pocketexpensepro.overview.budgets.BudgetFragment;
import com.appxy.pocketexpensepro.passcode.Activity_Start;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.reports.CashReportFragment;
import com.appxy.pocketexpensepro.reports.CategorysReportFragment;
import com.appxy.pocketexpensepro.reports.ReportCashFragment;
import com.appxy.pocketexpensepro.reports.ReportCategoryFragment;
import com.appxy.pocketexpensepro.reports.ReportOverviewFragment;
import com.appxy.pocketexpensepro.search.SearchActivity;
import com.appxy.pocketexpensepro.service.PastDueService;
import com.appxy.pocketexpensepro.setting.SettingActivity;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.appxy.pocketexpensepro.util.IabHelper;
import com.appxy.pocketexpensepro.util.IabResult;
import com.appxy.pocketexpensepro.util.Inventory;
import com.appxy.pocketexpensepro.util.Purchase;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxRecord;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class MainActivity extends BaseHomeActivity implements
		OnWeekSelectedListener, OnBackTimeListener, OnUpdateNavigationListener,
		OnTellUpdateMonthListener, OnBillToActivityListener, TellMainBuyPro {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private static final String [] mainText= {"Overview","Accounts","Reports","Bills","Budgets"};
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private LinearLayout mLinearLayout;
	private LinearLayout mOverViewLinearLayout;
	private LinearLayout mAccountLinearLayout;
	private LinearLayout mReportLinearLayout;
	private LinearLayout mBillsLinearLayout;
	private LinearLayout mBudgetLinearLayout;
	
	private android.support.v4.app.FragmentManager fragmentManager;
	private OnUpdateListListener onUpdateListListener;
	private OnChangeStateListener onChangeStateListener;
	private OverviewFragment overviewFragment;
	private final static long DAYMILLIS = 86400000L;

	public static long selectedDate;// overview 定位时间
	private OnUpdateWeekSelectListener onUpdateWeekSelectListener;
	public static int mItemPosition = 0;
	private ActionBar actionBar;
	private OverViewNavigationListAdapter overViewNavigationListAdapter;
	public static long selectedMonth;// bill定位时间
	private BillsFragmentMonth billsFragmentMonth;
	
	public static int rangePositon = 0; 
	public static int rangeCashPositon = 0; 
	public static long startDate;
	public static long endDate;
	
	public static long startCashDate;
	public static long endCashDate;
	
	private ArrayList<LinearLayout> layoutArrayList = new ArrayList<LinearLayout>();
    private ProgressDialog mDialog;
	public static int sqlChange = 0;
	public static int sqlChange2 = 2;
	public static int sqlChange3 = 3;
	
	public static int overViewpage = 3;
	
	public static boolean isFirstSync = false ;
	
	 // The helper object
	static final int RC_REQUEST = 10001;
	private IabHelper mHelper;
	public static final String Paid_Id_VF = "upgrade";
	static final String TAG = "Expense";
	private static final String PREFS_NAME = "SAVE_INFO";
	private boolean iap_is_ok = false;
	private OnRefreshADS refreshADS;
	public static Fragment attachFragment;
	public static DbxAccountManager mDbxAcctMgr1;
	public static DbxDatastore mDatastore1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkeVCBLDOb3nw6/HYl+3SrvnxHB1Dlq2ppbQOEvlNdP0LrNi5ra5eDX6slG3zM2AgflC3w3fy7AqG/xUusM183PWj1mDSi29cMCdqYNLttAKN+ywktt3xtVPzOklXVLE9kz5GoflX/xcKBuiocM+nSIePf6MSbH1cMLE88miajA7slNz0s45uETm/bzzUmjRf7JOV6WhLRw+YEnyclhLAYJrjDNmJXXEFS474po8XBkVF4AGXFmdy2oi9UvZCo9mP+PxnOGRr5RMh+W9wA0cWbcOLr5nGGr5NAl7/ZSVR/G7eVX3MmXhkzoyO3cWfhKIT0utxa7jhD0h6YXrXy1r6vwIDAQAB";
		
		List<Map<String, Object>> mList = SettingDao.selectSetting(this);
		Common.CURRENCY = (Integer) mList.get(0).get("currency");
		
		 loadIsPaid();//ispaid
		 //bitcoin address: 1Po29hbdAK1jH3fMKLypRC9SZfWUHBeiMj
		 
		 try {
			 
			 mHelper = new IabHelper(this, base64EncodedPublicKey);
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
					 mHelper.queryInventoryAsync(mGotInventoryListener);
				}
			});
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		startDate = MEntity.getFirstDayOfMonthMillis(System.currentTimeMillis());
		endDate = MEntity.getLastDayOfMonthMillis(System.currentTimeMillis());

	    Calendar c4 = Calendar.getInstance();
		c4.set(Calendar.MONTH, 0);
		startCashDate = MEntity.getFirstDayOfMonthMillis(c4
				.getTimeInMillis());
		c4.set(Calendar.MONTH, 11);
		endCashDate = MEntity.getLastDayOfMonthMillis(c4
				.getTimeInMillis());
		
		
		mTitle = mDrawerTitle = getTitle();
		mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
		
		mOverViewLinearLayout = (LinearLayout) findViewById(R.id.overview_linearlayout);
		mAccountLinearLayout = (LinearLayout) findViewById(R.id.account_linearlayout);
		mReportLinearLayout = (LinearLayout) findViewById(R.id.report_linearLayout);
		mBillsLinearLayout = (LinearLayout) findViewById(R.id.bills_linearLayout);
		mBudgetLinearLayout = (LinearLayout) findViewById(R.id.budget_linearLayout);
		
		layoutArrayList.add(mOverViewLinearLayout);
		layoutArrayList.add(mAccountLinearLayout);
		layoutArrayList.add(mReportLinearLayout);
		layoutArrayList.add(mBillsLinearLayout);
		layoutArrayList.add(mBudgetLinearLayout);
		
		fragmentManager = getSupportFragmentManager();
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setDisplayShowHomeEnabled(false);
		
		getActionBar().setTitle("Pocket Expense");   
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
//				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				if (mItemPosition == 1 || mItemPosition == 4) {
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
					if (mItemPosition == 1) {
						getActionBar().setTitle("Accounts");
					}else if (mItemPosition == 4) {
						getActionBar().setTitle("Budgets");
					}
					
				} else {
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
					actionBar.setDisplayShowTitleEnabled(false);
				}
			}                                                                                   
                                                                                                
			public void onDrawerOpened(View drawerView) {                                       
				                                       
				invalidateOptionsMenu(); // creates call to                                     
										 // onPrepareOptionsMenu()                                           
				// onPrepareOptionsMenu()
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				actionBar.setDisplayShowTitleEnabled(true);
				getActionBar().setTitle("Pocket Expense");

			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		for (int i = 0; i < layoutArrayList.size(); i++) {
			LinearLayout mLayout = layoutArrayList.get(i);
			mLayout.setOnClickListener(mClickListener);
			TextView mTextView =  (TextView) mLayout.findViewById(R.id.text_view);
			mTextView.setText(mainText[i]);
		}
		
		mDrawerLayout.closeDrawer(mLinearLayout);

		Calendar calendar = Calendar.getInstance();
		long todayLong = calendar.getTimeInMillis();

		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);
		selectedDate = calendar1.getTimeInMillis();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		overViewNavigationListAdapter = new OverViewNavigationListAdapter(this);
		overViewNavigationListAdapter.setChoosed(0);
		overViewNavigationListAdapter.setTitle("Overview ");
		overViewNavigationListAdapter.setSubTitle(turnToDate(todayLong));
		List<String> itemStrings = new ArrayList<String>();
		itemStrings.add("Week                    ");
		itemStrings.add("Month                   ");
		overViewNavigationListAdapter.setDownItemData(itemStrings);
		actionBar.setListNavigationCallbacks(overViewNavigationListAdapter,
				new DropDownListenser());

		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.HOUR_OF_DAY, 0);
		calendar2.set(Calendar.MINUTE, 0);
		calendar2.set(Calendar.SECOND, 0);
		calendar2.set(Calendar.MILLISECOND, 0);
		// calendar2.set(Calendar.DAY_OF_MONTH, 1);
		this.selectedMonth = calendar2.getTimeInMillis();
		
		reFreshMainView(0);

	}
	
	private void reFreshMainView(int mItemPosition) {
		
		for (int i = 0; i < layoutArrayList.size(); i++) {
			
			LinearLayout mLayout = layoutArrayList.get(i);
			ImageView mImageView = (ImageView) mLayout.findViewById(R.id.image_View);
			View mView = (View) mLayout.findViewById(R.id.choose_view);
			TextView mTextView =  (TextView) mLayout.findViewById(R.id.text_view);
			
			if (i == mItemPosition) {
				mLayout.setBackgroundResource(R.color.main_choose_gray);
				mImageView.setImageResource(Common.MAIN_ICON_SEL[i]);
				mTextView.setTextColor(Color.parseColor("#3b7fbb"));
				mView.setBackgroundResource(R.color.main_choose_blue);
			} else {
				mLayout.setBackgroundResource(R.color.white);
				mImageView.setImageResource(Common.MAIN_ICON[i]);
				mTextView.setTextColor(Color.parseColor("#5e6168"));
				mView.setBackgroundResource(R.color.white);
			}
		}
	}
	
	

	class DropDownListenser implements OnNavigationListener // overview actionbar下拉菜单监听
	{

		public boolean onNavigationItemSelected(int itemPosition, long itemId) {

			if (itemPosition == 0) {
				mItemPosition = 0;
				overViewNavigationListAdapter.setChoosed(0);
				overViewNavigationListAdapter.setSubTitle(turnToDate(MainActivity.selectedDate));
				overViewNavigationListAdapter.notifyDataSetChanged();

				
				overviewFragment = new OverviewFragment();
				Bundle bundle = new Bundle();
				bundle.putLong("selectedDate", MainActivity.selectedDate);
				overviewFragment.setArguments(bundle);

				FragmentTransaction fragmentTransaction1 = fragmentManager
						.beginTransaction();
				fragmentTransaction1.replace(R.id.content_frame,
						overviewFragment);
				fragmentTransaction1.commit();
				mItemPosition = 0;
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowTitleEnabled(false);

			} else if (itemPosition == 1) {

				overViewNavigationListAdapter.setChoosed(1);
				overViewNavigationListAdapter.setSubTitle(turnToDate(MainActivity.selectedDate));
				overViewNavigationListAdapter.notifyDataSetChanged();

				OverViewFragmentMonth overViewFragmentMonth = new OverViewFragmentMonth();
				Bundle bundle = new Bundle();
				bundle.putLong("selectedDate", selectedDate);
				overViewFragmentMonth.setArguments(bundle);

				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.content_frame,
						overViewFragmentMonth);
				fragmentTransaction.commit();
				mItemPosition = 0;
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowTitleEnabled(false);

			}

			return false;

		}
	};

	public String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.overview_linearlayout:

				if (mItemPosition == 0) {
					mDrawerLayout.closeDrawer(mLinearLayout);
					actionBar.setDisplayShowTitleEnabled(false);
				} else {

					mDrawerLayout.closeDrawer(mLinearLayout);

					mItemPosition = 0;
					actionBar.setDisplayHomeAsUpEnabled(true);
					actionBar.setDisplayShowTitleEnabled(false);

					Calendar calendar1 = Calendar.getInstance();
					calendar1.set(Calendar.HOUR_OF_DAY, 0);
					calendar1.set(Calendar.MINUTE, 0);
					calendar1.set(Calendar.SECOND, 0);
					calendar1.set(Calendar.MILLISECOND, 0);
					selectedDate = calendar1.getTimeInMillis();

					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
					overViewNavigationListAdapter.setChoosed(0);
					overViewNavigationListAdapter.setTitle("Overview ");
					overViewNavigationListAdapter
							.setSubTitle(turnToDate(selectedDate));
					List<String> itemStrings = new ArrayList<String>();
					itemStrings.add("Week                    ");
					itemStrings.add("Month                   ");
					overViewNavigationListAdapter.setDownItemData(itemStrings);
					actionBar.setListNavigationCallbacks(
							overViewNavigationListAdapter,
							new DropDownListenser());
				}
				
				reFreshMainView(mItemPosition);
				
				break;
			case R.id.account_linearlayout:
				if (mItemPosition == 1) {
					mDrawerLayout.closeDrawer(mLinearLayout);
					getActionBar().setTitle("Accounts");
					actionBar.setDisplayShowTitleEnabled(true);
				} else {

					mDrawerLayout.closeDrawer(mLinearLayout);
					AccountsFragment accountFragment = new AccountsFragment();
					FragmentTransaction fragmentTransaction2 = fragmentManager
							.beginTransaction();
					fragmentTransaction2.replace(R.id.content_frame,
							accountFragment);
					fragmentTransaction2.commit();
					mItemPosition = 1;
					actionBar.setDisplayHomeAsUpEnabled(true);
					actionBar.setDisplayShowTitleEnabled(true);
					actionBar
							.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				}
				
				reFreshMainView(mItemPosition);

				break;
			case R.id.report_linearLayout:

				if (mItemPosition == 2) {
					mDrawerLayout.closeDrawer(mLinearLayout);
					actionBar.setDisplayShowTitleEnabled(false);
				} else {
					
					rangePositon = 0;
					startDate = MEntity.getFirstDayOfMonthMillis(System.currentTimeMillis());
					endDate = MEntity.getLastDayOfMonthMillis(System.currentTimeMillis());

					mDrawerLayout.closeDrawer(mLinearLayout);
					actionBar.setDisplayHomeAsUpEnabled(true);
					actionBar.setDisplayShowTitleEnabled(false);
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
					overViewNavigationListAdapter.setChoosed(0);
					overViewNavigationListAdapter.setTitle("Reports ");
					overViewNavigationListAdapter
							.setSubTitle(turnToDate(selectedDate));
					List<String> reportStrings = new ArrayList<String>();
					reportStrings.add("Category                 ");
					reportStrings.add("Cash Flow                ");
					overViewNavigationListAdapter
							.setDownItemData(reportStrings);
					actionBar.setListNavigationCallbacks(
							overViewNavigationListAdapter,
							new ReportDropDownListenser());
					mItemPosition = 2;
				}

				reFreshMainView(mItemPosition);
				
				break;
			case R.id.bills_linearLayout:
				if (mItemPosition == 3) {
					mDrawerLayout.closeDrawer(mLinearLayout);
					actionBar.setDisplayShowTitleEnabled(false);
				} else {

					Calendar calendar2 = Calendar.getInstance();
					calendar2.set(Calendar.HOUR_OF_DAY, 0);
					calendar2.set(Calendar.MINUTE, 0);
					calendar2.set(Calendar.SECOND, 0);
					calendar2.set(Calendar.MILLISECOND, 0);
					MainActivity.selectedMonth = calendar2.getTimeInMillis();

					mDrawerLayout.closeDrawer(mLinearLayout);

					actionBar.setDisplayHomeAsUpEnabled(true);
					actionBar.setDisplayShowTitleEnabled(false);
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
					overViewNavigationListAdapter.setChoosed(0);
					overViewNavigationListAdapter.setTitle("Bills      ");
					overViewNavigationListAdapter
							.setSubTitle(turnToDate(MainActivity.selectedMonth));
					List<String> billStrings = new ArrayList<String>();
					billStrings.add("List                      ");
					billStrings.add("Calendar                  ");
					overViewNavigationListAdapter.setDownItemData(billStrings);
					actionBar.setListNavigationCallbacks(
							overViewNavigationListAdapter,
							new BillsDropDownListenser());
					mItemPosition = 3;

				}
				reFreshMainView(mItemPosition);

				break;
			case R.id.budget_linearLayout:
				
				mDrawerLayout.closeDrawer(mLinearLayout);
				
				if (mItemPosition == 4) {
					getActionBar().setTitle("Budget");
					actionBar.setDisplayShowTitleEnabled(true);
				} else {

					BudgetFragment budgetFragment = new BudgetFragment();
					FragmentTransaction fragmentTransaction2 = fragmentManager
							.beginTransaction();
					fragmentTransaction2.replace(R.id.content_frame,
							budgetFragment);
					fragmentTransaction2.commit();
					
					mItemPosition = 4;
					actionBar.setDisplayHomeAsUpEnabled(true);
					actionBar.setDisplayShowTitleEnabled(true);
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				}
				
				reFreshMainView(mItemPosition);
				
				break;
			default:
				reFreshMainView(mItemPosition);
				break;
			}
		}
	};

	class ReportDropDownListenser implements OnNavigationListener // actionbar下拉菜单监听
	{

		public boolean onNavigationItemSelected(int itemPosition, long itemId) {

			if (itemPosition == 0) {
				overViewNavigationListAdapter.setChoosed(0);
				overViewNavigationListAdapter
				.setSubTitle("Categories");
				
				CategorysReportFragment reportOverviewFragment = new CategorysReportFragment();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.content_frame,
						reportOverviewFragment);
				fragmentTransaction.commit();

			} else if (itemPosition == 1) {
				overViewNavigationListAdapter.setChoosed(1);
				overViewNavigationListAdapter
				.setSubTitle("Cash Flow");
				
				CashReportFragment cashFragment = new CashReportFragment();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.content_frame,
						cashFragment);
				fragmentTransaction.commit();

				
			} 
			overViewNavigationListAdapter.notifyDataSetChanged();
			return false;

		}
	};

	class BillsDropDownListenser implements OnNavigationListener // actionbar下拉菜单监听
	{

		public boolean onNavigationItemSelected(int itemPosition, long itemId) {

			if (itemPosition == 0) {
				overViewNavigationListAdapter.setChoosed(0);
				BillsFragment billsFragment = new BillsFragment();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.content_frame, billsFragment);
				fragmentTransaction.commit();

				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowTitleEnabled(false);

			} else if (itemPosition == 1) {
				overViewNavigationListAdapter.setChoosed(1);

				billsFragmentMonth = new BillsFragmentMonth();
				FragmentTransaction fragmentTransaction4 = fragmentManager
						.beginTransaction();
				fragmentTransaction4.replace(R.id.content_frame,
						billsFragmentMonth);
				fragmentTransaction4.commit();

				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowTitleEnabled(false);
			}
			overViewNavigationListAdapter.notifyDataSetChanged();
			return false;

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mLinearLayout);
		menu.findItem(R.id.action_search).setVisible(drawerOpen);
		menu.findItem(R.id.action_settings).setVisible(drawerOpen);
//		actionBar.setDisplayShowTitleEnabled(drawerOpen);
		
		if (mItemPosition == 0 || mItemPosition == 2 || mItemPosition == 4) {
			menu.findItem(R.id.action_add).setVisible(false);
			
			if (drawerOpen) {

				if (OverviewFragment.item != null) {
					OverviewFragment.item.setVisible(false);
				}

				if (OverViewFragmentMonth.item != null) {
					OverViewFragmentMonth.item.setVisible(false);
				}
				
				if (CategorysReportFragment.item != null) {
					CategorysReportFragment.item.setVisible(false);
				}
				
				if (CashReportFragment.item != null) {
					CashReportFragment.item.setVisible(false);
				}
				
			}
			
		} else {
			menu.findItem(R.id.action_add).setVisible(!drawerOpen);
		}

		if (mItemPosition == 1 && drawerOpen == false) {

			if (AccountsFragment.item0 != null
					&& AccountsFragment.sortCheck == 1) {
				AccountsFragment.item1.setVisible(false);
				AccountsFragment.item0.setVisible(true);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_search:
			
			Intent intent1 = new Intent();
			intent1.setClass(MainActivity.this, SearchActivity.class);
			startActivity(intent1);
			
			return true;
		case R.id.action_settings:

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void OnWeekSelected(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		overViewNavigationListAdapter
				.setSubTitle(turnToDate(this.selectedDate));
		overViewNavigationListAdapter.notifyDataSetChanged();
		if (overviewFragment != null) {
			onUpdateListListener = (OnUpdateListListener) overviewFragment;
			onUpdateListListener.OnUpdateList(this.selectedDate);
		}

	}

	@Override
	public void OnBackTime(long selectedDate, int viewPagerPosition) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		if (ViewPagerAdapter.registeredFragments.get(viewPagerPosition) != null) {
			onUpdateWeekSelectListener = (OnUpdateWeekSelectListener) (ViewPagerAdapter.registeredFragments
					.get(viewPagerPosition));
			onUpdateWeekSelectListener.OnUpdateWeekSelect(selectedDate);
		}
		
	}

	@Override
	public void OnUpdateNavigation( ) {
		// TODO Auto-generated method stub
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);
		this.selectedDate = calendar1.getTimeInMillis();
		
		overviewFragment = new OverviewFragment();
		Bundle bundle = new Bundle();
		bundle.putLong("selectedDate", this.selectedDate);
		overviewFragment.setArguments(bundle);

		FragmentTransaction fragmentTransaction1 = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction1.replace(R.id.content_frame,
				overviewFragment);
		fragmentTransaction1.commit();
		
		overViewNavigationListAdapter.setChoosed(0);
		overViewNavigationListAdapter.setSubTitle(turnToDate(this.selectedDate));
		overViewNavigationListAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	public void OnUpdateNavigation(int itemPosition, long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		actionBar.setSelectedNavigationItem(0);
		
	}

	@Override
	public void OnUpdateNavigation(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		overViewNavigationListAdapter
				.setSubTitle(turnToDate(this.selectedDate));
		overViewNavigationListAdapter.notifyDataSetChanged();

	}

	@Override
	public void OnTellTime(int viewpagerPositon) {
		// TODO Auto-generated method stub
		OnUpdateMonthListener onUpdateMonthListener = (OnUpdateMonthListener) (MonthViewPagerAdapter.registeredFragments
				.get(viewpagerPositon));
		onUpdateMonthListener.OnUpdateMonth();
	}

	@Override
	public void OnBillToActivity() {
		// TODO Auto-generated method stub
		overViewNavigationListAdapter
				.setSubTitle(turnToDate(this.selectedMonth));
		overViewNavigationListAdapter.notifyDataSetChanged();
		
		if (billsFragmentMonth != null) {
			OnActivityToBillListener onActivityToBillListener = (OnActivityToBillListener) (billsFragmentMonth);
			onActivityToBillListener.OnActivityToBill();
		}
		

	}

	 void loadIsPaid() {
		    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
	        Common.mIsPaid = sharedPreferences.getBoolean("isPaid", false);
	        
	}
	 
	 void complain(String message) {
	        Log.e(TAG, "**** Expense Error: " + message);
	        alert("Error: " + message);
	    }
	 void alert(String message) {
	        AlertDialog.Builder bld = new AlertDialog.Builder(this);
	        bld.setMessage(message);
	        bld.setNeutralButton("OK", null);
	        Log.d(TAG, "Showing alert dialog: " + message);
	        bld.create().show();
	    }
	 
	 /** Verifies the developer payload of a purchase. */
	    boolean verifyDeveloperPayload(Purchase p) {
	        String payload = p.getDeveloperPayload();

	        return true;
	    }
	    
	    private long exitTime = 0;
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			
			if (keyCode == KeyEvent.KEYCODE_BACK && AccountsFragment.sortCheck == 1) {
				AccountsFragment.sortCheck = 0;

				if (AccountsFragment.item1 != null) {

					AccountsFragment.item1.setVisible(true);
					AccountsFragment.item0.setVisible(false);
					AccountsFragment.c.setSortEnabled(false);
					AccountsFragment.mListView.setLongClickable(true);
					AccountsFragment.mAccountsListViewAdapter.sortIsChecked(-1);
					AccountsFragment.mAccountsListViewAdapter
							.notifyDataSetChanged();

				}
			}
			
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if ((System.currentTimeMillis() - exitTime) > 2000) {
					Toast.makeText(MainActivity.this, "Press again to exit!!",
							Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {

					// MyApplication.folder_id = 0;
					// MyApplication.mMemoryCache.evictAll();
					Intent intent = new Intent();
					intent.setAction("ExitLogin");
					this.sendBroadcast(intent);
					super.finish();
					System.exit(0);

				}
			}
			return false;
		}
		
		 @Override
			protected void onActivityResult(int requestCode, int resultCode, Intent data) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
				if (mHelper == null) return;
				if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
					super.onActivityResult(requestCode, resultCode, data);
					 if (requestCode == RC_REQUEST) {     
					      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
					      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
					      String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
					        
					      if (resultCode == RESULT_OK) {
					         try {
					            JSONObject jo = new JSONObject(purchaseData);
					            String sku = jo.getString("productId");
					            alert("Thank you for upgrading to pro! ");
					            
					             SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(PREFS_NAME,0);   //已经设置密码 
							     SharedPreferences.Editor meditor = sharedPreferences.edit();  
								 meditor.putBoolean("isPaid",true ); 
								 meditor.commit();
								 
					            if (overviewFragment != null) {
					   				   refreshADS = (OnRefreshADS) overviewFragment;
					   				   refreshADS.refreshADS();
									
								}
					          }
					          catch (JSONException e) {
					             alert("Failed to parse purchase data.");
					             e.printStackTrace();
					          }
					      }
					   }
					 
		        }
		       
		        else {
		            Log.d(TAG, "onActivityResult handled by IABUtil.");
		        }
			}
		 
		// Callback for when a purchase is finished
		    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		    	
		    	@Override
		        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

		            // if we were disposed of in the meantime, quit.
		            if (mHelper == null) return;
		   			
		            mHelper.queryInventoryAsync(mGotInventoryListener);
					
		            if (!result.isSuccess()) {
//		                complain("Error purchasing: " + result);
		                return;
		            }
					
		            if (!verifyDeveloperPayload(purchase)) {
//		                complain("Error purchasing. Authenticity verification failed.");
		                return;
		            }

		            if (purchase.getSku().equals(Paid_Id_VF)) {
		                // bought the premium upgrade!
		                alert("Thank you for upgrading to pro!");
		                
		             Common.mIsPaid =true;
		   		     SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   //已经设置密码 
		   		     SharedPreferences.Editor meditor = sharedPreferences.edit();  
		   			 meditor.putBoolean("isPaid",true );  
		   			 meditor.commit();
		   			 
						
						
		   			 if (overviewFragment != null) {
		   				 
		   				   refreshADS = (OnRefreshADS) overviewFragment;
		   				   refreshADS.refreshADS();
						
					}
		           }
		        }

			
		    };
		    
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
				     SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(PREFS_NAME,0);   //已经设置密码 
				     SharedPreferences.Editor meditor = sharedPreferences.edit();  
					 meditor.putBoolean("isPaid",Common.mIsPaid ); 
					 meditor.commit();
					 
		   			 if (overviewFragment != null) {
		   				 
		   				   refreshADS = (OnRefreshADS) overviewFragment;
		   				   refreshADS.refreshADS();
						
					}
				}
			};


			@Override
			public void mainBuyPro() {
				// TODO Auto-generated method stub
				if (iap_is_ok && mHelper != null) {
					
					 String payload = "";
					 mHelper.launchPurchaseFlow(MainActivity.this, Paid_Id_VF, RC_REQUEST, mPurchaseFinishedListener);
				}else{
				}
			}
			
			 @Override
			    public void onDestroy() {
			        super.onDestroy();

			        // very important:
			        if (mHelper != null) {
			            mHelper.dispose();
			            mHelper = null;
			        }
			    }

			@Override
			public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
				// TODO Auto-generated method stub
				Toast.makeText(this, "Dropbox sync successed",
						Toast.LENGTH_SHORT).show();
				if (attachFragment != null ) {
					 OnSyncFinishedListener onSyncFinishedListener = (OnSyncFinishedListener)attachFragment;
					 onSyncFinishedListener.onSyncFinished();
					 
				}
				
			}
			
			@Override
			protected void onResume() {
				// TODO Auto-generated method stub
				super.onResume();
				mDbxAcctMgr1 = mDbxAcctMgr;
				mDatastore1 = mDatastore;
			}
			


}
