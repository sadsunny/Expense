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
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortController;

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

public class AccountsFragment extends Fragment implements OnSyncFinishedListener{

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
	private Activity mActivity;
	private TextView notiTextView;
	private LinearLayout networthLayout;
	private LinearLayout outLayout;
	
	public AccountsFragment() {
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mAccountsListViewAdapter.notifyDataSetChanged();
	}
	
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
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
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
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.mInflater = inflater;
		View view = inflater.inflate(R.layout.fragment_account, container,
				false);
		
		getActivity().getActionBar().setTitle("Accounts");

		networthLayout =  (LinearLayout)view.findViewById(R.id.networthLayout);
		outLayout =  (LinearLayout)view.findViewById(R.id.outLayout);
		tranfer_linearLayout = (LinearLayout) view
				.findViewById(R.id.tranfer_linearLayout);
		tranfer_linearLayout.setOnClickListener(mClickListener);
		netTextView = (TextView) view.findViewById(R.id.net_worth_txt);
		outTextView = (TextView) view.findViewById(R.id.outstanding_txt);
		notiTextView= (TextView)view.findViewById(R.id.notice_txt);
		
		mListView = (DragSortListView) view.findViewById(R.id.mListview);
		mAccountsListViewAdapter = new AccountsListViewAdapter(mActivity);
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
				intent.setClass(mActivity, NetOutActivity.class);
				startActivityForResult(intent, 21);
			}
		});
		
		outLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("type", 1);
				intent.setClass(mActivity, NetOutActivity.class);
				startActivityForResult(intent, 21);
			}
		});
		
		
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		}
		
		return view;
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
				intent.setClass(mActivity, AccountTransferActivity.class);
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
			intent.setClass(mActivity, AccountToTransactionActivity.class);
			startActivityForResult(intent, 12);
		}
	};

	private OnItemLongClickListener listener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final int id = (Integer) mDataList.get(arg2).get("_id");
			final String  uuid = (String) mDataList.get(arg2).get("uuid");
			
			View dialogView = mInflater.inflate(R.layout.dialog_item_operation,
					null);

			String[] data = {"Edit","Delete","Edit Account List"};
			
			ListView diaListView = (ListView) dialogView
					.findViewById(R.id.dia_listview);
			DialogItemAdapter mDialogItemAdapter = new DialogItemAdapter(mActivity , data);
			diaListView.setAdapter(mDialogItemAdapter);
			diaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (arg2 == 0) {

						Intent intent = new Intent();
						intent.putExtra("_id", id);
						intent.setClass(mActivity, EditAccountActivity.class);
						startActivityForResult(intent, 8);
						alertDialog.dismiss();

					} else if (arg2 == 1) {
						int size = AccountDao.selectAccountRelate(getActivity(), id);
						if (size > 0) {
							
							new AlertDialog.Builder(getActivity())
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
											long row = AccountDao.deleteAccount(getActivity(), id, uuid ,MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
											mHandler.post(mTask);
											alertDialog.dismiss();
										}
									}).show();

						} else {
							long row = AccountDao.deleteAccount(getActivity(), id, uuid ,MainActivity.mDbxAcctMgr1, MainActivity.mDatastore1);
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

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
			mDataList = AccountDao.selectAccount(mActivity);

			for (Map<String, Object> iMap : mDataList) {
				int _id = (Integer) iMap.get("_id");
				String amount = (String) iMap.get("amount");
				BigDecimal b1 = new BigDecimal(amount);

				List<Map<String, Object>> mTemList = AccountDao
						.selectTransactionByAccount(mActivity, _id);
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

			List<Map<String, Object>> mOutList = AccountDao.selectTransactionOnClear(mActivity, 0);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		this.mMenu = menu;
		inflater.inflate(R.menu.confirm, menu);

		item0 = mMenu.findItem(R.id.confirm);// 设置actionbar中的按钮不可见
		item0.setVisible(false);
		item1 = mMenu.findItem(R.id.action_add);

		super.onCreateOptionsMenu(menu, inflater);
	}

	public void accountSort(List<Map<String, Object>> mData) { // 更新数据库排序字段

		int size = mData.size();
		int a = 0;
		String[] mKeyString = new String[size];
		for (Map<String, Object> iMap : mData) {
			mKeyString[a] = iMap.get("_id").toString();
			a++;
		}

		SQLiteDatabase db = AccountDao.getConnection(mActivity);
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
			intent.setClass(mActivity, CreatNewAccountActivity.class);
			startActivityForResult(intent, 2);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		Toast.makeText(mActivity, "Dropbox sync successed",Toast.LENGTH_SHORT).show();
		mHandler.post(mTask);
		
	}

}
