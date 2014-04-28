package com.appxy.pocketexpensepro.overview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnBackTimeListener;
import com.appxy.pocketexpensepro.expinterface.OnChangeStateListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class OverviewFragment extends Fragment implements OnUpdateListListener,
		OnChangeStateListener {
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private ViewPager mViewPager;
	private FragmentActivity mActivity;
	public static ViewPagerAdapter mViewPagerAdapter;
	private WeekFragment weekFragment;
	private ListView mListView;
	private List<Map<String, Object>> mDataList;
	private ListViewAdapter mListViewAdapter;
	private Thread mThread;
	private long selectedDate;
	private int viewPagerPosition;
	private RelativeLayout weekLayout;
	private RelativeLayout calendarLayout;
	private OnBackTimeListener onBackTimeListener;

	private GridView mGridView;
	private CalendarGridViewAdapter calendarGridViewAdapter;
	public GregorianCalendar month;// calendar instances.
	private List<Map<String, Object>> mGridDataList;

	public static SparseArray<Fragment> registeredFragments;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				if (mDataList != null) {

					mListViewAdapter.setAdapterDate(mDataList);
					mListViewAdapter.notifyDataSetChanged();
				}

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
		onBackTimeListener = (OnBackTimeListener) mActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		registeredFragments = new SparseArray<Fragment>();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_overview, container,
				false);

		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mViewPagerAdapter = new ViewPagerAdapter(
				mActivity.getSupportFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(MID_VALUE);
		viewPagerPosition = MID_VALUE;

		mViewPagerAdapter.getItem(MID_VALUE);
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

		mListView = (ListView) view.findViewById(R.id.mListView);
		mListView.setDividerHeight(0);
		mListViewAdapter = new ListViewAdapter(mActivity);
		mListView.setAdapter(mListViewAdapter);

		weekLayout = (RelativeLayout) view.findViewById(R.id.RelativeLayout1);
		calendarLayout = (RelativeLayout) view
				.findViewById(R.id.RelativeLayout2);
		weekLayout.setVisibility(View.VISIBLE);
		calendarLayout.setVisibility(View.INVISIBLE);

		Locale.setDefault(Locale.ENGLISH);
		month = (GregorianCalendar) GregorianCalendar.getInstance();
<<<<<<< HEAD
		month.setTimeInMillis(month.getTimeInMillis());
	    mGridView = (GridView)view.findViewById(R.id.mGridview);
	    calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity, month);
	    mGridView.setAdapter(calendarGridViewAdapter);
	    
=======

		mGridView = (GridView) view.findViewById(R.id.mGridview);
		calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity, month);
		mGridView.setAdapter(calendarGridViewAdapter);

>>>>>>> FETCH_HEAD
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}

		return view;
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList = OverViewDao.selectTransactionByTime(mActivity,selectedDate);
			reFillData(mDataList);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(selectedDate);
<<<<<<< HEAD
			long firstDay = getFirstDayOfMonthMillis(calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.MONTH)+1);
			long lastDay = getLastDayOfMonthMillis(calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.MONTH)+1);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	public static long getFirstDayOfMonthMillis(int year, int month) {   //锟斤拷取某锟斤拷某锟铰的碉拷一锟斤拷暮锟斤拷锟斤拷锟�
        Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR, year);   
        cal.set(Calendar.MONTH, month-1);
        
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));
        
        String dateTime = new SimpleDateFormat( "MM-dd-yyyy").format(cal.getTime());
        
        Calendar c = Calendar.getInstance();

        try {
 		c.setTime(new SimpleDateFormat( "MM-dd-yyyy").parse(dateTime));
 	 } catch (ParseException e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	 }
        return  c.getTimeInMillis();
    } 
	
	 public static long getLastDayOfMonthMillis(int year, int month) {   //锟斤拷取某锟斤拷某锟铰碉拷锟斤拷锟揭伙拷锟侥猴拷锟斤拷锟斤拷
	        Calendar cal = Calendar.getInstance();   
	        cal.set(Calendar.YEAR, year);   
	        cal.set(Calendar.MONTH, month-1); 
//	        cal.set(Calendar.HOUR, 23);
//	        cal.set(Calendar.MINUTE, 59);
//	        cal.set(Calendar.SECOND, 59);
	        
	        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE)); //xxx.set锟斤拷Calendar.DAY_OF_MONTH,12锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷为12锟斤拷..cal.getActualMaximum(Calendar.DATE)某锟斤拷某锟铰碉拷锟斤拷锟揭伙拷锟�   
	        
	        String dateTime = new SimpleDateFormat( "MM-dd-yyyy").format(cal.getTime());
	        Calendar c = Calendar.getInstance();

	       try {
			c.setTime(new SimpleDateFormat( "MM-dd-yyyy").parse(dateTime));
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
	     
	       return  c.getTimeInMillis();
	       
	    } 
	 
