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
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnBackTimeListener;
import com.appxy.pocketexpensepro.expinterface.OnChangeStateListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateListListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateNavigationListener;
import com.appxy.pocketexpensepro.expinterface.OnUpdateWeekSelectListener;
import com.appxy.pocketexpensepro.expinterface.OnWeekSelectedListener;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.overview.budgets.EditBudgetActivity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AccelerateInterpolator;  

public class OverviewFragment extends Fragment implements 
		OnChangeStateListener,OnUpdateListListener {
	
//	 try {               
//    Field mField = ViewPager.class.getDeclaredField("mScroller");               
//    mField.setAccessible(true);     
//     //设置加速度 ，通过改变FixedSpeedScroller这个类中的mDuration来改变动画时间（如mScroller.setmDuration(mMyDuration);）    
//    mScroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());           
//    mField.set(mViewPager, mScroller);           
//    } catch (Exception e) {           
//        e.printStackTrace();  
//    }   
	
	private static final int MID_VALUE = 10000;
	private static final int MAX_VALUE = 20000;
	private static final int MSG_FAILURE = 0;
	private static final int MSG_SUCCESS = 1;

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
	private OnBackTimeListener onBackTimeListener;

	public static SparseArray<Fragment> registeredFragments;
	
	private RelativeLayout budgetRelativeLayout ;
	private TextView budgeTextView;
	private ProgressBar mProgressBar;
	private FixedSpeedScroller mScroller; 
	
	private double budgetAmount;
	private double transactionAmount;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;
	private long argumentsDate;
	private int currentPosition;
	private OnUpdateWeekSelectListener onUpdateWeekSelectListener;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				if (mDataList != null) {

					mListViewAdapter.setAdapterDate(mDataList);
					mListViewAdapter.notifyDataSetChanged();
				}
				
				mProgressBar.setMax((int)budgetAmount);
				mProgressBar.setProgress((int) transactionAmount);
				
				budgeTextView.setText(MEntity.doublepoint2str((budgetAmount-transactionAmount)+""));
				
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
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			argumentsDate = bundle.getLong("selectedDate");
		}
		int mOffset = MEntity.getWeekOffsetByDay(argumentsDate, System.currentTimeMillis());
		Log.v("mtest", "mOffset"+mOffset);
		currentPosition = MID_VALUE + mOffset;
		selectedDate = argumentsDate;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_overview, container,
				false);
		mInflater = inflater;

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
		
		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mViewPagerAdapter = new ViewPagerAdapter(mActivity.getSupportFragmentManager());
		
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(currentPosition);
		
		viewPagerPosition = currentPosition;
		
		 final OnUpdateNavigationListener onUpdateNavigationListener;
		 onUpdateNavigationListener = (OnUpdateNavigationListener) mActivity;
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						viewPagerPosition = position;
						
						if (position == MID_VALUE) {
							Calendar calendar1 = Calendar.getInstance();
							calendar1.set(Calendar.HOUR_OF_DAY, 0);
							calendar1.set(Calendar.MINUTE, 0);
							calendar1.set(Calendar.SECOND, 0);
							calendar1.set(Calendar.MILLISECOND, 0);
							onUpdateNavigationListener.OnUpdateNavigation(calendar1.getTimeInMillis());
						}else {
							long theSelectedDate = MEntity.getFirstDayByOffset(position-MID_VALUE);
							onUpdateNavigationListener.OnUpdateNavigation(theSelectedDate);
						}
						
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
		mListView.setOnItemLongClickListener(longClickListener);
		
		
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

			List<Map<String, Object>>  mBudgetList = OverViewDao.selectBudget(mActivity);
			List<Map<String, Object>> mTransferList = OverViewDao.selectBudgetTransfer(mActivity);
			long firstDay = MEntity.getFirstDayOfMonthMillis(MainActivity.selectedDate);
			long lastDay = MEntity.getLastDayOfMonthMillis(MainActivity.selectedDate);
			
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
	
	private OnItemLongClickListener longClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int _id = (Integer) mDataList.get(arg2).get("_id");
			final Map<String, Object> mMap = mDataList.get(arg2);
			
			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,null);

			String[] data = { "Duplicate", "Delete" };
			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(mActivity, data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					if (arg2 == 0) {

						Calendar c = Calendar.getInstance(); //处理为当天固定格式时间
						Date date = new Date(c.getTimeInMillis());
						SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
						try {
							c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(sdf.format(date)));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						String amount = (String) mMap.get("amount");
						long dateTime = c.getTimeInMillis();
						int isClear = (Integer) mMap.get("isClear");
						String notes = (String) mMap.get("notes");
						String photoName = (String) mMap.get("photoName");
						int recurringType = (Integer) mMap.get("recurringType");
						int category = (Integer) mMap.get("category");
						String childTransactions = (String) mMap
								.get("childTransactions");
						int expenseAccount = (Integer) mMap
								.get("expenseAccount");
						int incomeAccount = (Integer) mMap.get("incomeAccount");
						int parTransaction = (Integer) mMap
								.get("parTransaction");
						int payee = (Integer) mMap.get("payee");

						long row = TransactionDao.insertTransactionAll(
								mActivity, amount,
								dateTime, isClear, notes, photoName,
								recurringType, category, childTransactions,
								expenseAccount, incomeAccount, parTransaction,
								payee);
						alertDialog.dismiss();


						mHandler.post(mTask);

					} else if (arg2 == 1) {

						long row = AccountDao.deleteTransaction(
								mActivity, _id);
						alertDialog.dismiss();
						mHandler.post(mTask);
					}
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(
					mActivity);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();
			return true;
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

				onBackTimeListener.OnBackTime(MainActivity.selectedDate, viewPagerPosition);// viewPagerPosition用于判断具体的fragment
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
	public void OnChangeState(int state) {
		// TODO Auto-generated method stub
		if (state == 0) {
			weekLayout.setVisibility(View.VISIBLE);
//			calendarLayout.setVisibility(View.GONE);
		} else {
			weekLayout.setVisibility(View.GONE);
//			calendarLayout.setVisibility(View.VISIBLE);
		}

	}

	public String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy, HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	@Override
	public void OnUpdateList(long selectedDate) {
		// TODO Auto-generated method stub
		this.selectedDate = selectedDate;
		mHandler.post(mTask);

	}

}
