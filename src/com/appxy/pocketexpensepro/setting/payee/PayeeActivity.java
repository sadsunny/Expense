package com.appxy.pocketexpensepro.setting.payee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.R.id;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.category.DialogItemAdapter;
import com.dropbox.sync.android.DbxRecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

/*
 * add code 4,edit code 5
 */
public class PayeeActivity extends BaseHomeActivity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private ListView mListView;
	private List<Map<String, Object>> mDataList;
	private PayeeListViewAdapter mAdapter;
	private LayoutInflater mInflater;
	private ListView diaListView;
	private DialogItemAdapter mDialogItemAdapter;
	private AlertDialog alertDialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				mAdapter.setAdapterDate(mDataList);
				mAdapter.notifyDataSetChanged();

				break;

			case MSG_FAILURE:
				Toast.makeText(PayeeActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payee);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mInflater = LayoutInflater.from(this);

		mListView = (ListView) findViewById(R.id.mListView);
		mDataList = new ArrayList<Map<String, Object>>();
		mAdapter = new PayeeListViewAdapter(PayeeActivity.this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mListener);
		mListView.setOnItemLongClickListener(mLongListener);

		Thread mThread = new Thread(mTask);
		mThread.start();

	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList.clear();
			mDataList = PayeeDao.selectPayee(PayeeActivity.this);
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
		//
	};

	private OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int _id = (Integer) mDataList.get(arg2).get("_id");
			String  uuid = (String) mDataList.get(arg2).get("uuid");
			
			Intent intent = new Intent();
			intent.setClass(PayeeActivity.this, EditPayeeActivity.class);
			intent.putExtra("_id", _id);
			intent.putExtra("uuid", uuid);
			startActivityForResult(intent, 5);

		}
	};

	private OnItemLongClickListener mLongListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub

			final int _id = (Integer) mDataList.get(arg2).get("_id");
			final String uuid = (String) mDataList.get(arg2).get("uuid");

			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);
			diaListView = (ListView) dialogView.findViewById(R.id.dia_listview);
			mDialogItemAdapter = new DialogItemAdapter(PayeeActivity.this);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (arg2 == 0) {

//						List<Map<String, Object>> mList = PayeeDao
//								.selectPayeeRelate(PayeeActivity.this, _id);
//						if (mList.size() > 0) {
//
//							new AlertDialog.Builder(PayeeActivity.this)
//									.setTitle("Delete This Payee? ")
//									.setMessage(
//											" There have already been transactions " +
//											"related to this payee. Deleting a payee will" +
//											" also cause to delete all related transactions. Are you sure you want to delete it? ")
//									.setNegativeButton(
//											"No",
//											new DialogInterface.OnClickListener() {
//
//												@Override
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//													// TODO Auto-generated
//													// method stub
//													dialog.dismiss();
//													alertDialog.dismiss();
//												}
//
//											})
//									.setPositiveButton(
//											"Yes",
//											new DialogInterface.OnClickListener() {
//
//												@Override
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//													// TODO Auto-generated
//													// method stub
//													long id = PayeeDao
//															.deletePayee(
//																	PayeeActivity.this,
//																	_id, uuid, mDbxAcctMgr, mDatastore);
//													mHandler.post(mTask);
//													dialog.dismiss();
//													alertDialog.dismiss();
//													Log.v("mtest", ""+id);
//													if (id >0 && MainActivity.mItemPosition == 0) {
//														MainActivity.sqlChange = 1;
//													} 
//												}
//											}).show();
//
//						} else {

							long id = PayeeDao.deletePayee(PayeeActivity.this,
									_id, uuid, mDbxAcctMgr, mDatastore);
							mHandler.post(mTask);
							alertDialog.dismiss();
							
							if (id >0 && MainActivity.mItemPosition == 0) {
								MainActivity.sqlChange = 1;
//							} 

						}
					}

				}
			});
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PayeeActivity.this);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.add_menu, menu);
		// Menu itemMenu = (Menu) menu.findItem(R.id.navigation_but);
		// Menu itemMenu1 = (Menu) menu.findItem(R.id.action_add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_add:
			Intent intent = new Intent();
			intent.setClass(PayeeActivity.this, CreatPayeeActivity.class);
			startActivityForResult(intent, 4);
			break;
		case R.id.navigation_but:

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case 4:
			mHandler.post(mTask);
			break;
		case 5:
			mHandler.post(mTask);
			
			if (MainActivity.mItemPosition == 0) {
				MainActivity.sqlChange = 1;
			}
			
			break;
		}
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		
		if (mMap.containsKey("db_payee_table") || mMap.containsKey("db_category_table") ) {
			mHandler.post(mTask);
		}
		
	}
}
