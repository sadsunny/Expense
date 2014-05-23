package com.appxy.pocketexpensepro.overview.budgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.setting.payee.CreatPayeeActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BudgetSelectCategoryActivity extends Activity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private LayoutInflater inflater;
	private LinearLayout mRootLayout;
	private List<LinearLayout> mViewList;
	private List<Map<String, Object>> mDataList;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				break;

			case MSG_FAILURE:
				Toast.makeText(BudgetSelectCategoryActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget_select_category);

		mRootLayout = (LinearLayout) findViewById(R.id.rootView);
		inflater = LayoutInflater.from(BudgetSelectCategoryActivity.this);

		mViewList = new ArrayList<LinearLayout>();
		mDataList = new ArrayList<Map<String, Object>>();

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

		Thread mThread = new Thread(mTask);
		mThread.start();

	}

	public Runnable mTask = new Runnable() {

		@Override 
		public void run() {
			// TODO Auto-generated method stub
			mDataList = BudgetsDao
					.selectCategoryNotIn(BudgetSelectCategoryActivity.this);
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

						if (amount > 0) {
							int categoryId = (Integer) mDataList.get(i).get(
									"_id");
							long temId = BudgetsDao.insertBudgetTemplate(
									BudgetSelectCategoryActivity.this, amountString,
									categoryId);
							if (temId > 0) {
								long itemId = BudgetsDao.insertBudgetItem(
										BudgetSelectCategoryActivity.this, amountString,
										(int) temId);
							}
							Intent intent = new Intent();
							intent.putExtra("done", 1);
							setResult(4, intent);
						}
					}
				}

				finish();
				break;
			}
		}
	};

}
