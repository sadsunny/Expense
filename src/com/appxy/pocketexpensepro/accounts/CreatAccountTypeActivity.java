package com.appxy.pocketexpensepro.accounts;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class CreatAccountTypeActivity extends BaseHomeActivity {
	private LayoutInflater inflater;
	private EditText typeEditText;
	private GridView mGridView;
	private GridViewAdapter mAdapter;
	private int mPosition = 8; // the icon selected position, default 0
    private ImageView mImageView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_account_type);

		inflater = LayoutInflater.from(CreatAccountTypeActivity.this); 
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
		cancelActionView.setOnClickListener(mActionBarListener);
		View doneActionView = customActionBarView
				.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mActionBarListener);

		mImageView = (ImageView) findViewById(R.id.account_img);
		mImageView.setImageResource(Common.ACCOUNT_TYPE_ICON[mPosition]);
		typeEditText = (EditText) findViewById(R.id.account_type_edit);
		mGridView = (GridView) findViewById(R.id.mGridView);
		mAdapter = new GridViewAdapter(this);
		mGridView.setAdapter(mAdapter);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); 
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mPosition = arg2;
				mImageView.setImageResource(Common.ACCOUNT_TYPE_ICON[mPosition]);
			}
		});

	}

	private final View.OnClickListener mActionBarListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
				String typeName = typeEditText.getText().toString();
				if (typeName == null || typeName.trim().length() == 0
						|| typeName.trim().equals("")) {

					new AlertDialog.Builder(CreatAccountTypeActivity.this)
							.setTitle("Warning! ")
							.setMessage(
									"Please make sure the type name is not empty! ")
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

					long id = AccountDao.insertAccountType(
							CreatAccountTypeActivity.this, mPosition, 0,
							typeName);
					
					if (id > 0) {
						Intent intent = new Intent();
						intent.putExtra("_id", (int)id);
						intent.putExtra("typeName", typeName);
						setResult(1, intent);
						finish();
					} else {
						finish();
					}

				}

			}
		}
	};

}
