package com.appxy.pocketexpensepro.overview.budgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.bills.BillEditActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.BudgetActivity;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.sync.SyncDao;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BudgetListActivity extends BaseHomeActivity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	
	private Button mButton;
	private List<Map<String, Object>> mDataList; 
	private LayoutInflater inflater;
	private LinearLayout mRootLayout;
	private List<LinearLayout> mViewList;
	private TextView count_txt;
	private TextView count_label;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				int count = mDataList.size();
				count_txt.setText(count+"");
				if (count > 1) {
					count_label.setText("ITEMS");
				} else {
					count_label.setText("ITEM");
				}
				
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

						currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
						mImageView
								.setImageResource(Common.CATEGORY_ICON[(Integer) mDataList
										.get(i).get("iconName")]);
						categoryTextView.setText((String) mDataList.get(i).get(
								"categoryName"));
						String amonString = (String) mDataList.get(i).get("amount");
						
						mEditText.setText(MEntity.doubl2str(amonString));
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
				}else{
					mRootLayout.removeAllViews();
				}
					
				break;

			case MSG_FAILURE:
				Toast.makeText(BudgetListActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget_list);
		mRootLayout = (LinearLayout) findViewById(R.id.rootView);
		inflater = LayoutInflater.from(BudgetListActivity.this);

		mViewList = new ArrayList<LinearLayout>();
		mDataList = new ArrayList<Map<String, Object>>();
		
		count_txt =(TextView) findViewById(R.id.count_txt);
		count_label = (TextView) findViewById(R.id.count_label); 
		
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
		
		mButton = (Button) findViewById(R.id.mButton);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BudgetListActivity.this, BudgetSelectCategoryActivity.class);
				startActivityForResult(intent, 4);
			}
		});
		
		mDataList = OverViewDao.selectBudget(BudgetListActivity.this);
		Thread mThread = new Thread(mTask);
		mThread.start();
		
	}

	public Runnable mTask = new Runnable() {

		@Override 
		public void run() {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
		//
	};
	
	
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
//				BudgetsDao.deleteBudgetAll(BudgetListActivity.this);
				Intent intent = new Intent();
				intent.putExtra("done", 1);
				setResult(4, intent);
				
				int checkDo = 1;
				if (mViewList != null && mViewList.size() > 0) {
					for (int i = 0; i < mViewList.size(); i++) {
						
						EditText mEditText = (EditText) mViewList.get(i)
								.findViewById(R.id.amount_edit);
						String amountString = mEditText.getText().toString();
						
						double amount;
						try {
							amount = Double.parseDouble(amountString);
						} catch (NumberFormatException e) {
							amount = 0.00;
						}
						
						if (amount == 0) {
							
							new AlertDialog.Builder(BudgetListActivity.this)
							.setTitle("Warning! ")
							.setMessage(
									"Please make sure the amount is not zero! ")
							.setPositiveButton("Retry",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();

										}
									}).show();
							checkDo = 0;
							return;
						}
					}
				}
				    List<Integer> hasDoLsit = new ArrayList<Integer>();
					if(checkDo == 1){
						
						for (int i = 0; i < mViewList.size(); i++) {
							EditText mEditText = (EditText) mViewList.get(i)
									.findViewById(R.id.amount_edit);
							String amountString = mEditText.getText().toString();

							double amount;
							try {
								amount = Double.parseDouble(amountString);
							} catch (NumberFormatException e) {
								amount = 0.00;
							}
							
							if (amount > 0) {
								
								int categoryId = (Integer) mDataList.get(i).get(
										"category");
								hasDoLsit.add(categoryId);
								
								String categoryUUid = SyncDao.selectCategoryUUid(BudgetListActivity.this, categoryId);
								long dateTime = System.currentTimeMillis();
								
								List<Map<String, Object>> mTempList = BudgetsDao.checkBudgetTemplateByCategory(BudgetListActivity.this, categoryId);
								List<Map<String, Object>> mItemList = new ArrayList<Map<String, Object>>();
								int tempId = 0;
								
								if(mTempList.size() > 0){
									tempId = (Integer)mTempList.get(0).get("_id");
									mItemList = BudgetsDao.checkBudgetItemByTemp(BudgetListActivity.this, tempId);
								}
								
								if(mItemList.size() > 0){
									int itemId = (Integer)mItemList.get(0).get("_id");
									BudgetsDao.updateBudget(BudgetListActivity.this, itemId, amountString,mDbxAcctMgr ,mDatastore);
									
								}else{
									
									long temId = BudgetsDao.insertBudgetTemplate(
											BudgetListActivity.this, amountString,
											categoryId, dateTime, mDbxAcctMgr, mDatastore);
									if (temId > 0) {
										long itemId = BudgetsDao.insertBudgetItem(
												BudgetListActivity.this, amountString,
												(int) temId, dateTime, mDbxAcctMgr, mDatastore);
									}
									
								}
								
							}
						}
					
							
							List<Map<String, Object>> mBudAllList =  BudgetsDao.selectBudgetTemplateAll(BudgetListActivity.this);
							
							for( Map<String, Object> mMap:mBudAllList){
								
								int t_id = (Integer)mMap.get("_id");
								int c_id = (Integer)mMap.get("category");
								boolean tag = true; //标志位
								
								for(int cId:hasDoLsit){
									if ( c_id == cId ){
										tag = false;
									}
								}
								
								if (tag) {
									List<Map<String, Object>> mBudItemList = BudgetsDao.selectItemByTemId(BudgetListActivity.this, t_id);
									if( mBudItemList.size() > 0){
										int temId = (Integer)mBudItemList.get(0).get("_id");
										String uuid = (String)mBudItemList.get(0).get("uuid");
										if(temId > 0){
											long row = BudgetsDao.deleteBudget(BudgetListActivity.this,
													temId, uuid, mDbxAcctMgr, mDatastore);
										}
										
									}
								}
								
							}
							
						
						finish();
					}
					
				
				break;
			}
		}
	};

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 4:

			if (data != null) {
				List<Map<String, Object>> mReturnDataList = (List<Map<String, Object>>) data.getSerializableExtra("mReturnDataList");
				List<Map<String, Object>> removeDataList = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> removeThisList = new ArrayList<Map<String,Object>>();
				
				if (mReturnDataList!=null) {
					if (mReturnDataList.size() > 0) {
						
						for (Map<String, Object> iMap:mDataList) {
							int categoryId = (Integer) iMap.get("category");
							boolean tag = true;
							
							for (Map<String, Object> jMap :mReturnDataList) {
								int categoryId2 = (Integer) jMap.get("category");
								if (categoryId == categoryId2) {
									removeDataList.add(jMap);
									tag = false;
								}
							}
							if (tag) {
								removeThisList.add(iMap);
							}
							
						}
						
						mReturnDataList.removeAll(removeDataList);
						mDataList.removeAll(removeThisList);
						mDataList.addAll(mReturnDataList);
						mHandler.post(mTask);
						
					} else {
						mDataList.clear();
						mViewList.clear();
						mHandler.post(mTask);
					}
				} 
				
			}
			break;
		}
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
	}
	

	
}
