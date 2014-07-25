package com.appxy.pocketexpensepro.bills;

import com.appxy.pocketexpensepro.CircleView;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.RingView;
import com.appxy.pocketexpensepro.entity.MEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class CalendarGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private Calendar month;
	public Calendar pmonth; // calendar instance for previous month
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public Calendar pmonthmaxset;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;
	String itemvalue;
	DateFormat df;

	public List<String> dayString;
	private List<Map<String, Object>> mDataList;
	private String checkDate;
	private ArrayList<String> items;
	private int itemWidth;
	private long todayTime;
	private LayoutInflater inflater;
	private RelativeLayout.LayoutParams lp1;

	public CalendarGridViewAdapter(Context c, Calendar monthCalendar) {

		this.dayString = new ArrayList<String>();
		month = monthCalendar;
		mContext = c;
		month.set(Calendar.DAY_OF_MONTH, 1);
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		refreshDays();
		itemWidth = getWeekItemWidth();
		this.items = new ArrayList<String>();
		inflater = LayoutInflater.from(c);

		todayTime = MEntity.getNowMillis();
		lp1 = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
	}

	public void setMonth(Calendar monthCalendar) {
		month = monthCalendar;
		refreshDays();
	}

	public List<String> getDayString() {
		return this.dayString;
	}

	public void setDataList(List<Map<String, Object>> mDataList) {
		this.mDataList = mDataList;
		this.items.clear();
		for (Map<String, Object> iMap : mDataList) {
			long mDayDate = (Long) iMap.get("dateTime");
			String mDayDateString = df.format(mDayDate);
			this.items.add(mDayDateString);
		}
	}

	public List<String> getCalendarItem() {
		return this.dayString;
	}

	public void setCheckDat(long date) {
		this.checkDate = df.format(date);
	}

	public long getStringtoDate(String day) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(day));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();

	}

	public int getCount() {
		return dayString.size();
	}

	public Object getItem(int position) {
		return dayString.get(position);
	}

	public int getWeekItemWidth() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		return dm.widthPixels / 7;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		String[] separatedTime = dayString.get(position).split("-");
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			return false;
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			return false;
		} else {
			return true;
		}
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;

		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			LayoutInflater vi = LayoutInflater.from(mContext);

			convertView = vi.inflate(R.layout.bill_calendar_item, null);
			viewholder = new ViewHolder();

			viewholder.dayView = (TextView) convertView
					.findViewById(R.id.date_text);
			viewholder.mLayout = (RelativeLayout) convertView
					.findViewById(R.id.RelativeLayout1);
			viewholder.LinearLayout1 = (LinearLayout) convertView
					.findViewById(R.id.LinearLayout1);
			viewholder.LinearLayout2 = (LinearLayout) convertView
					.findViewById(R.id.LinearLayout2);
			viewholder.LinearLayout3 = (LinearLayout) convertView
					.findViewById(R.id.LinearLayout3);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) viewholder.mLayout
				.getLayoutParams();
		relativeParams.width = itemWidth;
		viewholder.mLayout.setLayoutParams(relativeParams);

		String everyDay = dayString.get(position); // 定位为当天

		// separates daystring into parts.
		String[] separatedTime = dayString.get(position).split("-");
		// taking last part of date. ie; 2 from 2012-12-02
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		// checking whether the day is in current month or not.
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			// setting offdays to white color.
			viewholder.dayView.setTextColor(Color.rgb(205, 205, 205));
			viewholder.dayView.setClickable(false);
			viewholder.dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			viewholder.dayView.setTextColor(Color.rgb(205, 205, 205));
			viewholder.dayView.setClickable(false);
			viewholder.dayView.setFocusable(false);
		} else {

			if (everyDay.equals(turnToDate())) {
				viewholder.dayView.setTextColor(Color.rgb(3, 128, 255));
			} else {
				viewholder.dayView.setTextColor(Color.rgb(94, 99, 117));
			}
		}
		viewholder.dayView.setText(gridvalue);

		if (everyDay.equals(checkDate)) {
			if (everyDay.equals(turnToDate())) {
				viewholder.mLayout.setBackgroundResource(R.color.text_blue);
			} else {
				viewholder.mLayout.setBackgroundResource(R.color.sel_gray);
			}

		} else {
			viewholder.mLayout.setBackgroundResource(R.color.bag_gray);
		}

		// CircleView circleView = new CircleView(mContext);
		// circleView.setBacground(Color.RED);
		// viewholder.LinearLayout1.addView(circleView, lp1);

		if (items != null && items.contains(everyDay)) { // 该位置表示当天

			for (Map<String, Object> iMap : mDataList) {
				long mDayDate = (Long) iMap.get("dateTime");
				String mDayDateString = df.format(mDayDate);

				int never = (Integer) iMap.get("never");
				int all = (Integer) iMap.get("all");
				int part = (Integer) iMap.get("part");

				ArrayList<Integer> sortArrayList = new ArrayList<Integer>();
				sortArrayList.add(never);
				sortArrayList.add(all);
				sortArrayList.add(part);

				ArrayList<LinearLayout> layoutArrayList = new ArrayList<LinearLayout>();
				layoutArrayList.add(viewholder.LinearLayout1);
				layoutArrayList.add(viewholder.LinearLayout2);
				layoutArrayList.add(viewholder.LinearLayout3);

				if (mDayDateString.equals(everyDay)) {
					
					viewholder.LinearLayout1.removeAllViews();
					viewholder.LinearLayout2.removeAllViews();
					viewholder.LinearLayout3.removeAllViews();

					if (mDayDate < todayTime) {

						int j = 0;
						for (int i = 0; i < sortArrayList.size(); i++) {
							int value = sortArrayList.get(i);
							if (value == 1) {
								
								if (i == 0) {
									CircleView circleView = new CircleView(
											mContext);
									circleView.setBacground(Color.rgb(221, 86, 86));//红色
									layoutArrayList.get(j).addView(circleView,
											lp1);
								} else {
									CircleView circleView = new CircleView(
											mContext);
									circleView.setBacground(Color.rgb(83, 150, 39));
									layoutArrayList.get(j).addView(circleView,
											lp1);
								} 
								j++;
								if (j == 2 ) {
									j = 1;
								}
							}
							
						}
						
						
					} else {
						

						int j = 0;
						for (int i = 0; i < sortArrayList.size(); i++) {
							int value = sortArrayList.get(i);
							if (value == 1) {
								if (i == 0) {
									CircleView circleView = new CircleView(
											mContext);
									circleView.setBacground(Color.rgb(172, 172, 172)); //灰色
									layoutArrayList.get(j).addView(circleView,
											lp1);
								} else if (i == 1) {
									CircleView circleView = new CircleView(
											mContext);
									circleView.setBacground(Color.rgb(83, 150, 39));//绿色
									layoutArrayList.get(j).addView(circleView,
											lp1);
								} else {
									RingView ringView = new RingView(mContext);
									layoutArrayList.get(j).addView(ringView,
											lp1);
								}
								j++;
							}
						}
						
						

					}

				}
			}

		} else {
			Log.v("mtest", "everyDay" + everyDay);
			viewholder.LinearLayout1.removeAllViews();
			viewholder.LinearLayout2.removeAllViews();
			viewholder.LinearLayout3.removeAllViews();
		}

		return convertView;
	}

	private class ViewHolder {
		RelativeLayout mLayout;
		LinearLayout LinearLayout1;
		LinearLayout LinearLayout2;
		LinearLayout LinearLayout3;
		TextView dayView;
	}

	public static String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy, HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public void refreshDays() {
		// clear items
		dayString.clear();
		pmonth = (Calendar) month.clone();
		// Log.v("mtest","month在adapter中"+MEntity.getMilltoDate(month.getTimeInMillis()));
		// month start day. ie; sun, mon, etc
		firstDay = month.get(Calendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		maxWeeknumber = month.getActualMaximum(Calendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		pmonthmaxset = (Calendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(Calendar.DAY_OF_MONTH, calMaxP + 1);
		/**
		 * filling calendar gridview.
		 */
		for (int n = 0; n < mnthlength; n++) {
			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(Calendar.DATE, 1);
			dayString.add(itemvalue);
		}
		Log.v("mtest",
				"dayString" + MEntity.getMilltoDate(month.getTimeInMillis()));
	}

	private int getMaxP() {
		int maxP;
		if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
			pmonth.set((month.get(Calendar.YEAR) - 1),
					month.getActualMaximum(Calendar.MONTH), 1);
		} else {
			pmonth.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(Calendar.DAY_OF_MONTH);

		return maxP;
	}

	@Override
	public long getItemId(int paramInt) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String turnToDate() {

		return df.format(System.currentTimeMillis());
	}

}