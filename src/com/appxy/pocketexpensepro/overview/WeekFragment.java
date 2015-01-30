package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateWeekSelectListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;
import com.appxy.pocketexpensepro.reports.ReportDao;
import com.crashlytics.android.internal.f;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class WeekFragment extends Fragment implements OnUpdateWeekSelectListener, OnSyncFinishedListener{

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private final static long DAYMILLIS = 86400000L;

	private Activity mActivity;
	private LayoutInflater mInflater;
	private GridView mGridView;
	private OnWeekSelectedListener weekCallBack = mWeekCallBackListener;
	private int position = 10000;
	private GridViewAdapter mAdapter;
	private List<Map<String, Object>> mDataList;
	private Thread mThread;
	private long selectedDate;
	private int offset;
	
	private long firstDayDate ;
	private ArrayList<Long> mGroupList;
	private HashMap<String, Double> mChildMap; 

	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				if (mDataList != null) {
					mAdapter.setChildDate(mChildMap);
					mAdapter.setChoosedTime(MainActivity.selectedDate);
					mAdapter.notifyDataSetChanged();
				}
				
				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private static OnWeekSelectedListener mWeekCallBackListener = new OnWeekSelectedListener() {

		@Override
		public void OnWeekSelected(long selectedDate) {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
		try {
			weekCallBack = (OnWeekSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnWeekSelectedListener");
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {

			MainActivity.attachFragment = this;
			
			weekCallBack.OnWeekSelected(MainActivity.selectedDate);
			
			if (mThread == null) {
				mThread = new Thread(mTask);
				mThread.start();
			}else {
				mHandler.post(mTask);
			}
			
		}
	}

	public WeekFragment() {
		
	}
	
	public String turnToDateKey(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			position = bundle.getInt("position");
		}
		offset = position - MID_VALUE;
		selectedDate = MainActivity.selectedDate;
		mDataList = new ArrayList<Map<String, Object>>();
		
		mGroupList = new ArrayList<Long>();
		mChildMap =  new HashMap<String, Double>();
		
		
		long firstWeekDay = getWeekByOffset(offset);
		
		firstDayDate = firstWeekDay;
		
		for (int i = 0; i < 7; i++) {
			
			mGroupList.add(firstWeekDay);
			firstWeekDay = firstWeekDay + DAYMILLIS;
		}
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;
		View view = inflater.inflate(R.layout.fragment_week, container, false);
		mGridView = (GridView) view.findViewById(R.id.mGridView);
		mAdapter = new GridViewAdapter(mActivity);
		mAdapter.setGroupDate(mGroupList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(mListener);
		
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}
		
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		weekCallBack = mWeekCallBackListener;
	}

	private OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {
			// TODO Auto-generated method stub
			selectedDate = (Long) mGroupList.get(paramInt);
			
			weekCallBack.OnWeekSelected(selectedDate);
			mAdapter.setChoosedTime(selectedDate);
			mAdapter.notifyDataSetChanged();
			
			
		}
	};

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			mChildMap = ReportDao
					.selectTransactionGroup(mActivity, firstDayDate, firstDayDate+7*DAYMILLIS);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
			
		}
	};

	public long getFirstDayByOffset(int offset) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		long returnDate = calendar.getTimeInMillis();

		if (offset == 0) {
			return returnDate;
		} else {
			long offsetDate = offset * 7 * DAYMILLIS;
			calendar.setTimeInMillis(returnDate + offsetDate);
			calendar.setFirstDayOfWeek(Calendar.SUNDAY);
			calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
			returnDate = calendar.getTimeInMillis();
		}

		return returnDate;
	}

	public long getWeekByOffset(int offset) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long returnDate = calendar.getTimeInMillis();

		long offsetDate = offset * 7 * DAYMILLIS;
		calendar.setTimeInMillis(returnDate + offsetDate);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		returnDate = calendar.getTimeInMillis();

		return returnDate;
	}

	@Override
	public void OnUpdateWeekSelect(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
	    mHandler.post(mTask);
	    weekCallBack.OnWeekSelected(selectedDate);
	
		mAdapter.setChoosedTime(selectedDate);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		
		weekCallBack.OnWeekSelected(MainActivity.selectedDate);
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}else {
			mHandler.post(mTask);
		}
		
	}
	

}
