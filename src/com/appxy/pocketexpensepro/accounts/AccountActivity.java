package com.appxy.pocketexpensepro.accounts;

/*
 * 发出ResultCode 2
 */
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeActivity;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortController;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;

public class AccountActivity extends BaseHomeActivity {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	public static DragSortListView mListView;
	public static AccountsListViewAdapter mAccountsListViewAdapter;
	private List<Map<String, Object>> mDataList;
	private Thread mThread;
	private double netWorth;
	private double outDouble;

	private TextView netTextView;
	private TextView outTextView;

	public static SectionController c;
	private LayoutInflater mInflater;
	public static int sortCheck = 0; // 是否正在排序
	private AlertDialog alertDialog;

	private Menu mMenu;
	public static MenuItem item0;
	public static MenuItem item1;

	private LinearLayout tranfer_linearLayout;
	private TextView notiTextView;
	private LinearLayout networthLayout;
	private LinearLayout outLayout;
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:

				outTextView.setText(MEntity.doublepoint2str(String.valueOf(outDouble)));
				if (mDataList != null && mDataList.size() > 0) {
					mListView.setVisibility(View.VISIBLE);
					notiTextView.setVisibility(View.INVISIBLE);
					
					mListView.setLongClickable(true);

					mAccountsListViewAdapter.setAdapterDate(mDataList);
					mAccountsListViewAdapter.sortIsChecked(-1);
					mAccountsListViewAdapter.notifyDataSetChanged();

					BigDecimal b1 = new BigDecimal("0");
					for (Map<String, Object> iMap : mDataList) {
						String amount = (String) iMap.get("lastAmount");
						BigDecimal b2 = new BigDecimal(amount);
						b1 = b1.add(b2);
					}
					
					netWorth = b1.doubleValue();
					netTextView.setText(MEntity.doublepoint2str(String.valueOf(netWorth)));
				}else {
					mListView.setVisibility(View.INVISIBLE);
					notiTextView.setVisibility(View.VISIBLE);
				}

				break;

			case MSG_FAILURE:
				Toast.makeText(AccountActivity.this, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				
				Map<String, Object> mdata = mDataList.remove(from);
				mDataList.add(to, mdata);
				mAccountsListViewAdapter.notifyDataSetChanged();

			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_account);

		mInflater = LayoutInflater.from(AccountActivity.this);
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		networthLayout =  (LinearLayout)findViewById(R.id.networthLayout);
		outLayout =  (LinearLayout)findViewById(R.id.outLayout);
		tranfer_linearLayout = (LinearLayout)findViewById(R.id.tranfer_linearLayout);
		tranfer_linearLayout.setOnClickListener(mClickListener);
		netTextView = (TextView)findViewById(R.id.net_worth_txt);
		outTextView = (TextView)findViewById(R.id.outstanding_txt);
		notiTextView= (TextView)findViewById(R.id.notice_txt);
		
