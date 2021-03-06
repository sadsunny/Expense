package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateMonthListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.reports.ReportDao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MonthViewFragment extends Fragment implements
		OnUpdateListListener, OnUpdateMonthListener , OnSyncFinishedListener{
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;

	private FragmentActivity mActivity;

	private GridView mGridView;
	private CalendarGridViewAdapter calendarGridViewAdapter;
	public Calendar month;// calendar instances.
	private List<Map<String, Object>> mGridDataList;
	private TextView expenseTextView;
	private TextView incomeTextView;
	private TextView amountTextView;
	private TextView currencyTextView1;
	private TextView currencyTextView2;
	private TextView currencyTextView3;

	private double expense = 0;
	private double income = 0;
	private double amount = 0;

	private double pexpense = 0;
	private double pincome = 0;
	private double pamount = 0;

	private OnUpdateNavigationListener onUpdateNavigationListener;
	public static SparseArray<Fragment> registeredMonthFragments;

	private long selectedDate;
	private int viewPagerPosition;
	private Thread mThread;
	private int offset;
	private int position = 10000;

	private ImageView trend1;
	private TextView percent_txt1;
	private ImageView trend2;
	private TextView percent_txt2;
	private ImageView trend3;
	private TextView percent_txt3;
	
	private TextView currency_txt1;
	private TextView currency_txt2;
	private TextView currency_txt3;
	private final static long DAYMILLIS = 86400000L - 1L;
	
	private ArrayList<Long> mGroupList;
	private HashMap<String, Double> mChildMap;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				
				currency_txt1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
				currency_txt2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
				currency_txt3.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
				
				
				NumberFormat percentFormat = NumberFormat.getPercentInstance();
				percentFormat.setMinimumFractionDigits(2);

				// red d02f3a 208 47 58
				// green 539627 83 150 39
				expenseTextView.setText(MEntity.doublepoint2str(expense + ""));
				expenseTextView.setTextColor(Color.rgb(208, 47, 58));
				currencyTextView1.setTextColor(Color.rgb(208, 47, 58));
				incomeTextView.setText(MEntity.doublepoint2str(income + ""));
				incomeTextView.setTextColor(Color.rgb(83, 150, 39));
				currencyTextView2.setTextColor(Color.rgb(83, 150, 39));

				if (amount > 0) {
					amountTextView
							.setText(MEntity.doublepoint2str(amount + ""));
					amountTextView.setTextColor(Color.rgb(83, 150, 39));
					currencyTextView3.setTextColor(Color.rgb(83, 150, 39));
				} else {
					amountTextView.setText(MEntity.doublepoint2str((0 - amount)
							+ ""));
					amountTextView.setTextColor(Color.rgb(208, 47, 58));
					currencyTextView3.setTextColor(Color.rgb(208, 47, 58));
				}

				if ((expense - pexpense) > 0) {
					trend1.setImageResource(R.drawable.up);
					if (pexpense == 0) {
						percent_txt1.setText(percentFormat.format(1));
					} else {
						percent_txt1.setText(percentFormat
								.format((expense - pexpense) / pexpense));
					}

				} else if ((expense - pexpense) == 0) {
					trend1.setImageResource(R.drawable.balence);
					percent_txt1.setText(percentFormat.format(0));
				} else {

					trend1.setImageResource(R.drawable.down);
					if (pexpense == 0) {
						percent_txt1.setText(percentFormat.format(1));
					} else {
						percent_txt1.setText(percentFormat
								.format((pexpense - expense) / pexpense));
					}
				}

				if ((income - pincome) > 0) {
					trend2.setImageResource(R.drawable.up);
					if (pincome == 0) {
						percent_txt2.setText(percentFormat.format(1));
					} else {
						percent_txt2.setText(percentFormat
								.format((income - pincome) / pincome));
					}

				} else if ((income - pincome) == 0) {
					trend2.setImageResource(R.drawable.balence);
					percent_txt2.setText(percentFormat.format(0));
				} else {
					trend2.setImageResource(R.drawable.down);
					if (pincome == 0) {
						percent_txt2.setText(percentFormat.format(1));
					} else {
						percent_txt2.setText(percentFormat
								.format((pincome - income) / pincome));
					}
				}

				if ((amount - pamount) > 0) {
					trend3.setImageResource(R.drawable.up);
					if (pamount == 0) {
						percent_txt3.setText(percentFormat.format(1));
					} else {
						double percent = (amount - pamount) / pamount;
						if (percent<0) {
							percent = 0-percent;
						} 
						percent_txt3.setText(percentFormat
								.format(percent));
					}

				} else if ((amount - pamount) == 0) {
					trend3.setImageResource(R.drawable.balence);
					percent_txt3.setText(percentFormat.format(0));
				} else {
					if (pamount == 0) {
						percent_txt3.setText(percentFormat.format(1));
					} else {
						trend3.setImageResource(R.drawable.down);
						double percent = (amount - pamount) / pamount;
						if (percent<0) {
							percent = 0-percent;
						} 
						percent_txt3.setText(percentFormat
								.format(percent));
					}
				}

				calendarGridViewAdapter.setCheckDat(MainActivity.selectedDate);
				calendarGridViewAdapter.setChildData(mChildMap);
				calendarGridViewAdapter.notifyDataSetChanged();

				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public MonthViewFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (FragmentActivity) activity;
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		currency_txt1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currency_txt2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currency_txt3.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		onUpdateNavigationListener = (OnUpdateNavigationListener) mActivity;
		Bundle bundle = getArguments();

		if (bundle != null) {
			position = bundle.getInt("position");
		}
		offset = position - MID_VALUE;
		selectedDate = MainActivity.selectedDate;
		month = (Calendar) Calendar.getInstance();
		
		mGroupList = new ArrayList<Long>();
		mChildMap =  new HashMap<String, Double>();
		
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		
		
		if (isVisibleToUser) {

			MainActivity.attachFragment = this;
			// weekCallBack.OnWeekSelected(selectedDate); // nouse
			//
			if (mThread == null) {
				mThread = new Thread(mTask);
				mThread.start();
			} else {
				mHandler.post(mTask);
			}

		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_month_view, container,
				false);

		expenseTextView = (TextView) view.findViewById(R.id.expense_txt);
		incomeTextView = (TextView) view.findViewById(R.id.income_txt);
		amountTextView = (TextView) view.findViewById(R.id.balance_txt);

		currency_txt1 = (TextView) view.findViewById(R.id.currency_txt1);
		currency_txt2 = (TextView) view.findViewById(R.id.currency_txt2);
		currency_txt3 = (TextView) view.findViewById(R.id.currency_txt3);

		
		trend1 = (ImageView) view.findViewById(R.id.trend1);
		percent_txt1 = (TextView) view.findViewById(R.id.percent_txt1);
		trend2 = (ImageView) view.findViewById(R.id.trend2);
		percent_txt2 = (TextView) view.findViewById(R.id.percent_txt2);
		trend3 = (ImageView) view.findViewById(R.id.trend3);
		percent_txt3 = (TextView) view.findViewById(R.id.percent_txt3);

		currencyTextView1 = (TextView) view.findViewById(R.id.currency_txt1);
		currencyTextView2 = (TextView) view.findViewById(R.id.currency_txt2);
		currencyTextView3 = (TextView) view.findViewById(R.id.currency_txt3);

		month.setTimeInMillis(MEntity
				.getFirstDayOfMonthMillis(getMonthByOffset(offset)));


		getCalendarDate(month);
		
		mGridView = (GridView) view.findViewById(R.id.mGridview);
		calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity);
		calendarGridViewAdapter.setGroupData(mGroupList,month.getTimeInMillis());
		mGridView.setAdapter(calendarGridViewAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				long mChooseTime = mGroupList.get(paramInt);
				
				selectedDate = mChooseTime;

				calendarGridViewAdapter.setCheckDat(selectedDate);
				calendarGridViewAdapter.notifyDataSetChanged();

				Calendar todaCalendar = Calendar.getInstance();
				
				int offset = MEntity.getWeekOffsetByDay(selectedDate,
						todaCalendar.getTimeInMillis());

				viewPagerPosition = MID_VALUE + offset;
				onUpdateNavigationListener.OnUpdateNavigation(0, mChooseTime);
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
			selectedDate = getMonthByOffset(offset);

			long beginTime = MEntity.getFirstDayOfMonthMillis(selectedDate);
			long endTime = MEntity.getLastDayOfMonthMillis(selectedDate);
			
			List<Map<String, Object>> mSumList = ReportDao.selectCategoryAllSum(mActivity, beginTime, endTime);  //查询该时间段的总额
			
			if (mSumList.size() > 0) { 
				
				for (int i = 0; i < mSumList.size(); i++) {
					double sum = (Double) mSumList.get(i).get("sum");
					int categoryType = (Integer) mSumList.get(i).get("categoryType");
					if (categoryType == 0 ) {
						expense = sum;
					}else if (categoryType == 1) {
						income = sum ;
					}
					
				}
			}
			
			amount = income - expense;

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(selectedDate);
			calendar.add(Calendar.MONTH, -1);

			long preBeginTime = MEntity.getFirstDayOfMonthMillis(calendar
					.getTimeInMillis());
			long preEndTime = MEntity.getLastDayOfMonthMillis(calendar
					.getTimeInMillis());
			
			List<Map<String, Object>> mPreSumList = ReportDao.selectCategoryAllSum(mActivity, preBeginTime, preEndTime);  //查询该时间段的总额
			
			if (mSumList.size() > 0) { 
				
				for (int i = 0; i < mSumList.size(); i++) {
					double sum = (Double) mSumList.get(i).get("sum");
					int categoryType = (Integer) mSumList.get(i).get("categoryType");
					if (categoryType == 0 ) {
						pexpense = sum;
					}else if (categoryType == 1) {
						pincome = sum ;
					}
					
				}
			}
			
			pamount = pincome - pexpense;
			
			mChildMap = ReportDao
					.selectTransactionGroup(mActivity, beginTime, endTime);
			
			if (position == 9998) {
				Log.v("mtag", "position"+position+" mChildMap "+mChildMap);
				Log.v("mtag", "position"+position+" mChildMap大小 "+mChildMap.size());
				Log.v("mtag", "时间范围"+turnToDateKey(beginTime)+"  "+turnToDateKey(endTime));
			}

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	
	public String turnToDateKey(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	
	public void getCalendarDate( Calendar month) {
		// clear items
		mGroupList.clear();
		
		month.setFirstDayOfWeek(Calendar.SUNDAY); // testFor
		month.set(Calendar.HOUR_OF_DAY, 0);
		month.set(Calendar.MINUTE, 0);
		month.set(Calendar.SECOND, 0);
		month.set(Calendar.MILLISECOND, 0);
		
		Calendar pmonth = (Calendar) month.clone(); 
		
		// month start day. ie; sun, mon, etc
		int firstDay = month.get(Calendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		int maxWeeknumber = month.getActualMaximum(Calendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		int mnthlength = maxWeeknumber * 7;
		
		if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
			pmonth.set((month.get(Calendar.YEAR) - 1),month.getActualMaximum(Calendar.MONTH), 1);
		} else {
			pmonth.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
		}
		int maxP = pmonth.getActualMaximum(Calendar.DAY_OF_MONTH);// previous month maximum day 31,30....
		
		int calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		Calendar pmonthmaxset = (Calendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(Calendar.DAY_OF_MONTH, calMaxP + 1);
		/**
		 * filling calendar gridview.
		 */
		for (int n = 0; n < mnthlength; n++) {
			mGroupList.add(pmonthmaxset.getTimeInMillis());
			pmonthmaxset.add(Calendar.DATE, 1);
		}
	}


	public List<Map<String, Object>> filterDataByTime(
			List<Map<String, Object>> mData) {// Transaction根据时间分类计算

		List<Map<String, Object>> mReturnList = new ArrayList<Map<String, Object>>();
		ArrayList<Long> mTemlist = new ArrayList<Long>();

		for (Map<String, Object> mMap : mData) {
			long dateTime = (Long) mMap.get("dateTime");
			Log.v("mtag", "dateTime"+(dateTime));
			mTemlist.add(getMilltoDateInt(dateTime));
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
		Log.v("mtag", "1mReturnList"+mReturnList);
		for (Map<String, Object> iMap : mReturnList) {
			long dayTime = (Long) iMap.get("dateTime");
			BigDecimal b1 = new BigDecimal("0");
			BigDecimal b2 = new BigDecimal("0");

			for (Map<String, Object> mMap : mData) {
				long dateTime = (Long) mMap.get("dateTime");
				String amount = (String) mMap.get("amount");
				int expenseAccount = (Integer) mMap.get("expenseAccount");
				int incomeAccount = (Integer) mMap.get("incomeAccount");

				if (dayTime <= dateTime && (dayTime+DAYMILLIS) > dateTime) {

					Log.v("mtag", "dayTime"+dayTime);
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
		Log.v("mtag", "2mReturnList"+mReturnList);
		return mReturnList;
	}

	public long getMonthByOffset(int offset) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		long returnDate = calendar.getTimeInMillis();

		return MEntity.getFirstDayOfMonthMillis(returnDate);
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

	public static long getMilltoDateInt(long datetime) { //除去时分秒
		Date date1 = new Date();
		date1.setTime(datetime);
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis();

		return nowMillis;
	}

	
	@Override
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mHandler.post(mTask);

		month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(selectedDate));
//		calendarGridViewAdapter.refreshDays();
		calendarGridViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void OnUpdateMonth() {
		// TODO Auto-generated method stub
		mHandler.post(mTask);
	}

	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		mHandler.post(mTask);
	}

}
