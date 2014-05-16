package com.appxy.pocketexpensepro.bills;

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

public class BillListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public BillListViewAdapter(Context context) {
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
		ViewHolder viewholder;

		if (convertView == null) {
			viewholder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.bill_list_item,
					null);

			viewholder.mIcon = (ImageView) convertView.findViewById(R.id.account_type_img);
			viewholder.accountTextView = (TextView) convertView.findViewById(R.id.account_txt);
			viewholder.dateTextView = (TextView) convertView.findViewById(R.id.date_txt);
			viewholder.symbolTextView = (TextView) convertView.findViewById(R.id.symbol_txt);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.amountTextView = (TextView) convertView.findViewById(R.id.amounttextView);
			
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		viewholder.mIcon.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer)mData.get(position).get("iconName")]);
		viewholder.accountTextView.setText(mData.get(position).get("ep_billName")+"");
		viewholder.currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		long dueDate = (Long) mData.get(position).get("ep_billDueDate");
		viewholder.dateTextView.setText(MEntity.turnToDateString(dueDate));
		viewholder.amountTextView.setText( MEntity.doublepoint2str((String)mData.get(position).get("ep_billAmount")));
			
		return convertView;
	}

	private class ViewHolder {
		public ImageView mIcon;
		public TextView accountTextView;
		public TextView dateTextView;
		public TextView symbolTextView;
		public TextView currencyTextView;
		public TextView amountTextView;
	}
}
