package com.appxy.pocketexpensepro.reports;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private Context context;

	
	private ArrayList<HashMap<String, Object>> mGroupList;
	private HashMap<String, ArrayList<HashMap<String, Object>>> mChildMap; 
	
	
	private double allAmount = 0;
	private String mCurrencyLabel = "$";
	private NumberFormat percentFormat;

	public ExpandableListViewAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		mCurrencyLabel = Common.CURRENCY_SIGN[Common.CURRENCY];
		percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(2);
	}

	public void setAdapterData( ArrayList<HashMap<String, Object>> mGroupList,
			HashMap<String, ArrayList<HashMap<String, Object>>> mChildMap) {

		this.mGroupList = mGroupList;
		this.mChildMap = mChildMap;

	}

	public void setAllAmount(double allAmount) {
		this.allAmount = allAmount;
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
		if (mGroupList == null) {
			return 0;
		}
		return mGroupList.size();
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
			convertView = inflater.inflate(R.layout.category_list_group,
					parent, false);
			viewholder.mNameView = (TextView) convertView
					.findViewById(R.id.category_txt);
			viewholder.mPercentView = (TextView) convertView
					.findViewById(R.id.percent_txt);
			viewholder.mCureencyView = (TextView) convertView
					.findViewById(R.id.currency_txt);
			viewholder.mAmountView = (TextView) convertView
					.findViewById(R.id.amount_txt);

			convertView.setTag(viewholder);
		} else {
			viewholder = (gViewHolder) convertView.getTag();
		}

		String mCategoyNameKey = (String) mGroupList.get(groupPosition).get("categoryName");
		double amount = (Double) mGroupList.get(groupPosition).get("sum");
				
		String mPercent = "0.00%";
		if (allAmount == 0) {
			mPercent = percentFormat.format(0);
		} else {
			mPercent = percentFormat.format(amount/allAmount);
		}

		viewholder.mNameView.setText(mCategoyNameKey);
		viewholder.mPercentView.setText(mPercent);
		viewholder.mCureencyView.setText(mCurrencyLabel);
		viewholder.mAmountView.setText(MEntity.doublepoint2str(amount + ""));

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
	public View getChildView( int groupPosition,  int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		cViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new cViewHolder();
			convertView = inflater.inflate(R.layout.category_list_child, parent,
					false);

			viewholder.mNameView = (TextView) convertView
					.findViewById(R.id.account_txt);
			viewholder.mDateView = (TextView) convertView
					.findViewById(R.id.type_txt);
			viewholder.mCurrencyView = (TextView) convertView
					.findViewById(R.id.currency_txt);
			viewholder.mAmountView = (TextView) convertView
					.findViewById(R.id.amount_txt);
			viewholder.mLineView = (View) convertView
					.findViewById(R.id.mLineView);

			convertView.setTag(viewholder);
		} else {
			viewholder = (cViewHolder) convertView.getTag();

		}
		
		if (childPosition == 0) {
			viewholder.mLineView.setVisibility(View.INVISIBLE);
		} else {
			viewholder.mLineView.setVisibility(View.VISIBLE);
			
		}
		
		String mCategoyNameKey =  (String) mGroupList.get(groupPosition).get("categoryName");
		
		long dateTime = (Long) mChildMap.get(mCategoyNameKey).get(childPosition).get("dateTime");
		double amount = (Double) mChildMap.get(mCategoyNameKey).get(childPosition).get("amount");
		int categoryType = (Integer) mChildMap.get(mCategoyNameKey).get(childPosition).get("categoryType");
		String payeeName  = (String) mChildMap.get(mCategoyNameKey).get(childPosition).get("payeeName");
		if (payeeName == null || payeeName.length() == 0) {
			payeeName = "--" ;
		} 
		viewholder.mNameView.setText(payeeName);
		viewholder.mDateView.setText(turnToDateString(dateTime));
		viewholder.mCurrencyView.setText(mCurrencyLabel);
		viewholder.mAmountView.setText(MEntity.doublepoint2str( String.valueOf( amount ) ));
		
		if (categoryType == 0) {
			viewholder.mCurrencyView.setTextColor(Color.rgb(208, 47 ,58));
			viewholder.mAmountView.setTextColor(Color.rgb(208, 47 ,58));
		} else {
			viewholder.mCurrencyView.setTextColor( Color.rgb(83,150,39));
			viewholder.mAmountView.setTextColor(Color.rgb(83,150,39));
			
		}

		return convertView;
	}

	public class cViewHolder {
		public TextView mNameView;
		public TextView mDateView;
		public TextView mPercenView;
		public TextView mCurrencyView;
		public TextView mAmountView;
		public View mLineView;
	}

	@Override
	// counts the number of children items so the list knows how many times
	// calls getChildView() method
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub

		if (mChildMap == null) {
			return 0;
		}
		
		if (mGroupList == null) {
			return 0;
		}
		return mChildMap.get(mGroupList.get(groupPosition).get("categoryName")).size();

	}

	class gViewHolder {
		public TextView mNameView;
		public TextView mPercentView;
		public TextView mCureencyView;
		public TextView mAmountView;
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
