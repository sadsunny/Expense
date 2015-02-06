package com.appxy.pocketexpensepro.overview.budgets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.BudgetActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditBudgetActivity extends BaseHomeActivity {
	private int _id;
	private TextView mTextView;
	private EditText mEditText;
	private List<Map<String, Object>> mList;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private LayoutInflater inflater;
	private String uuid ;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				if (mList != null) {
					String categoryName  = (String) mList.get(0).get("categoryName");
					String amount  = (String) mList.get(0).get("amount");
					mTextView.setText(categoryName);
					mEditText.setText(MEntity.doubl2str(amount));
					mEditText.setSelection(amount.length());
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
				break;

			case MSG_FAILURE:
				Toast.makeText(EditBudgetActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editbudget);
		inflater = LayoutInflater.from(EditBudgetActivity.this);
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
		
		Intent intent = getIntent();
		_id = intent.getIntExtra("_id", 0);
		uuid = intent.getStringExtra("uuid");
		if (_id <= 0) {
			finish();
		}
		mTextView = (TextView) findViewById(R.id.category_txt);
		mEditText = (EditText) findViewById(R.id.amount_edit);
		
		Thread mThread = new Thread(mTask);
		mThread.start();

	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mList = BudgetsDao.selectBudgetById(EditBudgetActivity.this, _id);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
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
				
				String amountString = mEditText.getText().toString();
				double amount;
				try {
					amount = Double.parseDouble(amountString);
				} catch (NumberFormatException e) {
					amount = 0.00;
				}
				if (amount > 0) {
					BudgetsDao.updateBudget(EditBudgetActivity.this, _id, amountString,mDbxAcctMgr ,mDatastore);
				}
				Intent intent = new Intent();
				intent.putExtra("done", 1);
				setResult(16, intent);
				
				finish();
				break;
			}
		}
	};

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
	}
	
	
}
