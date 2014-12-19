package com.appxy.pocketexpensepro.setting.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.accounts.CreatAccountTypeActivity;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EditCategoryActivity extends BaseHomeActivity {
	private LayoutInflater inflater;
	private EditText mEditText;
	private RelativeLayout mRelativeLayout;
	private ImageView mImageView;
	private Button mButton;
	private int mcheck;
	private AlertDialog mCaDialog;
	private AlertDialog mPaDialog;
	private GridView mGridView;
	private GridViewAdapter mAdapter;
	private int iconPosition;
	private ListView mListView;
	private ChooseTypeListViewAdapter mListViewAdapter;
	private int checkedItem = 0;
	private List<Map<String, Object>> mCategoryList;
	private int selectWhether = -1;
	private int _id;
    private String categoryName = "";
    private String uuid ;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_creat_category);
		Intent intent = getIntent();
		_id = intent.getIntExtra("_id", 0);
		uuid = intent.getStringExtra("uuid");
		mcheck = intent.getIntExtra("categoryType", 0); 
		
		if (_id == 0 || uuid == null) {
			finish();
		}
		
		List<Map<String, Object>> mList = CategoryDao.selectCategoryById(this, _id);

		inflater = LayoutInflater.from(EditCategoryActivity.this);
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
		mButton = (Button) findViewById(R.id.parent_btn);
		mEditText = (EditText) findViewById(R.id.category_edit);
		
		if (mList != null && mList.size() > 0) {
			categoryName = (String) mList.get(0).get("categoryName");
			iconPosition = (Integer) mList.get(0).get("iconName");
			if (categoryName.contains(":")) {
				String[] name =categoryName.split(":");
				mButton.setText(name[0]);
				mEditText.setText(name[1]);
				mEditText.setSelection(name[1].length());
			} else {
				mButton.setText("");
				mEditText.setText(categoryName);
				mEditText.setSelection(categoryName.length());
			}
		} else {
			finish();
		}
		
		
		mRelativeLayout = (RelativeLayout) findViewById(R.id.mRelativeLayout);
		mImageView = (ImageView) findViewById(R.id.category_img);
	
		mRelativeLayout.setOnClickListener(mClickListener);
		mButton.setOnClickListener(mClickListener);

		mImageView.setImageResource(Common.CATEGORY_ICON[iconPosition]);

	}

	private final View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.action_cancel:
				finish();
				break;

			case R.id.action_done:
				String mCategoryString = mEditText.getText().toString();
				mCategoryString = mCategoryString.replace(":", "");
				String parentNameString = mButton.getText().toString();
				
				String allNameSrting = "";
				if (parentNameString != null && parentNameString.length() > 0) {
					allNameSrting = parentNameString+":"+mCategoryString;
				} else {
					allNameSrting = parentNameString;
				}
				
				if (mCategoryString == null
						|| mCategoryString.trim().length() == 0
						|| mCategoryString.trim().equals("")) {

					new AlertDialog.Builder(EditCategoryActivity.this)
							.setTitle("Warning! ")
							.setMessage(
									"Please make sure the category name is not empty! ")
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

				} else if ( !comparisonName(mCategoryString,CategoryDao.selectCategoryAll(EditCategoryActivity.this)) && !allNameSrting.equals(categoryName) ) {

					new AlertDialog.Builder(EditCategoryActivity.this)
							.setTitle("Warning! ")
							.setMessage("Category already exists! ")
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

				} else if( parentNameString != null && parentNameString.length() > 0 && CategoryDao.selectCategoryByName(EditCategoryActivity.this, parentNameString).size() <= 0 ){
					
					new AlertDialog.Builder(EditCategoryActivity.this)
					.setTitle("Failed to add category! ")
					.setMessage("This parent category has been removed by other devices. Please choose another one. ")
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}).show();
					
				}else if( CategoryDao.selectCategoryById(EditCategoryActivity.this,  _id).size() <= 0 ){
					
					Toast.makeText(EditCategoryActivity.this, "This category has been removed by other devices",Toast.LENGTH_SHORT).show();
					finish();
					
				}else{

						String parentString = mButton.getText().toString();
						if (parentString != null && parentString.length() > 0) {
							long id = CategoryDao.updateCategory(
									EditCategoryActivity.this,_id, parentString
											+ ":" + mCategoryString, mcheck, 0,
									iconPosition, 0,  uuid, mDbxAcctMgr,  mDatastore);
							if (id > 0) {
								Intent intent = new Intent();
								intent.putExtra("_id", id);
								setResult(11, intent);
							}
						}else {
							
							long id = CategoryDao.updateCategory(
									EditCategoryActivity.this,_id, mCategoryString,
									mcheck, 0, iconPosition, 0,uuid, mDbxAcctMgr, mDatastore);
							  if(!categoryName.contains(":")){
								  updateCategoryChild(categoryName, mCategoryString);
							}
							if (id > 0) {
								Intent intent = new Intent();
								intent.putExtra("_id", id);
								setResult(11, intent);
							}
						}

						finish();

				}

				break;

			case R.id.mRelativeLayout:

				View view = inflater.inflate(R.layout.dialog_choose_icon, null);
				mGridView = (GridView) view.findViewById(R.id.mGridView);
				mAdapter = new GridViewAdapter(EditCategoryActivity.this);
				mGridView.setAdapter(mAdapter);
				mGridView.setSelector(R.color.blue);
				mGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						iconPosition = arg2;
						mCaDialog.dismiss();
						mImageView
								.setImageResource(Common.CATEGORY_ICON[iconPosition]);
					}
				});

				AlertDialog.Builder mBuilder = new AlertDialog.Builder(
						EditCategoryActivity.this);
				mBuilder.setTitle("Choose Category icon");
				mBuilder.setView(view);
				mCaDialog = mBuilder.create();
				mCaDialog.show();

				break;

			case R.id.parent_btn:

				View mView = inflater
						.inflate(R.layout.dialog_choose_type, null);
				mListView = (ListView) mView.findViewById(R.id.mListView);
				mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				mListView.setItemsCanFocus(false);
				mListViewAdapter = new ChooseTypeListViewAdapter(
						EditCategoryActivity.this);

				mCategoryList = filterData(CategoryDao.selectCategory(
						EditCategoryActivity.this, mcheck));

				mListView.setAdapter(mListViewAdapter);
				mListView.setSelection(checkedItem);
				mListViewAdapter
						.setItemChecked((checkedItem - 1) > 0 ? (checkedItem - 1)
								: 0);
				mListViewAdapter.setAdapterDate(mCategoryList);
				mListViewAdapter.notifyDataSetChanged();
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						checkedItem = arg2;
						selectWhether = arg2;
						mListViewAdapter.setItemChecked(checkedItem);
						mListViewAdapter.notifyDataSetChanged();
						mButton.setText(mCategoryList.get(checkedItem).get(
								"categoryName")
								+ "");
						mPaDialog.dismiss();
					}
				});

				AlertDialog.Builder mPaBuilder = new AlertDialog.Builder(
						EditCategoryActivity.this);
				mPaBuilder.setTitle("Select Parent");
				mPaBuilder.setView(mView);
				mPaBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						});

				mPaDialog = mPaBuilder.create();
				mPaDialog.show();

				break;
			}
		}
	};

	public List<Map<String, Object>> filterData(List<Map<String, Object>> mData) {// 过滤子类和父类

		List<Map<String, Object>> temDataList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> mMap : mData) { // 分离父类和子类
			String categoryName = (String) mMap.get("categoryName");

			if (!categoryName.contains(":")) {
				temDataList.add(mMap);
			}
		}
		return temDataList;
	}

	public boolean comparisonName(String cName, List<Map<String, Object>> mData) {
		boolean mCheck = true;

		for (Map<String, Object> mMap : mData) { // 分离父类和子类
			String categoryName = (String) mMap.get("categoryName");

			String temp[] = categoryName.split(":");
			if (temp[0].equals(cName)) {
				mCheck = false;
			}
			if (temp.length > 1) {
				if (temp[1].equals(cName)) {
					mCheck = false;
				}
			}

		}

		return mCheck;
	}
	
	public void updateCategoryChild(String parString, String newParString) {
		List<Map<String, Object>> mList = CategoryDao.selectCategoryChild(EditCategoryActivity.this, parString);
		if (mList.size() > 0) {
			for (Map<String, Object> iMap:mList) {
				String categoryNameString = (String) iMap.get("categoryName");
				String temp[] = categoryNameString.split(":");
				String newString = newParString+":"+temp[1];
				int id = (Integer) iMap.get("_id");
				String uuid = (String)iMap.get("uuid");
				
				CategoryDao.updateCategoryName(EditCategoryActivity.this, id, newString, uuid, mDbxAcctMgr, mDatastore);
				try {
					mDatastore.sync();
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
	}
	

}
