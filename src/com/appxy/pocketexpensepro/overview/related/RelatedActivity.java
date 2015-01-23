package com.appxy.pocketexpensepro.overview.related;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountsFragment;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class RelatedActivity extends BaseHomeActivity {
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	public static DbxAccountManager mDbxAcctMgr1;
	public static DbxDatastore mDatastore1;
	private Fragment attachFragment;
	private AccountFragment accountFragment;
	private CategoryFragment categoryFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_tab);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		Intent intent = getIntent();
		int categoryId = intent.getIntExtra("categoryId", 0);
		int expenseAccountId = intent.getIntExtra("expenseAccountId", 0);
		int incomeAccountId = intent.getIntExtra("incomeAccountId", 0);
		
		if (categoryId == 0 && expenseAccountId == 0 && incomeAccountId == 0 ) {
			finish();
		}
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};
		actionBar.addTab(actionBar.newTab().setText("Account")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Category")
				.setTabListener(tabListener));

		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);
		
		accountFragment = new AccountFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("expenseAccountId", expenseAccountId);
		bundle.putInt("incomeAccountId", incomeAccountId);
		accountFragment.setArguments(bundle);
		
		categoryFragment = new CategoryFragment();
		Bundle bundle1 = new Bundle();
		bundle1.putInt("categoryId", categoryId);
		categoryFragment.setArguments(bundle1);
		
		attachFragment = accountFragment;
		
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						final ActionBar actionBar = getActionBar();
						actionBar.setSelectedNavigationItem(position);
						
						if (position == 0) {
							attachFragment = accountFragment;
						}else if (position == 1) {
							attachFragment = categoryFragment;
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
		
		Intent rIntent = new Intent();
		rIntent.putExtra("done", 1);
		setResult(15, rIntent);

	}

	public class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
		
			case 0:
				return accountFragment;
			case 1:
				return categoryFragment;

			}
			throw new IllegalStateException("No fragment at position " + arg0);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: 
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mDbxAcctMgr1 = mDbxAcctMgr;
		this.mDatastore1 = mDatastore;
	}
	

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		
		if (attachFragment != null) {
			OnSyncFinishedListener onSyncFinishedListener = (OnSyncFinishedListener)attachFragment;
			onSyncFinishedListener.onSyncFinished();
		}
		
	}
	
	
	

}
