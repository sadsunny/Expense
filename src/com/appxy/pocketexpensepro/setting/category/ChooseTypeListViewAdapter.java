package com.appxy.pocketexpensepro.setting.category;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseTypeListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private int checkedPositon;

	public ChooseTypeListViewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterDate(List<Map<String, Object>> data) {
		this.mData = data;
	}

	public void setItemChecked(int position) {
		this.checkedPositon = position;
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

			convertView = mInflater.inflate(R.layout.dialog_choose_type_item,
					null);

			viewholder.mIcon = (ImageView) convertView
					.findViewById(R.id.type_icon);
			viewholder.mTextView = (CheckedTextView) convertView
					.findViewById(R.id.type_name);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		if (checkedPositon == position) {
			viewholder.mTextView.setChecked(true);
		} else {
			viewholder.mTextView.setChecked(false);
		}

		viewholder.mIcon.setImageResource(Common.CATEGORY_ICON[(Integer)mData.get(position).get("iconName")]);
		viewholder.mTextView.setText(mData.get(position).get("categoryName")+"");
		
		return convertView;
	}

	public class ViewHolder {
		public ImageView mIcon;
		public CheckedTextView mTextView;
	}
}
