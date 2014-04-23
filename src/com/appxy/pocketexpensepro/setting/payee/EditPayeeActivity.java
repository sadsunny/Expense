package com.appxy.pocketexpensepro.setting.payee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

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
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnChildClickListener;

@SuppressLint("ResourceAsColor")
public class EditPayeeActivity extends Activity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
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
	private Button expenseButton;
	private Button incomeButton;
	private int mCategoryType = 0; // 0 expense 1 income
	private int mCheckCategoryType = 0; // 0 expense 1 income
	private int categoryId; // 初次others的位置
	/*
	 * edit 变量
	 */
	private int payeeId;
	private List<Map<String, Object>> payeeDataList;
	private String payeeNameE;
    private int categoryTypeE;
    private String payeeMemoE;
    private String categoryNameE;
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_payee);

		Intent intent = getIntent();
		payeeId = intent.getIntExtra("_id", 0);
		payeeDataList = PayeeDao.selectPayeeById(this, payeeId);
		
		payeeNameE = (String) payeeDataList.get(0).get("name");
		categoryTypeE = (Integer) payeeDataList.get(0).get("categoryType");
		payeeMemoE = (String) payeeDataList.get(0).get("memo");
		categoryId = (Integer) payeeDataList.get(0).get("categoryId");
		categoryNameE = (String) payeeDataList.get(0).get("categoryName");
		mCategoryType = categoryTypeE;
		
		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		inflater = LayoutInflater.from(EditPayeeActivity.this);
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
		
		categoryButton.setText(categoryNameE);
		nameEditText.setText(payeeNameE);
		nameEditText.setSelection(payeeNameE.length());
		nameEditText.setSelectAllOnFocus(true);
		memoEditText.setText(payeeMemoE);
		
		List<Map<String, Object>> mDataList = PayeeDao.selectCategory(
				EditPayeeActivity.this, mCategoryType);
		filterData(mDataList);
		gCheckedItem = locationCategoryPosition(groupDataList, categoryId);
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

				if (mPayeeName == null || mPayeeName.trim().length() == 0
						|| mPayeeName.trim().equals("")) {
					new AlertDialog.Builder(EditPayeeActivity.this)
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

				} else {

					if (categoryId > 0) {
						long id = PayeeDao.updatePayee(EditPayeeActivity.this,payeeId,mPayeeName, mMemo, categoryId);
						if (id > 0) {
							Intent intent = new Intent();
							intent.putExtra("_id", id);
							setResult(5, intent);
							finish();
						}
					}

				}

				finish();
				break;

			case R.id.category_btn:

				View view = inflater.inflate(R.layout.dialog_choose_category,
						null);
				expenseButton = (Button) view.findViewById(R.id.expense_btn);
				incomeButton = (Button) view.findViewById(R.id.income_btn);

				expenseButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(EditPayeeActivity.this, 0);
						filterData(mDataList);

						mDialogExpandableListViewAdapter.notifyDataSetChanged();

						mExpandableListView
								.setOnChildClickListener(new OnChildClickListener() {

									@Override
									public boolean onChildClick(
											ExpandableListView parent, View v,
											int groupPosition,
											int childPosition, long id) {
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
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();

										categoryId = (Integer) childrenAllDataList
												.get(groupPosition)
												.get(childPosition).get("_id");
										String cName = (String) childrenAllDataList
												.get(groupPosition)
												.get(childPosition)
												.get("categoryName");
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
																groupPosition,
																0));

										gCheckedItem = groupPosition;
										cCheckedItem = -1;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();
										mDialog.dismiss();

										categoryId = (Integer) groupDataList
												.get(groupPosition).get("_id");
										String cName = (String) groupDataList
												.get(groupPosition).get(
														"categoryName");
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
						mCategoryType = 1;
						List<Map<String, Object>> mDataList = PayeeDao
								.selectCategory(EditPayeeActivity.this, 1);
						filterData(mDataList);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();

						mExpandableListView
								.setOnChildClickListener(new OnChildClickListener() {

									@Override
									public boolean onChildClick(
											ExpandableListView parent, View v,
											int groupPosition,
											int childPosition, long id) {
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
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();

										categoryId = (Integer) childrenAllDataList
												.get(groupPosition)
												.get(childPosition).get("_id");
										String cName = (String) childrenAllDataList
												.get(groupPosition)
												.get(childPosition)
												.get("categoryName");
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
																groupPosition,
																0));

										gCheckedItem = groupPosition;
										cCheckedItem = -1;

										mDialogExpandableListViewAdapter
												.setSelectedPosition(
														gCheckedItem,
														cCheckedItem);
										mDialogExpandableListViewAdapter
												.notifyDataSetChanged();
										mDialog.dismiss();

										categoryId = (Integer) groupDataList
												.get(groupPosition).get("_id");
										String cName = (String) groupDataList
												.get(groupPosition).get(
														"categoryName");
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

				mExpandableListView = (ExpandableListView) view
						.findViewById(R.id.mExpandableListView);
				mDialogExpandableListViewAdapter = new DialogExpandableListViewAdapter(
						EditPayeeActivity.this);
				mExpandableListView
						.setAdapter(mDialogExpandableListViewAdapter);
				mExpandableListView
						.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
				mExpandableListView.setItemsCanFocus(false);
				mExpandableListView.setGroupIndicator(null);

				checkedItem = mExpandableListView
						.getFlatListPosition(ExpandableListView
								.getPackedPositionForChild(gCheckedItem, 0)); // 根据group和child找到绝对位置

				if (mCategoryType == 0) {
					List<Map<String, Object>> mDataList = PayeeDao
							.selectCategory(EditPayeeActivity.this, 0);
					filterData(mDataList);

					mExpandableListView
							.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
									: 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(
							gCheckedItem, cCheckedItem);

				} else if (mCategoryType == 1) {
					List<Map<String, Object>> mDataList = PayeeDao
							.selectCategory(EditPayeeActivity.this, 1);
					filterData(mDataList);

					mExpandableListView
							.setSelection((checkedItem - 1) > 0 ? (checkedItem - 1)
									: 0);
					mDialogExpandableListViewAdapter.setSelectedPosition(
							gCheckedItem, cCheckedItem);

				}

				mDialogExpandableListViewAdapter.setAdapterData(groupDataList,
						childrenAllDataList);
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
								String cName = (String) childrenAllDataList
										.get(groupPosition).get(childPosition)
										.get("categoryName");
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
								String cName = (String) groupDataList.get(
										groupPosition).get("categoryName");
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
						EditPayeeActivity.this);
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

	public int locationCategoryPosition(List<Map<String, Object>> mData,int cId) { // 定位others的位置
		int i = 0;
		int position = 0;
		for (Map<String, Object> mMap : mData) {
			int categoryId = (Integer) mMap.get("_id");
			if (categoryId == cId) {
				position = i;
			}
			i = i + 1;
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

}
