package com.appxy.pocketexpensepro.setting.export;

import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.SettingActivity;
import com.dropbox.sync.android.DbxRecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class ExportAllActivity extends BaseHomeActivity {

	private LinearLayout transactionCSVLayout;
	private LinearLayout transactionPDFLayout;
	private LinearLayout flowPDFLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export_all);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		transactionCSVLayout = (LinearLayout) findViewById(R.id.export_trans_csv);
		transactionPDFLayout = (LinearLayout) findViewById(R.id.export_trans_pdf);
		flowPDFLayout = (LinearLayout) findViewById(R.id.export_flow_pdf);

		transactionCSVLayout.setOnClickListener(mClickListener);
		transactionPDFLayout.setOnClickListener(mClickListener);
		flowPDFLayout.setOnClickListener(mClickListener);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.export_trans_csv:
				
				Intent intent = new Intent();
				intent.setClass(ExportAllActivity.this, ExportTransactionCSVActivity.class);
				startActivity(intent);
				
				break;
			case R.id.export_trans_pdf:
				
				Intent intent1 = new Intent();
				intent1.setClass(ExportAllActivity.this, ExportTransactionPDFActivity.class);
				startActivity(intent1);
				
				break;
			case R.id.export_flow_pdf:

				Intent intent2 = new Intent();
				intent2.setClass(ExportAllActivity.this, ExportFlowPDFActivity.class);
				startActivity(intent2);
				
				break;
			}
		}
	};
	
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
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		
	}


}
