package com.appxy.pocketexpensepro.reports;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReCashListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public ReCashListAdapter(Context context) {
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

			convertView = mInflater.inflate(R.layout.cash_list_item,
					null);
			
			viewholder.payeeTextView = (TextView) convertView.findViewById(R.id.account_txt);
			viewholder.dateTextView = (TextView) convertView.findViewById(R.id.type_txt);
			viewholder.symbolTextView = (TextView) convertView.findViewById(R.id.symbol_txt);
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_txt);
			viewholder.amountTextView = (TextView) convertView.findViewById(R.id.amount_txt);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}


		viewholder.currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		long dateTime = (Long) mData.get(position).get("dateTime");
		viewholder.dateTextView.setText(turnToDateString(dateTime));
		
		viewholder.amountTextView.setText((String)mData.get(position).get("amount"));
		
		int expenseAccount = (Integer) mData.get(position).get("expenseAccount");
		int incomeAccount = (Integer) mData.get(position).get("incomeAccount");

		if (expenseAccount > 0 && incomeAccount > 0) {
			List<Map<String, Object>> mList1 = AccountDao.selectAccountById(
					context, expenseAccount);
			List<Map<String, Object>> mList2 = AccountDao.selectAccountById(
					context, incomeAccount);
			String mFromAccount = (String) mList1.get(0).get("accName");
			String mInAccount = (String) mList2.get(0).get("accName");
			
			if (mData.get(position).get("payeeName") != null) {
				viewholder.payeeTextView.setText((String) mData.get(position).get(
						"payeeName")
						+ "(" + mFromAccount + " > " + mInAccount + ")");
			} else {
				viewholder.payeeTextView.setText( "--"+ "(" + mFromAccount + " > " + mInAccount + ")");
			}
			
			
			
		} else {
			
			if (mData.get(position).get("payeeName") != null) {
				viewholder.payeeTextView.setText((String)mData.get(position).get("payeeName"));
			} else {
				viewholder.payeeTextView.setText("--");
			}
			
		}

		if (expenseAccount > 0 && incomeAccount <= 0) {
			viewholder.currencyTextView.setTextColor(Color.rgb(208, 47, 58));
			viewholder.amountTextView.setTextColor(Color.rgb(208, 47, 58));
			viewholder.amountTextView.setText(MEntity.doublepoint2str((String) mData.get(position).get("amount")));
		} else if (expenseAccount <= 0 && incomeAccount > 0) {
			viewholder.currencyTextView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.amountTextView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.amountTextView.setText(MEntity
					.doublepoint2str((String) mData.get(position).get("amount")));
		}else {
			viewholder.currencyTextView.setTextColor(Color.rgb(54, 55, 60));
			viewholder.amountTextView.setTextColor(Color.rgb(54, 55, 60));
			viewholder.amountTextView.setText(MEntity
					.doublepoint2str((String) mData.get(position).get("amount")));
		}

		return convertView;
	}
	
	public String turnToDateString(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public class ViewHolder {
		public TextView payeeTextView;
		public TextView dateTextView;
		public TextView symbolTextView;
		public TextView currencyTextView;
		public TextView amountTextView;
		
	}
	
	
}
