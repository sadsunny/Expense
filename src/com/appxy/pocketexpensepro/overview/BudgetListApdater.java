package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class BudgetListApdater extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public BudgetListApdater(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterDate(List<Map<String, Object>> data) {
		this.mData = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mData == null) {
			return 0;
		}
		return mData.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.budget_item, null);

			viewholder.mIcon = (ImageView) convertView
					.findViewById(R.id.imageView1);
			viewholder.categoryTextView = (TextView) convertView
					.findViewById(R.id.budget_txt);
			viewholder.leftTextView = (TextView) convertView
					.findViewById(R.id.budget_amount);
			viewholder.mProgressBar = (ProgressBar) convertView
					.findViewById(R.id.mProgressBar);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.mIcon
				.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer) mData.get(
						position).get("iconName")]);
		viewholder.categoryTextView.setText((String) mData.get(position).get(
				"categoryName"));

		String amount = (String) mData.get(position).get("amount");
		String tAmount = (String) mData.get(position).get("tAmount");

		BigDecimal b1 = new BigDecimal(amount);
		BigDecimal b2 = new BigDecimal(tAmount);
		double left = b1.subtract(b2).doubleValue();
		
		viewholder.leftTextView.setText(left+"");
		Log.v("mtest", "amount"+amount);
		viewholder.mProgressBar.setMax((int) Double.parseDouble(amount));
		viewholder.mProgressBar.setProgress((int) Double.parseDouble(tAmount));

		return convertView;
	}

	public class ViewHolder {
		public ImageView mIcon;
		public TextView categoryTextView;
		public TextView leftTextView;
		public ProgressBar mProgressBar;
	}
}
