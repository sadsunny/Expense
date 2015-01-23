package com.appxy.pocketexpensepro.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.Attributes.Name;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountActivity;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransferActivity;
import com.appxy.pocketexpensepro.entity.LoadMoreListView;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.AgendaListenerInterface;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactonByAccountActivity;
import com.appxy.pocketexpensepro.overview.transaction.TransactionDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxRecord;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class CategoryListActivity extends BaseHomeActivity {
	
	
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	
	private ArrayList<HashMap<String, Object>>  mGroupList;//父类，同属数据库根据sum排序
	private HashMap<String, ArrayList<HashMap<String, Object>>> mChildMap; // 存放childMap,根据group的string寻找对应的child。再根据child position查找map数据

	private ReCashListAdapter mAdapter;
	private LayoutInflater mInflater;
	private AlertDialog alertDialog;
	private String categoryName = "";
	private double allAmount = 0; // 该类别下所有交易的总和，用于计算百分比
	private ExpandableListView mExpandableListView;
	private int loadSize = 0;// 每次加载的条数，记录已经加载的条数,删除自动减去一个，如果新增就走新增算法，判断是在在这之前加载
	private ExpandableListViewAdapter mExpandableListViewAdapter ;
	
	private Thread mThread;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				mExpandableListViewAdapter.setAllAmount(allAmount);
				mExpandableListViewAdapter.setAdapterData(mGroupList, mChildMap);
				mExpandableListViewAdapter.notifyDataSetChanged();
				for (int i = 0; i <  mGroupList.size(); i++) {
					mExpandableListView.expandGroup(i);
				}
				mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

					@Override
					public boolean onGroupClick(ExpandableListView parent, View v,
							int groupPosition, long id) {
						// TODO Auto-generated method stub
						return true;
					}
				});

				mExpandableListView.setCacheColorHint(0);
				
				break;

			case MSG_FAILURE:
				Toast.makeText(CategoryListActivity.this, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list);

		mChildMap = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		mGroupList = new ArrayList<HashMap<String,Object>>() ;
		
		Intent intent = getIntent();
		categoryName = intent.getStringExtra("categoryName");
		
		this.getActionBar().setDisplayShowTitleEnabled(true);
		this.getActionBar().setTitle(categoryName);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mExpandableListView = (ExpandableListView) findViewById(R.id.mExpandableListView);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setDividerHeight(0);
		mExpandableListView.setOnChildClickListener(onChildClickListener);
		
		mExpandableListViewAdapter = new ExpandableListViewAdapter(this);
		mExpandableListView.setAdapter(mExpandableListViewAdapter);
		
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}

	}
	
	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			
			mGroupList.clear();
			mChildMap.clear();
			
			allAmount = ReportDao.selectTransactionSumByNameLike(CategoryListActivity.this, categoryName, MainActivity.startDate, MainActivity.endDate);
			mGroupList =   ReportDao.selectTransactionSumByNameLikeGroup(CategoryListActivity.this, categoryName, MainActivity.startDate, MainActivity.endDate);
			
			if (mGroupList != null && mGroupList.size() > 0) {
				
				for (HashMap<String, Object> iMap:mGroupList) {
					String categoryName = (String) iMap.get("categoryName");
					ArrayList<HashMap<String, Object>> mChildArrayList = ReportDao.selectTransactionByNameLikeLeftJoin(CategoryListActivity.this, categoryName, MainActivity.startDate, MainActivity.endDate);

					if (mChildMap.containsKey(categoryName)) {
						mChildMap.get(categoryName).add(iMap);
					} else {
						mChildMap.put(categoryName, mChildArrayList);
					}
					
				}
				
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	
	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// TODO Auto-generated method stub
			
			String categoryNameKey = (String) mGroupList.get(groupPosition).get("categoryName");
			
			final int tId  = (Integer) mChildMap.get(categoryNameKey).get(childPosition).get("_id");
			
			int expenseAccount = (Integer) mChildMap.get(categoryNameKey).get(childPosition).get("expenseAccount");
			int incomeAccount =  (Integer) mChildMap.get(categoryNameKey).get(childPosition).get("incomeAccount");

			if (expenseAccount > 0 && incomeAccount > 0) {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(CategoryListActivity.this,
						EditTransferActivity.class);
				startActivityForResult(intent, 13);

			} else {

				Intent intent = new Intent();
				intent.putExtra("_id", tId);
				intent.setClass(CategoryListActivity.this,
						EditTransactionActivity.class);
				startActivityForResult(intent, 13);

			}
			return true;
		}

	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (resultCode) {
		case 13:

			if (data != null) {
				
					mHandler.post(mTask);
					
					    Intent resultintent = new Intent();
						resultintent.putExtra("row", 1);
						setResult(2, resultintent);
			}
			break;
		}
		
	}
	

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed", Toast.LENGTH_SHORT)
				.show();
	}

}
