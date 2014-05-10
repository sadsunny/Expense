package com.appxy.pocketexpensepro.bills;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnActivityToBillListener;
import com.appxy.pocketexpensepro.expinterface.OnBillToActivityListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BillsFragmentMonth extends Fragment implements OnActivityToBillListener{

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private FragmentActivity mActivity;
	private ViewPager mViewPager;
	private GridView mGridView;
	private ListView mListView;
	private BillMonthViewPagerAdapter billMonthViewPagerAdapter;
	public Calendar month;
	private CalendarGridViewAdapter calendarGridViewAdapter;
	private Thread mThread; 
	private OnBillToActivityListener onBillToActivityListener;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				Log.v("mtest", "bill time MSG_SUCCESS"+MEntity.turnMilltoMonthYear(MainActivity.selectedMonth));
				calendarGridViewAdapter.setMonth(month);
				calendarGridViewAdapter.notifyDataSetChanged();
				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	
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
		month = (Calendar) Calendar.getInstance();
		onBillToActivityListener = (OnBillToActivityListener)mActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bill_month, container,
				false);
		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mGridView = (GridView) view.findViewById(R.id.mGridview);
		mListView = (ListView) view.findViewById(R.id.mListView);

		month.setTimeInMillis(MainActivity.selectedMonth);
		calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity, month);
		mGridView.setAdapter(calendarGridViewAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				long mChooseTime = getMilltoDate(calendarGridViewAdapter.getDayString()
						.get(paramInt));
				Log.v("mtest", "mChooseTime"+MEntity.getMilltoDate(mChooseTime));
				
			}
		});
		
		billMonthViewPagerAdapter = new BillMonthViewPagerAdapter(
				mActivity.getSupportFragmentManager());
		mViewPager.setAdapter(billMonthViewPagerAdapter);
		
		Log.v("mtest", "重新获取的日期"+MEntity.turnMilltoMonthYear(MainActivity.selectedMonth));
		int mOffset = MEntity.getOffsetByMonth(MainActivity.selectedMonth);
		Log.v("mtest", "重新获取的mOffset"+mOffset);
		mViewPager.setCurrentItem(MID_VALUE+mOffset);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						if (position == MID_VALUE) {
							
							Calendar calendar1 = Calendar.getInstance();
							calendar1.set(Calendar.HOUR_OF_DAY, 0);
							calendar1.set(Calendar.MINUTE, 0);
							calendar1.set(Calendar.SECOND, 0);
							calendar1.set(Calendar.MILLISECOND, 0);
							calendar1.set(Calendar.DAY_OF_MONTH, 1);
							MainActivity.selectedMonth = calendar1.getTimeInMillis();
						} else {
							int offset = position - MID_VALUE;
							MainActivity.selectedMonth = MEntity.getFirstMonthByOffset(offset);
						}
						onBillToActivityListener.OnBillToActivity();
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
		
		
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}else {
			mHandler.post(mTask);
		}

		return view;
	}
	
	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			calendarGridViewAdapter.setCheckDat(1);
			month.setTimeInMillis(MainActivity.selectedMonth);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	public long getMilltoDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return calendar.getTimeInMillis();
		}
		return calendar.getTimeInMillis();
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

	@Override
	public void OnActivityToBill() {
		// TODO Auto-generated method stub
		mHandler.post(mTask);
//		month.setTimeInMillis(MainActivity.selectedMonth);
//		calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity, month);
//		mGridView.setAdapter(calendarGridViewAdapter);
//		Log.v("mtest", "bill");
//		Log.v("mtest", "bill time"+MEntity.turnMilltoMonthYear(MainActivity.selectedMonth));
	}

}
