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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
	private OnUpdateNavigationListener onUpdateNavigationListener;
	public static MenuItem item;
	
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
		onUpdateNavigationListener = (OnUpdateNavigationListener) mActivity;
		
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.today_menu, menu);
		item = menu.findItem(R.id.today);
		 menu.findItem(R.id.today).setIcon(getTodayIcon()); 
	}

	/**
     * 在给定的图片的右上角加上联系人数量。数量用红色表示
     * @param icon 给定的图片
     * @return 带联系人数量的图片
     */
    private Drawable getTodayIcon(){
        //初始化画布
    	Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.today).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas=new Canvas(originalBitmap);
        
        int size = MEntity.dip2px(mActivity, 32);
        int px = MEntity.dip2px(mActivity, 14.25f);
        
        Calendar calendar = Calendar.getInstance();
        int contacyCount= calendar.get(Calendar.DAY_OF_MONTH);
        //启用抗锯齿和使用设备的文本字距
        Paint countPaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.WHITE);
        countPaint.setTextSize(26f);
        countPaint.setTextAlign(Paint.Align.CENTER);
        FontMetrics fontMetrics = countPaint.getFontMetrics();  
        float ascentY =  fontMetrics.ascent;  
        float descentY = fontMetrics.descent;  
        Log.v("mtest", "ascentY"+ascentY);
        Log.v("mtest", "descentY"+descentY);
        
        canvas.drawText(String.valueOf(contacyCount), size/2, size-px-(ascentY+descentY)/2, countPaint);
        BitmapDrawable bd= new BitmapDrawable(mActivity.getResources(), originalBitmap);
        return bd;
    }
 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.today:

			Calendar calendar1 = Calendar.getInstance();
			calendar1.set(Calendar.HOUR_OF_DAY, 0);
			calendar1.set(Calendar.MINUTE, 0);
			calendar1.set(Calendar.SECOND, 0);
			calendar1.set(Calendar.MILLISECOND, 0);
			MainActivity.selectedDate = calendar1.getTimeInMillis();
			Log.v("mtest", "action_add");
			onUpdateNavigationListener.OnUpdateNavigation(0, MainActivity.selectedDate);
			
			
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
