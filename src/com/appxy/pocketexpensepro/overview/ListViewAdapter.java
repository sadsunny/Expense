package com.appxy.pocketexpensepro.overview;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	public static int ISCHECKED = -1;

	public ListViewAdapter(Context context) {
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

	public String turnToDateString(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.transaction_child_item,
					parent, false);

			viewholder.mImageView = (ImageView) convertView
					.findViewById(R.id.mImageView);
			viewholder.mImageView1 = (ImageView) convertView
					.findViewById(R.id.mImageView1);
			viewholder.mImageView2 = (ImageView) convertView
					.findViewById(R.id.mImageView2);
			viewholder.mTextView1 = (TextView) convertView
					.findViewById(R.id.TextView1);
			viewholder.mTextView2 = (TextView) convertView
					.findViewById(R.id.TextView2);
			viewholder.symbol_txt = (TextView) convertView
					.findViewById(R.id.symbol_txt);
			viewholder.currency_textView = (TextView) convertView
					.findViewById(R.id.currency_txt);
			viewholder.amount_textView = (TextView) convertView
					.findViewById(R.id.amounttextView);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.currency_textView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		viewholder.mImageView.setImageResource(Common.CATEGORY_ICON[(Integer) mData.get(position).get("iconName")]);
		
		long dateTime = (Long) mData.get(position).get("dateTime");
		viewholder.mTextView2.setText(turnToDateString(dateTime));
		
		int recurringType = (Integer) mData.get(position).get("recurringType");
		if (recurringType > 0) {
			viewholder.mImageView1.setVisibility(View.VISIBLE);
		} else {
			viewholder.mImageView1.setVisibility(View.INVISIBLE);
		}

		String photoName = (String) mData.get(position).get("photoName");
		File file = new File(photoName);
		if (photoName.length() > 0 && file.exists()) {

			viewholder.mImageView2.setVisibility(View.VISIBLE);
		} else {
			viewholder.mImageView2.setVisibility(View.INVISIBLE);
		}

		String notes = (String) mData.get(position).get("notes");
		
		Double mAmount;
		try {
			mAmount = Double.parseDouble((String) mData.get(position).get("amount"));
		} catch (Exception e) {
			// TODO: handle exception
			mAmount = 0.0;
		}

		int expenseAccount = (Integer) mData.get(position).get("expenseAccount");
		int incomeAccount = (Integer) mData.get(position).get("incomeAccount");

		if (expenseAccount > 0 && incomeAccount > 0) {
			List<Map<String, Object>> mList1 = AccountDao.selectAccountById(
					context, expenseAccount);
			List<Map<String, Object>> mList2 = AccountDao.selectAccountById(
					context, incomeAccount);
			String mFromAccount = (String) mList1.get(0).get("accName");
			String mInAccount = (String) mList2.get(0).get("accName");
			viewholder.mTextView1.setText((String) mData.get(position).get(
					"payeeName")
					+ "(" + mFromAccount + " > " + mInAccount + ")");
		} else {
			viewholder.mTextView1.setText((String) mData.get(position).get(
					"payeeName"));
		}

		if (expenseAccount > 0 && incomeAccount <= 0) {
			viewholder.currency_textView.setTextColor(Color.rgb(208, 47, 58));
			viewholder.amount_textView.setTextColor(Color.rgb(208, 47, 58));
			double amount = mAmount;
			viewholder.amount_textView.setText(MEntity.doublepoint2str(amount
					+ ""));
		} else if (expenseAccount <= 0 && incomeAccount > 0) {
			viewholder.currency_textView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.amount_textView.setTextColor(Color.rgb(83, 150, 39));
			viewholder.amount_textView.setText(MEntity
					.doublepoint2str((String) mData.get(position).get("amount")));
		}else {
			viewholder.currency_textView.setTextColor(Color.rgb(54, 55, 60));
			viewholder.amount_textView.setTextColor(Color.rgb(54, 55, 60));
			viewholder.amount_textView.setText(MEntity
					.doublepoint2str((String) mData.get(position).get("amount")));
		}

		return convertView;
	}

	public class ViewHolder {
		public ImageView mImageView;
		public ImageView mImageView1;
		public ImageView mImageView2;
		public TextView mTextView1;
		public TextView mTextView2;
		public TextView symbol_txt;
		public TextView currency_textView;
		public TextView amount_textView;
	}
}
