package com.appxy.pocketexpensepro.setting.payee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.R.id;
import com.appxy.pocketexpensepro.setting.category.DialogItemAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class PayeeActivity extends Activity {
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
	protected void onCreate(Bundle savedInstanceState) {
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
		    int _id = (Integer)mDataList.get(arg2).get("_id");
		    
			Intent intent = new Intent();
			intent.setClass(PayeeActivity.this, EditPayeeActivity.class);
			intent.putExtra("_id", _id);
			startActivityForResult(intent, 5);
			
			
		}
	};
	
	private OnItemLongClickListener mLongListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			
			 final int _id = (Integer)mDataList.get(arg2).get("_id");
				
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
							
							long id = PayeeDao.deletePayee(PayeeActivity.this,_id);
							 
							mHandler.post(mTask);
							alertDialog.dismiss();
						}
						
					}
				});
				AlertDialog.Builder builder = new AlertDialog.Builder(PayeeActivity.this);
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
//		Menu itemMenu = (Menu) menu.findItem(R.id.navigation_but);
//		Menu itemMenu1 = (Menu) menu.findItem(R.id.action_add);
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
			break;
		}
	}
}
