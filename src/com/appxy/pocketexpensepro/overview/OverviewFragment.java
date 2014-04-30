package com.appxy.pocketexpensepro.overview;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnBackTimeListener;
import com.appxy.pocketexpensepro.expinterface.OnChangeStateListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AccelerateInterpolator;  

public class OverviewFragment extends Fragment implements OnUpdateListListener,
		OnChangeStateListener {
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private ViewPager mViewPager;
	private FragmentActivity mActivity;
	public  ViewPagerAdapter mViewPagerAdapter;
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
	private TextView expenseTextView;
	private TextView incomeTextView;
	private TextView amountTextView;
	private double expense;
	private double income;
	private double amount;
	private OnUpdateNavigationListener onUpdateNavigationListener;
	public static SparseArray<Fragment> registeredFragments;
	
	private RelativeLayout budgetRelativeLayout ;
	private TextView budgeTextView;
	private ProgressBar mProgressBar;
	private FixedSpeedScroller mScroller; 
	
	private double budgetAmount;
	private double transactionAmount;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				if (mDataList != null) {

					mListViewAdapter.setAdapterDate(mDataList);
					mListViewAdapter.notifyDataSetChanged();
				}
				expenseTextView.setText(MEntity.doublepoint2str(expense + ""));
				incomeTextView.setText(MEntity.doublepoint2str(income + ""));
				amountTextView.setText(MEntity.doublepoint2str(amount + ""));

				calendarGridViewAdapter.setCheckDat(selectedDate);
				calendarGridViewAdapter.setDataList(mGridDataList);
				calendarGridViewAdapter.notifyDataSetChanged();
				
				mProgressBar.setMax((int)budgetAmount);
				mProgressBar.setProgress((int) transactionAmount);
				
				budgeTextView.setText((budgetAmount-transactionAmount)+"");
				
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

		expenseTextView = (TextView) view.findViewById(R.id.expense_txt);
		incomeTextView = (TextView) view.findViewById(R.id.income_txt);
		amountTextView = (TextView) view.findViewById(R.id.amount_txt);

		budgetRelativeLayout = (RelativeLayout) view.findViewById(R.id.budget_relativeLayout);
		budgeTextView = (TextView) view.findViewById(R.id.budget_amount);
		mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
		
		budgetRelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), BudgetActivity.class);
				startActivityForResult(intent, 14);
				
			}
		});
		
		onUpdateNavigationListener = (OnUpdateNavigationListener)mActivity;
		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mViewPagerAdapter = new ViewPagerAdapter(
				mActivity.getSupportFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(MID_VALUE);
		
//		 try {               
//	            Field mField = ViewPager.class.getDeclaredField("mScroller");               
//	            mField.setAccessible(true);     
//	             //设置加速度 ，通过改变FixedSpeedScroller这个类中的mDuration来改变动画时间（如mScroller.setmDuration(mMyDuration);）    
//	            mScroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());           
//	            mField.set(mViewPager, mScroller);           
//	            } catch (Exception e) {           
//	                e.printStackTrace();  
//	            }   
	    
	
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

		mGridView = (GridView) view.findViewById(R.id.mGridview);
		calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity, month);
		mGridView.setAdapter(calendarGridViewAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				selectedDate = getMilltoDate(CalendarGridViewAdapter.dayString
						.get(paramInt));
				calendarGridViewAdapter.setCheckDat(selectedDate);
				calendarGridViewAdapter.notifyDataSetChanged();

				int offset = MEntity.getOffsetByDay(selectedDate,
						MEntity.getNowMillis());
				viewPagerPosition = MID_VALUE + offset;
				mViewPager.setCurrentItem(viewPagerPosition);

				long choosedTime = getMilltoDate(CalendarGridViewAdapter.dayString
						.get(paramInt));
				onBackTimeListener.OnBackTime(choosedTime, viewPagerPosition);
				onUpdateNavigationListener.OnUpdateNavigation(0);
			}
		});

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
			mDataList = OverViewDao.selectTransactionByTime(mActivity,
					selectedDate);
			reFillData(mDataList);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(selectedDate);
			long beginTime = MEntity.getFirstDayOfMonthMillis(selectedDate);
			long endTime = MEntity.getLastDayOfMonthMillis(selectedDate);

			List<Map<String, Object>> mCalendarDataList = OverViewDao
					.selectTransactionByTimeBE(mActivity, beginTime, endTime);

			BigDecimal b1 = new BigDecimal("0");
			BigDecimal b2 = new BigDecimal("0");
			for (Map<String, Object> iMap : mCalendarDataList) {
				String amount = (String) iMap.get("amount");
				int expenseAccount = (Integer) iMap.get("expenseAccount");
				int incomeAccount = (Integer) iMap.get("incomeAccount");
				BigDecimal b3 = new BigDecimal(amount);

				if (expenseAccount > 0 && incomeAccount <= 0) {
					b1 = b1.subtract(b3);
				} else if (incomeAccount > 0 && expenseAccount <= 0) {
					b2 = b2.add(b3);
				}
			}
			expense = b1.doubleValue();
			income = b2.doubleValue();
			amount = b1.add(b2).doubleValue();

			mGridDataList = filterDataByTime(mCalendarDataList);
			
			
			List<Map<String, Object>>  mBudgetList = OverViewDao.selectBudget(mActivity);
			List<Map<String, Object>> mTransferList = OverViewDao.selectBudgetTransfer(mActivity);
			Calendar todayCalendar = Calendar.getInstance();
			long firstDay = MEntity.getFirstDayOfMonthMillis(todayCalendar.getTimeInMillis());
			long lastDay = MEntity.getLastDayOfMonthMillis(todayCalendar.getTimeInMillis());
			
			BigDecimal budgetBig = new BigDecimal("0");
			BigDecimal transactionBig = new BigDecimal("0");
			for (Map<String, Object> iMap: mBudgetList) {
				int _id = (Integer) iMap.get("_id");
				String amount = (String) iMap.get("amount");
				int category_id = (Integer) iMap.get("category");
				
				BigDecimal big1 = new BigDecimal(amount);
				budgetBig = budgetBig.add(big1);
				for (Map<String, Object> mMap: mTransferList) {
					
					int fromBudget = (Integer) mMap.get("fromBudget");
					int toBudget = (Integer) mMap.get("toBudget");
					String amountTransfer = (String) mMap.get("amount");
					
					BigDecimal big2 = new BigDecimal(amountTransfer);
					if (_id == fromBudget) {
						big1 = big1.subtract(big2);
					} else if (_id == toBudget) {
						big1 = big1.add(big2);
					}
				}
				iMap.put("amount", big1.doubleValue()+"");
				
				BigDecimal bigz = new BigDecimal("0");
				List<Map<String, Object>> mTransactionList = OverViewDao.selectTransactionByCategoryIdAndTime(mActivity, category_id, firstDay, lastDay) ;
				for (Map<String, Object> tMap: mTransactionList){
					
					String tAmount = (String) tMap.get("amount");
					int expenseAccount = (Integer) tMap.get("expenseAccount");
					int incomeAccount = (Integer) tMap.get("incomeAccount");
					BigDecimal big3 = new BigDecimal(tAmount);

					if (expenseAccount > 0 && incomeAccount <= 0) {
						bigz = bigz.add(big3);
					} 
				}
				transactionBig = transactionBig.add(bigz);
				double tAmount = bigz.doubleValue();
				iMap.put("tAmount", tAmount+"");
				
			}
			
			budgetAmount = budgetBig.doubleValue();
			transactionAmount = transactionBig.doubleValue();

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	public List<Map<String, Object>> filterDataByTime(
			List<Map<String, Object>> mData) {// Transaction根据时间分类计算

		List<Map<String, Object>> mReturnList = new ArrayList<Map<String, Object>>();
		ArrayList<Long> mTemlist = new ArrayList<Long>();

		for (Map<String, Object> mMap : mData) {
			long dateTime = (Long) mMap.get("dateTime");
			mTemlist.add(dateTime);
		}

		Iterator<Long> it1 = mTemlist.iterator();
		Map<Long, Long> msp = new TreeMap<Long, Long>();

		while (it1.hasNext()) {
			long obj = it1.next();
			msp.put(obj, obj);
		}
		Iterator<Long> it2 = msp.keySet().iterator();
		while (it2.hasNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();
			mMap.put("dateTime", (Long) it2.next());
			mReturnList.add(mMap);
		}

		for (Map<String, Object> iMap : mReturnList) {
			long dayTime = (Long) iMap.get("dateTime");
			BigDecimal b1 = new BigDecimal("0");
			BigDecimal b2 = new BigDecimal("0");

			for (Map<String, Object> mMap : mData) {
				long dateTime = (Long) mMap.get("dateTime");
				String amount = (String) mMap.get("amount");
				int expenseAccount = (Integer) mMap.get("expenseAccount");
				int incomeAccount = (Integer) mMap.get("incomeAccount");

				if (dayTime == dateTime) {

					BigDecimal b3 = new BigDecimal(amount);
					if (expenseAccount > 0 && incomeAccount <= 0) {
						b1 = b1.subtract(b3);
					} else if (incomeAccount > 0 && expenseAccount <= 0) {
						b2 = b2.add(b3);
					}
				}
				double expense = b1.doubleValue();
				double income = b2.doubleValue();
				Map<String, Object> rMap = new HashMap<String, Object>();
				iMap.put("expense", expense);
				iMap.put("income", income);
			}

		}
		return mReturnList;
	}

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

				onBackTimeListener.OnBackTime(selectedDate, viewPagerPosition);// viewPagerPosition用于判断具体的fragment
			}
			break;
		case 14:

			if (data != null) {

				mHandler.post(mTask);
			}
			break;
		}
	}

	@Override
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mHandler.post(mTask);
		Log.v("mtest", "turnToDate selectedDate" + turnToDate(selectedDate));

		month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(selectedDate));
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

	public String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy, HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}

}
