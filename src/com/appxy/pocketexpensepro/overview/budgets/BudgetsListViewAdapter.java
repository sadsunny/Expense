package com.appxy.pocketexpensepro.overview.budgets;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BudgetsListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private int checkedPositon;

	public BudgetsListViewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterDate(List<Map<String, Object>> data) {
		this.mData = data;
	}

	public void setItemChecked(int position) {
		this.checkedPositon = position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
//		if (mData == null) {
//			return 0;
//		}
		return 20;
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
	private Integer index = -1;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.budget_child_item,
					null);

			viewholder.mIcon = (ImageView) convertView
					.findViewById(R.id.category_img);
			viewholder.categoryTextView = (TextView) convertView
					.findViewById(R.id.category_txt);
			viewholder.currencyTextView = (TextView) convertView
					.findViewById(R.id.currency_txt);
			viewholder.mEditText = (EditText) convertView
					.findViewById(R.id.amount_edit);
			
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		
		
//		viewholder.mIcon.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer)mData.get(position).get("iconName")]);
		
		return convertView;
	}

	public class ViewHolder {
		public ImageView mIcon;
		public TextView categoryTextView;
		public TextView currencyTextView;
		public EditText mEditText;
	}
}
