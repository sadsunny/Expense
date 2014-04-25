package com.appxy.pocketexpensepro.overview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

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
	private List<Map<String, Object>> mList;
	private long choosedTime;

	public GridViewAdapter(Context context) {
		this.context = context;
	}

	public void setDate(List<Map<String, Object>> mList) {
		this.mList = mList;
	}
	
	public void setChoosedTime(long chooseTime) {
		this.choosedTime = chooseTime;
	}

	@Override
	public int getCount() {
		if (mList == null || mList.size() == 0) {
			return 0;
		}
		return mList.size();
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
		relativeParams.width = getWeekItemWidth()+1;
		viewHolder.mLayout.setLayoutParams(relativeParams);

		long todayTime = (Long) mList.get(position).get("weekTime");
		viewHolder.dateTextView.setText(turnToDate(todayTime));
		
		if (choosedTime == todayTime) {
			viewHolder.mLayout.setBackgroundResource(R.color.blue);
		} else {
			viewHolder.mLayout.setBackgroundResource(R.drawable.week_item_selector);
		}
		
		viewHolder.expenseTextView.setTextColor(Color.RED);
		viewHolder.incomeTextView.setTextColor(Color.GREEN);
		
		double expense =  (Double) mList.get(position).get("expense");
		double income =  (Double) mList.get(position).get("income");
		
		viewHolder.expenseTextView.setText(MEntity.doublepoint2str(expense+""));
		viewHolder.incomeTextView.setText(MEntity.doublepoint2str(income+""));

		return view;
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
