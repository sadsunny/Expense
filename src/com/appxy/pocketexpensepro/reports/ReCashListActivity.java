package com.appxy.pocketexpensepro.reports;

import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.OverViewDao;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class ReCashListActivity extends Activity {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private Thread mThread;

	private ListView mListView;
	private long startDate;
	private long endDate;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				break;

			case MSG_FAILURE:
				Toast.makeText(ReCashListActivity.this, "Exception",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recashlist);
		mListView = (ListView) findViewById(R.id.mListView);
		mListView.setDividerHeight(0);
		
		Intent intent = getIntent();
		int rangType = intent.getIntExtra("rangType", 0);
		long dateLong = intent.getLongExtra("dateLong", 0);

		if (rangType == 4 || rangType ==  5) {
			startDate = MEntity.getFirstDayOfMonthMillis(dateLong);
			endDate = MEntity.getLastDayOfMonthMillis(dateLong);
		} else {
			startDate = dateLong;
			endDate = dateLong;
		}
		
		
		mThread = new Thread(mTask);
		mThread.start();

	}
	
	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<Map<String, Object>> mDataList = OverViewDao.selectTransactionByTimeBE(ReCashListActivity.this,startDate, endDate);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	

}
