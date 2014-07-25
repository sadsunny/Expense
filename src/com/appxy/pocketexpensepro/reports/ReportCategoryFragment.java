package com.appxy.pocketexpensepro.reports;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.OverViewDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ReportCategoryFragment extends Fragment {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;

	private FragmentActivity mActivity;
	public static MenuItem item;
	private Button rangButton;
	private LayoutInflater mInflater;
	private PopupWindow mPopupWindow;
	private ListView lvPopupList;
	private Button startButton;
	private Button endButton;

	private int sYear;
	private int sMonth;
	private int sDay;
	private int eYear;
	private int eMonth;
	private int eDay;
	private long startDate;
	private long endDate;

	private Thread mThread;
	private List<Map<String, Object>> mCategoryDataList;
	private List<double[]> values;
	private List<String[]> titles;
	private int mCategoryType = 0;
	private List<Map<String, Object>> mCategoryDataListAll;
	private double total;
	private GraphicalView pChart;
	private LinearLayout pieLayout;
	private ListView mListView;
	private Button changeButton;
	private TextView dateTextView;
	private ReportCategoryListViewAdapter mAdapter;
	  
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				pieLayout.removeAllViews();
				DefaultRenderer renderer = buildCategoryRenderer();
				pChart = (GraphicalView) ChartFactory.getDoughnutChartView(
						mActivity, buildMultipleCategoryDataset(), renderer);
				pChart.repaint();
				pieLayout.addView(pChart, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				 mAdapter.setAdapterDate(mCategoryDataList, mCategoryType,
				 total);
				 mAdapter.notifyDataSetChanged();
				String dateString = MEntity
						.turnToDateString(MainActivity.startDate)
						+ " - "
						+ MEntity.turnToDateString(MainActivity.endDate);
				dateTextView.setText(dateString);

				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public ReportCategoryFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (FragmentActivity) activity;
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
		View view = inflater.inflate(R.layout.fragment_report_category,
				container, false);
		mInflater = inflater;
		iniPopupWindow();
		mCategoryDataList = new ArrayList<Map<String, Object>>();
		
		dateTextView = (TextView) view.findViewById(R.id.date_txt);
		pieLayout = (LinearLayout) view.findViewById(R.id.PieChartLayout);
		mListView = (ListView) view.findViewById(R.id.mListView);
		changeButton = (Button) view.findViewById(R.id.change_btn);
		
		mAdapter = new ReportCategoryListViewAdapter(mActivity);
		mListView.setAdapter(mAdapter);
		
		changeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				mCategoryType = (mCategoryType == 0) ? 1 : 0;
				if (mCategoryType == 0) {
					changeButton.setText("Expense");
				} else {
					changeButton.setText("Income");
				}
				mHandler.post(mTask);
			}
		});
		
		mCategoryDataList = new ArrayList<Map<String, Object>>();
		values = new ArrayList<double[]>();
		titles = new ArrayList<String[]>();
		
		if (mThread == null) {
			mThread = new Thread(mTask);
			mThread.start();
		} else {
			mHandler.post(mTask);
		}

		return view;
	}

	public Runnable mTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mCategoryDataListAll = OverViewDao.selectSumCategory(mActivity,
					MainActivity.startDate, MainActivity.endDate);
			mCategoryDataList.clear();
			for (Map<String, Object> iMap : mCategoryDataListAll) {
				int categoryType = (Integer) iMap.get("categoryType");
				if (mCategoryType == categoryType) {
					mCategoryDataList.add(iMap);
				}
			}

			Collections.sort(mCategoryDataList,
					new MEntity.MapComparatorAmount());

			values.clear();
			titles.clear();

			double[] valueDoubles = new double[mCategoryDataList.size()];
			String[] titleStrings = new String[mCategoryDataList.size()];
			int k = 0;
			BigDecimal b0 = new BigDecimal("0");
			if (mCategoryDataList.size() == 0) {
				valueDoubles = new double[1];
				titleStrings = new String[1];
				valueDoubles[0] = 1;
				titleStrings[0] = "";

			} else {
				for (Map<String, Object> iMap : mCategoryDataList) {
					String title = (String) iMap.get("categoryName");
					double sum = (Double) iMap.get("sum");
					BigDecimal b1 = new BigDecimal(sum);
					b0 = b0.add(b1);
					valueDoubles[k] = sum;
					titleStrings[k] = title;
					k++;
				}
			}
			total = b0.doubleValue();
			values.add(valueDoubles);
			titles.add(titleStrings);

			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	protected MultipleCategorySeries buildMultipleCategoryDataset() {// 圆环设值
		MultipleCategorySeries series = new MultipleCategorySeries("");

		int k = 0;
		for (double[] value : values) {
			series.add("", titles.get(k), value);
			k++;
		}
		return series;
	}

	protected DefaultRenderer buildCategoryRenderer() {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setLabelsColor(Color.GRAY);
		renderer.setStartAngle(180);

		renderer.setDisplayValues(false);
		renderer.setPanEnabled(false);
		renderer.setShowLabels(false);
		renderer.setShowLegend(false);
		renderer.setZoomEnabled(false);
		renderer.setAntialiasing(true);

		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(10);
		renderer.setZoomRate(13);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		renderer.setScale(1.43f);

		if (mCategoryDataList.size() == 0) {

			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			if (mCategoryType == 0) {
				r.setColor(Common.ExpenseColors[0]);
			} else {
				r.setColor(Common.IncomeColors[0]);
			}
			renderer.addSeriesRenderer(r);

		} else {
			for (int i = 0; i < mCategoryDataList.size(); i++) {
				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				if (mCategoryType == 0) {
					r.setColor(Common.ExpenseColors[i % 10]);
				} else {
					r.setColor(Common.IncomeColors[i % 10]);
				}
				renderer.addSeriesRenderer(r);
			}
		}

		return renderer;
	}

	public String turnDateToMonthString(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public String turnDateToString(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		String theDate = sdf.format(date2);
		return theDate;
	}

	private List<Map<String, String>> getPopupData() {

		List<Map<String, String>> moreList = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		map = new HashMap<String, String>();
		map.put("item", "This Month");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "Last Month");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "This Quarter");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "Last Quarter");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "This Year");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "Last Year");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "Custom Range");
		moreList.add(map);

		return moreList;
	}

	private void iniPopupWindow() {

		View layout = mInflater.inflate(R.layout.popupwindow_drop_list, null);
		lvPopupList = (ListView) layout.findViewById(R.id.mList);
		mPopupWindow = new PopupWindow(layout);
		mPopupWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件
		Log.v("mtest", "iniPopupWindow");

		PopupListViewAdapter mAdapter = new PopupListViewAdapter(mActivity);
		mAdapter.setAdapterDate(getPopupData());
		lvPopupList.setAdapter(mAdapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				MainActivity.rangePositon = arg2;
				rangButton.setText(getPopupData()
						.get(MainActivity.rangePositon).get("item"));
				setRangDate(arg2);
			}
		});

		// 控制popupwindow的宽度和高度自适应
		lvPopupList.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		mPopupWindow.setWidth(lvPopupList.getMeasuredWidth());
		mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

		// 控制popupwindow点击屏幕其他地方消失
		mPopupWindow.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.view_dropshadow));// 设置背景图片，不能在布局中设置，要通过代码来设置
		mPopupWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上
	}

	public void setRangDate(int rangPosition) {

		if (rangPosition == 0) {
			Calendar c0 = Calendar.getInstance();
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c0
					.getTimeInMillis());
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c0
					.getTimeInMillis());
			mHandler.post(mTask);
		} else if (rangPosition == 1) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MONTH, -1);
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c1
					.getTimeInMillis());
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c1
					.getTimeInMillis());
			mHandler.post(mTask);
		} else if (rangPosition == 2) {
			Calendar c2 = Calendar.getInstance();
			GetQuarter(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH));
			Log.v("mtest", "Calendar.MONTH" + Calendar.MONTH);
			mHandler.post(mTask);
		} else if (rangPosition == 3) {
			Calendar c3 = Calendar.getInstance();
			c3.add(Calendar.MONTH, -3);
			GetQuarter(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH));
			mHandler.post(mTask);
		} else if (rangPosition == 4) {
			Calendar c4 = Calendar.getInstance();
			c4.set(Calendar.MONTH, 0);
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c4
					.getTimeInMillis());
			c4.set(Calendar.MONTH, 11);
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c4
					.getTimeInMillis());
			mHandler.post(mTask);
		} else if (rangPosition == 5) {

			Calendar c5 = Calendar.getInstance();
			c5.add(Calendar.YEAR, -1);
			c5.set(Calendar.MONTH, 0);
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c5
					.getTimeInMillis());
			c5.set(Calendar.MONTH, 11);
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c5
					.getTimeInMillis());
			mHandler.post(mTask);
		} else if (rangPosition == 6) {

			View view = mInflater.inflate(R.layout.dialog_custom_range, null);
			Calendar c1 = Calendar.getInstance();
			c1.setTimeInMillis(MainActivity.startDate);
			sYear = c1.get(Calendar.YEAR);
			sMonth = c1.get(Calendar.MONTH);
			sDay = c1.get(Calendar.DAY_OF_MONTH);

			Calendar c2 = Calendar.getInstance();
			c2.setTimeInMillis(MainActivity.endDate);
			eYear = c2.get(Calendar.YEAR);
			eMonth = c2.get(Calendar.MONTH);
			eDay = c2.get(Calendar.DAY_OF_MONTH);

			startButton = (Button) view.findViewById(R.id.start_btn);
			endButton = (Button) view.findViewById(R.id.end_btn);
			startButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {

					// TODO Auto-generated method stub
					DatePickerDialog DPD = new DatePickerDialog( // 改变theme
							new ContextThemeWrapper(mActivity,
									android.R.style.Theme_Holo_Light),
							mDateSetListenerStart, sYear, sMonth, sDay);
					DPD.setTitle("EndDate");
					DPD.show();

				}
			});

			endButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub

					DatePickerDialog DPD = new DatePickerDialog( // 改变theme
							new ContextThemeWrapper(mActivity,
									android.R.style.Theme_Holo_Light),
							mDateSetListenerEnd, eYear, eMonth, eDay);
					DPD.setTitle("EndDate");
					DPD.show();

				}
			});

			updateDisplayStart();
			updateDisplayEnd();

			AlertDialog.Builder mBuilder5 = new AlertDialog.Builder(mActivity);
			mBuilder5.setTitle("Range");
			mBuilder5.setView(view);
			mBuilder5.setPositiveButton("Done",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							MainActivity.startDate = startDate;
							MainActivity.endDate = endDate;
							mHandler.post(mTask);
						}
					});
			mBuilder5.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});

			mBuilder5.create().show();

		}
	}

	private int GetQuarter(int year, int month) {
		Calendar c = Calendar.getInstance();

		int mQuarter = 0;
		if (1 <= month && month <= 3) {
			mQuarter = 1;
		} else if (4 <= month && month <= 6) {
			mQuarter = 2;
		} else if (7 <= month && month <= 9) {
			mQuarter = 3;
		} else if (10 <= month && month <= 12) {
			mQuarter = 4;
		}

		if (mQuarter == 1) {
			MainActivity.startDate = getFirstDayOfMonthMillis(year, 0 + 1);
			MainActivity.endDate = getLastDayOfMonthMillis(year, 2 + 1);
		} else if (mQuarter == 2) {
			MainActivity.startDate = getFirstDayOfMonthMillis(year, 3 + 1);
			MainActivity.endDate = getLastDayOfMonthMillis(year, 5 + 1);
		} else if (mQuarter == 3) {
			MainActivity.startDate = getFirstDayOfMonthMillis(year, 6 + 1);
			MainActivity.endDate = getLastDayOfMonthMillis(year, 8 + 1);
		} else if (mQuarter == 4) {
			MainActivity.startDate = getFirstDayOfMonthMillis(year, 9 + 1);
			MainActivity.endDate = getLastDayOfMonthMillis(year, 11 + 1);
		}

		return mQuarter;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			sYear = year;
			sMonth = monthOfYear;
			sDay = dayOfMonth;
			updateDisplayStart();
		}
	};

	private void updateDisplayStart() {
		// TODO Auto-generated method stub

		String mdateString = (new StringBuilder().append(sMonth + 1)
				.append("-").append(sDay).append("-").append(sYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(mdateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startDate = c.getTimeInMillis();

		Date date = new Date(startDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
		startButton.setText(sdf.format(date));
	}

	private void updateDisplayEnd() {
		// TODO Auto-generated method stub

		String mdateString = (new StringBuilder().append(eMonth + 1)
				.append("-").append(eDay).append("-").append(eYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(mdateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		endDate = c.getTimeInMillis();

		Date date = new Date(endDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
		endButton.setText(sdf.format(date));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			eYear = year;
			eMonth = monthOfYear;
			eDay = dayOfMonth;
			updateDisplayEnd();
		}
	};

	public long getLastDayOfMonthMillis(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());
		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c.getTimeInMillis();

	}

	public long getFirstDayOfMonthMillis(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());

		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.dropmenu_menu, menu);
		item = menu.findItem(R.id.rang_item);

		LinearLayout viewGroup = (LinearLayout) menu.findItem(R.id.rang_item)
				.getActionView();
		rangButton = (Button) viewGroup.findViewById(R.id.rang_btn);
		rangButton.setText(getPopupData().get(MainActivity.rangePositon).get(
				"item"));
		rangButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mPopupWindow.showAsDropDown(rangButton);

			}
		});

	}
	
	
}
