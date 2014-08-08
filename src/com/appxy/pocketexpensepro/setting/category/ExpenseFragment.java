package com.appxy.pocketexpensepro.setting.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;

/*
 * code 3
 */

public class ExpenseFragment extends Fragment {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private ExpandableListView mExpandableListView;
	private List<Map<String, Object>> mDataList;
	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	private CategoryExpandableListViewAdapter mAdapter;
	private LayoutInflater mInflater;
	private ListView diaListView;
	private DialogItemAdapter mDialogItemAdapter;
	private AlertDialog alertDialog;

	public ExpenseFragment() {

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				mAdapter.setAdapterData(groupDataList, childrenAllDataList);
				mAdapter.notifyDataSetChanged();
				int groupCount = groupDataList.size();

				for (int i = 0; i < groupCount; i++) {
					mExpandableListView.expandGroup(i);
				}
				// mExpandableListView
				// .setOnGroupClickListener(new OnGroupClickListener() {
				//
				// @Override
				// public boolean onGroupClick(
				// ExpandableListView parent, View v,
				// int groupPosition, long id) {
				// // TODO Auto-generated method stub
				// return true;
				// }
				// });

				mExpandableListView.setCacheColorHint(0);

				break;

			case MSG_FAILURE:
				Toast.makeText(getActivity(), "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.mInflater = inflater;
		View view = inflater.inflate(R.layout.activity_category, container,
				false);

		mExpandableListView = (ExpandableListView) view
				.findViewById(R.id.mExpandableListView);
		mAdapter = new CategoryExpandableListViewAdapter(getActivity());

		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setOnItemLongClickListener(listener);
		mExpandableListView.setOnChildClickListener(onChildClickListener);
		mExpandableListView.setOnGroupClickListener(onGroupClickListener);

		Thread mThread = new Thread(mTask);
		mThread.start();

		return view;
	}

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// TODO Auto-generated method stub
			int cId = (Integer) childrenAllDataList.get(groupPosition)
					.get(childPosition).get("_id");
			if (0 <= cId && cId <= 49) {
				return true;
			} else {

				Intent intent = new Intent();
				intent.putExtra("_id", cId);
				intent.setClass(getActivity(), EditCategoryActivity.class);
				startActivityForResult(intent, 11);
				return true;
			}

		}
	};

	private OnGroupClickListener onGroupClickListener = new OnGroupClickListener() {

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			// TODO Auto-generated method stub
			int cId = (Integer) groupDataList.get(groupPosition).get("_id");

			if (0 <= cId && cId <= 49) {
				return true;
			} else {
				Intent intent = new Intent();
				intent.putExtra("_id", cId);
				intent.setClass(getActivity(), EditCategoryActivity.class);
				startActivityForResult(intent, 11);
				return true;
			}

		}
	};

	private OnItemLongClickListener listener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int groupPosition = mExpandableListView
					.getPackedPositionGroup(arg3);
			final int childPosition = mExpandableListView
					.getPackedPositionChild(arg3);

			final int mPositionType = ExpandableListView
					.getPackedPositionType(arg3);
			int cId = 0;

			if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				cId = (Integer) childrenAllDataList.get(groupPosition)
						.get(childPosition).get("_id");

			} else if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				cId = (Integer) groupDataList.get(groupPosition).get("_id");

			}

			if (0 <= cId && cId <= 49) {
				return true;
			} else {
				View dialogView = mInflater.inflate(
						R.layout.dialog_item_operation, null);
				diaListView = (ListView) dialogView
						.findViewById(R.id.dia_listview);
				mDialogItemAdapter = new DialogItemAdapter(getActivity());
				diaListView.setAdapter(mDialogItemAdapter);
				diaListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						
						int d_id = 0;
						if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
							d_id = (Integer) childrenAllDataList
									.get(groupPosition).get(childPosition)
									.get("_id");

						} else if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
							d_id = (Integer) groupDataList
									.get(groupPosition).get("_id");
						}
						
						if (arg2 == 0) {
							int size = CategoryDao.selectCategoryRelate(getActivity(), d_id);
							if (size > 0) {
								
								new AlertDialog.Builder(getActivity())
								.setTitle("Delete This Category? ")
								.setMessage(
										" Deleting a category will cause to delete all associated transactions, bills and budgets. Are you sure you want to delete it? ")
								.setNegativeButton(
										"No",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated
												// method stub
												dialog.dismiss();
												alertDialog.dismiss();
											}

										})
								.setPositiveButton(
										"Yes",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated
												// method stub
												
												int _id = 0;
												if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
													_id = (Integer) childrenAllDataList
															.get(groupPosition).get(childPosition)
															.get("_id");
													if (_id > 0) {
														long row = CategoryDao.deleteCategory(
																getActivity(), _id);
														if (row > 0) {
															MainActivity.sqlChange = 1;
														}
														if (row > 0 && MainActivity.mItemPosition == 2) { //从report过来的删除
															MainActivity.sqlChange3 = 1;
														}
													}

												} else if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
													_id = (Integer) groupDataList
															.get(groupPosition).get("_id");
													String cName = (String) groupDataList.get(
															groupPosition).get("categoryName");
													Log.v("mtest", "cName" + cName);
													if (_id > 0) {
														long row = CategoryDao.deleteCategory(
																getActivity(), _id);
														long row2 = CategoryDao.deleteCategoryLike(
																getActivity(), cName);
														if (row > 0) {
															MainActivity.sqlChange = 1;
														}
														if (row > 0 && MainActivity.mItemPosition == 2) { //从report过来的删除
															MainActivity.sqlChange3 = 1;
														}
													}
												}

												mHandler.post(mTask);
												alertDialog.dismiss();
												
											}
										}).show();

								
								
							} else {
								
								int _id = 0;
								if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
									_id = (Integer) childrenAllDataList
											.get(groupPosition).get(childPosition)
											.get("_id");
									if (_id > 0) {
										long row = CategoryDao.deleteCategory(
												getActivity(), _id);
										if (row > 0) {
											MainActivity.sqlChange = 1;
										}
										if (row > 0 && MainActivity.mItemPosition == 2) { //从report过来的删除
											MainActivity.sqlChange3 = 1;
										}
									}

								} else if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
									_id = (Integer) groupDataList
											.get(groupPosition).get("_id");
									String cName = (String) groupDataList.get(
											groupPosition).get("categoryName");
									Log.v("mtest", "cName" + cName);
									if (_id > 0) {
										long row = CategoryDao.deleteCategory(
												getActivity(), _id);
										long row2 = CategoryDao.deleteCategoryLike(
												getActivity(), cName);
										if (row > 0) {
											MainActivity.sqlChange = 1;
										}
										if (row > 0 && MainActivity.mItemPosition == 2) { //从report过来的删除
											MainActivity.sqlChange3 = 1;
										}
									}
								}

								mHandler.post(mTask);
								alertDialog.dismiss();


							}
							
						}

					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setView(dialogView);
				alertDialog = builder.create();
				alertDialog.show();

				return true;
			}

		}

	};

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList = CategoryDao.selectCategory(getActivity(), 0);
			if (mDataList != null) {
				filterData(mDataList);
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent();
			intent.putExtra("mcheck", 0);
			intent.setClass(getActivity(), CreatExpenseActivity.class);
			startActivityForResult(intent, 3);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case 3:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;

		case 11:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;
		}
	}

}
