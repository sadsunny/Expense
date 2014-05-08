package com.appxy.pocketexpensepro.bills;

import java.util.Calendar;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

public class BillsFragmentMonth extends Fragment{
	
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private FragmentActivity mActivity;
	private ViewPager mViewPager;
	private GridView mGridView;
	private ListView mListView;
	private BillMonthViewPagerAdapter billMonthViewPagerAdapter;
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bill_month, container,
				false);
		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mGridView = (GridView)view.findViewById(R.id.mGridview);
		mListView = (ListView)view.findViewById(R.id.mListView);
		
		billMonthViewPagerAdapter = new BillMonthViewPagerAdapter(mActivity.getSupportFragmentManager());
		mViewPager.setAdapter(billMonthViewPagerAdapter);
		mViewPager.setCurrentItem(MID_VALUE);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				
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
			intent.setClass(getActivity(), CreatBillsActivity.class);
			startActivityForResult(intent, 8);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	
}
