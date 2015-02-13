package com.appxy.pocketexpensepro.accounts;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnAtoBListenner;
import com.crashlytics.android.internal.t;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

import android.R.bool;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
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
	private Context context;
	private boolean isReconcile = false; // 是否显示前面的选择框，ture代表显示前面的checkbox
	private String currencyLable = "$";
	private int accountId = 0;
	private int colorGreen = Color.rgb(83,150,39);
	private int colorRed = Color.rgb(208, 47 ,58);
	
	private boolean hideClear = true;
	
	private DbxAccountManager mDbxAcctMgr;
	private DbxDatastore mDatastore;
	
	private ArrayList<String> mGroupList;
	private HashMap<String, ArrayList<HashMap<String, Object>>> mChildMap; 
	
	private OnAtoBListenner a2bAtoBListenner ;
	
	public ExpandableListViewAdapter(Context context,int accountId ) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		currencyLable = Common.CURRENCY_SIGN[Common.CURRENCY] ;
		this.accountId = accountId;
		
		if (context != null) {
			a2bAtoBListenner = (OnAtoBListenner)context;
		}
		
	}

	public void setSyncMgr(DbxAccountManager mDbxAcctMgr, DbxDatastore mDatastore) {
		this.mDbxAcctMgr = mDbxAcctMgr;
		this.mDatastore = mDatastore;
	}
	
	public void setAdapterData(ArrayList<String> mGroupList,
			HashMap<String, ArrayList<HashMap<String, Object>>> mChildMap) {

		this.mGroupList = mGroupList;
		this.mChildMap = mChildMap;
		
		
	}

	public void setReconcile( boolean isReconcile) {
		this.isReconcile = isReconcile;
	}
	
	public void setShowClear( boolean hideClear) {
		this.hideClear = hideClear;
	}

	@Override
	// gets the title of each parent/group
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	// counts the number of group/parent items so the list knows how many
	// times
	// calls getGroupView() method
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if (mGroupList == null) {
			return 0;
		}
		return mGroupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
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

		String dateKey = mGroupList.get(groupPosition);
		viewholder.mTextView.setText( dateKey  );

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

	public String turnToDateString(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	@Override
	public View getChildView( final int groupPosition,
			 final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		cViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new cViewHolder();
			convertView = inflater.inflate(R.layout.transaction_item,
					parent, false);

			viewholder.mCheckBox = (CheckBox) convertView
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
		
		final String dateKey = mGroupList.get(groupPosition) ;
		
		final int _id = (Integer) mChildMap.get(dateKey).get(childPosition).get("_id");
		int iconName = (Integer) mChildMap.get(dateKey).get(childPosition).get("iconName");
		final int isClear = (Integer) mChildMap.get(dateKey).get(childPosition).get("isClear");
		long dateTime = (Long) mChildMap.get(dateKey).get(childPosition).get("dateTime");
		int recurringType = (Integer) mChildMap.get(dateKey).get(childPosition).get("recurringType");
		String photoName = (String) mChildMap.get(dateKey).get(childPosition).get("photoName");
		String notes = (String) mChildMap.get(dateKey).get(childPosition).get("notes");
		String amount = (String) mChildMap.get(dateKey).get(childPosition).get("amount");
		int expenseAccount = (Integer)  mChildMap.get(dateKey).get(childPosition).get("expenseAccount");
		int incomeAccount = (Integer)  mChildMap.get(dateKey).get(childPosition).get("incomeAccount");
		String payeeName = (String) mChildMap.get(dateKey).get(childPosition).get("payeeName");
		
		viewholder.mImageView.setImageResource(Common.CATEGORY_ICON[iconName]);
		viewholder.currency_textView.setText(currencyLable);

		
		if (isReconcile) {
			viewholder.mCheckBox.setVisibility(View.VISIBLE);
			viewholder.mImageView.setVisibility(View.INVISIBLE);
		} else {
			viewholder.mCheckBox.setVisibility(View.INVISIBLE);
			viewholder.mImageView.setVisibility(View.VISIBLE);
		}


		if (isClear == 1) {
			viewholder.mCheckBox.setChecked(true);
		} else {
			viewholder.mCheckBox.setChecked(false);
		}

		
		viewholder.mCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int settedClear = (isClear == 1)?0:1;
				
				long rId = AccountDao.updateTransactionClear(context, _id, settedClear, mDbxAcctMgr, mDatastore);
				
				if (rId >0) {
					
				if (!hideClear) {
					 mChildMap.get(dateKey).get(childPosition).put("isClear", settedClear);
				} else {
					 mChildMap.get(dateKey).remove(childPosition);
					 if (mChildMap.get(dateKey) == null) {
						mGroupList.remove(groupPosition);
					}else {
						
						 if ( mChildMap.get(dateKey).size() <=0) 
						 {
							 mGroupList.remove(groupPosition); 
						 }
						
					}
				}
				
				  if (a2bAtoBListenner != null) {
					  a2bAtoBListenner.OnA2B();
				   }
					 notifyDataSetChanged();
			 }

			}
		});

		viewholder.mTextView2.setText(turnToDateString(dateTime));

	
		if (recurringType > 0) {
			viewholder.mImageView1.setVisibility(View.VISIBLE);
		} else {
			viewholder.mImageView1.setVisibility(View.INVISIBLE);
		}

		
		if (photoName != null && photoName.length() > 0) {
		
		File file = new File(photoName);
		if ( file.exists()) {

			viewholder.mImageView1.setVisibility(View.VISIBLE);
		} else {
			viewholder.mImageView1.setVisibility(View.INVISIBLE);
		}
		} else {
			viewholder.mImageView1.setVisibility(View.INVISIBLE);
		}
		
		
		if (notes != null && notes.length()>0) {
			viewholder.mImageView2.setVisibility(View.VISIBLE);
		} else {
			viewholder.mImageView2.setVisibility(View.INVISIBLE);
		}

		int thisItemColor = colorRed;

		if (expenseAccount > 0 && incomeAccount > 0) {
			List<Map<String, Object>> mList1 = AccountDao
					.selectAccountById(context, expenseAccount);
			List<Map<String, Object>> mList2 = AccountDao
					.selectAccountById(context, incomeAccount);
			String mFromAccount = (String) mList1.get(0).get("accName");
			String mInAccount = (String) mList2.get(0).get("accName");
			viewholder.mTextView1.setText("(" + mFromAccount + " > " + mInAccount + ")");
			
			if (expenseAccount == accountId) {
				thisItemColor = colorRed;
			} else {
				
				if (accountId == -1) {
					thisItemColor = Color.BLACK;
				} else {
					thisItemColor = colorGreen;
				}
				
			}
			
		} else {
			viewholder.mTextView1.setText(payeeName);
			if (expenseAccount > 0) {
				thisItemColor = colorRed;
			} else {
				thisItemColor = colorGreen;
			}
		}

			viewholder.symbol_txt.setTextColor(thisItemColor);
			viewholder.currency_textView.setTextColor(thisItemColor);
			viewholder.amount_textView.setTextColor(thisItemColor);
			viewholder.amount_textView.setText(MEntity.doublepoint2str(amount));

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
		
		if (mGroupList == null) {
			return 0;
		}
		
		if (mChildMap == null) {
			return 0;
		}
		
		return mChildMap.get(mGroupList.get(groupPosition)).size();

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
