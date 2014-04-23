package com.appxy.pocketexpensepro.overview;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class OverviewFragment extends Fragment implements MainActivity.OnUpdateListListener {
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private ViewPager mViewPager;
	private FragmentActivity mActivity;
	private ViewPagerAdapter mViewPagerAdapter;
	private WeekFragment weekFragment;
	private ListView mListView;
	private List<Map<String, Object>> mDataList;
	private ListViewAdapter mListViewAdapter;
	private Thread mThread;
	private long selectedDate;

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
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Log.v("mtest", "1onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_overview, container,
				false);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPagerAdapter = new ViewPagerAdapter(
				mActivity.getSupportFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(MID_VALUE);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {

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
		mListViewAdapter = new ListViewAdapter(mActivity);
		mListView.setAdapter(mListViewAdapter);

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
			mDataList = OverViewDao.selectTransactionByTime(mActivity, selectedDate);
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	public class ViewPagerAdapter extends FragmentStatePagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			WeekFragment weekFragment = new WeekFragment();
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
			return super.instantiateItem(arg0, arg1);
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
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mDataList = OverViewDao.selectTransactionByTime(mActivity, selectedDate);
		mListViewAdapter.setAdapterDate(mDataList);
		mListViewAdapter.notifyDataSetChanged();
		
		Log.v("mdb", "shauxui mDataList" +mDataList);
		Log.v("mdb", "selectedDate back to fragment" +selectedDate);
		mHandler.post(mTask);
	}

}
