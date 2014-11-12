package com.appxy.pocketexpensepro.overview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class MonthViewPagerAdapter extends FragmentStatePagerAdapter{
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	public static SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
	
	public MonthViewPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position,
			Object object) {
		// TODO Auto-generated method stub
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public android.support.v4.app.Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		MonthViewFragment monthViewFragment = new MonthViewFragment();
		 
		if (arg0 >= 0 && arg0 < 20000) {

			Bundle bundle = new Bundle();
			bundle.putInt("position", arg0);
			monthViewFragment.setArguments(bundle);

		} else {

			Bundle bundle = new Bundle();
			bundle.putInt("position", MID_VALUE);
			monthViewFragment.setArguments(bundle);
		}
		return monthViewFragment;

	}

	@Override
	public Object instantiateItem(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		Fragment fragment = (Fragment) super.instantiateItem(arg0, arg1);
		registeredFragments.put(arg1, fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		registeredFragments.remove(position);
	}

	public Fragment getRegisteredFragment(int position) {
		return registeredFragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return MAX_VALUE;
	}
}