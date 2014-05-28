package com.appxy.pocketexpensepro.overview.budgets;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BudgetsListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private int checkedPositon;
    public SparseArray<Integer> chooseArray;

	public BudgetsListViewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterDate(List<Map<String, Object>> data) {
		this.mData = data;
	}
	
	public void setChooseArray(SparseArray<Integer> chooseArray) {
		this.chooseArray = chooseArray;
	}
	

	public void setItemChecked(int position) {
		this.checkedPositon = position;
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
	
	private Integer index = -1;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.budget_category_item,
					null);

			viewholder.mIcon = (ImageView) convertView
					.findViewById(R.id.category_img);
			viewholder.categoryTextView = (TextView) convertView
					.findViewById(R.id.category_txt);
			viewholder.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.checkBox1);
			
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.mIcon.setImageResource(Common.ACCOUNT_TYPE_ICON[(Integer)mData.get(position).get("iconName")]);
		viewholder.categoryTextView.setText((String)mData.get(position).get("categoryName"));
		
			int hasChild = chooseArray.get(position);
			if (hasChild == 1) {
				viewholder.mCheckBox.setChecked(true);
			} else {
				viewholder.mCheckBox.setChecked(false);
			}
		
		return convertView;
	}

	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}


	public class ViewHolder {
		public ImageView mIcon;
		public TextView categoryTextView;
		public CheckBox mCheckBox;
	}
}
