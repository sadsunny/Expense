package com.appxy.pocketexpensepro.accounts;

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

public class AccountsListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	public  static int ISCHECKED = -1;

	public AccountsListViewAdapter(Context context) {
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
	
	public void sortIsChecked(int checked){
		this.ISCHECKED=checked;
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

			convertView = mInflater.inflate(R.layout.account_list_item,
					null);

			viewholder.mIcon = (ImageView) convertView.findViewById(R.id.account_type_img);
			viewholder.accountTextView = (TextView) convertView.findViewById(R.id.account_txt);
			viewholder.typeTextView = (TextView) convertView.findViewById(R.id.type_txt);
			viewholder.symbolTextView = (TextView) convertView.findViewById(R.id.symbol_txt);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.amountTextView = (TextView) convertView.findViewById(R.id.amounttextView);
			viewholder.sortImageView = (ImageView) convertView.findViewById(R.id.Sort_ImageView);
			
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		viewholder.mIcon.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer)mData.get(position).get("iconName")]);
		viewholder.accountTextView.setText(mData.get(position).get("accName")+"");
		viewholder.typeTextView.setText(mData.get(position).get("typeName")+"");
		
		Double mAmount;
		try {
			mAmount = Double.parseDouble(mData.get(position).get("lastAmount")+"");
		} catch (Exception e) {
			// TODO: handle exception
			mAmount = 0.0;
		}
		
		viewholder.currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		if (mAmount < 0) {
			viewholder.symbolTextView.setTextColor(Color.rgb(208, 47, 58));
			viewholder.currencyTextView.setTextColor(Color.rgb(208, 47, 58));
			viewholder.amountTextView.setTextColor(Color.rgb(208, 47, 58));
			double amount = 0-mAmount;
			viewholder.amountTextView.setText( MEntity.doublepoint2str(amount+""));
		} else {
			viewholder.symbolTextView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.currencyTextView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.amountTextView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.amountTextView.setText( MEntity.doublepoint2str((String)mData.get(position).get("lastAmount")));
		}
		
		
		 if (ISCHECKED == 1) {
			 viewholder.sortImageView.setImageResource(R.drawable.sort);
			 viewholder.sortImageView.setVisibility(View.VISIBLE);
		} else {
			 viewholder.sortImageView.setImageDrawable(null);
			 viewholder.sortImageView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	public class ViewHolder {
		public ImageView mIcon;
		public TextView accountTextView;
		public TextView typeTextView;
		public TextView symbolTextView;
		public TextView currencyTextView;
		public TextView amountTextView;
		public ImageView sortImageView;
	}
}
