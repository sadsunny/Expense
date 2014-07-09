package com.appxy.pocketexpensepro.bills;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

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

public class ExpandableListViewAdapter extends
		BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private List<Map<String, Object>> groupList;
	private List<List<Map<String, Object>>> childList;

	public ExpandableListViewAdapter(Context context) {

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
			convertView = inflater.inflate(R.layout.bill_group, parent,
					false);
			 viewholder.dueTextView = (TextView)convertView.findViewById(R.id.dueTextView);
			 viewholder.dueCountTextView = (TextView)convertView.findViewById(R.id.dueCountTextView);
			convertView.setTag(viewholder);
		} else {
			viewholder = (gViewHolder) convertView.getTag();
		}

		viewholder.dueTextView.setText(groupList.get(groupPosition).get("dueString")+"");
		viewholder.dueCountTextView.setText(groupList.get(groupPosition).get("dueCount")+"");
		
		convertView.setOnTouchListener(new View.OnTouchListener() { //设置Group是否可点击

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

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		cViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new cViewHolder();
			convertView = inflater.inflate(R.layout.bill_list_item, parent,
					false);

			viewholder.mIcon = (ImageView) convertView.findViewById(R.id.account_type_img);
			viewholder.accountTextView = (TextView) convertView.findViewById(R.id.account_txt);
			viewholder.dateTextView = (TextView) convertView.findViewById(R.id.date_txt);
			viewholder.symbolTextView = (TextView) convertView.findViewById(R.id.symbol_txt);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.amountTextView = (TextView) convertView.findViewById(R.id.amounttextView);
			viewholder.mline_label = (View) convertView.findViewById(R.id.mline_label); 
			convertView.setTag(viewholder);
		} else {
			viewholder = (cViewHolder) convertView.getTag();

		}
		viewholder.mIcon.setImageResource(Common.CATEGORY_ICON[(Integer)childList.get(groupPosition).get(childPosition).get("iconName")]);
		viewholder.accountTextView.setText(childList.get(groupPosition).get(childPosition).get("ep_billName")+"");
		viewholder.currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		long dueDate = (Long) childList.get(groupPosition).get(childPosition).get("ep_billDueDate");
		viewholder.dateTextView.setText(MEntity.turnToDateString(dueDate));
		viewholder.amountTextView.setText( MEntity.doublepoint2str((String)childList.get(groupPosition).get(childPosition).get("ep_billAmount")));

		if (childPosition == 0 ) {
			viewholder.mline_label.setVisibility(View.INVISIBLE);
		} else {
			viewholder.mline_label.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	public class cViewHolder {
		public ImageView mIcon;
		public TextView accountTextView;
		public TextView dateTextView;
		public TextView symbolTextView;
		public TextView currencyTextView;
		public TextView amountTextView;
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
		public TextView dueTextView;
		public TextView dueCountTextView;
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
