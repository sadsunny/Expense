package com.appxy.pocketexpensepro.overview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
	private SharedPreferences mPreferences;
	private int  BdgetSetting;

	public BudgetListApdater(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		mPreferences = context.getSharedPreferences("Expense", context.MODE_PRIVATE);  
		BdgetSetting = mPreferences.getInt("BdgetSetting", 0);
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
			viewholder.currencyTextView = (TextView) convertView
					.findViewById(R.id.currency_label);


			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		viewholder.mIcon
				.setImageResource(Common.CATEGORY_ICON[(Integer) mData.get(
						position).get("iconName")]);
		viewholder.categoryTextView.setText((String) mData.get(position).get(
				"categoryName"));

		String amount = (String) mData.get(position).get("amount");
		String tAmount = (String) mData.get(position).get("tAmount");

		BigDecimal b1 = new BigDecimal(amount);
		BigDecimal b2 = new BigDecimal(tAmount);
		double left_spent = 0;
		
		if (BdgetSetting == 0) {
			left_spent = b1.subtract(b2).doubleValue();
		} else {
			left_spent = b2.doubleValue();
		}
		viewholder.leftTextView.setText(MEntity.doublepoint2str(left_spent+""));
		
		Log.v("mtest", "amount"+amount);
		viewholder.mProgressBar.setMax((int) Double.parseDouble(amount));
		viewholder.mProgressBar.setProgress((int) Double.parseDouble(tAmount));

		return convertView;
	}

	private class ViewHolder {
		public ImageView mIcon;
		public TextView categoryTextView;
		public TextView leftTextView;
		public TextView currencyTextView;
		public ProgressBar mProgressBar;
	}
}
