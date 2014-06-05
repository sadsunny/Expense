package com.appxy.pocketexpensepro.bills;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

public class BillsFragmentMonth extends Fragment implements
		OnActivityToBillListener {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private FragmentActivity mActivity;
	private ViewPager mViewPager;
	private GridView mGridView;
	private ListView mListView;
	private BillMonthViewPagerAdapter billMonthViewPagerAdapter;
	public  Calendar month;
	private CalendarGridViewAdapter calendarGridViewAdapter;
	private Thread mThread;
	private OnBillToActivityListener onBillToActivityListener;
	private List<Map<String, Object>> mDateList;
	private List<Map<String, Object>> mCalendartList;
	private BillListViewAdapter billListViewAdapter;
	private List<Map<String, Object>> mListViewData;
	private View lineView;
	public BillsFragmentMonth() {
		
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				Log.v("mtest",
						"bill time MSG_SUCCESS"
								+ MEntity
										.turnMilltoMonthYear(MainActivity.selectedMonth));
				calendarGridViewAdapter.setMonth(month);
				calendarGridViewAdapter.setDataList(mCalendartList);
				calendarGridViewAdapter.notifyDataSetChanged();
				
				getListviewData(MainActivity.selectedMonth, mDateList);
				billListViewAdapter.setAdapterDate(mListViewData);
				billListViewAdapter.notifyDataSetChanged();
				
				if (mListViewData.size()>0) {
					lineView.setVisibility(View.VISIBLE);
				} else {
					lineView.setVisibility(View.INVISIBLE);
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
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		month = (Calendar) Calendar.getInstance();
		onBillToActivityListener = (OnBillToActivityListener) mActivity;
		mCalendartList = new ArrayList<Map<String, Object>>();
		mListViewData = new ArrayList<Map<String, Object>>();
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
		billListViewAdapter = new BillListViewAdapter(mActivity);
		mListView.setAdapter(billListViewAdapter);
		mListView.setDividerHeight(0);
		
		lineView = (View)view.findViewById(R.id.line_view2);
		 
		month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(MainActivity.selectedMonth));
		calendarGridViewAdapter = new CalendarGridViewAdapter(mActivity, month);
		mGridView.setAdapter(calendarGridViewAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				long mChooseTime = getMilltoDate(calendarGridViewAdapter
						.getDayString().get(paramInt));
				Log.v("mtest",
						"mChooseTime" + MEntity.getMilltoDate(mChooseTime));

				calendarGridViewAdapter.setCheckDat(mChooseTime);
				calendarGridViewAdapter.notifyDataSetChanged();
				
				getListviewData(mChooseTime, mDateList);
				billListViewAdapter.setAdapterDate(mListViewData);
				billListViewAdapter.notifyDataSetChanged();
				
				if (mListViewData.size()>0) {
					lineView.setVisibility(View.VISIBLE);
				} else {
					lineView.setVisibility(View.INVISIBLE);
				}
				
			}
		});

		billMonthViewPagerAdapter = new BillMonthViewPagerAdapter(
				mActivity.getSupportFragmentManager());
		mViewPager.setAdapter(billMonthViewPagerAdapter);

		int mOffset = MEntity.getOffsetByMonth(MainActivity.selectedMonth);
		Log.v("mtest", "重新获取的mOffset" + mOffset);
		mViewPager.setCurrentItem(MID_VALUE + mOffset);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						if (position == MID_VALUE) {

							Calendar calendar1 = Calendar.getInstance();
							calendar1.set(Calendar.HOUR_OF_DAY, 0);
							calendar1.set(Calendar.MINUTE, 0);
							calendar1.set(Calendar.SECOND, 0);
							calendar1.set(Calendar.MILLISECOND, 0);
							MainActivity.selectedMonth = calendar1
									.getTimeInMillis();
						} else {
							int offset = position - MID_VALUE;
							MainActivity.selectedMonth = MEntity
									.getFirstMonthByOffset(offset);
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
		} else {
			mHandler.post(mTask);
		}

		return view;
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			long thSelectedTime = MainActivity.selectedMonth;
			calendarGridViewAdapter.setCheckDat(thSelectedTime);
			month.setTimeInMillis(MEntity.getFirstDayOfMonthMillis(thSelectedTime));
			mDateList = RecurringEventBE.recurringData(mActivity,
					MEntity.getFirstDayOfMonthMillis(thSelectedTime),
					MEntity.getLastDayOfMonthMillis(thSelectedTime));
			judgePayment(mDateList);

			mCalendartList.clear();
			ArrayList<Long> mTemlist = new ArrayList<Long>();

			for (Map<String, Object> mMap : mDateList) {
				long dateTime = (Long) mMap.get("ep_billDueDate");
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
				mCalendartList.add(mMap);
			}

			for (Map<String, Object> iMap : mCalendartList) {
				long dayTime = (Long) iMap.get("dateTime");
				int never = 0;
				int part = 0;
				int all = 0;

				for (Map<String, Object> mMap : mDateList) {
					long dateTime = (Long) mMap.get("ep_billDueDate");
					int payState = (Integer) mMap.get("payState");

					if (dayTime == dateTime) {

						if (payState == 0) {
							never = 1;
						} else if (payState == 1) {
							part = 1;
						} else if (payState == 2) {
							all = 1;
						}

					}
				}

				iMap.put("never", never);
				iMap.put("part", part);
				iMap.put("all", all);

			}

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	public void getListviewData(long selectedGridDate,List<Map<String, Object>> dataList) {
		mListViewData.clear();
		for(Map<String, Object> bMap:dataList){ 
	   		long mbillduedate = (Long) bMap.get("ep_billDueDate");
	   		
	   		if (selectedGridDate == mbillduedate) {
		        
		        mListViewData.add(bMap);
			}
	   	 }
	}

	public void judgePayment(List<Map<String, Object>> dataList) {

		for (Map<String, Object> iMap : dataList) {

			int _id = (Integer) iMap.get("_id");
			String ep_billAmount = (String) iMap.get("ep_billAmount");
			int indexflag = (Integer) iMap.get("indexflag");
			BigDecimal b0 = new BigDecimal(ep_billAmount);

			if (indexflag == 0 || indexflag == 1) {

				List<Map<String, Object>> pDataList = BillsDao
						.selectTransactionByBillRuleId(mActivity, _id);
				BigDecimal b1 = new BigDecimal(0.0); // pay的总额
				for (Map<String, Object> pMap : pDataList) {
					String pAmount = (String) pMap.get("amount");
					BigDecimal b2 = new BigDecimal(pAmount);
					b1 = b1.add(b2);
				}
				double remain = (b1.subtract(b0)).doubleValue();
				double payTotal = b1.doubleValue();
				if (payTotal > 0) {

					if (remain >= 0) {
						iMap.put("payState", 2);// 全部支付
					} else {
						iMap.put("payState", 1);// 部分支付
					}

				} else {
					iMap.put("payState", 0);// 完全未支付
				}

			} else if (indexflag == 2) {

				iMap.put("payState", 0);

			}
			if (indexflag == 3) {

				List<Map<String, Object>> pDataList = BillsDao
						.selectTransactionByBillItemId(mActivity, _id);
				BigDecimal b1 = new BigDecimal(0.0); // pay的总额
				for (Map<String, Object> pMap : pDataList) {
					String pAmount = (String) pMap.get("amount");
					BigDecimal b2 = new BigDecimal(pAmount);
					b1 = b1.add(b2);
				}
				double remain = (b1.subtract(b0)).doubleValue();
				double payTotal = b1.doubleValue();
				if (payTotal > 0) {

					if (remain >= 0) {
						iMap.put("payState", 2);
					} else {
						iMap.put("payState", 1);
					}

				} else {
					iMap.put("payState", 0);
				}
			}

		}

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 8:

			if (data != null) {
				mHandler.post(mTask);
				MonthFragment  monthFragment = (MonthFragment) billMonthViewPagerAdapter.registeredFragments.get(MID_VALUE);
				monthFragment.refresh();
			}
			break;
		}
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
		
	}

}
