package com.appxy.pocketexpensepro.overview.budgets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BudgetSelectCategoryActivity extends BaseHomeActivity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private LayoutInflater inflater;
	private List<Map<String, Object>> mDataList;
	private ListView mListView;
	private BudgetsListViewAdapter mAdapter;
	private SparseArray<Integer> choosedArray;
	private TreeMap<Integer, Integer> choosedMap;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				mAdapter.setAdapterDate(mDataList);
				mAdapter.setChooseArray(choosedArray);
				mAdapter.notifyDataSetChanged();
				
				break;

			case MSG_FAILURE:
				Toast.makeText(BudgetSelectCategoryActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget_select_category);

		mListView = (ListView) findViewById(R.id.mListView);
		mAdapter = new BudgetsListViewAdapter(this);
		mListView.setDividerHeight(0);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mAdapter);
		
		choosedArray = new SparseArray<Integer>();
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub.
				int hasChild  = choosedArray.get(arg2);
				choosedArray.put(arg2, (hasChild == 1)?0:1);	
				mAdapter.setChooseArray(choosedArray);
				mAdapter.notifyDataSetChanged();
			}
		});
		
	
		
		inflater = LayoutInflater.from(BudgetSelectCategoryActivity.this);

		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(
				R.layout.activity_custom_actionbar, null, false);

		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView, lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);

		View cancelActionView = customActionBarView
				.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(mClickListener);
		View doneActionView = customActionBarView
				.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mClickListener);
		
		
		Thread mThread = new Thread(mTask);
		mThread.start();

	}

	public Runnable mTask = new Runnable() {

		@Override 
		public void run() {
			// TODO Auto-generated method stub
			mDataList = BudgetsDao.selectCategoryLeftBudget(BudgetSelectCategoryActivity.this);
			choosedArray.clear();
			for (int i = 0; i < mDataList.size(); i++) {
				int hasChild =  (Integer) mDataList.get(i).get("hasChild");
				if (hasChild > 0) {
					choosedArray.put(i, 1);
				}else{
					choosedArray.put(i, 0);

				}
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
		//
	};

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.action_cancel:
				
				finish();
				break;

			case R.id.action_done:
				
				List<Map<String, Object>> mReturnDataList = new ArrayList<Map<String,Object>>();
				for (int i = 0; i < choosedArray.size(); i++) {
					int hasChild = choosedArray.get(i);
					if (hasChild == 1) {
						mReturnDataList.add(mDataList.get(i));
					}
				}
				
				Intent intent = new Intent();
				intent.putExtra("mReturnDataList", (Serializable)mReturnDataList);
				setResult(4, intent);
				
				finish();
				break;
			}
		}
	};
	
	

}
