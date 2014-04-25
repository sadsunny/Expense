package com.appxy.pocketexpensepro;

import java.util.List;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class OverViewNavigationListAdapter extends BaseAdapter implements
		SpinnerAdapter {

	private LayoutInflater mInflater;
	private String title;
	private String subTitle;
	private List<String> content;
	private int choosePostion;

	public OverViewNavigationListAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public void setChoosed(int position) {
		this.choosePostion = position;
	}

	public void setDownItemData(List<String> itemStrings) {
		this.content = itemStrings;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (content == null) {
			return 0;
		}
		return content.size();
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
			convertView = mInflater.inflate(R.layout.navigation_getview, null);
			viewholder.mTitleTextView = (TextView) convertView
					.findViewById(R.id.title_text);
			viewholder.mSubTextView = (TextView) convertView
					.findViewById(R.id.subtitle_text);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.mTitleTextView.setText(title + " ");
		viewholder.mSubTextView.setText(subTitle);

		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DropViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new DropViewHolder();
			convertView = mInflater.inflate(R.layout.navigation_dropview, null);
			viewholder.mTextView1 = (TextView) convertView.findViewById(R.id.report_drop_text);
			 viewholder.lineView = (View)convertView.findViewById(R.id.choose_view);
			convertView.setTag(viewholder);
		} else {
			viewholder = (DropViewHolder) convertView.getTag();
		}
		viewholder.mTextView1.setText(content.get(position));
		//
		if (position == choosePostion) {
			viewholder.lineView.setVisibility(View.VISIBLE);
		} else {
			viewholder.lineView.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	class ViewHolder {
		public TextView mTitleTextView;
		public TextView mSubTextView;
	}

	class DropViewHolder {
		public TextView mTextView1;
		public View lineView;
	}

}