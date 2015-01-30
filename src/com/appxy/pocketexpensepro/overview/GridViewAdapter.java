package com.appxy.pocketexpensepro.overview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.flurry.sdk.ex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private int mSelect = 0;
	private int mBack = -1;
	private long choosedTime;
	
	private ArrayList<Long> mGroupList;
	private HashMap<String, Double> mChildMap;

	public GridViewAdapter(Context context) {
		this.context = context;
	}

	public void setGroupDate(ArrayList<Long> mGroupList) {
		this.mGroupList = mGroupList;
	}
	
	public void setChildDate(HashMap<String,Double> mChildMap) {
		this.mChildMap = mChildMap;
	}

	public void setChoosedTime(long chooseTime) {
		this.choosedTime = chooseTime;
	}

	@Override
	public int getCount() {
		if (mGroupList == null || mGroupList.size() == 0) {
			return 0;
		}
		return mGroupList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	// get the current selector's id number
	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getWeekItemWidth() {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();

		return dm.widthPixels / 7;
	}

	// create view method
	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.week_item, null);

			viewHolder.mLayout = (RelativeLayout) view
					.findViewById(R.id.RelativeLayout1);
			viewHolder.dateTextView = (TextView) view
					.findViewById(R.id.date_text);
			viewHolder.expenseTextView = (TextView) view
					.findViewById(R.id.expense_text);
			viewHolder.incomeTextView = (TextView) view
					.findViewById(R.id.income_text);
			view.setTag(viewHolder);
			// view.setPadding(-5,-5, -5,-5);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) viewHolder.mLayout
				.getLayoutParams();
		relativeParams.width = getWeekItemWidth() + 1;
		viewHolder.mLayout.setLayoutParams(relativeParams);

		long todayTime = (Long) mGroupList.get(position);
		viewHolder.dateTextView.setText(turnToDate(todayTime));
		
		String expenseKey = turnToDateKey(todayTime)+"-0";
		String incomeKey = turnToDateKey(todayTime)+"-1";

		if (todayTime == MEntity.getNowMillis()) {
			viewHolder.dateTextView.setTextColor(Color.rgb(3, 128, 255));
		} else {
			viewHolder.dateTextView.setTextColor(Color.rgb(94, 99, 117));
		}

		if (choosedTime == todayTime) {

			if (todayTime == MEntity.getNowMillis()) {
				viewHolder.mLayout.setBackgroundColor(Color.rgb(206, 223, 249));
			} else {
				viewHolder.mLayout.setBackgroundColor(Color.rgb(223, 223, 223));
			}

		} else {
			viewHolder.mLayout.setBackgroundColor(Color.rgb(242, 242, 242));
		}

		double expense = 0;
		double income = 0;
		
		if (mChildMap != null) {
		
		   if (mChildMap.containsKey(expenseKey)) {
			
	         expense = mChildMap.get(expenseKey);
			
	     	} 
		
		
		    if (mChildMap.containsKey(incomeKey)) {
			
		       income = mChildMap.get(incomeKey);
				
		    } 
		
		}

		if (expense != 0) {
			viewHolder.expenseTextView.setText(Common.doublepoint2str(expense
					+ ""));
		} else {
			viewHolder.expenseTextView.setText("");
		}

		if (income != 0) {
			viewHolder.incomeTextView.setText(Common.doublepoint2str(income
					+ ""));
		} else {
			viewHolder.incomeTextView.setText("");
		}

		return view;
	}
	
	public String turnToDateKey(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		String theDate = sdf.format(date2);
		return theDate;
	}

	class ViewHolder {
		RelativeLayout mLayout;
		TextView dateTextView;
		TextView expenseTextView;
		TextView incomeTextView;
	}

}
