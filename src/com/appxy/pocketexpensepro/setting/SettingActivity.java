package com.appxy.pocketexpensepro.setting;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.setting.category.CategoryActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SettingActivity extends Activity {
	private RelativeLayout payeeLinearLayout;
	private RelativeLayout categoryLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		payeeLinearLayout = (RelativeLayout) findViewById(R.id.payee_rel);
		categoryLinearLayout = (RelativeLayout) findViewById(R.id.category_rel);
		payeeLinearLayout.setOnClickListener(mClickListener);
		categoryLinearLayout.setOnClickListener(mClickListener);
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
			}
		}
	};
	
	
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
	
}
