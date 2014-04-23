package com.appxy.pocketexpensepro.setting.payee;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogItemAdapter extends BaseAdapter {

	private String[] data = { "Delete" };
	private LayoutInflater mInflater;
	private Context context;

	public DialogItemAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			convertView = mInflater.inflate(
					R.layout.dialog_item_operation_item, null);

			viewholder.textView1 = (TextView) convertView
					.findViewById(R.id.diaTextView1);

			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.textView1.setText(data[position]);
		return convertView;
	}

	public class ViewHolder {
		public TextView textView1;
	}
}
