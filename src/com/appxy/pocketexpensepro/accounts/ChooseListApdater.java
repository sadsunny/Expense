package com.appxy.pocketexpensepro.accounts;

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

public class ChooseListApdater extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
    private int checkPositon;
	
	public ChooseListApdater(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setItemChecked(int payeeCheckItem) {
		// TODO Auto-generated method stub
		this.checkPositon = payeeCheckItem;
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

			convertView = mInflater.inflate(R.layout.dialog_account_item,
					null);

			viewholder.mIcon = (ImageView) convertView.findViewById(R.id.account_type_img);
			viewholder.accountTextView = (TextView) convertView.findViewById(R.id.account_txt);
			viewholder.typeTextView = (TextView) convertView.findViewById(R.id.type_txt);
			viewholder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton1);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}


		viewholder.mIcon.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer)mData.get(position).get("iconName")]);
		viewholder.accountTextView.setText(mData.get(position).get("accName")+"");
		viewholder.typeTextView.setText(mData.get(position).get("typeName")+"");
		
		if (checkPositon == position) {
			viewholder.radioButton.setChecked(true);
		} else {
			viewholder.radioButton.setChecked(false);
		}
		
		return convertView;
	}

	public class ViewHolder {
		public ImageView mIcon;
		public TextView accountTextView;
		public TextView typeTextView;
		public RadioButton radioButton;
	}
}
