package com.appxy.pocketexpensepro.overview;

import java.util.Calendar;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnTellUpdateMonthListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class OverViewFragmentMonth extends Fragment {
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	
	private FragmentActivity mActivity;
	private ViewPager mViewPager;
	private MonthViewPagerAdapter mViewPagerAdapter;
	private long argumentsDate;
	private int currentPosition;
	private int viewPagerPosition;
	private OnTellUpdateMonthListener onTellUpdateMonthListener;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (FragmentActivity) activity;
		onTellUpdateMonthListener = (OnTellUpdateMonthListener) mActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			argumentsDate = bundle.getLong("selectedDate");
		}
		currentPosition = MID_VALUE + MEntity.getMonthOffsetByDay(System.currentTimeMillis(), argumentsDate);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_overview_month, container,
				false);
		
		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mViewPagerAdapter = new MonthViewPagerAdapter(mActivity.getSupportFragmentManager());
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
					onUpdateNavigationListener.OnUpdateNavigation(calendar1.getTimeInMillis());
				}else {
					long theSelectedDate = MEntity.getMonthByOffset(position-MID_VALUE);
					onUpdateNavigationListener.OnUpdateNavigation(theSelectedDate);
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
		
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.action_add:

			Intent intent = new Intent();
			intent.setClass(getActivity(), CreatTransactionActivity.class);
			startActivityForResult(intent, 6);
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

				onTellUpdateMonthListener.OnTellTime(viewPagerPosition);
				
			}
			break;
		}
	}

}
