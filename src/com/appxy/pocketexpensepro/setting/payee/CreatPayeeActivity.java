package com.appxy.pocketexpensepro.setting.payee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.category.CreatExpenseActivity;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnChildClickListener;

@SuppressLint("ResourceAsColor")
public class CreatPayeeActivity extends BaseHomeActivity {
	private LayoutInflater inflater;
	private EditText nameEditText;
	private Button categoryButton;
	private EditText memoEditText;
	private AlertDialog mDialog;
	private ExpandableListView mExpandableListView;
	private DialogExpandableListViewAdapter mDialogExpandableListViewAdapter;
	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	public Handler handler;
	
	private int checkedItem;
	private int gCheckedItem;// 选择位置
	private int cCheckedItem;
	
	private RelativeLayout expenseButton;
	private RelativeLayout incomeButton;
	private View chooseView1;
	private View chooseView2;
	
	private int mCategoryType = 0; // 0 expense 1 income
	private int mCheckCategoryType = 0; // 0 expense 1 income
	private int categoryId; // 初次others的位置

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_payee);

		
		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		inflater = LayoutInflater.from(CreatPayeeActivity.this);
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

		nameEditText = (EditText) findViewById(R.id.payee_edit);
		categoryButton = (Button) findViewById(R.id.category_btn);
		memoEditText = (EditText) findViewById(R.id.memo_edit);
		categoryButton.setOnClickListener(mClickListener);
		categoryButton.setText("Others");

		
		List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
				CreatPayeeActivity.this, 0);
		filterData(mDataList);
		categoryId = locationOthersId(groupDataList);
		gCheckedItem = locationOthersPosition(groupDataList);
		cCheckedItem = -1;
		
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
				String mPayeeName = nameEditText.getText().toString();
				String mMemo = memoEditText.getText().toString();
				List<Map<String, Object>> mPayeeData = PayeeDao.selectPayee(CreatPayeeActivity.this);
				
				if (mPayeeName == null
						|| mPayeeName.trim().length() == 0
						|| mPayeeName.trim().equals("")) {
					new AlertDialog.Builder(CreatPayeeActivity.this)
					.setTitle("Warning! ")
					.setMessage(
							"Please make sure the name is not empty! ")
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}).show();
					
				}else if( judgePayee(mPayeeData,mPayeeName)){
					
					new AlertDialog.Builder(CreatPayeeActivity.this)
					.setTitle("Warning! ")
					.setMessage(
							"Name already exists! ")
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}).show();
					
				}else{
					
					if(categoryId > 0){
						long id = PayeeDao.insertPayee(CreatPayeeActivity.this, mPayeeName, mMemo, categoryId);
						Log.v("mtest","id"+id);
						if (id > 0) {
							Intent intent = new Intent();
							intent.putExtra("_id", id);
							setResult(4, intent);
							finish();
						}
					}
					
				}
				
				break;

			case R.id.category_btn:

				View view = inflater.inflate(R.layout.dialog_choose_category, null);
				expenseButton = (RelativeLayout) view
						.findViewById(R.id.expense_btn);
				incomeButton = (RelativeLayout) view.findViewById(R.id.income_btn);
				chooseView1  = (View) view.findViewById(R.id.view1);
				chooseView2  = (View) view.findViewById(R.id.view2);
				
				if(mCategoryType == 0){
					chooseView1.setVisibility(View.VISIBLE);
					chooseView2.setVisibility(View.INVISIBLE);
				}else{
					chooseView1.setVisibility(View.INVISIBLE);
					chooseView2.setVisibility(View.VISIBLE);
				}

				expenseButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						chooseView1.setVisibility(View.VISIBLE);
						chooseView2.setVisibility(View.INVISIBLE);
						
						List<Map<String, Object>> mDataList = PayeeDao.selectCategory(CreatPayeeActivity.this, 0);
						filterData(mDataList);
						
						mDialogExpandableListViewAdapter.notifyDataSetChanged();
						
						mExpandableListView
								.setOnChildClickListener(new OnChildClickListener() {

									@Override
									public boolean onChildClick(
											ExpandableListView parent, View v,
											int groupPosition, int childPosition,
											long id) {
										// TODO Auto-generated method stub
										mCategoryType = 0;
										
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition,
																childPosition));

										gCheckedItem = groupPosition;
										cCheckedItem = childPosition;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();

										categoryId = (Integer) childrenAllDataList
												.get(groupPosition).get(childPosition)
												.get("_id");
										String cName = (String) childrenAllDataList.get(groupPosition).get(childPosition).get("categoryName");
										categoryButton.setText(cName);
										mDialog.dismiss();

										return true;
									}
								});

						mExpandableListView
								.setOnGroupClickListener(new OnGroupClickListener() {

									@Override
									public boolean onGroupClick(
											ExpandableListView parent, View v,
											int groupPosition, long id) {
										// TODO Auto-generated method stub
										mCategoryType = 0;
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition, 0));
										
										gCheckedItem = groupPosition;
										cCheckedItem = -1;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();
										mDialog.dismiss();

										categoryId = (Integer) groupDataList.get(
												groupPosition).get("_id");
										String cName = (String) groupDataList.get(groupPosition).get("categoryName");
										categoryButton.setText(cName);
										return true;
									}
								});

						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);
						
					}
				});
				

				incomeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						chooseView1.setVisibility(View.INVISIBLE);
						chooseView2.setVisibility(View.VISIBLE);
						List<Map<String, Object>> mDataList = PayeeDao.selectCategory(CreatPayeeActivity.this, 1);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();
						
						mExpandableListView
								.setOnChildClickListener(new OnChildClickListener() {

									@Override
									public boolean onChildClick(
											ExpandableListView parent, View v,
											int groupPosition, int childPosition,
											long id) {
										// TODO Auto-generated method stub
										mCategoryType = 1;
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition,
																childPosition));

										gCheckedItem = groupPosition;
										cCheckedItem = childPosition;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();

										categoryId = (Integer) childrenAllDataList
												.get(groupPosition).get(childPosition)
												.get("_id");
										String cName = (String) childrenAllDataList.get(groupPosition).get(childPosition).get("categoryName");
										categoryButton.setText(cName);
										
										mDialog.dismiss();

										return true;
									}
								});

						mExpandableListView
								.setOnGroupClickListener(new OnGroupClickListener() {

									@Override
									public boolean onGroupClick(
											ExpandableListView parent, View v,
											int groupPosition, long id) {
										// TODO Auto-generated method stub
										mCategoryType = 1;
										checkedItem = mExpandableListView
												.getFlatListPosition(ExpandableListView
														.getPackedPositionForChild(
																groupPosition, 0));
										
										gCheckedItem = groupPosition;
										cCheckedItem = -1;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();
										mDialog.dismiss();

										categoryId = (Integer) groupDataList.get(
												groupPosition).get("_id");
										String cName = (String) groupDataList.get(groupPosition).get("categoryName");
										categoryButton.setText(cName);
										
										return true;
									}
								});

						int groupCount = groupDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);
						
					}
				});

				mExpandableListView = (ExpandableListView) view.findViewById(R.id.mExpandableListView);
				mDialogExpandableListViewAdapter = new DialogExpandableListViewAdapter(CreatPayeeActivity.this);
				mExpandableListView.setAdapter(mDialogExpandableListViewAdapter);
				mExpandableListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
				mExpandableListView.setItemsCanFocus(false);
				mExpandableListView.setGroupIndicator(null);

				checkedItem = mExpandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(gCheckedItem, 0)); //根据group和child找到绝对位置
				
				if (mCategoryType == 0) {
					List<Map<String, Object>> mDataList = PayeeDao.selectCategory(CreatPayeeActivity.this, 0);
					filterData(mDataList);
					
					mExpandableListView.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1): 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(gCheckedItem, cCheckedItem);

				
				} else if (mCategoryType == 1) {
					List<Map<String, Object>> mDataList = PayeeDao.selectCategory(CreatPayeeActivity.this, 1);
					filterData(mDataList);
					
					mExpandableListView.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1): 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(gCheckedItem, cCheckedItem);
					
				}
				
				
				mDialogExpandableListViewAdapter.setAdapterData(groupDataList,childrenAllDataList);
				mDialogExpandableListViewAdapter.notifyDataSetChanged();

				mExpandableListView
						.setOnChildClickListener(new OnChildClickListener() {

							@Override
							public boolean onChildClick(
									ExpandableListView parent, View v,
									int groupPosition, int childPosition,
									long id) {
								// TODO Auto-generated method stub

								checkedItem = mExpandableListView
										.getFlatListPosition(ExpandableListView
												.getPackedPositionForChild(
														groupPosition,
														childPosition));

								gCheckedItem = groupPosition;
								cCheckedItem = childPosition;

								mDialogExpandableListViewAdapter
										.setSelectedPosition(gCheckedItem,
												cCheckedItem);
								mDialogExpandableListViewAdapter
										.notifyDataSetChanged();

								categoryId = (Integer) childrenAllDataList
										.get(groupPosition).get(childPosition)
										.get("_id");
								String cName = (String) childrenAllDataList.get(groupPosition).get(childPosition).get("categoryName");
								categoryButton.setText(cName);
								mDialog.dismiss();

								return true;
							}
						});

				mExpandableListView
						.setOnGroupClickListener(new OnGroupClickListener() {

							@Override
							public boolean onGroupClick(
									ExpandableListView parent, View v,
									int groupPosition, long id) {
								// TODO Auto-generated method stub

								checkedItem = mExpandableListView
										.getFlatListPosition(ExpandableListView
												.getPackedPositionForChild(
														groupPosition, 0));
								
								gCheckedItem = groupPosition;
								cCheckedItem = -1;

								mDialogExpandableListViewAdapter
										.setSelectedPosition(gCheckedItem,
												cCheckedItem);
								mDialogExpandableListViewAdapter
										.notifyDataSetChanged();
								mDialog.dismiss();

								categoryId = (Integer) groupDataList.get(
										groupPosition).get("_id");
								String cName = (String) groupDataList.get(groupPosition).get("categoryName");
								categoryButton.setText(cName);
								return true;
							}
						});

				int groupCount = groupDataList.size();

				for (int i = 0; i < groupCount; i++) {
					mExpandableListView.expandGroup(i);
				}
				mExpandableListView.setCacheColorHint(0);

				AlertDialog.Builder mBuilder = new AlertDialog.Builder(
						CreatPayeeActivity.this);
				mBuilder.setTitle("Choose Category");
				mBuilder.setView(view);
				mBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mDialog = mBuilder.create();
				mDialog.show();

				break;
			}
		}
	};

	public int locationOthersPosition(List<Map<String, Object>> mData) { // 定位others的位置
		int i = 0;
		int position = 0;
		for (Map<String, Object> mMap : mData) {
			String categoryName = (String) mMap.get("categoryName");
			if (categoryName.equals("Others")) {
				position = i;
			}
			i = i+1;
		}

		return position;
	}

	public int locationOthersId(List<Map<String, Object>> mData) { // 定位others的位置
		int id = 0;
		for (Map<String, Object> mMap : mData) {
			String categoryName = (String) mMap.get("categoryName");
			String temp[] = categoryName.split(":");
			if (temp[0].equals("Others")) {
				id = (Integer) mMap.get("_id");
			}
		}
		return id;
	}
	
	public boolean judgePayee(List<Map<String, Object>> mData ,String pName){
		boolean check = false;
		 for (Map<String, Object> iMap: mData) {
			String name = (String) iMap.get("name");
			if (name.equals(pName)) {
				check = true;
			}
		}
		return check;
	}

	public void filterData(List<Map<String, Object>> mData) {// 过滤子类和父类

		List<Map<String, Object>> temChildDataList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList;

		groupDataList.clear();
		childrenAllDataList.clear();

		for (Map<String, Object> mMap : mData) { // 分离父类和子类
			String categoryName = (String) mMap.get("categoryName");

			if (categoryName.contains(":")) {
				temChildDataList.add(mMap);
			} else {
				groupDataList.add(mMap);
			}

		}

		for (Map<String, Object> mMap : groupDataList) {

			String categoryName = (String) mMap.get("categoryName");
			tempList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> iMap : temChildDataList) {

				String cName = (String) iMap.get("categoryName");
				String temp[] = cName.split(":");
				if (temp[0].equals(categoryName)) {
					tempList.add(iMap);
				}

			}
			childrenAllDataList.add(tempList);
		}
	}

	@Override
	public void syncDateChange() {
		// TODO Auto-generated method stub
		
	}
	
}
