package com.appxy.pocketexpensepro.accounts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private List<Map<String, Object>> groupList;
	private List<List<Map<String, Object>>> childList;
	private int accountId;
	private Context context;
	private int reconcileCheck; 

	public ExpandableListViewAdapter(Context context, int id) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.accountId = id;
	}

	public void setAdapterData(List<Map<String, Object>> groupList,
			List<List<Map<String, Object>>> childList) {

		this.groupList = groupList;
		this.childList = childList;

	}

	public List<List<Map<String, Object>>> getAdapterDate() {
		return this.childList;
	}
	
	public void setReconcile(int check) {
		this.reconcileCheck = check;
	}

	@Override
	// gets the title of each parent/group
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	// counts the number of group/parent items so the list knows how many times
	// calls getGroupView() method
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if (groupList == null) {
			return 0;
		}
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	public String turnToDate(long mills) { // 锟斤拷锟斤拷锟斤拷锟斤拷转锟斤拷为锟斤拷锟节猴拷锟斤拷锟斤拷

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		gViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new gViewHolder();
			convertView = inflater.inflate(R.layout.transaction_group_item,
					parent, false);
			viewholder.mTextView = (TextView) convertView
					.findViewById(R.id.dateTextView);
			convertView.setTag(viewholder);
		} else {
			viewholder = (gViewHolder) convertView.getTag();
		}

		viewholder.mTextView.setText(turnToDate((Long) groupList.get(
				groupPosition).get("dateTime")));

		convertView.setOnTouchListener(new View.OnTouchListener() { // 设置Group是否可点击

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return true;
					}
				});

		return convertView;
	}

	@Override
	// gets the name of each item
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	public String turnToDateString(long mills) { // 锟斤拷锟斤拷锟斤拷锟斤拷转锟斤拷为锟斤拷锟节猴拷锟斤拷锟斤拷

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		cViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new cViewHolder();
			convertView = inflater.inflate(R.layout.transaction_child_item, parent,
					false);
			
			viewholder.mCheckBox =(CheckBox) convertView
					.findViewById(R.id.checkBox1); 
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
			viewholder = (cViewHolder) convertView.getTag();

		}
		viewholder.mImageView.setImageResource( Common.ACCOUNT_TYPE_ICON[(Integer) childList.get(groupPosition).get(childPosition).get("iconName")]);
		viewholder.currency_textView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		if (reconcileCheck == 1) {
			viewholder.mCheckBox.setVisibility(View.VISIBLE);
			viewholder.mImageView.setVisibility(View.INVISIBLE);
		} else {
			viewholder.mCheckBox.setVisibility(View.INVISIBLE);
			viewholder.mImageView.setVisibility(View.VISIBLE);
		}
		
		final int cleard = (Integer) childList.get(groupPosition).get(childPosition).get("isClear");
		
		if (cleard == 1) {
			viewholder.mCheckBox.setChecked(true);
		} else {
			viewholder.mCheckBox.setChecked(false);
		}
		
		final int _id = (Integer) childList.get(groupPosition).get(childPosition).get("_id");
		viewholder.mCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cleard == 1) {
					childList.get(groupPosition).get(childPosition).put("isClear", 0);
//					AccountDao.updateTransactionClear(context, _id, 0);
				} else {
					childList.get(groupPosition).get(childPosition).put("isClear", 1);
//					AccountDao.updateTransactionClear(context, _id, 1);
				}
				notifyDataSetChanged();
			}
		});
		
		
		long dateTime = (Long)childList.get(groupPosition).get(childPosition).get("dateTime");
		viewholder.mTextView2.setText(turnToDateString(dateTime));
		
		int recurringType = (Integer)childList.get(groupPosition).get(childPosition).get("recurringType");
		if (recurringType > 0) {
			viewholder.mImageView1.setVisibility(View.VISIBLE);
		}else {
			viewholder.mImageView1.setVisibility(View.INVISIBLE);
		}
		
		String photoName = (String)childList.get(groupPosition).get(childPosition).get("photoName");
		File file = new File(photoName);
		if (photoName.length() > 0 && file.exists()) {
			
			viewholder.mImageView2.setVisibility(View.VISIBLE);
		}else {
			viewholder.mImageView2.setVisibility(View.INVISIBLE);
		}
		
		Double mAmount;
		try {
			mAmount = Double.parseDouble((String)childList.get(groupPosition).get(childPosition).get("amount"));
		} catch (Exception e) {
			// TODO: handle exception
			mAmount = 0.0;
		}
		
		int expenseAccount = (Integer)childList.get(groupPosition).get(childPosition).get("expenseAccount");
		int incomeAccount = (Integer)childList.get(groupPosition).get(childPosition).get("incomeAccount");
		

		if (expenseAccount == accountId) {
			mAmount = 0 - mAmount;
		}
		
		if (expenseAccount > 0 && incomeAccount >0) {
			List<Map<String, Object>> mList1 = AccountDao.selectAccountById(context,expenseAccount);
			List<Map<String, Object>> mList2 = AccountDao.selectAccountById(context,incomeAccount);
			String mFromAccount = (String) mList1.get(0).get("accName");
			String mInAccount = (String) mList2.get(0).get("accName");
			viewholder.mTextView1.setText((String)childList.get(groupPosition).get(childPosition).get("payeeName")+"("+mFromAccount+" > "+mInAccount +")");
		}else {
			viewholder.mTextView1.setText((String)childList.get(groupPosition).get(childPosition).get("payeeName"));
		}
		
		if (mAmount < 0) {
			viewholder.symbol_txt.setVisibility(View.VISIBLE);
			viewholder.symbol_txt.setText("-");
			viewholder.symbol_txt.setTextColor(Color.RED);
			viewholder.currency_textView.setTextColor(Color.RED);
			viewholder.amount_textView.setTextColor(Color.RED);
			double amount = 0-mAmount;
			viewholder.amount_textView.setText( MEntity.doublepoint2str(amount+""));
		} else {
			viewholder.symbol_txt.setVisibility(View.INVISIBLE);
			viewholder.symbol_txt.setText("");
			viewholder.symbol_txt.setTextColor(Color.GREEN);
			viewholder.currency_textView.setTextColor(Color.GREEN);
			viewholder.amount_textView.setTextColor(Color.GREEN);
			viewholder.amount_textView.setText( MEntity.doublepoint2str((String)childList.get(groupPosition).get(childPosition).get("amount")));
		}
		
		
		viewholder.amount_textView.setText((String)childList.get(groupPosition).get(childPosition).get("amount"));
		
		if (childPosition == 0) {
			viewholder.mline_label.setVisibility(View.INVISIBLE);
		} else {
			viewholder.mline_label.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	public class cViewHolder {
		public CheckBox mCheckBox;
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

	@Override
	// counts the number of children items so the list knows how many times
	// calls getChildView() method
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub

		if (childList == null) {
			return 0;
		}
		return childList.get(groupPosition).size();

	}

	class gViewHolder {
		public TextView mTextView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
