package com.appxy.pocketexpensepro.reports;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private int categoryType;
	private double total;
	private NumberFormat percentFormat;
	public ReportListViewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		
		percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(2);
	}

	public void setAdapterDate(List<Map<String, Object>> data, int categoryType, double total) {
		this.mData = data;
		this.categoryType = categoryType;
		this.total = total;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mData == null) {
			return 0;
		}
		if (mData.size() > 5) {
			return 5;
		} else {
			return mData.size();
		}
		
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.report_list_item,
					null);

			viewholder.colorView = (View) convertView.findViewById(R.id.color_view);
			viewholder.categoryTextView = (TextView) convertView.findViewById(R.id.account_txt);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.amoutTextView = (TextView) convertView.findViewById(R.id.amonut_txt);
			viewholder.percentTextView = (TextView) convertView.findViewById(R.id.percentTextView);
			
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		if (categoryType == 0) {
			viewholder.colorView.setBackgroundColor(Common.ExpenseColors[position%10]);
		} else {
			viewholder.colorView.setBackgroundColor(Common.IncomeColors[position%10]);
		}
		
		viewholder.categoryTextView.setText(mData.get(position).get("categoryName")+"");
		viewholder.currencyTextView.setText( Common.CURRENCY_SIGN[Common.CURRENCY] );
		double sum =  (Double) mData.get(position).get("sum");
		viewholder.amoutTextView.setText(MEntity.doublepoint2str(sum+""));
		
		if (total == 0) {
			viewholder.percentTextView.setText(percentFormat.format(0));
		} else {
			viewholder.percentTextView.setText(percentFormat.format(sum / total));
		}
		
		
		return convertView;
	}

	public class ViewHolder {
		public View colorView;
		public TextView categoryTextView;
		public TextView currencyTextView;
		public TextView amoutTextView;
		public TextView percentTextView;
	}
}
