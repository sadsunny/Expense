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

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	public  static int ISCHECKED = -1;

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
	
	public String turnToDateString(long mills) { // 锟斤拷锟斤拷锟斤拷锟斤拷转锟斤拷为锟斤拷锟节猴拷锟斤拷锟斤拷

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

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
			viewholder.mline_label = (View) convertView
					.findViewById(R.id.mline_label);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();

		}
		
		viewholder.currency_textView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);

		viewholder.amount_textView.setText((String)mData.get(position).get("amount"));

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
		public View mline_label;
	}
}
