package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateMonthListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MonthViewFragment extends Fragment implements OnUpdateListListener, OnUpdateMonthListener {
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
	private double expense;
	private double income;
	private double amount;
	private OnUpdateNavigationListener onUpdateNavigationListener;
	public static SparseArray<Fragment> registeredMonthFragments;

	private long selectedDate;
	private int viewPagerPosition;
	private Thread mThread;
    private int offset;
    private int position = 10000;
    
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				
				expenseTextView.setText(MEntity.doublepoint2str(expense + ""));
				incomeTextView.setText(MEntity.doublepoint2str(income + ""));
				amountTextView.setText(MEntity.doublepoint2str(amount + ""));

				calendarGridViewAdapter.setCheckDat(MainActivity.selectedDate);
				calendarGridViewAdapter.setDataList(mGridDataList);
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
		onUpdateNavigationListener = (OnUpdateNavigationListener) mActivity;
		Bundle bundle = getArguments();

		if (bundle != null) {
			position = bundle.getInt("position");
		}
		offset = position - MID_VALUE;
		selectedDate = MainActivity.selectedDate;
		month = (Calendar) Calendar.getInstance();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {

			// weekCallBack.OnWeekSelected(selectedDate);
			//
			 if (mThread == null) {
			 mThread = new Thread(mTask);
			 mThread.start();
			 }else {
			 mHandler.post(mTask);
			 }
			 Log.v("mtest", "主actiivity中的日期"+MEntity.getMilltoDate(MainActivity.selectedDate));
			 
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
		amountTextView = (TextView) view.findViewById(R.id.amount_txt);
		
		Locale.setDefault(Locale.ENGLISH);
		
		month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(getMonthByOffset(offset)));
		
//		Log.v("mtest", "offset"+offset);
//		Log.v("mtest", "offset代表的时间"+MEntity.getMilltoDate(getMonthByOffset(offset)));
		
		mGridView = (GridView) view.findViewById(R.id.mGridview);
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
				selectedDate = mChooseTime;

				calendarGridViewAdapter.setCheckDat(selectedDate);
				calendarGridViewAdapter.notifyDataSetChanged();

				int offset = MEntity.getWeekOffsetByDay(selectedDate,
						MEntity.getNowMillis());

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

	@Override
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mHandler.post(mTask);

		month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(selectedDate));
		calendarGridViewAdapter.refreshDays();
		calendarGridViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void OnUpdateMonth() {
		// TODO Auto-generated method stub
		mHandler.post(mTask);
	}

}
