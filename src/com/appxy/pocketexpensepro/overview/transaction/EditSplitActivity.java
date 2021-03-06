package com.appxy.pocketexpensepro.overview.transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.budgets.BudgetsDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditSplitActivity extends BaseHomeActivity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private LayoutInflater inflater;
	private ListView mListView;
	private BudgetsListViewAdapter mAdapter;
	private LinearLayout mRootLayout;
	private List<LinearLayout> mViewList;
	private List<Map<String, Object>> mDataList;
	private List<Map<String, Object>> mReturnList;
	private LinearLayout chooseButton;
	private LinearLayout clearButton;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				if (mDataList != null && mDataList.size() > 0) {
					mRootLayout.removeAllViews();
					mViewList.clear();
					for (int i = 0; i < mDataList.size(); i++) {
						LinearLayout mchildLayout = (LinearLayout) inflater
								.inflate(R.layout.budget_child_item, null);
						mRootLayout.addView(mchildLayout);
						mViewList.add(mchildLayout);

						ImageView mImageView = (ImageView) mchildLayout
								.findViewById(R.id.category_img);
						TextView categoryTextView = (TextView) mchildLayout
								.findViewById(R.id.category_txt);
						TextView currencyTextView = (TextView) mchildLayout
								.findViewById(R.id.currency_txt);
						final EditText mEditText = (EditText) mchildLayout
								.findViewById(R.id.amount_edit);

						mImageView
								.setImageResource(Common.CATEGORY_ICON[(Integer) mDataList
										.get(i).get("iconName")]);
						categoryTextView.setText((String) mDataList.get(i).get(
								"categoryName"));

						String amount = (String) mDataList.get(i).get("amount");
						mEditText.setText(MEntity.doubl2str(amount));
						mEditText.addTextChangedListener(new TextWatcher() { // 设置保留两位小数
									private boolean isChanged = false;

									@Override
									public void onTextChanged(CharSequence s,
											int start, int before, int count) {
										// TODO Auto-generated method stub

									}

									@Override
									public void beforeTextChanged(
											CharSequence s, int start,
											int count, int after) {
										// TODO Auto-generated method stub
									}

									@Override
									public void afterTextChanged(Editable s) {
										// TODO Auto-generated method stub

										if (isChanged) {// ----->如果字符未改变则返回
											return;
										}
										String str = s.toString();

										isChanged = true;
										String cuttedStr = str;
										/* 删除字符串中的dot */
										for (int i = str.length() - 1; i >= 0; i--) {
											char c = str.charAt(i);
											if ('.' == c) {
												cuttedStr = str.substring(0, i)
														+ str.substring(i + 1);
												break;
											}
										}
										/* 删除前面多余的0 */
										int NUM = cuttedStr.length();
										int zeroIndex = -1;
										for (int i = 0; i < NUM - 2; i++) {
											char c = cuttedStr.charAt(i);
											if (c != '0') {
												zeroIndex = i;
												break;
											} else if (i == NUM - 3) {
												zeroIndex = i;
												break;
											}
										}
										if (zeroIndex != -1) {
											cuttedStr = cuttedStr
													.substring(zeroIndex);
										}
										/* 不足3位补0 */
										if (cuttedStr.length() < 3) {
											cuttedStr = "0" + cuttedStr;
										}
										/* 加上dot，以显示小数点后两位 */
										cuttedStr = cuttedStr.substring(0,
												cuttedStr.length() - 2)
												+ "."
												+ cuttedStr.substring(cuttedStr
														.length() - 2);

										mEditText.setText(cuttedStr);
										mEditText.setSelection(cuttedStr
												.length());
										isChanged = false;
									}
								});

					}
				}
				break;

			case MSG_FAILURE:
				Toast.makeText(EditSplitActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_split);

		mRootLayout = (LinearLayout) findViewById(R.id.rootView);
		inflater = LayoutInflater.from(EditSplitActivity.this);

		mViewList = new ArrayList<LinearLayout>();
		mDataList = new ArrayList<Map<String, Object>>();
		mReturnList = new ArrayList<Map<String, Object>>();
		
		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(
				R.layout.activity_custom_actionbar, null, false);

		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView, lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);

		View cancelActionView = customActionBarView
				.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(mClickListener);
		View doneActionView = customActionBarView
				.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mClickListener);

		chooseButton = (LinearLayout) findViewById(R.id.choose_btn);
		clearButton = (LinearLayout) findViewById(R.id.clear_btn);
		
		chooseButton.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				intent.setClass(
						EditSplitActivity.this,
						SplitCategoryActivity.class);
				startActivityForResult(intent, 9);
			}
		});
		
		clearButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRootLayout.removeAllViews();
				mDataList.clear();
				mViewList.clear();
			}
		});
		
		Thread mThread = new Thread(mTask);
		mThread.start();

	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = getIntent();
			mDataList = (List<Map<String, Object>>) intent.getSerializableExtra("returnList");
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
		//
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 9:
			if (data != null) {
				mReturnList = (List<Map<String, Object>>) data
						.getSerializableExtra("returnList");
				
				Intent intent1 = new Intent();
				intent1.putExtra("returnList", (Serializable)mReturnList);
				setResult(10, intent1);
				finish();
			}
			break;
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
				

				if (mViewList != null && mViewList.size() > 0) {
					
					mReturnList.clear();
					for (int i = 0; i < mViewList.size(); i++) {
						EditText mEditText = (EditText) mViewList.get(i).findViewById(R.id.amount_edit);
						String amountString = mEditText.getText().toString();

						double amount;
						try {
							amount = Double.parseDouble(amountString);
						} catch (NumberFormatException e) {
							amount = 0.00;
						}

						if (amount > 0) {
							int categoryId = (Integer) mDataList.get(i).get("categoryId");
							String categoryName = (String) mDataList.get(i).get("categoryName");
							int iconName = (Integer) mDataList.get(i).get("iconName");
							Map<String, Object> mMap = new HashMap<String, Object>();
							mMap.put("categoryId", categoryId);
							mMap.put("amount", amountString);
							mMap.put("categoryName", categoryName);
							mMap.put("iconName", iconName);
							mReturnList.add(mMap);
						}
					}
					
					
				}
				
				Intent intent1 = new Intent();
				intent1.putExtra("returnList", (Serializable)mReturnList);
				setResult(10, intent1);

				finish();
				break;
			}
		}
	};

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
	}

}
