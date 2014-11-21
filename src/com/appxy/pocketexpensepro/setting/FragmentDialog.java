package com.appxy.pocketexpensepro.setting;

import com.appxy.pocketexpensepro.R;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FragmentDialog extends DialogFragment {

	private LinearLayout circle1;
	private LinearLayout circle2;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setCancelable(true);

		int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
		setStyle(style, theme);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.dialog_purchase, container, false);

		ViewPager mViewPager = (ViewPager) view.findViewById(R.id.ttPager);
		ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(
				this.getChildFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);

		circle1 = (LinearLayout) view.findViewById(R.id.circle1);
		circle2 = (LinearLayout) view.findViewById(R.id.circle2);
		
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						
						if (position  ==  0) {
							circle1.setBackgroundResource(R.drawable.circle_black);
							circle2.setBackgroundResource(R.drawable.circle_gray);
						}else{
							circle1.setBackgroundResource(R.drawable.circle_gray);
							circle2.setBackgroundResource(R.drawable.circle_black);
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

}
