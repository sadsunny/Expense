package com.appxy.pocketexpensepro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.appxy.pocketexpensepro.accounts.AccountsFragment;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.bills.BillsFragment;
import com.appxy.pocketexpensepro.expinterface.OnBackTimeListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateWeekSelectListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;
import com.appxy.pocketexpensepro.overview.OverviewFragment;
import com.appxy.pocketexpensepro.setting.SettingActivity;
import com.appxy.pocketexpensepro.overview.WeekFragment;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements
		OnWeekSelectedListener, OnBackTimeListener {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private LinearLayout mLinearLayout;
	private LinearLayout mOverViewLinearLayout;
	private LinearLayout mAccountLinearLayout;
	private LinearLayout mReportLinearLayout;
	private LinearLayout mBillsLinearLayout;
	private android.support.v4.app.FragmentManager fragmentManager;
	private OnUpdateListListener onUpdateListListener;
	private OverviewFragment overviewFragment;

	private final static long DAYMILLIS = 86400000L;

	private long selectedDate;
	private OnUpdateWeekSelectListener onUpdateWeekSelectListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long returnDate = calendar.getTimeInMillis();

		mTitle = mDrawerTitle = getTitle();
		mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
		mOverViewLinearLayout = (LinearLayout) findViewById(R.id.overview_linearlayout);
		mAccountLinearLayout = (LinearLayout) findViewById(R.id.account_linearlayout);
		mReportLinearLayout = (LinearLayout) findViewById(R.id.report_linearLayout);
		mBillsLinearLayout = (LinearLayout) findViewById(R.id.bills_linearLayout);
		fragmentManager = getSupportFragmentManager();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setDisplayShowHomeEnabled(false);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mOverViewLinearLayout.setOnClickListener(mClickListener);
		mAccountLinearLayout.setOnClickListener(mClickListener);
		mReportLinearLayout.setOnClickListener(mClickListener);
		mBillsLinearLayout.setOnClickListener(mClickListener);

		overviewFragment = new OverviewFragment();
		FragmentTransaction fragmentTransaction1 = fragmentManager
				.beginTransaction();
		fragmentTransaction1.replace(R.id.content_frame, overviewFragment);
		fragmentTransaction1.commit();
		mDrawerLayout.closeDrawer(mLinearLayout);

	}

	public View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.overview_linearlayout:

				mDrawerLayout.closeDrawer(mLinearLayout);
				overviewFragment = new OverviewFragment();
				FragmentTransaction fragmentTransaction1 = fragmentManager
						.beginTransaction();
				fragmentTransaction1.replace(R.id.content_frame,
						overviewFragment);
				fragmentTransaction1.commit();

				break;
			case R.id.account_linearlayout:

				mDrawerLayout.closeDrawer(mLinearLayout);
				AccountsFragment accountFragment = new AccountsFragment();
				FragmentTransaction fragmentTransaction2 = fragmentManager
						.beginTransaction();
				fragmentTransaction2.replace(R.id.content_frame,
						accountFragment);
				fragmentTransaction2.commit();

				break;
			case R.id.report_linearLayout:

				break;
			case R.id.bills_linearLayout:

				mDrawerLayout.closeDrawer(mLinearLayout);
				BillsFragment billsFragment = new BillsFragment();
				FragmentTransaction fragmentTransaction4 = fragmentManager
						.beginTransaction();
				fragmentTransaction4.replace(R.id.content_frame, billsFragment);
				fragmentTransaction4.commit();

				break;
			default:
				break;
			}
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
		menu.findItem(R.id.action_add).setVisible(!drawerOpen);

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
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

				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void OnWeekSelected(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		onUpdateListListener = (OnUpdateListListener) overviewFragment;
		onUpdateListListener.OnUpdateList(this.selectedDate);
	}
	
	

	@Override
	public void OnBackTime(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		Fragment fragement = getSupportFragmentManager().findFragmentByTag(10000+""); 
		onUpdateWeekSelectListener = (OnUpdateWeekSelectListener)fragement;
		onUpdateWeekSelectListener.OnUpdateWeekSelect(this.selectedDate);
	}

}
