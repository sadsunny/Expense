package com.appxy.pocketexpensepro.setting.payee;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PayeeListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public PayeeListViewAdapter(Context context) {
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

			convertView = mInflater.inflate(R.layout.payee_list_item,
					null);

			viewholder.mImageView = (ImageView) convertView.findViewById(R.id.mImageView);
			viewholder.payeeTextView = (TextView) convertView.findViewById(R.id.payee_txt);
			viewholder.categoryTextView = (TextView) convertView.findViewById(R.id.category_txt);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}


		viewholder.mImageView.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer)mData.get(position).get("iconName")]);
		viewholder.payeeTextView.setText(mData.get(position).get("name")+"");
		viewholder.categoryTextView.setText(mData.get(position).get("categoryName")+"");
		
		return convertView;
	}

	public class ViewHolder {
		public ImageView mImageView;
		public TextView payeeTextView;
		public TextView categoryTextView;
	}
}
