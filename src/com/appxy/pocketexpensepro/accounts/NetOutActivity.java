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
import com.appxy.pocketexpensepro.entity.LoadMoreListView;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.AgendaListenerInterface;
import com.appxy.pocketexpensepro.expinterface.OnAtoBListenner;
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
public class NetOutActivity extends BaseHomeActivity implements AgendaListenerInterface, OnAtoBListenner {

	private ActionBar actionBar;

	private int type;
	private String accName;
	private LoadMoreListView mExpandableListView;

	private ExpandableListViewAdapter mExpandableListViewAdapter;
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

	private int loadSize = 0 ;
	
	private ArrayList<String> mGroupList;
	private HashMap<String, Long> mGroupMap;
	private HashMap<String, ArrayList<HashMap<String, Object>>> mChildMap; 
	
	private int clickedGroupPosition = 0;
	private int clickedChildPosition = 0;
	private int clickedId = -1;
		
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
	  	
		
		if (type == 0) {
			accName = "Net Worth";
			
		} else {
			hideBoolean = true;
			accName = "Outstanding";
		}
		
		mChildMap = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		mGroupList = new ArrayList<String>();
		mGroupMap = new HashMap<String, Long>();
		
		mNavigationListAdapter = new NavigationListAdapter(this, accName);
		
		content = new ArrayList<String>();
		content.add(item1);
		if (type == 0) {
			content.add(item2);
		}
		mNavigationListAdapter.setDownItemData(content);
		actionBar.setListNavigationCallbacks(mNavigationListAdapter,
				new DropDownListenser());
		
		
		currencyTextView1= (TextView) findViewById(R.id.currency_txt1);
		currencyTextView2 = (TextView) findViewById(R.id.currency_txt2);
		symbolTextView1 =  (TextView) findViewById(R.id.symbol_txt1);
		symbolTextView2 =  (TextView) findViewById(R.id.symbol_txt2);
		outstandingTextView = (TextView) findViewById(R.id.outstanding_txt);
		balanceTextView = (TextView) findViewById(R.id.balance_txt);
		
		mExpandableListView = (LoadMoreListView) findViewById(R.id.mExpandableListView);
		
		mExpandableListView.setPullLoadEnable(true);
		mExpandableListView.setPullRefreshEnable(true);
		mExpandableListView.setXListViewListener(this);
		
		mExpandableListViewAdapter = new ExpandableListViewAdapter(this, -1);
		mExpandableListViewAdapter.setAdapterData(mGroupList, mChildMap, mDbxAcctMgr, mDatastore);
		mExpandableListView.setAdapter(mExpandableListViewAdapter);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setDividerHeight(0);
		mExpandableListViewAdapter.setReconcile(reconcileBoolean);
		mExpandableListViewAdapter.setShowClear(hideBoolean);
		mExpandableListView.setOnChildClickListener(onChildClickListener);
		mExpandableListView.setOnItemLongClickListener(onLongClickListener);
		
