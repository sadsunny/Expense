package com.appxy.pocketexpensepro.overview;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
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
		
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				viewPagerPosition = position;
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
}
