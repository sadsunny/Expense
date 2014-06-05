package com.appxy.pocketexpensepro.bills;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mList;
	private long choosedTime;
	private int itemWidth;

	public GridViewAdapter(Context context) {
		this.context = context;
		itemWidth = getWeekItemWidth();
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
	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.fragment_bill_month_view_item,
					null);

			viewHolder.mLayout = (RelativeLayout) view
					.findViewById(R.id.RelativeLayout1);
			viewHolder.mView = (View) view
					.findViewById(R.id.line_view1);
			viewHolder.monthTextView = (TextView) view
					.findViewById(R.id.month_text);
			viewHolder.yearTextView = (TextView) view
					.findViewById(R.id.year_text);
			viewHolder.countTextView = (TextView) view
					.findViewById(R.id.count_text);
			
			viewHolder.bottomLine = (View) view
					.findViewById(R.id.line_view3);
			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) viewHolder.mLayout
				.getLayoutParams();
		relativeParams.width = itemWidth;
		viewHolder.mLayout.setLayoutParams(relativeParams);
		
		long monthTime = (Long)mList.get(position).get("monthTime");
		
		if (monthTime == choosedTime) {
			viewHolder.mLayout.setBackgroundResource(R.color.bag_gray);
			viewHolder.mView.setVisibility(View.VISIBLE);
			viewHolder.yearTextView.setVisibility(View.VISIBLE);
			viewHolder.monthTextView.setTextColor(Color.parseColor("#0380ff"));
			viewHolder.bottomLine.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.mLayout.setBackgroundResource(R.color.white);
			viewHolder.mView.setVisibility(View.INVISIBLE);
			viewHolder.yearTextView.setVisibility(View.INVISIBLE);
			viewHolder.monthTextView.setTextColor(Color.parseColor("#aeb1bc"));
			viewHolder.bottomLine.setVisibility(View.VISIBLE);
		}
		
		viewHolder.monthTextView.setText(turnMilltoMonth(monthTime));
		viewHolder.yearTextView.setText(turnMilltoYear(monthTime));
		
		int count = (Integer)mList.get(position).get("count");
		if (count > 0) {
			viewHolder.countTextView.setText(count+"");
		} else {
			viewHolder.countTextView.setText("");
		}
		
		
		return view;
	}

	public String turnMilltoMonth(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String turnMilltoYear(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	class ViewHolder {
		RelativeLayout mLayout;
		View mView;
		TextView yearTextView;
		TextView monthTextView;
		TextView countTextView;
		View bottomLine;
	}

}