		mListView = (DragSortListView)findViewById(R.id.mListview);
		mAccountsListViewAdapter = new AccountsListViewAdapter(this);
		mListView.setAdapter(mAccountsListViewAdapter);
		mListView.setDividerHeight(0);
		mListView.setOnItemLongClickListener(listener);
		mListView.setOnItemClickListener(itemListener);
		
		
		networthLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("type", 0);
				intent.setClass(AccountActivity.this, NetOutActivity.class);
				startActivityForResult(intent, 21);
			}
		});
		
		outLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("type", 1);
				intent.setClass(AccountActivity.this, NetOutActivity.class);
				startActivityForResult(intent, 21);
			}
		});

		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}

		Intent intent = new Intent();
		intent.putExtra("done", 1);
		setResult(14, intent);
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHandler.post(mTask);
		Log.v("mtag", "*--------------*+++++++++++++++*");
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		sortCheck = 0;
		super.onDestroy();
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tranfer_linearLayout:

				Intent intent = new Intent();
				intent.setClass(AccountActivity.this, AccountTransferActivity.class);
				startActivityForResult(intent, 11);
				break;
			}
		}
	};

	private OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int _id = (Integer) mDataList.get(arg2).get("_id");
			String accName = (String)mDataList.get(arg2).get("accName");
			Intent intent = new Intent();
			intent.putExtra("_id", _id);
			intent.putExtra("accName", accName);
			intent.setClass(AccountActivity.this, AccountToTransactionActivity.class);
			startActivityForResult(intent, 12);
		}
	};

	private OnItemLongClickListener listener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int id = (Integer) mDataList.get(arg2).get("_id");
			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = {"Edit","Delete","Edit Account List"};
			
			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(AccountActivity.this , data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (arg2 == 0) {

						Intent intent = new Intent();
						intent.putExtra("_id", id);
						intent.setClass(AccountActivity.this, EditAccountActivity.class);
						startActivityForResult(intent, 8);
						alertDialog.dismiss();

					} else if (arg2 == 1) {
						int size = AccountDao.selectAccountRelate(AccountActivity.this, id);
						if (size > 0) {
							
							new AlertDialog.Builder(AccountActivity.this)
							.setTitle("Delete This Account? ")
							.setMessage(
									" Deleting an account will cause to remove all its transactions. Are you sure you want to delete it? ")
							.setNegativeButton(
									"No",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub
											dialog.dismiss();
											alertDialog.dismiss();
										}

									})
							.setPositiveButton(
									"Yes",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub
											long row = AccountDao.deleteAccount(AccountActivity.this, id);
											mHandler.post(mTask);
											alertDialog.dismiss();
										}
									}).show();

						} else {
							long row = AccountDao.deleteAccount(AccountActivity.this, id);
							mHandler.post(mTask);
							alertDialog.dismiss();
						}
						

					} else if (arg2 == 2) {
						sortCheck = 1;
						mAccountsListViewAdapter.sortIsChecked(1);
						mAccountsListViewAdapter.notifyDataSetChanged();
						mListView.setLongClickable(false);

						c = new SectionController(mListView,
								mAccountsListViewAdapter);
						mAccountsListViewAdapter.sortIsChecked(1);
						c.setSortEnabled(true);
						mListView.setDropListener(onDrop);
						mListView.setFloatViewManager(c);
						mListView.setOnTouchListener(c);
						item0.setVisible(true);
						item1.setVisible(false);

						alertDialog.dismiss();
					}

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
			builder.setView(dialogView);
			alertDialog = builder.create();
			alertDialog.show();

			return true;
		}
	};

	public class SectionController extends DragSortController { // list item 拖曳

		private AccountsListViewAdapter mAdapter;

		DragSortListView mDslv;

		public SectionController(DragSortListView dslv,
				AccountsListViewAdapter adapter) {
			super(dslv);
			setDragHandleId(R.id.Sort_ImageView);
			mDslv = dslv;
			mAdapter = adapter;
		}

		@Override
		public int startDragPosition(MotionEvent ev) {
			int res = super.dragHandleHitPosition(ev);
			int width = mDslv.getWidth();
			return res;
		}

		@Override
		public View onCreateFloatView(int position) {

			View v = mAdapter.getView(position, null, mDslv);
			v.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.bg_handle_section1));
			v.getBackground().setLevel(10000);
			return v;
		}

		private int origHeight = -1;

		@Override
		public void onDragFloatView(View floatView, Point floatPoint,
				Point touchPoint) {

		}

		@Override
		public void onDestroyFloatView(View floatView) {
			// do nothing; block super from crashing
		}

	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mDataList = AccountDao.selectAccount(AccountActivity.this);

			for (Map<String, Object> iMap : mDataList) {
				int _id = (Integer) iMap.get("_id");
				String amount = (String) iMap.get("amount");
				BigDecimal b1 = new BigDecimal(amount);

				List<Map<String, Object>> mTemList = AccountDao
						.selectTransactionByAccount(AccountActivity.this, _id);
				BigDecimal b0 = new BigDecimal(0);
				for (Map<String, Object> tMap : mTemList) {

					String tAmount = (String) tMap.get("amount");
					BigDecimal b2 = new BigDecimal(tAmount);

					int expenseAccount = (Integer) tMap.get("expenseAccount");
					int incomeAccount = (Integer) tMap.get("incomeAccount");

					if (expenseAccount == _id) {
						b0 = b0.subtract(b2);
					} else if (incomeAccount == _id) {
						b0 = b0.add(b2);
					}

				}

				b1 = b1.add(b0);
				double lastAmount = b1.doubleValue();
				iMap.put("lastAmount", lastAmount + "");
			}

			List<Map<String, Object>> mOutList = AccountDao.selectTransactionOnClear(AccountActivity.this, 0);
			BigDecimal b1 = new BigDecimal("0");
			for (Map<String, Object> iMap : mOutList) {
				
				String amount = (String) iMap.get("amount");
				int expenseAccount = (Integer) iMap.get("expenseAccount");
				int incomeAccount = (Integer) iMap.get("incomeAccount");
				BigDecimal b2 = new BigDecimal(amount);
				
				if (expenseAccount > 0 && incomeAccount <= 0) {
					b1 = b1.subtract(b2);
				} else if (incomeAccount > 0 && expenseAccount <= 0) {
					b1 = b1.add(b2);
				}
			}
			outDouble = b1.doubleValue();

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();

		}
	};


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.add_menu, menu);
		MenuInflater inflater = getMenuInflater();
		this.mMenu = menu;
		inflater.inflate(R.menu.confirm, menu);

		item0 = mMenu.findItem(R.id.confirm);// 设置actionbar中的搜索按钮不可见
		item0.setVisible(false);
		item1 = mMenu.findItem(R.id.action_add);
		
		return super.onCreateOptionsMenu(menu);
	}

	public void accountSort(List<Map<String, Object>> mData) { // 更新数据库排序字段

		int size = mData.size();
		int a = 0;
		String[] mKeyString = new String[size];
		for (Map<String, Object> iMap : mData) {
			mKeyString[a] = iMap.get("_id").toString();
			a++;
		}

		SQLiteDatabase db = AccountDao.getConnection(AccountActivity.this);
		db.beginTransaction();// 开始事务
		try {
			for (int i = 0; i < size; i++) {

				ContentValues cv = new ContentValues();
				cv.put("orderIndex", i);
				db.update("Accounts", cv, "_id = ?",
						new String[] { mKeyString[i] });
			}
			db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
											// 时提交当前事务，如果不调用此方法会回滚事务
		} finally {
			db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
		}
		db.close();

	}
	
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 2:

			if (data != null) {
				// mThread = new Thread(mTask);
				// mThread.start();
				mHandler.post(mTask);
			}
			break;
		case 8:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;
			
		case 11:

			if (data != null) {
				mHandler.post(mTask);
			}
			break;
			
		case 12:

			if (data != null) {
				mHandler.post(mTask);
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
			
		case R.id.confirm:

			c.setSortEnabled(false);
			sortCheck = 0;
			item0.setVisible(false);
			item1.setVisible(true);
			mListView.setLongClickable(true);
			accountSort(mDataList);
			mHandler.post(mTask);

			return true;

		case R.id.action_add:
			Intent intent = new Intent();
			intent.setClass(AccountActivity.this, CreatNewAccountActivity.class);
			startActivityForResult(intent, 2);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}
