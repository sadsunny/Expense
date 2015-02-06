package com.appxy.pocketexpensepro.overview;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class CalendarGridViewAdapter extends BaseAdapter {
	
	private final static long DAYMILLIS = 86400000L - 1L;
	
	private Context context ;
	private ArrayList<Long> mGroupList;
	private HashMap<String, Double> mChildMap;
	
	private long checkDate;
	private int itemWidth;
	private long firstDayDate;
	private long LastDayDate ;
	private long todayLong ;
	
	public CalendarGridViewAdapter(Context context) {
		this.context = context;
		itemWidth = getWeekItemWidth();
		todayLong = MEntity.getNowMillis();
	}

	public void setGroupData(ArrayList<Long> mGroupList, long thisMonth) {
		this.mGroupList = mGroupList;
		firstDayDate = MEntity.getFirstDayOfMonthMillis(thisMonth);
		LastDayDate = MEntity.getLastDayOfMonthMillis(thisMonth);
	}

	public void setChildData(HashMap<String, Double> mChildMap) {
		this.mChildMap = mChildMap;
	}

	public void setCheckDat(long checkDate) {
		this.checkDate = checkDate;
	}

	public int getCount() {
		
		if (mGroupList == null) {
			return 0;
		}
		return mGroupList.size();
	}

	public Object getItem(int position) {
		return mGroupList.get(position);
	}

	public int getWeekItemWidth() {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels / 7;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		long dateLong = mGroupList.get(position);
//		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
//			return false;
//		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
//			return false;
//		} else {
//			return true;
//		}
		return true;
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;

		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			LayoutInflater vi = LayoutInflater.from(context);

			convertView = vi.inflate(R.layout.fragment_overview_calender_item,null);
			viewholder = new ViewHolder();

			viewholder.dayView = (TextView) convertView
					.findViewById(R.id.date_text);
			viewholder.mLayout = (RelativeLayout) convertView
					.findViewById(R.id.RelativeLayout1);
			viewholder.expenseTextView = (TextView) convertView
					.findViewById(R.id.expense_text);
			viewholder.incomeTextView = (TextView) convertView
					.findViewById(R.id.income_text);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) viewholder.mLayout
				.getLayoutParams();
		relativeParams.width = itemWidth;
		viewholder.mLayout.setLayoutParams(relativeParams);

		long todayTime = mGroupList.get(position);
		String dayString = turnToDate(todayTime);
		
		String expenseKey = turnToDateKey(todayTime)+"-0";
		String incomeKey = turnToDateKey(todayTime)+"-1";
		

		if ( todayTime<firstDayDate || todayTime  >LastDayDate ) {
			// setting offdays to white color.
			viewholder.dayView.setTextColor(Color.rgb(205, 205, 205));
			viewholder.dayView.setClickable(false);
			viewholder.dayView.setFocusable(false);
			
		} else {

			if ( turnToDateKey(todayLong).equals (turnToDateKey(todayTime)) ) {
				viewholder.dayView.setTextColor(Color.rgb(3, 128, 255));
			} else {
				viewholder.dayView.setTextColor(Color.rgb(94, 99, 117));
			}

		}
		
		viewholder.dayView.setText(dayString);

		if (todayTime == checkDate ) {
			
			if ( turnToDateKey(todayLong).equals (turnToDateKey(todayTime)) ) {
				viewholder.mLayout.setBackgroundResource(R.color.text_blue);
			} else {
				viewholder.mLayout.setBackgroundResource(R.color.sel_gray);
			}

		} else {
			viewholder.mLayout.setBackgroundResource(R.color.bag_gray);
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
			    
			    
				if ( todayTime  >LastDayDate) {
					
					if (mChildMap.containsKey(expenseKey)) {
						
				         Log.v("mtag", "position "+position+" 数据"+mChildMap.get(expenseKey)); 
						
				     	} 
					
				}

			
			}

			if (expense != 0) {
				viewholder.expenseTextView.setText(Common.doublepoint2strNoPoint(expense
						+ ""));
				viewholder.expenseTextView.setTextColor(Color.rgb(208, 47, 58));
			} else {
				viewholder.expenseTextView.setText("");
			}
			
			if (income != 0) {
				viewholder.incomeTextView.setText(Common.doublepoint2strNoPoint(income
						+ ""));
				viewholder.incomeTextView.setTextColor(Color.rgb(83, 150, 39));
			} else {
				viewholder.incomeTextView.setText("");
			}
		
		return convertView;
	}

	public class ViewHolder {
		RelativeLayout mLayout;
		TextView dayView;
		TextView expenseTextView;
		TextView incomeTextView;
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

	@Override
	public long getItemId(int paramInt) {
		// TODO Auto-generated method stub
		return 0;
	}

}