=======
			
			long beginTime = MEntity.getFirstDayOfMonthMillis(selectedDate);
			long endTime = MEntity.getLastDayOfMonthMillis(selectedDate);
					
			mGridDataList = OverViewDao.selectTransactionByTimeBE(mActivity, beginTime, endTime);
					
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

>>>>>>> FETCH_HEAD
	public void reFillData(List<Map<String, Object>> mData) {

		for (Map<String, Object> mMap : mData) {
			int category = (Integer) mMap.get("category");
			int payee = (Integer) mMap.get("payee");

			if (category > 0) {
				List<Map<String, Object>> mList = AccountDao
						.selectCategoryById(mActivity, category);
				if (mList != null) {
					int iconName = (Integer) mList.get(0).get("iconName");
					mMap.put("iconName", iconName);
				} else {
					mMap.put("iconName", 0);
				}
			} else {
				mMap.put("iconName", 0); // 设置为not sure
			}

			if (payee > 0) {
				List<Map<String, Object>> mList = AccountDao.selectPayeeById(
						mActivity, payee);
				if (mList != null) {
					String payeeName = (String) mList.get(0).get("name");
					mMap.put("payeeName", payeeName);
				} else {
					mMap.put("payeeName", "--");
				}
			} else {
				mMap.put("payeeName", "--");
			}

		}
	}

	public class ViewPagerAdapter extends FragmentStatePagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
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
			WeekFragment weekFragment = new WeekFragment();
			weekFragment.setTargetFragment(weekFragment, arg0);
			if (arg0 >= 0 && arg0 < 20000) {

				Bundle bundle = new Bundle();
				bundle.putInt("position", arg0);
				weekFragment.setArguments(bundle);

			} else {

				Bundle bundle = new Bundle();
				bundle.putInt("position", MID_VALUE);
				weekFragment.setArguments(bundle);
			}
			return weekFragment;

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
			registeredFragments.remove(position);
			super.destroyItem(container, position, object);
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

				onBackTimeListener.OnBackTime(selectedDate,viewPagerPosition);//viewPagerPosition用于判断具体的fragment
			}
			break;
		}
	}

	@Override
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mHandler.post(mTask);
<<<<<<< HEAD
		Log.v("mtest", "turnToDate selectedDate"+turnToDate(selectedDate));
		
		month.set(2014, 3, 1);
		
		calendarGridViewAdapter.setDateTime(month);
=======
		Log.v("mtest", "turnToDate selectedDate" + turnToDate(selectedDate));
		
		month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(selectedDate));
>>>>>>> FETCH_HEAD
		calendarGridViewAdapter.refreshDays();
		calendarGridViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void OnChangeState(int state) {
		// TODO Auto-generated method stub
		if (state == 0) {
			weekLayout.setVisibility(View.VISIBLE);
			calendarLayout.setVisibility(View.INVISIBLE);
		} else {
			weekLayout.setVisibility(View.INVISIBLE);
			calendarLayout.setVisibility(View.VISIBLE);
		}

	}

	public static String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy, HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}

}