		setData() ;

	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		getOutAndBanlance();
		currencyTextView1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currencyTextView2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
	}


	public void getOutAndBanlance(){ // 计算两者总和
		
		 List<Map<String, Object>> newwothList = AccountDao.selectAccountNetworth(NetOutActivity.this);
		 if (newwothList!= null && newwothList.size() >0) {
			 banlance = (Double) newwothList.get(0).get("allAmount");
		}else {
			banlance = 0;
		}
		
		 List<Map<String, Object>> outList = AccountDao.selectTransactionOutstanding(NetOutActivity.this, 0);
		 if (outList!= null && outList.size() > 0) {
			 outstanding= (Double) outList.get(0).get("outstandingAmount");
		} else {
			outstanding = 0;
		}
		 
		 outstandingTextView.setText(MEntity.doublepoint2str(outstanding+""));
	     balanceTextView.setText(MEntity.doublepoint2str(banlance+""));
	}
	
	private void setData() {
		
		ArrayList<HashMap<String, Object>> mDataList ;
		if (hideBoolean) {
			
			 mDataList = AccountDao.selectTransactionByAccountLimitLeftJoinClear(NetOutActivity.this, 0, loadSize);
			
		} else {
			 mDataList = AccountDao.selectTransactionByAccountLimitLeftJoin(NetOutActivity.this, loadSize);
		}
		
			
		 loadSize = loadSize + mDataList.size();
		 
			if (mDataList != null ) {
				reFillData(mDataList);
				filterData(mDataList);
			}
			
			
			mExpandableListViewAdapter.notifyDataSetChanged();

			int groupCount = mGroupList.size();

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
			
		}


	class DropDownListenser implements OnNavigationListener // actionbar下拉菜单监听
	{

		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			
			if (itemPosition == 0) {
				if (firstCheck) {
					actionBar.setSelectedNavigationItem(2);
					if (reconcileBoolean) {
						item1 = "Reconcile OFF              ";
					} else {
						item1 = "Reconcile ON               ";
					}
					mExpandableListViewAdapter.setReconcile(reconcileBoolean);
					mExpandableListViewAdapter.notifyDataSetChanged();
					
				}else{
					
					reconcileBoolean = reconcileBoolean ? false : true;
					actionBar.setSelectedNavigationItem(2);
					
					 SharedPreferences sharedPreferences = getSharedPreferences("reconcileBoolean",MODE_PRIVATE); 
		   		     SharedPreferences.Editor meditor = sharedPreferences.edit();  
		   			 meditor.putBoolean("reconcileBoolean",reconcileBoolean );  
		   			 meditor.commit();
		   			 
					if (reconcileBoolean) {
						item1 = "Reconcile OFF              ";
					} else {
						item1 = "Reconcile ON               ";
					}
					mExpandableListViewAdapter.setReconcile(reconcileBoolean);
					mExpandableListViewAdapter.notifyDataSetChanged();
					
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
					item2 = "Show Cleared";
				} else {
					item2 = "Hide Cleared";
				}
				
				mExpandableListViewAdapter.setShowClear(hideBoolean);
				loadSize = 0;
				mGroupList.clear();
				mGroupMap.clear();
				mChildMap.clear();
				setData();
				
			}
			
			content.clear();
			content.add(item1);
			content.add(item2);
			
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
			   clickedGroupPosition = groupPosition;
			   clickedChildPosition = childPosition;
			   
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

					String dateKey = mGroupList.get(groupPosition);
					
					int _id = (Integer) mChildMap.get(dateKey).get(childPosition).get("_id");
					clickedId = _id;
					String uuid = (String) mChildMap.get(dateKey).get(childPosition).get("uuid");

					if (arg2 == 0) {
						HashMap<String, Object> mMap = mChildMap.get(dateKey).get(childPosition);

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
								payee, new String(), 0, 0 , mDbxAcctMgr, mDatastore);
						
						judegeReturn((int)row);
						getOutAndBanlance();
						alertDialog.dismiss();

					} else if (arg2 == 1) {

						long row = AccountDao.deleteTransaction(
								NetOutActivity.this, _id, uuid, mDbxAcctMgr, mDatastore);
						
						mChildMap.get(dateKey).remove(childPosition);
						
						if (mChildMap.get(dateKey) == null) {
							mGroupList.remove(groupPosition);
							mGroupMap.remove(dateKey);
						} else {
							
							if (mChildMap.get(dateKey).size() == 0) {
								mGroupList.remove(groupPosition);
								mGroupMap.remove(dateKey);
							}

						}
						loadSize = loadSize -1;
						
						getOutAndBanlance();
						
						 mExpandableListViewAdapter.notifyDataSetChanged();
						  int groupCount = mGroupList.size();

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
						
						alertDialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("row", row);
						setResult(12, intent);
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
			
		   clickedGroupPosition = groupPosition;
		   clickedChildPosition = childPosition;
			
			String dateKey = mGroupList.get(groupPosition);
			
			final int tId = (Integer) mChildMap.get(dateKey).get(childPosition).get("_id");
			clickedId = tId;
			
			int expenseAccount = (Integer) mChildMap.get(dateKey).get(childPosition).get("expenseAccount");
			int incomeAccount = (Integer) mChildMap.get(dateKey).get(childPosition).get("incomeAccount");

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
				
				long rId = data.getLongExtra("row", 0);
				
				if (rId >0) {
					judegeReturn(clickedId);
				}
				
			}
			break;
		case 6:

			if (data != null) {
				
				long rId = data.getLongExtra("done", 0);
				
				judegeReturn((int)rId);
			}
			break;
		}
		
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
		loadSize = 0;
		
		if (mGroupList != null) {
			mGroupList.clear();
		}
		
		if (mChildMap != null) {
			mChildMap.clear();
		}
		
		if (mGroupMap != null) {
			mGroupMap.clear();
		}
		
		setData();
		getOutAndBanlance();
	}



	public void reFillData( ArrayList<HashMap<String, Object>> mData) {

		for (Map<String, Object> mMap : mData) {
			
			String payeeName = (String) mMap.get("payeeName");
			int categoryId = (Integer) mMap.get("categoryId");

			if (categoryId <= 0) {
				int expenseAccount = (Integer) mMap.get("expenseAccount");
				int incomeAccount = (Integer) mMap.get("incomeAccount");
				
				if (expenseAccount > 0 && incomeAccount > 0) {
					mMap.put("iconName", 69);
				} else {
					mMap.put("iconName", 56);
				}
				
			}
			
			if (payeeName == null) {
				String memoString = (String) mMap.get("notes");
				if (memoString == null ) {
					mMap.put("payeeName", "--");
				} else {
					
					if (memoString.length() > 0) {
						mMap.put("payeeName", memoString);
					} else {
						mMap.put("payeeName", "--");
					}
					
				}
			} 
			

		}
	}
	
	
	public void filterData( ArrayList<HashMap<String, Object>> mData) {// 根据时间过滤子类和父类

		
        for (HashMap<String, Object> iMap : mData) {
			
			long dateTime = (Long) iMap.get("dateTime");
			String dateKey = turnToDate( dateTime );
			
			if (mGroupMap.containsKey(dateKey)) {

		        mChildMap.get(dateKey).add(iMap);
		        
			} else {
				
				ArrayList<HashMap<String, Object>> mChildArrayList = new ArrayList<HashMap<String,Object>>();
				mChildArrayList.add(iMap);												
				mChildMap.put(dateKey, mChildArrayList);
				mGroupList.add(dateKey);
				mGroupMap.put(dateKey, dateTime);
			}

		}

	}
	
	public String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	
