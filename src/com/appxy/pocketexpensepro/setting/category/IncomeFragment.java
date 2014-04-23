package com.appxy.pocketexpensepro.setting.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.setting.SettingDao;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class IncomeFragment extends Fragment {

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
		View view = inflater.inflate(R.layout.activity_category, container,
				false);
		this.mInflater = inflater;
		mExpandableListView = (ExpandableListView) view
				.findViewById(R.id.mExpandableListView);
		mAdapter = new CategoryExpandableListViewAdapter(getActivity());

		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView.setOnItemLongClickListener(listener);

		Thread mThread = new Thread(mTask);
		mThread.start();

		return view;
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList = CategoryDao.selectCategory(getActivity(), 1);
			if (mDataList != null) {
				filterData(mDataList);
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
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
			
			final int mPositionType = ExpandableListView.getPackedPositionType(arg3);
			  
			Log.v("mtest", "groupPosition"+groupPosition+"  childPosition"+childPosition);
			
			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);
			diaListView = (ListView) dialogView.findViewById(R.id.dia_listview);
			mDialogItemAdapter = new DialogItemAdapter(getActivity());
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					if (arg2 == 0) {
						
					    int _id = 0;
					    
						if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
							_id = (Integer) childrenAllDataList.get(groupPosition)
									.get(childPosition).get("_id");
							if ( _id > 0) {
								long row =CategoryDao.deleteCategory(getActivity(), _id);
							}
							
						}else if (mPositionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
							_id = (Integer) groupDataList.get(groupPosition).get("_id");
							String cName = (String)groupDataList.get(groupPosition).get("categoryName");
							Log.v("mtest", "cName"+cName);
							if ( _id > 0) {
								long row = CategoryDao.deleteCategory(getActivity(), _id);
								long row2 = CategoryDao.deleteCategoryLike(getActivity(), cName);
							}
						}
						
						mHandler.post(mTask);
						alertDialog.dismiss();

					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
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
			intent.putExtra("mcheck", 1);
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
		}
	}
	

}
