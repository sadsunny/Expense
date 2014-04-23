package com.appxy.pocketexpensepro.setting.category;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryExpandableListViewAdapter extends
		BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private List<Map<String, Object>> groupList;
	private List<List<Map<String, Object>>> childList;

	public CategoryExpandableListViewAdapter(Context context) {

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

	@Override
	// gets the title of each parent/group
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	// counts the number of group/parent items so the list knows how many times
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

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		gViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new gViewHolder();
			convertView = inflater.inflate(R.layout.category_group, parent,
					false);
			viewholder.mImageView = (ImageView) convertView
					.findViewById(R.id.category_img);
			viewholder.mTextView = (TextView) convertView
					.findViewById(R.id.category_txt);
			convertView.setTag(viewholder);
		} else {
			viewholder = (gViewHolder) convertView.getTag();
		}

		viewholder.mImageView
				.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer) groupList
						.get(groupPosition).get("iconName")]);
		viewholder.mTextView.setText(groupList.get(groupPosition).get(
				"categoryName")
				+ "");

//		convertView.setOnTouchListener(new View.OnTouchListener() { //设置Group是否可点击
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return true;
//			}
//		});

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

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		cViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new cViewHolder();
			convertView = inflater.inflate(R.layout.category_child, parent,
					false);

			viewholder.mImageView = (ImageView) convertView
					.findViewById(R.id.category_img);
			viewholder.mTextView = (TextView) convertView
					.findViewById(R.id.category_txt);

			convertView.setTag(viewholder);
		} else {
			viewholder = (cViewHolder) convertView.getTag();

		}
		viewholder.mImageView
		.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer) childList.get(groupPosition).get(childPosition).get("iconName")]);
		
		String cName = (String) childList.get(groupPosition).get(childPosition)
				.get("categoryName");
		String temp[] = cName.split(":");
		String categoryName = temp[1] ;
		viewholder.mTextView.setText(categoryName);

		return convertView;
	}

	public class cViewHolder {
		public ImageView mImageView;
		public TextView mTextView;
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
		public ImageView mImageView;
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
