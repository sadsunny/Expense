package com.appxy.pocketexpensepro.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.R.color;
import com.appxy.pocketexpensepro.accounts.AccountToTransactionActivity.thisExpandableListViewAdapter;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.passcode.Activity_ChangePass;
import com.appxy.pocketexpensepro.passcode.Activity_SetPass;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.category.CategoryActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends BaseHomeActivity {
	private RelativeLayout payeeLinearLayout;
	private RelativeLayout categoryLinearLayout;
	private TextView versionTextView;
	private Switch passcode_switch;
	private RelativeLayout currency_RelativeLayout;
	private TextView currency_TextView;
	private LayoutInflater inflater;
	private ListView currencyListView;
	private AlertDialog currencyDialog;
	private int listPositionSelect;
	private int currencyPosition = 148;
	private String passCode;
	private RelativeLayout feedback_RelativeLayout;
	private String info;

	private LinearLayout left_LinearLayout;
	private TextView left_txt;
	private LinearLayout spent_LinearLayout;
	private TextView spent_txt;

	private SharedPreferences mPreferences;
	private int  BdgetSetting;
	
	@SuppressLint("ResourceAsColor")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		inflater = LayoutInflater.from(this);

		mPreferences = getSharedPreferences("Expense", MODE_PRIVATE);
		BdgetSetting = mPreferences.getInt("BdgetSetting", 0);
		
		versionTextView = (TextView) findViewById(R.id.version_txt);
		payeeLinearLayout = (RelativeLayout) findViewById(R.id.payee_rel);
		categoryLinearLayout = (RelativeLayout) findViewById(R.id.category_rel);
		passcode_switch = (Switch) findViewById(R.id.passcode_switch);
		currency_RelativeLayout = (RelativeLayout) findViewById(R.id.currency_RelativeLayout);
		currency_TextView = (TextView) findViewById(R.id.currency_TextView);
		feedback_RelativeLayout = (RelativeLayout) findViewById(R.id.feedback_RelativeLayout);

		left_LinearLayout = (LinearLayout) findViewById(R.id.left_LinearLayout);
		left_txt = (TextView) findViewById(R.id.left_txt);
		spent_LinearLayout = (LinearLayout) findViewById(R.id.spent_LinearLayout);
		spent_txt = (TextView) findViewById(R.id.spent_txt);

		if (BdgetSetting ==0) {
			left_LinearLayout.setBackgroundColor(Color.parseColor("#4ea3cc"));
			left_txt.setTextColor(Color.rgb(255, 255, 255));
			spent_LinearLayout.setBackgroundColor(Color.parseColor("#d4d4d4"));
			spent_txt.setTextColor(Color.parseColor("#36373c"));

		} else {
			left_LinearLayout.setBackgroundColor(Color.parseColor("#d4d4d4"));
			left_txt.setTextColor(Color.parseColor("#36373c") );
			spent_LinearLayout.setBackgroundColor(Color.parseColor("#4ea3cc"));
			spent_txt.setTextColor(Color.rgb(255, 255, 255) );

		}
	
		left_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				left_LinearLayout.setBackgroundColor(Color.parseColor("#4ea3cc"));
				left_txt.setTextColor(Color.rgb(255, 255, 255));
				spent_LinearLayout.setBackgroundColor(Color.parseColor("#d4d4d4"));
				spent_txt.setTextColor(Color.parseColor("#36373c"));
				
				SharedPreferences.Editor editor = mPreferences
						.edit();
				editor.putInt("BdgetSetting", 0);
				editor.commit();
			}
			
		});

		spent_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				left_LinearLayout.setBackgroundColor(Color.parseColor("#d4d4d4"));
				left_txt.setTextColor(Color.parseColor("#36373c") );
				spent_LinearLayout.setBackgroundColor(Color.parseColor("#4ea3cc"));
				spent_txt.setTextColor(Color.rgb(255, 255, 255) );
				
				SharedPreferences.Editor editor = mPreferences
						.edit();
				editor.putInt("BdgetSetting", 1);
				editor.commit();

			}
		});

		List<Map<String, Object>> mList = SettingDao.selectSetting(this);
		currencyPosition = (Integer) mList.get(0).get("currency");
		passCode = (String) mList.get(0).get("passcode");

		listPositionSelect = currencyPosition;
		currency_TextView.setText(Common.CURRENCY_SIGN[currencyPosition] + " "
				+ Common.CURRENCY_NAME[currencyPosition]);
		currency_RelativeLayout.setOnClickListener(mClickListener);
		payeeLinearLayout.setOnClickListener(mClickListener);
		categoryLinearLayout.setOnClickListener(mClickListener);
		feedback_RelativeLayout.setOnClickListener(mClickListener);
		versionTextView.setText(getVersion());

		if (passCode != null && passCode.length() > 2) {
			passcode_switch.setChecked(true);
		} else {
			passcode_switch.setChecked(false);
		}

		passcode_switch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

						List<Map<String, Object>> mList = SettingDao
								.selectSetting(SettingActivity.this);
						passCode = (String) mList.get(0).get("passcode");

						if (isChecked && passCode != null
								&& passCode.length() <= 2) {
							Intent intent = new Intent(SettingActivity.this,
									Activity_SetPass.class);
							startActivityForResult(intent, 11);
						} else if (!isChecked && passCode != null
								&& passCode.length() > 2) {

							Intent intent = new Intent(SettingActivity.this,
									Activity_ChangePass.class);
							startActivityForResult(intent, 22);
						}
					}
				});

	}

	private final View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.payee_rel:

				Intent intent1 = new Intent();
				intent1.setClass(SettingActivity.this, PayeeActivity.class);
				startActivity(intent1);

				break;
			case R.id.category_rel:

				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, CategoryActivity.class);
				startActivity(intent);

				break;

			case R.id.feedback_RelativeLayout:

				fillinfo();
				Intent email = new Intent(android.content.Intent.ACTION_SEND);
				email.setType("plain/text");
				String[] emailReciver = new String[] { "billkeeper.a@appxy.com" };
				String emailSubject = "Expense+ Feedback";

				List<Intent> targetedShareIntents = new ArrayList<Intent>();
				List<ResolveInfo> resInfo = getPackageManager()
						.queryIntentActivities(email, 0);
				if (!resInfo.isEmpty()) {
					for (ResolveInfo minfo : resInfo) {
						Intent targetedShare = new Intent(Intent.ACTION_SEND);

						targetedShare.putExtra(
								android.content.Intent.EXTRA_EMAIL,
								emailReciver);
						targetedShare.putExtra(
								android.content.Intent.EXTRA_SUBJECT,
								emailSubject);
						targetedShare.putExtra(
								android.content.Intent.EXTRA_TEXT, info);
						targetedShare.setType("plain/text");

						if (minfo.activityInfo.packageName.toLowerCase()
								.contains("mail")
								|| minfo.activityInfo.name.toLowerCase()
										.contains("mail")) {

							targetedShare
									.setPackage(minfo.activityInfo.packageName);
							targetedShareIntents.add(targetedShare);
						}
					}
					if (targetedShareIntents.size() > 0) {
						Intent chooserIntent = Intent.createChooser(
								targetedShareIntents.remove(0), "Export");
						chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
								targetedShareIntents
										.toArray(new Parcelable[] {}));
						startActivityForResult(chooserIntent, 3);
					} else {
						Toast.makeText(SettingActivity.this,
								"Can't find mail application",
								Toast.LENGTH_SHORT).show();
					}
				}

				break;

			case R.id.currency_RelativeLayout:

				View view = inflater
						.inflate(R.layout.dialog_set_currency, null);
				currencyListView = (ListView) view.findViewById(R.id.mListView);
				DialogCurrencyAdapter dcAdapter = new DialogCurrencyAdapter(
						SettingActivity.this);
				currencyListView.setAdapter(dcAdapter);
				currencyListView.setSelection(listPositionSelect);
				currencyListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								currencyDialog.dismiss();
								currency_TextView
										.setText(Common.CURRENCY_SIGN[arg2]
												+ " "
												+ Common.CURRENCY_NAME[arg2]);
								listPositionSelect = arg2;
								currencyPosition = arg2;
								Common.CURRENCY = arg2;
								SettingDao.updateCurrency(SettingActivity.this,
										arg2);
							}
						});

				AlertDialog.Builder builder = new AlertDialog.Builder(
						SettingActivity.this);
				builder.setView(view);
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
				currencyDialog = builder.create();
				currencyDialog.show();

				break;
			}
		}
	};

	public void fillinfo() {
		info = "";

		info += "Model : " + android.os.Build.MODEL + "\n";
		info += "Release : " + android.os.Build.VERSION.RELEASE + "\n";

		info += "App : " + getVersion() + "\n";
	}

	public String getVersion() {

		PackageManager manager = this.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = info.versionName;
		return version;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		List<Map<String, Object>> mList = SettingDao.selectSetting(this);
		passCode = (String) mList.get(0).get("passcode");

		if (passCode != null && passCode.length() > 2) {
			passcode_switch.setChecked(true);
		} else {
			passcode_switch.setChecked(false);
		}

	}

}
