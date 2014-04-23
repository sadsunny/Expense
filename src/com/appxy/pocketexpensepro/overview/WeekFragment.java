package com.appxy.pocketexpensepro.overview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

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
public class WeekFragment extends Fragment {

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

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				
				Log.v("mtest", "end");
				Log.v("mtest", "mDataList1112231"+mDataList);
				
				if (mDataList != null) {
					mAdapter.setDate(mDataList);
					mAdapter.notifyDataSetChanged();
					Log.v("mtest", "mDataList111"+mDataList);
				}

				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public interface OnWeekSelectedListener {
		public void OnWeekSelected(long selectedDate);
	}

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
	public void onDetach() {
		super.onDetach();
		weekCallBack = mWeekCallBackListener;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {
			int offset = position - MID_VALUE;
			weekCallBack.OnWeekSelected(getFirstDayByOffset(offset));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			position = bundle.getInt("position");
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
		mGridView.setAdapter(mAdapter);

		mDataList = new ArrayList<Map<String, Object>>();
		
		int offset = position - MID_VALUE;
		long firstDayDate = getWeekByOffset(offset);
		mDataList.clear();
		
		for (int i = 0; i < 7; i++) {
			Map<String, Object> mMap = new HashMap<String, Object>();
			mMap.put("weekTime", firstDayDate);
			firstDayDate = firstDayDate+DAYMILLIS;
			mDataList.add(mMap);
		}
		
		mAdapter.setDate(mDataList);
		mAdapter.notifyDataSetChanged();
		
		mGridView.setOnItemClickListener(mListener);
		
//		if (mThread == null) {
//			mThread = new Thread(mTask);
//			mThread.start();
//		}
		
		return view;
	}
	
	private OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {
			// TODO Auto-generated method stub
			long choosedTime = (Long) mDataList.get(paramInt).get("weekTime");
			weekCallBack.OnWeekSelected(choosedTime);
		}
	};

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int offset = position - MID_VALUE;
			long firstDayDate = getFirstDayByOffset(offset);
			mDataList.clear();
			
			for (int i = 0; i < 7; i++) {
				Map<String, Object> mMap = new HashMap<String, Object>();
				mMap.put("weekTime", firstDayDate);
				firstDayDate = firstDayDate+DAYMILLIS;
				mDataList.add(mMap);
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	public long getFirstDayByOffset(int offset) {

		Calendar calendar = Calendar.getInstance();
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
		long returnDate = calendar.getTimeInMillis();

			long offsetDate = offset * 7 * DAYMILLIS;
			calendar.setTimeInMillis(returnDate + offsetDate);
			calendar.setFirstDayOfWeek(Calendar.SUNDAY);
			calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
			returnDate = calendar.getTimeInMillis();

		return returnDate;
	}

}
