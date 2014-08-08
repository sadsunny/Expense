package com.appxy.pocketexpensepro.bills;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity;
import com.appxy.pocketexpensepro.accounts.DialogItemAdapter;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransferActivity;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.service.NotificationService;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class BillsFragment extends Fragment{

	private FragmentActivity mActivity;
	private ExpandableListView mExpandableListView;
	private ExpandableListViewAdapter expandableListViewAdapter;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private Thread mThread;
	
	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;
	private AlertDialog editDialog;
	private ListView diaListView;
	private DialogDeleteBillAdapter dialogEditBillAdapter ;
	private TextView notiTextView;
	
	public BillsFragment () {
		
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				if (groupDataList != null && groupDataList.size() >0) {
					
					mExpandableListView.setVisibility(View.VISIBLE);
					notiTextView.setVisibility(View.INVISIBLE);
				expandableListViewAdapter.setAdapterData(groupDataList, childrenAllDataList);
				expandableListViewAdapter.notifyDataSetChanged();
				int groupCount = groupDataList.size();

				for (int i = 0; i < groupCount; i++) {
					mExpandableListView.expandGroup(i);
				}
				mExpandableListView
						.setOnGroupClickListener(new OnGroupClickListener() {

							@Override
							public boolean onGroupClick(
									ExpandableListView parent, View v,
									int groupPosition, long id) {
								// TODO Auto-generated method stub
								return true;
							}
						});

				mExpandableListView.setCacheColorHint(0);
				
				} else {
					mExpandableListView.setVisibility(View.INVISIBLE);
					notiTextView.setVisibility(View.VISIBLE);
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (MainActivity.sqlChange == 1) {
			mHandler.post(mTask);
			MainActivity.sqlChange = 0;
		}
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		expandableListViewAdapter = new ExpandableListViewAdapter(mActivity);
		groupDataList = new ArrayList<Map<String,Object>>();
		childrenAllDataList = new ArrayList<List<Map<String,Object>>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bill_list, container,
				false);
		mInflater = inflater;
		
		notiTextView= (TextView)view.findViewById(R.id.notice_txt);
		mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		mExpandableListView.setAdapter(expandableListViewAdapter);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setDividerHeight(0);
		mExpandableListView.setOnChildClickListener(onChildClickListener);
		mExpandableListView.setOnItemLongClickListener(onLongClickListener);
		
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
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -2);
			long startDate = calendar.getTimeInMillis();
			
			long todayMills = MEntity.getNowMillis();
			long endDate = todayMills+30*24*3600*1000L;
			
			List<Map<String, Object>> mList = RecurringEventBE.recurringData(mActivity, 0, endDate);
			judgePayment(mList);
			filterData(mList);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// TODO Auto-generated method stub

			    Map<String, Object> mMap=childrenAllDataList.get(groupPosition).get(childPosition);
				Intent intent = new Intent();
				intent.putExtra("dataMap",(Serializable)mMap);
				intent.setClass(mActivity, BillDetailsActivity.class);
				startActivityForResult(intent, 16);

			return true;
		}
	};
	
	private OnItemLongClickListener onLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			
			// TODO Auto-generated method stub
			final int groupPosition = mExpandableListView.getPackedPositionGroup(arg3);
			final int childPosition = mExpandableListView.getPackedPositionChild(arg3);

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

					int _id = (Integer) childrenAllDataList.get(groupPosition)
							.get(childPosition).get("_id");
					int indexflag = (Integer) childrenAllDataList.get(groupPosition)
							.get(childPosition).get("indexflag");
					Map<String, Object> mMap = childrenAllDataList.get(groupPosition).get(childPosition);
					if (arg2 == 0) {
						Intent intent = new Intent();
						intent.putExtra("dataMap",(Serializable)mMap);
						intent.setClass(mActivity, BillEditActivity.class);
						startActivityForResult(intent, 19);
						
						alertDialog.dismiss();
						mHandler.post(mTask);

					} else if (arg2 == 1) {
						judgementDialog(indexflag, _id, mMap);
						alertDialog.dismiss();
						mHandler.post(mTask);
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
	
	
	
	public void filterData(List<Map<String, Object>> mData) {
		groupDataList.clear();
		childrenAllDataList.clear();
		
		List<Map<String, Object>> overDuedata = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> overDue7data = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> overDue30data = new ArrayList<Map<String, Object>>();
		long nowMillis = MEntity.getNowMillis();
		
		for (Map<String, Object> mMap:mData) {
			long bk_billDuedate = (Long)mMap.get("ep_billDueDate"); 
			int daysBetween = getDaysBetween(nowMillis,bk_billDuedate);
			int payState =  (Integer)mMap.get("payState"); 
			
			if (payState != 2) {
				
				if (daysBetween < 0) {
					overDuedata.add(mMap);
				} else if (0 <= daysBetween && daysBetween < 7){
					overDue7data.add(mMap);
				}else if (7 <= daysBetween && daysBetween < 30) {
					overDue30data.add(mMap);
				}
			}
			
		}
		
		Collections.sort(overDuedata, new MEntity.MapComparator());
		Collections.sort(overDue7data, new MEntity.MapComparator());
		Collections.sort(overDue30data, new MEntity.MapComparator());
		
		
		if (overDuedata.size() > 0) { 
			Map<String, Object> map = new HashMap<String, Object>();
			 map.put("dueString", "PAST DUE");
			 map.put("dueCount", overDuedata.size());
			 groupDataList.add(map);
			 childrenAllDataList.add(overDuedata);
		}

		if (overDue7data.size() > 0) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			 map.put("dueString", "DUE WITHIN 7 DAYS");
			 map.put("dueCount", overDue7data.size());
			 groupDataList.add(map);
			 childrenAllDataList.add(overDue7data);
		
		} 

		if (overDue30data.size() > 0) {
			
			 Map<String, Object> map = new HashMap<String, Object>();
			 map.put("dueString", "DUE WITHIN 30 DAYS");
			 map.put("dueCount", overDue30data.size());
			 groupDataList.add(map);
			 childrenAllDataList.add(overDue30data);
			
		}
		
		
	}
	
	public void judgePayment(List<Map<String, Object>> dataList) {

		for (Map<String, Object> iMap : dataList) {

			int _id = (Integer) iMap.get("_id");
			String ep_billAmount = (String) iMap.get("ep_billAmount");
			int indexflag = (Integer) iMap.get("indexflag");
			BigDecimal b0 = new BigDecimal(ep_billAmount);
			
			if (indexflag == 0 || indexflag == 1) {

				List<Map<String, Object>> pDataList = BillsDao.selectTransactionByBillRuleId(mActivity, _id);
				
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
	
	public int getDaysBetween(long beginDate, long endDate) {
		long between_days = (endDate - beginDate) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 8:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;
			
		case 19:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;
			
		case 16:

			if (data != null) {
				mHandler.post(mTask);
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
			intent.putExtra("selectDate", MEntity.getNowMillis());
			intent.setClass(getActivity(), CreatBillsActivity.class);
			startActivityForResult(intent, 8);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}
