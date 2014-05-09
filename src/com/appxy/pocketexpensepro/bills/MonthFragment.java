package com.appxy.pocketexpensepro.bills;

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
import com.appxy.pocketexpensepro.expinterface.OnBillToActivityListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateWeekSelectListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;

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
public class MonthFragment extends Fragment {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private final static long DAYMILLIS = 86400000L;

	private Activity mActivity;
	private LayoutInflater mInflater;
	private GridView mGridView;
	private int position = 10000;
	private GridViewAdapter mAdapter;
	private List<Map<String, Object>> mDataList;
	private Thread mThread;
	private int offset;
	private long selectedMonth;
	private OnBillToActivityListener onBillToActivityListener;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				if (mDataList != null) {
					mAdapter.setDate(mDataList);
					mAdapter.setChoosedTime(MainActivity.selectedMonth);
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

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {

			if (mThread == null) {
				mThread = new Thread(mTask);
				mThread.start();
			} else {
				mHandler.post(mTask);
			}
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
		offset = position - MID_VALUE;
		mDataList = new ArrayList<Map<String, Object>>();
		onBillToActivityListener = (OnBillToActivityListener)mActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;
		View view = inflater.inflate(R.layout.fragment_bill_month_view,
				container, false);
		mGridView = (GridView) view.findViewById(R.id.mGridView);
		mAdapter = new GridViewAdapter(mActivity);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(mListener);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				MainActivity.selectedMonth = (Long) mDataList.get(arg2).get("monthTime");
				Log.v("mtest", "mGridView time"+MEntity.turnMilltoMonthYear((Long) mDataList.get(arg2).get("monthTime")));
				mAdapter.setChoosedTime((Long) mDataList.get(arg2).get("monthTime"));
				mAdapter.notifyDataSetChanged();
				onBillToActivityListener.OnBillToActivity();
			}
		});

		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {
			// TODO Auto-generated method stub

		}
	};

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			long firstMonth = MEntity.getFirstMonthByOffset(offset);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(firstMonth);

			mDataList.clear();

			for (int i = 0; i < 7; i++) {
				Map<String, Object> mMap = new HashMap<String, Object>();

				long firstDayOfMonth = calendar.getTimeInMillis();
				long lastDayOfMonth = MEntity
						.getLastDayOfMonthMillis(firstDayOfMonth);

				mMap.put("monthTime", firstDayOfMonth);

				List<Map<String, Object>> mTemList = BillsDao.selectBillByBE(
						firstDayOfMonth, lastDayOfMonth);
				int count = i;
				mMap.put("count", count);

				calendar.add(Calendar.MONTH, 1);
				mDataList.add(mMap);
			}

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

}
