package com.appxy.pocketexpensepro.overview.transaction;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class PayeeListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private int checkPositon;

	public PayeeListViewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterDate(List<Map<String, Object>> data) {
		this.mData = data;
	}

	public void setItemChecked(int payeeCheckItem) {
		// TODO Auto-generated method stub
		this.checkPositon = payeeCheckItem;
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

			convertView = mInflater.inflate(R.layout.dialog_payee_item, null);

			viewholder.payeeTextView = (TextView) convertView
					.findViewById(R.id.payee_txt);
			viewholder.categoryTextView = (TextView) convertView
					.findViewById(R.id.category_txt);
			viewholder.radioButton = (RadioButton) convertView
					.findViewById(R.id.radioButton1);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		
		viewholder.payeeTextView.setText((String)mData.get(position).get("name"));
		viewholder.categoryTextView.setText((String)mData.get(position).get("categoryName"));
		
		if (checkPositon == position) {
			viewholder.radioButton.setChecked(true);
		} else {
			viewholder.radioButton.setChecked(false);
		}

		return convertView;
	}

	public class ViewHolder {
		public TextView payeeTextView;
		public TextView categoryTextView;
		public RadioButton radioButton;
	}

}
