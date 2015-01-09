package com.appxy.pocketexpensepro.reports;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CashReportListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private String currencyLabel = "";
	
	public CashReportListAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.currencyLabel = Common.CURRENCY_SIGN[Common.CURRENCY] ;
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

			convertView = mInflater.inflate(R.layout.report_cash_itemv2,
					null);
			
			viewholder.dateTextView = (TextView) convertView.findViewById(R.id.date_txt);
			viewholder.currencyTextView1 = (TextView) convertView.findViewById(R.id.currency_txt1);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.exAmountTextView = (TextView) convertView.findViewById(R.id.examount_txt);
			viewholder.inAmountTextView = (TextView) convertView.findViewById(R.id.inamount_txt);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}


		viewholder.dateTextView.setText(mData.get(position).get("date")+"");
		viewholder.currencyTextView.setText(currencyLabel);
		viewholder.currencyTextView1.setText(currencyLabel);
		
		viewholder.inAmountTextView.setText(MEntity.doublepoint2str(mData.get(position).get("inAmount")+""));
		viewholder.exAmountTextView.setText(MEntity.doublepoint2str(mData.get(position).get("exAmount")+""));
		
		
		return convertView;
	}

	public class ViewHolder {
		public TextView dateTextView;
		public TextView currencyTextView;
		public TextView currencyTextView1;
		public TextView exAmountTextView;
		public TextView inAmountTextView;
	}
}
