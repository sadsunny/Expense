package com.appxy.pocketexpensepro.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}


	@Override
	public android.support.v4.app.Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		
			    PurFragmentA weekFragment = new PurFragmentA();

				Bundle bundle = new Bundle();
				bundle.putInt("position", arg0);
				weekFragment.setArguments(bundle);

			    return weekFragment;

	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}
}