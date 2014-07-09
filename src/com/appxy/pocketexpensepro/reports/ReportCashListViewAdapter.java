package com.appxy.pocketexpensepro.reports;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportCashListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public ReportCashListViewAdapter(Context context) {
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

			convertView = mInflater.inflate(R.layout.report_cash_item,
					null);
			
			viewholder.dateTextView = (TextView) convertView.findViewById(R.id.date_txt);
			viewholder.mImageView = (ImageView) convertView.findViewById(R.id.up_down_img);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.amountTextView = (TextView) convertView.findViewById(R.id.amount_txt);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}


		viewholder.dateTextView.setText(mData.get(position).get("date")+"");
		viewholder.currencyTextView.setText("$");
		
		double preDouble = (Double) mData.get((position-1)>0?(position-1):0).get("amount");
		double thisDouble = (Double) mData.get(position).get("amount");
		viewholder.amountTextView.setText(thisDouble+"");
		
		if (thisDouble > preDouble) {
			viewholder.mImageView.setVisibility(View.VISIBLE);
			viewholder.mImageView.setBackgroundResource(R.drawable.up);
		} else {
			viewholder.mImageView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	public class ViewHolder {
		public TextView dateTextView;
		public ImageView mImageView;
		public TextView currencyTextView;
		public TextView amountTextView;
	}
}
