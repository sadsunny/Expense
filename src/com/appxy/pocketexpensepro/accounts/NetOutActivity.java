package com.appxy.pocketexpensepro.accounts;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactonByAccountActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxRecord;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;

@SuppressLint("ResourceAsColor")
public class NetOutActivity extends BaseHomeActivity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private ActionBar actionBar;
	private Thread mThread;

	private int type;
	private String accName;
	private ExpandableListView mExpandableListView;

	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	private thisExpandableListViewAdapter mExpandableListViewAdapter;
	private List<Map<String, Object>> mDataList;
	private NavigationListAdapter mNavigationListAdapter;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;
	
	private Boolean reconcileBoolean = true;
	private Boolean hideBoolean = false;
	
	private int queryCheck = 0; // 0查询所有，1查询clear的transaction
	private double outstanding;
	private double banlance;
	private TextView currencyTextView1;
	private TextView currencyTextView2;
	private TextView symbolTextView1;
	private TextView symbolTextView2;
	private TextView outstandingTextView;
	private TextView balanceTextView;
	private  String item1 = "Reconcile ON               ";
	private  String item2 = "Hide Cleared";
	private  List<String> content;
	private boolean iap_is_ok = false;
	public static final String Paid_Id_VF = "upgrade";
	static final int RC_REQUEST = 10001;
	private static final String PREFS_NAME = "SAVE_INFO";
	private boolean firstCheck = true; //下拉框第一次进去的check，第一次加载不允许改变值
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				
				if (outstanding < 0) {
//					symbolTextView1.setText("-");
//					symbolTextView1.setVisibility(View.VISIBLE);
//					symbolTextView1.setTextColor(Color.RED);
//					currencyTextView1.setTextColor(Color.RED);
//					outstandingTextView.setTextColor(Color.RED);
					double tem = 0 - outstanding;
					outstandingTextView.setText(MEntity.doublepoint2str(tem+""));
				} else {
//					symbolTextView1.setText("");
//					symbolTextView1.setVisibility(View.INVISIBLE);
//					symbolTextView1.setTextColor(R.color.black_gray);
//					currencyTextView1.setTextColor(R.color.black_gray);
//					outstandingTextView.setTextColor(R.color.black_gray);
					outstandingTextView.setText(MEntity.doublepoint2str(outstanding+""));
				}
				
				if (banlance < 0) {
//					symbolTextView2.setText("-");
//					symbolTextView2.setVisibility(View.VISIBLE);
//					symbolTextView2.setTextColor(Color.RED);
//					currencyTextView2.setTextColor(Color.RED);
//					balanceTextView.setTextColor(Color.RED);
					double tem = 0 - banlance;
					balanceTextView.setText(MEntity.doublepoint2str(tem+""));
				} else {
//					symbolTextView2.setText("");
//					symbolTextView2.setVisibility(View.INVISIBLE);
//					symbolTextView2.setTextColor(R.color.black_gray);
//					currencyTextView2.setTextColor(R.color.black_gray);
//					balanceTextView.setTextColor(R.color.black_gray);
					balanceTextView.setText(MEntity.doublepoint2str(banlance+""));
				}
				
				mExpandableListViewAdapter.setAdapterData(groupDataList,
						childrenAllDataList);
				mExpandableListViewAdapter.notifyDataSetChanged();

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

				break;

			case MSG_FAILURE:
				Toast.makeText(NetOutActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_to_transaction);
		
	  	
		mInflater = LayoutInflater.from(this);
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		Intent intent = getIntent();
		type = intent.getIntExtra("type", 0);
		
		if (type == 0) {
			accName = "Net Worth";
			
			  SharedPreferences sharedPreferences = getSharedPreferences("reconcileBoolean", MODE_PRIVATE);  
		      reconcileBoolean = sharedPreferences.getBoolean("reconcileBoolean", true);
		      
		      SharedPreferences sharedPreferences1 = getSharedPreferences("hideBoolean", MODE_PRIVATE);  
		      hideBoolean = sharedPreferences1.getBoolean("hideBoolean", false);
		      
		      if (reconcileBoolean) {
					item1 = "Reconcile OFF              ";
				} else {
					item1 = "Reconcile ON               ";
				}
		      
		  	if (hideBoolean) {
				queryCheck = 1;
				item2 = "Show Cleared";
			 } else {
				queryCheck = 0;
				item2 = "Hide Cleared";
			}
		  	
		} else {
			
			accName = "Outstanding";
			queryCheck = 1 ;
		}
		
		mNavigationListAdapter = new NavigationListAdapter(this, accName);
		
		content = new ArrayList<String>();
		content.add(item1);
		if (type == 0) {
			content.add(item2);
		}
		mNavigationListAdapter.setDownItemData(content);
		actionBar.setListNavigationCallbacks(mNavigationListAdapter,
				new DropDownListenser());
		
		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		
		currencyTextView1= (TextView) findViewById(R.id.currency_txt1);
		currencyTextView2 = (TextView) findViewById(R.id.currency_txt2);
		symbolTextView1 =  (TextView) findViewById(R.id.symbol_txt1);
		symbolTextView2 =  (TextView) findViewById(R.id.symbol_txt2);
		outstandingTextView = (TextView) findViewById(R.id.outstanding_txt);
		balanceTextView = (TextView) findViewById(R.id.balance_txt);
		
		mExpandableListView = (ExpandableListView) findViewById(R.id.mExpandableListView);
		mExpandableListViewAdapter = new thisExpandableListViewAdapter(this);
		mExpandableListView.setAdapter(mExpandableListViewAdapter);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setDividerHeight(0);
		mExpandableListView.setOnChildClickListener(onChildClickListener);
		mExpandableListView.setOnItemLongClickListener(onLongClickListener);

		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}
		
		Intent resultintent = new Intent();
		resultintent.putExtra("row", 1);
		setResult(12, resultintent);

	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		currencyTextView1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currencyTextView2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
	}



	public Runnable mTask = new Runnable() {

		@Override
		public void run() {

			if (queryCheck == 0) {

				mDataList = AccountDao.selectTransactionAllList(NetOutActivity.this);

			} else if (queryCheck == 1) {

				mDataList = AccountDao.selectTransactionAllAndClear(NetOutActivity.this);
			}

			if (mDataList != null ) {
				reFillData(mDataList);
				filterData(mDataList);
			}
			BigDecimal b0 = new BigDecimal("0");
			BigDecimal b1 = new BigDecimal("0");
			for (Map<String, Object> iMap : mDataList) {
				String amount = (String) iMap.get("amount");
				int clear = (Integer) iMap.get("isClear");
				BigDecimal b2 = new BigDecimal(amount);

				int expenseAccount = (Integer) iMap.get("expenseAccount");
				int incomeAccount = (Integer) iMap.get("incomeAccount");
				if (expenseAccount > 0 ) {
					b0 = b0.subtract(b2);
				} else if (incomeAccount> 0) {
					b0 = b0.add(b2);
				}
				
				if (clear == 0) {
					if (expenseAccount > 0) {
						b1 = b1.subtract(b2);
					} else if (incomeAccount > 0) {
						b1 = b1.add(b2);
					}
				}
				
			}
			outstanding = b1.doubleValue();
			banlance = b0.doubleValue();
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	class DropDownListenser implements OnNavigationListener // actionbar下拉菜单监听
	{

		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			
			if (itemPosition == 0) {
				if (firstCheck) {
					actionBar.setSelectedNavigationItem(2);
					if (reconcileBoolean) {
						mExpandableListViewAdapter.setReconcile(1);
						mExpandableListViewAdapter.notifyDataSetChanged();
						item1 = "Reconcile OFF              ";
					} else {
						mExpandableListViewAdapter.setReconcile(0);
						mExpandableListViewAdapter.notifyDataSetChanged();
						item1 = "Reconcile ON               ";
					}
				}else{
					
					reconcileBoolean = reconcileBoolean ? false : true;
					actionBar.setSelectedNavigationItem(2);
					
					 SharedPreferences sharedPreferences = getSharedPreferences("reconcileBoolean",MODE_PRIVATE); 
		   		     SharedPreferences.Editor meditor = sharedPreferences.edit();  
		   			 meditor.putBoolean("reconcileBoolean",reconcileBoolean );  
		   			 meditor.commit();
		   			 
					if (reconcileBoolean) {
						mExpandableListViewAdapter.setReconcile(1);
						mExpandableListViewAdapter.notifyDataSetChanged();
						item1 = "Reconcile OFF              ";
					} else {
						mExpandableListViewAdapter.setReconcile(0);
						mExpandableListViewAdapter.notifyDataSetChanged();
						item1 = "Reconcile ON               ";
					}
					
				}
				
				firstCheck = false;
				
			} else if (itemPosition == 1) {
				hideBoolean = hideBoolean ? false : true;
				actionBar.setSelectedNavigationItem(2);
				
				 SharedPreferences sharedPreferences = getSharedPreferences("hideBoolean",MODE_PRIVATE); 
	   		     SharedPreferences.Editor meditor = sharedPreferences.edit();  
	   			 meditor.putBoolean("hideBoolean",hideBoolean );  
	   			 meditor.commit();
				
				if (hideBoolean) {
					queryCheck = 1;
					item2 = "Show Cleared";
				} else {
					queryCheck = 0;
					item2 = "Hide Cleared";
				}
				
				mHandler.post(mTask);
			}
			
			content.clear();
			content.add(item1);
			if (type == 0) {
				content.add(item2);
			}
			
			
			mNavigationListAdapter.setDownItemData(content);
//			mNavigationListAdapter.notifyDataSetChanged();
			
			return false;

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

			String[] data = { "Duplicate", "Delete" };
			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(
					NetOutActivity.this, data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					int _id = (Integer) childrenAllDataList.get(groupPosition)
							.get(childPosition).get("_id");
					
					String uuid = (String) childrenAllDataList.get(groupPosition)
							.get(childPosition).get("uuid");

					if (arg2 == 0) {
						Map<String, Object> mMap = childrenAllDataList.get(
								groupPosition).get(childPosition);

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
								NetOutActivity.this, amount,
								dateTime, isClear, notes, photoName,
								recurringType, category, childTransactions,
								expenseAccount, incomeAccount, parTransaction,
								payee,new String(), 0, 0 , mDbxAcctMgr, mDatastore);
						alertDialog.dismiss();


						mHandler.post(mTask);

					} else if (arg2 == 1) {

						long row = AccountDao.deleteTransaction(
								NetOutActivity.this, _id, uuid, mDbxAcctMgr, mDatastore);
						alertDialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("row", row);
						setResult(12, intent);
						mHandler.post(mTask);
					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(
					NetOutActivity.this);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
		}
	};

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// TODO Auto-generated method stub
			final int tId = (Integer) childrenAllDataList.get(groupPosition)
					.get(childPosition).get("_id");
			int expenseAccount = (Integer) childrenAllDataList
					.get(groupPosition).get(childPosition)
					.get("expenseAccount");
			int incomeAccount = (Integer) childrenAllDataList
					.get(groupPosition).get(childPosition).get("incomeAccount");

			if (expenseAccount > 0 && incomeAccount > 0) {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(NetOutActivity.this,
						EditTransferActivity.class);
				startActivityForResult(intent, 13);

			} else {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(NetOutActivity.this,
						EditTransactionActivity.class);
				startActivityForResult(intent, 13);

			}
			return true;
		}

	};

	public void reFillData(List<Map<String, Object>> mData) {

		for (Map<String, Object> mMap : mData) {
			int category = (Integer) mMap.get("category");
			int payee = (Integer) mMap.get("payee");

			if (category > 0) {
				List<Map<String, Object>> mList = AccountDao
						.selectCategoryById(NetOutActivity.this,
								category);
				if (mList != null && mList.size() >0) {
					int iconName = (Integer) mList.get(0).get("iconName");
					mMap.put("iconName", iconName);
				} else {
					mMap.put("iconName", 56);
				}
			} else {
				mMap.put("iconName", 56); // 设置为not sure
			}

			if (payee > 0) {
				List<Map<String, Object>> mList = AccountDao.selectPayeeById(
						NetOutActivity.this, payee);
				if (mList != null && mList.size() > 0) {
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

	public static long getFirstDayOfMonthMillis(long mills) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mills);

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());
		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	public static long getLastDayOfMonthMillis(long mills) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mills);
		// cal.set(Calendar.HOUR, 23);
		// cal.set(Calendar.MINUTE, 59);
		// cal.set(Calendar.SECOND, 59);

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());

		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c.getTimeInMillis();

	}

	public long turnMillsToMonth(long mills) {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mills);
		String dateTime = new SimpleDateFormat("MM-yyyy").format(c.getTime());
		try {
			c.setTime(new SimpleDateFormat("MM-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	public void filterData(List<Map<String, Object>> mData) {// 根据时间过滤子类和父类

		groupDataList.clear();
		childrenAllDataList.clear();

		ArrayList<Long> mDatelist = new ArrayList<Long>();

		for (Map<String, Object> mMap : mData) {
			long dateTime = turnMillsToMonth((Long) mMap.get("dateTime"));
			mDatelist.add(dateTime);
		}
		

		Iterator<Long> it1 = mDatelist.iterator();
		Map<Long, Long> msp = new TreeMap<Long, Long>();
		
		while (it1.hasNext()) {
			long obj = it1.next();
			msp.put(obj, obj);
		}
		Iterator<Long> it2 = msp.keySet().iterator();
		while (it2.hasNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();
			mMap.put("dateTime", (Long) it2.next());
			groupDataList.add(mMap);
		}
		Collections.sort(groupDataList, new MEntity.MapComparatorTime());
		Log.v("mtest", "groupDataList降序"+groupDataList);

		for (Map<String, Object> iMap : groupDataList) {
			
			long dateTime = (Long) iMap.get("dateTime");
			List<Map<String, Object>> childrenDataList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> mMap : mData) {
				long mDateTime = (Long) mMap.get("dateTime");
				if (getFirstDayOfMonthMillis(dateTime) <= mDateTime
						&& mDateTime <= getLastDayOfMonthMillis(dateTime)) {
					childrenDataList.add(mMap);
				}
			}
			childrenAllDataList.add(childrenDataList);
		}

	}

	public class thisExpandableListViewAdapter extends
			BaseExpandableListAdapter {

		private LayoutInflater inflater;
		private List<Map<String, Object>> groupList;
		private List<List<Map<String, Object>>> childList;
		private Context context;
		private int reconcileCheck;

		public thisExpandableListViewAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		public void setAdapterData(List<Map<String, Object>> groupList,
				List<List<Map<String, Object>>> childList) {

			this.groupList = groupList;
			this.childList = childList;

		}

		public List<List<Map<String, Object>>> getAdapterDate() {
			return this.childList;
		}

		public void setReconcile(int check) {
			this.reconcileCheck = check;
		}

		@Override
		// gets the title of each parent/group
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		// counts the number of group/parent items so the list knows how many
		// times
		// calls getGroupView() method
		public int getGroupCount() {
			// TODO Auto-generated method stub
			if (groupList == null) {
				return 0;
			}
			return groupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		public String turnToDate(long mills) {

			Date date2 = new Date(mills);
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy");
			String theDate = sdf.format(date2);
			return theDate;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			gViewHolder viewholder = null;
			if (convertView == null) {
				viewholder = new gViewHolder();
				convertView = inflater.inflate(R.layout.transaction_group_item,
						parent, false);
				viewholder.mTextView = (TextView) convertView
						.findViewById(R.id.dateTextView);
				convertView.setTag(viewholder);
			} else {
				viewholder = (gViewHolder) convertView.getTag();
			}

			viewholder.mTextView.setText(turnToDate((Long) groupList.get(
					groupPosition).get("dateTime")));

			convertView.setOnTouchListener(new View.OnTouchListener() { // 设置Group是否可点击

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub
							return true;
						}
					});

			return convertView;
		}

		@Override
		// gets the name of each item
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		public String turnToDateString(long mills) {

			Date date2 = new Date(mills);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
			String theDate = sdf.format(date2);
			return theDate;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			cViewHolder viewholder = null;
			if (convertView == null) {
				viewholder = new cViewHolder();
				convertView = inflater.inflate(R.layout.transaction_item,
						parent, false);

				viewholder.mCheckBox = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				viewholder.mImageView = (ImageView) convertView
						.findViewById(R.id.mImageView);
				viewholder.mImageView1 = (ImageView) convertView
						.findViewById(R.id.mImageView1);
				viewholder.mImageView2 = (ImageView) convertView
						.findViewById(R.id.mImageView2);
				viewholder.mTextView1 = (TextView) convertView
						.findViewById(R.id.TextView1);
				viewholder.mTextView2 = (TextView) convertView
						.findViewById(R.id.TextView2);
				viewholder.symbol_txt = (TextView) convertView
						.findViewById(R.id.symbol_txt);
				viewholder.currency_textView = (TextView) convertView
						.findViewById(R.id.currency_txt);
				viewholder.amount_textView = (TextView) convertView
						.findViewById(R.id.amounttextView);
				viewholder.mline_label = (View) convertView
						.findViewById(R.id.mline_label);

				convertView.setTag(viewholder);
			} else {
				viewholder = (cViewHolder) convertView.getTag();

			}
			viewholder.mImageView
					.setImageResource(Common.CATEGORY_ICON[(Integer) childList
							.get(groupPosition).get(childPosition)
							.get("iconName")]);
			viewholder.currency_textView
					.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);

			if (reconcileCheck == 1) {
				viewholder.mCheckBox.setVisibility(View.VISIBLE);
				viewholder.mImageView.setVisibility(View.INVISIBLE);
			} else {
				viewholder.mCheckBox.setVisibility(View.INVISIBLE);
				viewholder.mImageView.setVisibility(View.VISIBLE);
			}

			final int cleard = (Integer) childList.get(groupPosition)
					.get(childPosition).get("isClear");

			if (cleard == 1) {
				viewholder.mCheckBox.setChecked(true);
			} else {
				viewholder.mCheckBox.setChecked(false);
			}

			final int _id = (Integer) childList.get(groupPosition)
					.get(childPosition).get("_id");
			viewholder.mCheckBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (cleard == 1) {
						// childList.get(groupPosition).get(childPosition).put("isClear",
						// 0);
						AccountDao.updateTransactionClear(context, _id, 0, mDbxAcctMgr, mDatastore);
					} else {
						// childList.get(groupPosition).get(childPosition).put("isClear",
						// 1);
						AccountDao.updateTransactionClear(context, _id, 1, mDbxAcctMgr, mDatastore);
					}

					mHandler.post(mTask);

				}
			});

			long dateTime = (Long) childList.get(groupPosition)
					.get(childPosition).get("dateTime");
			viewholder.mTextView2.setText(turnToDateString(dateTime));

			int recurringType = (Integer) childList.get(groupPosition)
					.get(childPosition).get("recurringType");
			if (recurringType > 0) {
				viewholder.mImageView1.setVisibility(View.VISIBLE);
			} else {
				viewholder.mImageView1.setVisibility(View.INVISIBLE);
			}

			String photoName = (String) childList.get(groupPosition)
					.get(childPosition).get("photoName");
			if (photoName != null) {
			
			File file = new File(photoName);
			if (photoName.length() > 0 && file.exists()) {

				viewholder.mImageView1.setVisibility(View.VISIBLE);
			} else {
				viewholder.mImageView1.setVisibility(View.INVISIBLE);
			}
			} else {
				viewholder.mImageView1.setVisibility(View.INVISIBLE);
			}
			
			String notes = (String) childList.get(groupPosition)
					.get(childPosition).get("notes");
			Log.v("mtest", "notes"+notes);
			
			if (notes != null && notes.length()>0) {
				viewholder.mImageView2.setVisibility(View.VISIBLE);
			} else {
				viewholder.mImageView2.setVisibility(View.INVISIBLE);
			}

			Double mAmount;
			try {
				mAmount = Double.parseDouble((String) childList
						.get(groupPosition).get(childPosition).get("amount"));
			} catch (Exception e) {
				// TODO: handle exception
				mAmount = 0.0;
			}

			int expenseAccount = (Integer) childList.get(groupPosition)
					.get(childPosition).get("expenseAccount");
			int incomeAccount = (Integer) childList.get(groupPosition)
					.get(childPosition).get("incomeAccount");

			if (expenseAccount > 0) {
				mAmount = 0 - mAmount;
			}

			if (expenseAccount > 0 && incomeAccount > 0) {
				List<Map<String, Object>> mList1 = AccountDao
						.selectAccountById(context, expenseAccount);
				List<Map<String, Object>> mList2 = AccountDao
						.selectAccountById(context, incomeAccount);
				String mFromAccount = (String) mList1.get(0).get("accName");
				String mInAccount = (String) mList2.get(0).get("accName");
				viewholder.mTextView1.setText((String) childList
						.get(groupPosition).get(childPosition).get("payeeName")
						+ "(" + mFromAccount + " > " + mInAccount + ")");
			} else {
				viewholder.mTextView1
						.setText((String) childList.get(groupPosition)
								.get(childPosition).get("payeeName"));
			}

			if (mAmount < 0) {
				viewholder.symbol_txt.setTextColor(Color.rgb(208, 47 ,58));
				viewholder.currency_textView.setTextColor(Color.rgb(208, 47 ,58));
				viewholder.amount_textView.setTextColor(Color.rgb(208, 47 ,58));
				double amount = 0 - mAmount;
				viewholder.amount_textView.setText(MEntity
						.doublepoint2str(amount + ""));
			} else {
				viewholder.symbol_txt.setTextColor(Color.rgb(83,150,39));
				viewholder.currency_textView.setTextColor( Color.rgb(83,150,39));
				viewholder.amount_textView.setTextColor(Color.rgb(83,150,39));
				viewholder.amount_textView.setText(MEntity
						.doublepoint2str((String) childList.get(groupPosition)
								.get(childPosition).get("amount")));
			}


			if (childPosition == 0) {
				viewholder.mline_label.setVisibility(View.INVISIBLE);
			} else {
				viewholder.mline_label.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		public class cViewHolder {
			public CheckBox mCheckBox;
			public ImageView mImageView;
			public ImageView mImageView1;
			public ImageView mImageView2;
			public TextView mTextView1;
			public TextView mTextView2;
			public TextView symbol_txt;
			public TextView currency_textView;
			public TextView amount_textView;
			public View mline_label;
		}

		@Override
		// counts the number of children items so the list knows how many times
		// calls getChildView() method
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub

			if (childList == null) {
				return 0;
			}
			return childList.get(groupPosition).size();

		}

		class gViewHolder {
			public TextView mTextView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.add_menu, menu);
//		Menu itemMenu = (Menu) menu.findItem(R.id.navigation_but);
//		Menu itemMenu1 = (Menu) menu.findItem(R.id.action_add);
		return super.onCreateOptionsMenu(menu);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;
		case R.id.action_add:
		        
			Intent intent = new Intent();
			intent.setClass(NetOutActivity.this, CreatTransactionActivity.class);
			startActivityForResult(intent, 6);
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	    
	
	 // Callback for when a purchase is finished
    
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (resultCode) {
		case 13:

			if (data != null) {
				// mThread = new Thread(mTask);
				// mThread.start();
				mHandler.post(mTask);
			}
			break;
		case 6:

			if (data != null) {
				// mThread = new Thread(mTask);
				// mThread.start();
				mHandler.post(mTask);
			}
			break;
		}
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
	}

}
