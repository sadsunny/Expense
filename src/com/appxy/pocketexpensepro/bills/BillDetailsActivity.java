package com.appxy.pocketexpensepro.bills;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BillDetailsActivity extends Activity{
	private Button payButton;
	private Map<String, Object> mMap;
	private ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_details);
		
		Intent intent= getIntent();
		if (intent == null) {
			finish();
		} 
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
		
		payButton = (Button) findViewById(R.id.paybill_btn);
		payButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("dataMap",(Serializable)mMap);
					intent.setClass(BillDetailsActivity.this, BillPayActivity.class);
					startActivityForResult(intent, 17);
			}
		});
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 17:

			if (data != null) {
				
			}
			break;
			
		case 18:

			if (data != null) {
				
			}
			break;
			
		}
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
