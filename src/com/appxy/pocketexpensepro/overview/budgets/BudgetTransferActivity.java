package com.appxy.pocketexpensepro.overview.budgets;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountTransferActivity;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BudgetTransferActivity extends BaseHomeActivity {
	private LayoutInflater inflater;
	private EditText amountEditText;
	private Button fromButton;
	private Button toButton;
	private List<Map<String, Object>> mBudgetList;
	private int from_id;
	private int to_id;
	private int fromSelected = 0;
	private int toSelected = 0;
	private AlertDialog mDialogFrom;
	private AlertDialog mDialogTo;
	private ChooseTypeListViewAdapter fromListViewAdapter;
	private ChooseTypeListViewAdapter toListViewAdapter;
	private String amountString = "0.00";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget_transfer);
		inflater = LayoutInflater.from(BudgetTransferActivity.this);

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

		amountEditText = (EditText) findViewById(R.id.amount_edit);
		fromButton = (Button) findViewById(R.id.from_btn);
		toButton = (Button) findViewById(R.id.to_btn);

		toButton.setOnClickListener(mClickListener);
		fromButton.setOnClickListener(mClickListener);

		mBudgetList = OverViewDao.selectBudget(this);

		if (mBudgetList != null && mBudgetList.size() > 0) {
			from_id = (Integer) mBudgetList.get(0).get("_id");
			fromButton.setText((String) mBudgetList.get(0).get("categoryName"));

			to_id = (Integer) mBudgetList.get(0).get("_id");
			toButton.setText((String) mBudgetList.get(0).get("categoryName"));
		}

		amountEditText.setText("0.00");
		amountEditText.setSelection(4);
		amountEditText.addTextChangedListener(new TextWatcher() { // 设置保留两位小数
					private boolean isChanged = false;

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
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
							cuttedStr = cuttedStr.substring(zeroIndex);
						}
						/* 不足3位补0 */
						if (cuttedStr.length() < 3) {
							cuttedStr = "0" + cuttedStr;
						}
						/* 加上dot，以显示小数点后两位 */
						cuttedStr = cuttedStr.substring(0,
								cuttedStr.length() - 2)
								+ "."
								+ cuttedStr.substring(cuttedStr.length() - 2);

						amountEditText.setText(cuttedStr);
						amountString = amountEditText.getText().toString();
						amountEditText.setSelection(cuttedStr.length());
						isChanged = false;
					}
				});

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

				double amountDouble = 0;
				try {
					amountDouble = Double.parseDouble(amountString);
				} catch (NumberFormatException e) {
					amountDouble = 0.00;
				}

				if (amountDouble == 0) {

					new AlertDialog.Builder(BudgetTransferActivity.this)
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

				} else if (from_id == 0) {

					new AlertDialog.Builder(BudgetTransferActivity.this)
							.setTitle("Warning! ")
							.setMessage("Please choose a Budget! ")
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

				} else if (to_id == 0) {

					new AlertDialog.Builder(BudgetTransferActivity.this)
							.setTitle("Warning! ")
							.setMessage("Please choose a Budget! ")
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
				} else {
					if (from_id != to_id) {

						long _id = BudgetsDao.insertBudgetTransfer(
								BudgetTransferActivity.this, amountString,
								from_id, to_id);
						
						Intent intent = new Intent();
						intent.putExtra("done", 1);
						setResult(15, intent);
						
					}
					finish();
				}

				break;

			case R.id.from_btn:
				View view1 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				ListView listViewFrom = (ListView) view1
						.findViewById(R.id.mListView);
				fromListViewAdapter = new ChooseTypeListViewAdapter(
						BudgetTransferActivity.this);
				fromListViewAdapter.setAdapterDate(mBudgetList);
				fromListViewAdapter.setItemChecked(fromSelected);
				listViewFrom.setAdapter(fromListViewAdapter);
				listViewFrom
						.setSelection((fromSelected - 1) > 0 ? (fromSelected - 1)
								: 0);
				listViewFrom.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						fromSelected = arg2;
						fromListViewAdapter.setItemChecked(arg2);
						fromListViewAdapter.notifyDataSetChanged();
						String nameString = (String) mBudgetList.get(arg2).get(
								"categoryName");
						fromButton.setText(nameString);

						from_id = (Integer) mBudgetList.get(arg2).get("_id");
						mDialogFrom.dismiss();
					}
				});
				AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(
						BudgetTransferActivity.this);
				mBuilder1.setTitle("From Budget");
				mBuilder1.setView(view1);
				mBuilder1.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mDialogFrom = mBuilder1.create();
				mDialogFrom.show();

				break;

			case R.id.to_btn:

				View view2 = inflater
						.inflate(R.layout.dialog_choose_type, null);
				ListView listViewTo = (ListView) view2
						.findViewById(R.id.mListView);
				toListViewAdapter = new ChooseTypeListViewAdapter(
						BudgetTransferActivity.this);
				toListViewAdapter.setAdapterDate(mBudgetList);
				toListViewAdapter.setItemChecked(toSelected);
				listViewTo.setAdapter(toListViewAdapter);
				listViewTo.setSelection((toSelected - 1) > 0 ? (toSelected - 1)
						: 0);
				listViewTo.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						toSelected = arg2;
						toListViewAdapter.setItemChecked(arg2);
						toListViewAdapter.notifyDataSetChanged();
						String nameString = (String) mBudgetList.get(arg2).get(
								"categoryName");
						toButton.setText(nameString);

						to_id = (Integer) mBudgetList.get(arg2).get("_id");
						mDialogTo.dismiss();
					}
				});
				AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(
						BudgetTransferActivity.this);
				mBuilder2.setTitle("To Budget");
				mBuilder2.setView(view2);
				mBuilder2.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mDialogTo = mBuilder2.create();
				mDialogTo.show();

				break;
			}
		}
	};

}
