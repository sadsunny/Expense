package com.appxy.pocketexpensepro.bills;

import java.io.Serializable;
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
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnActivityToBillListener;
import com.appxy.pocketexpensepro.expinterface.OnBillToActivityListener;
import com.appxy.pocketexpensepro.service.NotificationService;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;

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
	private long selectDate;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;
	private AlertDialog editDialog;
	private ListView diaListView;
	private DialogDeleteBillAdapter dialogEditBillAdapter ;
	
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
				calendarGridViewAdapter.setCheckDat(MainActivity.selectedMonth);
				
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
		mInflater = LayoutInflater.from(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bill_month, container,
				false);
		selectDate =  MainActivity.selectedMonth;
		mViewPager = (ViewPager) view.findViewById(R.id.mPager);
		mGridView = (GridView) view.findViewById(R.id.mGridview);
		mListView = (ListView) view.findViewById(R.id.mListView);
		billListViewAdapter = new BillListViewAdapter(mActivity);
		mListView.setAdapter(billListViewAdapter);
		mListView.setDividerHeight(0);
		mListView.setOnItemClickListener(mClickListener);
		mListView.setOnItemLongClickListener(mLongClickListener);
		
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
				selectDate = mChooseTime;

				calendarGridViewAdapter.setCheckDat(mChooseTime);
				calendarGridViewAdapter.notifyDataSetChanged();
				
				MainActivity.selectedMonth = mChooseTime;
				
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
	
	private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			    Map<String, Object> mMap = mListViewData.get(arg2);
				Intent intent = new Intent();
				intent.putExtra("dataMap",(Serializable)mMap);
				intent.setClass(mActivity, BillDetailsActivity.class);
				startActivityForResult(intent, 16);
			
		}
	};
	
	private OnItemLongClickListener mLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			
			// TODO Auto-generated method stub
			
			final int _id = (Integer) mListViewData.get(arg2).get("_id");
			final int indexflag = (Integer) mListViewData.get(arg2).get("indexflag");
			final Map<String, Object> mMap =  mListViewData.get(arg2);

			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,null);

			String[] data = { "Edit", "Delete" };
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
						Intent intent = new Intent();
						intent.putExtra("dataMap",(Serializable)mMap);
						intent.setClass(mActivity, BillEditActivity.class);
						startActivityForResult(intent, 19);
						alertDialog.dismiss();

					} else if (arg2 == 1) {
						judgementDialog(indexflag, _id, mMap);
						alertDialog.dismiss();
						mHandler.post(mTask);
						MonthFragment  monthFragment = (MonthFragment) billMonthViewPagerAdapter.registeredFragments.get(MID_VALUE+ MEntity.getOffsetByMonth(MainActivity.selectedMonth));
						monthFragment.refresh();
					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
		}
	};
	

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			long thSelectedTime = MainActivity.selectedMonth;
			calendarGridViewAdapter.setCheckDat(selectDate);
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
	
	
	public void judgementDialog(int mFlag , int mId , Map<String, Object> mMap) { 

		if (mFlag == 0) {

			deleteThisBill(mFlag,mId,mMap);
			mHandler.post(mTask);
			
		}else if (mFlag == 1) {

			int temPaydate = judgeTemPayDate(mId);		
			long firstPayDate = judgePayDate(mId);

			if (temPaydate > 0) {
				deleteThisBill(mFlag,mId,mMap);
				mHandler.post(mTask);
				
			} else {

				if (firstPayDate == 0) {

					editDialogShow(mFlag,mId,mMap);

				} else {

					deleteThisBill(mFlag,mId,mMap);
					mHandler.post(mTask);
				}

			}

		}else if(mFlag == 2){
			long firstPayDate = judgePayDate(mId);
			long mbk_billDuedate = (Long)mMap.get("ep_billDueDate");
			if (firstPayDate == 0) {
				editDialogShow(mFlag,mId,mMap);
			} else {

				if (firstPayDate < mbk_billDuedate) {

					editDialogShow(mFlag,mId,mMap);

				} else {
					deleteThisBill(mFlag,mId,mMap);
					mHandler.post(mTask);
				}

			}

		}else if(mFlag == 3){

			if (mMap.containsKey("billItemHasBillRule")) {
				int billo_id = (Integer)mMap.get("billItemHasBillRule");
				long firstPayDate = judgePayDate(billo_id);
				long mbk_billDuedate = (Long)mMap.get("ep_billDueDate");
				
				if (firstPayDate == 0) {
					editDialogShow(mFlag,mId,mMap);
				} else {

					if (firstPayDate < mbk_billDuedate) {

						editDialogShow(mFlag,mId,mMap);
						
					} else {
						deleteThisBill(mFlag,mId,mMap);
						mHandler.post(mTask);
					}
				}
			}else {

				deleteThisBill(mFlag,mId,mMap);
				mHandler.post(mTask);
		}

		}
	}
	
	public void editDialogShow(final int mFlag , final int mId , final Map<String, Object> mMap) {

		View  dialogview = mInflater.inflate(R.layout.dialog_upcoming_item_operation,null); 
		dialogEditBillAdapter = new DialogDeleteBillAdapter(mActivity);
		diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
		diaListView.setAdapter(dialogEditBillAdapter);
		diaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					
					deleteThisBill(mFlag,mId,mMap);
				    mHandler.post(mTask);
					editDialog.dismiss();

				} else if (arg2 == 1) {
					deleteAllFuture(mFlag,mId,mMap);
					mHandler.post(mTask);
					editDialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Details");
		builder.setView(dialogview);
		editDialog = builder.create();
		editDialog.show();
	}
	
	public void deleteAllFuture(int mFlag ,int theId ,Map<String, Object> mMap) {

		if(mFlag == 1){
			long row = BillsDao.deleteBill(mActivity, theId);
			BillsDao.deleteBillObjectByParId(mActivity, theId);
			if (row > 0) {
				 Intent service=new Intent(mActivity, NotificationService.class);  
				 mActivity.startService(service);  
			}
			
		}else if(mFlag == 2){

			billVirtualFutuDelete(theId,mMap);

		}else if (mFlag == 3) {
			int billo_id =0 ;

			if (mMap.containsKey("billObjecthasBill")) {
				billo_id = (Integer)mMap.get("billObjecthasBill");
			}else{
				
			}
			billVirtualFutuDelete(billo_id,mMap);
		}
	}
	
	public long billVirtualFutuDelete(int rowid ,Map<String, Object> mMap){
		
		long row = 0;
		long bk_billDuedate = (Long) mMap.get("ep_billDueDate"); // 相当于事件的开始日期
		long bk_billEndDate = (Long) mMap.get("ep_billEndDate"); // 重复事件的截止日期
		int bk_billRepeatType = (Integer) mMap.get("ep_recurringType");

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(bk_billDuedate);
		
		if (bk_billRepeatType == 1) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-7);

		} else if (bk_billRepeatType == 2) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-14);

		} else if (bk_billRepeatType == 3) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-28);

		} else if (bk_billRepeatType == 4) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-15);

		} else if (bk_billRepeatType == 5) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, -1);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, -1 - 1);
				} else {
					calendar.add(Calendar.MONTH, -1);
				}


		} else if (bk_billRepeatType == 6) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, -2);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, -2 - 2);
				} else {
					calendar.add(Calendar.MONTH, -2);
				}


		} else if (bk_billRepeatType == 7) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, -3);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, -3 - 3);
				} else {
					calendar.add(Calendar.MONTH, -3);
				}


		} else if (bk_billRepeatType == 8) {

				calendar.add(Calendar.YEAR, -1);

		}
		

		long preDuedate = calendar.getTimeInMillis();

		row = BillsDao.updateBillDateRule(mActivity, rowid, preDuedate);
		BillsDao.deleteBillObjectByAfterDate(mActivity, bk_billDuedate);
		if (row > 0) {
			 Intent service=new Intent(mActivity, NotificationService.class);  
			 mActivity.startService(service);  
		}
		return row;
	}
	
	
	public int judgeTemPayDate(int b_id) {

		List<Map<String, Object>> mList = BillsDao.selectTransactionByBillRuleId(mActivity, b_id);
		if (mList.size() > 0) {
			return 1;
		}else {
			return 0;
		}

	}
	
	public long judgePayDate(int b_id) {

		List<Map<String, Object>> dataList = BillsDao.selectBillItemByRuleId(mActivity, b_id);
		
		if (dataList.size() > 0) {
			ArrayList<Long> OPayList = new ArrayList<Long>();
			long reData = 0;
			for (Map<String, Object> oMap:dataList) {
				int Object_id = (Integer) oMap.get("_id");
				long bk_billODueDate = (Long) oMap.get("ep_billDueDate");
				List<Map<String, Object>> mList = BillsDao.selectTransactionByBillItemId(mActivity, Object_id);
				
				if (mList.size()  > 0) {
					OPayList.add(bk_billODueDate);
				} 
			}
			
			if (OPayList.size()>0) {

				long max = OPayList.get(0);
				for (int i = 0; i < OPayList.size(); i++) {

					if (OPayList.get(i)>max) {
						max=OPayList.get(i);
					} 

				}
				reData = max;
			} else {
				reData = 0;
			}

			return reData;

		}else {
			return 0;
		}

	}
	
	public void deleteThisBill(int mFlag ,int theId, Map<String, Object> mMap) {

		if (mFlag == 0) {
			long row = BillsDao.deleteBill(mActivity, theId);
			if (row > 0) {
				 Intent service=new Intent(mActivity, NotificationService.class);  
				 mActivity.startService(service);  
			}
		} else if(mFlag == 1){
			billParentDelete(theId,mMap);
		}else if(mFlag == 2){
			billVirtualThisDelete(theId,mMap);
		}else if (mFlag == 3) {
			long row = BillsDao.deleteBillObject(mActivity, theId);
			if (row > 0) {
				 Intent service=new Intent(mActivity, NotificationService.class);  
				 mActivity.startService(service);  
			}
		}

	}
	
	public long billVirtualThisDelete(int rowid, Map<String, Object> mMap){
		long ep_billItemDueDate = (Long)mMap.get("ep_billDueDate");
		long row = BillsDao.insertBillItem(mActivity, 1, "1", ep_billItemDueDate,
				ep_billItemDueDate, ep_billItemDueDate, " ", "",
				1, 1, ep_billItemDueDate,
				rowid, 1, 1);
		if (row > 0) {
			 Intent service=new Intent(mActivity, NotificationService.class);  
			 mActivity.startService(service);  
		}
		return row;
	}
	
	
	
	public long billParentDelete(int rowid, Map<String, Object> mMap){
		
		long row = 0;
		long bk_billDuedate = (Long) mMap.get("ep_billDueDate"); // 相当于事件的开始日期
		long bk_billEndDate = (Long) mMap.get("ep_billEndDate"); // 重复事件的截止日期
		int bk_billRepeatType = (Integer) mMap.get("ep_recurringType");

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(bk_billDuedate);
		
		if (bk_billRepeatType == 1) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+7);

		} else if (bk_billRepeatType == 2) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+14);

		} else if (bk_billRepeatType == 3) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+28);

		} else if (bk_billRepeatType == 4) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+15);

		} else if (bk_billRepeatType == 5) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, 1);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, 1 + 1);
				} else {
					calendar.add(Calendar.MONTH, 1);
				}


		} else if (bk_billRepeatType == 6) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, 2);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, 2 + 2);
				} else {
					calendar.add(Calendar.MONTH, 2);
				}


		} else if (bk_billRepeatType == 7) {


				Calendar calendarCloneCalendar = (Calendar) calendar
						.clone();
				int currentMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);
				calendarCloneCalendar.add(Calendar.MONTH, 3);
				int nextMonthDay = calendarCloneCalendar
						.get(Calendar.DAY_OF_MONTH);

				if (currentMonthDay > nextMonthDay) {
					calendar.add(Calendar.MONTH, 3 + 3);
				} else {
					calendar.add(Calendar.MONTH, 3);
				}


		} else if (bk_billRepeatType == 8) {

				calendar.add(Calendar.YEAR, 1);

		}
		long nextDuedate = calendar.getTimeInMillis();
		
		if (nextDuedate > bk_billEndDate) { 

			row =BillsDao.deleteBill(mActivity, rowid);
			if (row > 0) {
				 Intent service=new Intent(mActivity, NotificationService.class);  
				 mActivity.startService(service);  
			}

		} else {

			long rowUp = BillsDao.updateBillDateRule(mActivity, rowid, nextDuedate);
			BillsDao.deleteBillPayTransaction(mActivity, rowid);
			if (rowUp > 0) {
				 Intent service=new Intent(mActivity, NotificationService.class);  
				 mActivity.startService(service);  
			}
		}
		return row;
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
				MonthFragment  monthFragment = (MonthFragment) billMonthViewPagerAdapter.registeredFragments.get(MID_VALUE+ MEntity.getOffsetByMonth(MainActivity.selectedMonth));
				monthFragment.refresh();
			}
			break;
			
		case 16:

			if (data != null) {
				mHandler.post(mTask);
				MonthFragment  monthFragment = (MonthFragment) billMonthViewPagerAdapter.registeredFragments.get(MID_VALUE+ MEntity.getOffsetByMonth(MainActivity.selectedMonth));
				monthFragment.refresh();
			}
			break;
			
		case 19:

			if (data != null) {
				mHandler.post(mTask);
				MonthFragment  monthFragment = (MonthFragment) billMonthViewPagerAdapter.registeredFragments.get(MID_VALUE+ MEntity.getOffsetByMonth(MainActivity.selectedMonth));
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
			intent.putExtra("selectDate", selectDate);
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