private void judegeReturn(int id) { //根据id，判断改数据在页面是否插入
	
	ArrayList<HashMap<String, Object>> mList ;
	if (hideBoolean) {
		
		mList = AccountDao.selectTransactionByIdLeftJoinCleared(this, id, 0);
		
	} else {
		mList = AccountDao.selectTransactionByIdLeftJoin(this, id);
	}
	
		 if (mList.size() > 0) {
			
			long dateTime = (Long) mList.get(0).get("dateTime");
			String newKey = turnToDate(dateTime);
			
			if (mGroupMap.containsKey(newKey)) {
				
				if (clickedId == id) { //表示修改
					
					    String oldKey = mGroupList.get(clickedGroupPosition); // first Remove Old map
			    	    int oldChildSize = mChildMap.get(oldKey).size(); 
			    		mChildMap.get(oldKey).remove(clickedChildPosition);
			    		
			    	    if ( oldChildSize - 1 == 0) {
			    	    	
			    	    	if (!newKey.equals(oldKey)) { 
			    	    		mGroupMap.remove(oldKey);
			    	    		mGroupList.remove(clickedGroupPosition);
							} 
				    	    
						} 
			    	    
			    	    HashMap<String, Object> newMap = mList.get(0);
			    	    mChildMap.get(newKey).add(newMap);
			    	    
			    	    Collections.sort(mChildMap.get(newKey), new MEntity.MapComparatorTime());
			    	    
				} else { //表示新增数据
					
			    	    HashMap<String, Object> newMap = mList.get(0);
			    	    mChildMap.get(newKey).add(newMap);
			    	    
			    	    Collections.sort(mChildMap.get(newKey), new MEntity.MapComparatorTime());
			    	    loadSize = loadSize +1;

				}
				    	   
				    	    
			} else {
				
				if (clickedId == id) { //表示修改
					
				    String oldKey = mGroupList.get(clickedGroupPosition); // first Remove Old map
		    	    int oldChildSize = mChildMap.get(oldKey).size(); 
		    		mChildMap.get(oldKey).remove(clickedChildPosition);
		    		
		    		 loadSize = loadSize - 1;
		    	    if ( oldChildSize - 1 == 0) {
		    	    	
		    	    	if (!newKey.equals(oldKey)) { 
		    	    		mGroupMap.remove(oldKey);
		    	    		mGroupList.remove(clickedGroupPosition);
						} 
			    	    
					} 
		    	    
			   } 
				
				long lastGroupTime = 0;
				if (mGroupMap!= null && mGroupMap.size() >1) {
					
				   lastGroupTime = mGroupMap.get( mGroupList.get(mGroupList.size()-1) );
				   
				}else{
					lastGroupTime = 0;
				}
					
				
				 if ( dateTime > lastGroupTime || loadSize < 31 ) {// newTime > last Group time. bulid group
		    	    	
					    mGroupMap.put(newKey, dateTime);
					    mGroupList.add(newKey);
					    
					    Collections.sort(mGroupList, new MEntity.MapComparatorGroupTime());
					    
					    HashMap<String, Object> newMap = mList.get(0);
					    ArrayList<HashMap<String, Object>> mChildArrayList = new ArrayList<HashMap<String,Object>>();
						mChildArrayList.add(newMap);												
						mChildMap.put(newKey, mChildArrayList);
						
						loadSize = loadSize +1;
						
					}
				

			}
			
		}else { 
			
			
			if (clickedId == id) { 
				
			    String oldKey = mGroupList.get(clickedGroupPosition); // first Remove Old map
	    	    int oldChildSize = mChildMap.get(oldKey).size(); 
	    		mChildMap.get(oldKey).remove(clickedChildPosition);
	    		
	    		 loadSize = loadSize -1;
	    		 
	    		if ( oldChildSize - 1 == 0) {
	    	    	
	    	    		mGroupMap.remove(oldKey);
	    	    		mGroupList.remove(clickedGroupPosition);
		    	    
				} 
			}
	    		
			
		}
		 
		  mExpandableListViewAdapter.notifyDataSetChanged();
		  int groupCount = mGroupList.size();

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
			
			getOutAndBanlance() ;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		setData();
		mExpandableListView.stopRefresh();
		mExpandableListView.stopLoadMore();
	}


	@Override
	public void OnA2B() {
		// TODO Auto-generated method stub
		getOutAndBanlance();
	}

}
