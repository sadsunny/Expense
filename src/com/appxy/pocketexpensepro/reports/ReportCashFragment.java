package com.appxy.pocketexpensepro.reports;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
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

public class ReportCashFragment extends Fragment {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private FragmentActivity mActivity;
	private Button changeButton;
	private TextView dateTextView;
	private LinearLayout lineLayout;
	private ListView mListView;
	private Thread mThread;
	private ArrayList<Double> expenseArrayList;
	private ArrayList<Double> incomeArrayList;
	private ArrayList<String> labelArrayList;
	private int mCategoryType = 0;
	
	public static MenuItem item;
	private Button rangButton;
	private LayoutInflater mInflater;
	private PopupWindow mPopupWindow;
	private ListView lvPopupList;
	private Button startButton;
	private Button endButton;
	
	private int sYear;
	private int sMonth ;
	private int sDay;
	private int eYear;
	private int eMonth ;
	private int eDay;
	private long startDate;
	private long endDate;
	
	private GraphicalView lChart;
	private XYMultipleSeriesDataset dataset;
	private ReportCashListViewAdapter mAdapter;
	private List<Map<String, Object>> mExpenseData;
	private List<Map<String, Object>> mIncomeData;
	private int rangeType =  0;
	
	private final static long DAYMILLIS = 86400000L - 1L;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				lineLayout.removeAllViews();

				if (mCategoryType == 0) {
					dataset = getDataset(expenseArrayList);
					mAdapter.setAdapterDate(mExpenseData);
					
				} else {
					dataset = getDataset(incomeArrayList);
					mAdapter.setAdapterDate(mIncomeData);
				}
				mAdapter.notifyDataSetChanged();
				
				lChart = (GraphicalView) ChartFactory.getLineChartView(
						mActivity, dataset, getRenderer(labelArrayList));
				lChart.repaint();
				lineLayout.addView(lChart, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

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

	
	public ReportCashFragment() {

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
		View view = inflater.inflate(R.layout.fragment_report_cash,container, false);
		mInflater = inflater;
		
		iniPopupWindow();
		
		mExpenseData = new ArrayList<Map<String,Object>>();
		mIncomeData = new ArrayList<Map<String,Object>>();
		
		changeButton = (Button) view.findViewById(R.id.change_btn);
		dateTextView  = (TextView) view.findViewById(R.id.date_txt);
		lineLayout = (LinearLayout) view.findViewById(R.id.LineChartLayout);
		mListView = (ListView) view.findViewById(R.id.mListView);
		mListView.setDividerHeight(0);
		mAdapter = new ReportCashListViewAdapter(mActivity);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("rangType", rangeType);
				intent.putExtra("dateLong", (Long)mExpenseData.get(arg2).get("dateLong"));
				intent.setClass(mActivity, ReCashListActivity.class);
				startActivityForResult(intent, 1);
				
			}
		});
		
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

		expenseArrayList = new ArrayList<Double>();
		incomeArrayList = new ArrayList<Double>();
		labelArrayList = new ArrayList<String>();
		
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
			List<Map<String, Object>> mDataList = OverViewDao.selectTransactionByTimeBE(mActivity,MainActivity.startDate, MainActivity.endDate);
			filterData(mDataList);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};
	
	private XYMultipleSeriesDataset getDataset( ArrayList<Double> dataList) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series;
		if (mCategoryType == 0) {
			series = new XYSeries("Expense");
		} else {
			series = new XYSeries("Income");
		}
		
		for (int k = 0; k < dataList.size(); k++) {

			series.add(k, dataList.get(k));
		}
		dataset.addSeries(series);

		return dataset;
	}

	public XYMultipleSeriesRenderer getRenderer(ArrayList<String> xLable) {

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);// 设置坐标轴标题文本大小
		renderer.setChartTitleTextSize(20); // 设置图表标题文本大小
		renderer.setLabelsTextSize(18); // 设置轴标签文本大小
		renderer.setLegendTextSize(18); // 设置图例文本大小
		renderer.setXLabelsColor(Color.parseColor("#acadb2"));
		renderer.setYLabelsColor(0,Color.parseColor("#acadb2"));
		renderer.setMargins(new int[] { MEntity.dip2px(mActivity, 10),
				MEntity.dip2px(mActivity, 25), MEntity.dip2px(mActivity, 8), 0 }); // 设置4边留白上左下右
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setZoomEnabled(false, false); // 设置是否可缩放XY
		renderer.setShowGrid(true);
		renderer.setPanEnabled(true, false);// 设置XY轴的滑动
		
		XYSeriesRenderer r = new XYSeriesRenderer();
		FillOutsideLine fill = new FillOutsideLine(
				FillOutsideLine.Type.BOUNDS_ALL);
		if (mCategoryType == 0) {
			r.setColor(Color.argb(255, 243, 61, 36));
			r.setPointStyle(PointStyle.CIRCLE);
			// r.setLineWidth(Dp2Px(context, 1)); //设置线条宽度
			r.setFillPoints(true);
			
			fill.setColor(Color.argb(39, 243, 61, 36));
			r.addFillOutsideLine(fill);
			
			renderer.addSeriesRenderer(r);
			
		} else {
			
			r = new XYSeriesRenderer();
			r.setColor(Color.argb(255, 102, 175, 54));
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(true);
			
			fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
			fill.setColor(Color.argb(39, 102, 175, 54));
			r.addFillOutsideLine(fill);
			
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(7);
		renderer.setXLabels(0);

		for (int i = 0; i < xLable.size(); i++) {
			// 添加X轴便签
			renderer.addXTextLabel(i, xLable.get(i));
		}
		renderer.setPanLimits(new double[] { 0, xLable.size() + 0.4, 0,
				xLable.size() }); // 设置左右拉伸的界限
		renderer.setShowCustomTextGrid(true);
		return renderer;
	}
	
	private void filterData(List<Map<String, Object>> mData) {
		expenseArrayList.clear();
		incomeArrayList.clear();
		labelArrayList.clear();
		mExpenseData.clear();
		mIncomeData.clear();

		if (MainActivity.rangePositon == 4 || MainActivity.rangePositon == 5) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(MainActivity.startDate);

			do {

				BigDecimal ex = new BigDecimal("0");
				BigDecimal in = new BigDecimal("0");

				for (Map<String, Object> iMap : mData) {
					String amount = (String) iMap.get("amount");
					long dateTime = (Long) iMap.get("dateTime");
					int expenseAccount = (Integer) iMap.get("expenseAccount");
					int incomeAccount = (Integer) iMap.get("incomeAccount");

					BigDecimal amountBig = new BigDecimal(amount);

					if (MEntity.getFirstDayOfMonthMillis(calendar
							.getTimeInMillis()) <= dateTime
							&& MEntity.getLastDayOfMonthMillis(calendar
									.getTimeInMillis()) > dateTime) {

						if (expenseAccount > 0) {
							ex = ex.add(amountBig);
						}

						if (incomeAccount > 0) {
							in = in.add(amountBig);
						}

					}

				}
				expenseArrayList.add(ex.doubleValue());
				incomeArrayList.add(in.doubleValue());
				labelArrayList.add(turnDateToMonthString(calendar
						.getTimeInMillis()));
				Map<String, Object> mMap = new HashMap<String, Object>();
				mMap.put("date", turnMillsToMonth(calendar.getTimeInMillis()));
				mMap.put("dateLong", calendar.getTimeInMillis());
				mMap.put("amount", ex.doubleValue());
				mExpenseData.add(mMap);
				
				Map<String, Object> mMap1 = new HashMap<String, Object>();
				mMap1.put("date", turnMillsToMonth(calendar.getTimeInMillis()));
				mMap1.put("dateLong", calendar.getTimeInMillis());
				mMap1.put("amount", in.doubleValue());
				mIncomeData.add(mMap1);
				
				calendar.add(Calendar.MONTH, 1);
			} while (calendar.getTimeInMillis() <= MainActivity.endDate);

		} else {

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(MainActivity.startDate);

			do {

				BigDecimal ex = new BigDecimal("0");
				BigDecimal in = new BigDecimal("0");

				for (Map<String, Object> iMap : mData) {
					String amount = (String) iMap.get("amount");
					long dateTime = (Long) iMap.get("dateTime");
					int expenseAccount = (Integer) iMap.get("expenseAccount");
					int incomeAccount = (Integer) iMap.get("incomeAccount");

					BigDecimal amountBig = new BigDecimal(amount);

					if (dateTime >= calendar.getTimeInMillis() && dateTime < (calendar.getTimeInMillis()+DAYMILLIS)) {

						if (expenseAccount > 0) {
							ex = ex.add(amountBig);
						}

						if (incomeAccount > 0) {
							in = in.add(amountBig);
						}

					}

				}
				expenseArrayList.add(ex.doubleValue());
				incomeArrayList.add(in.doubleValue());
				labelArrayList
						.add(turnDateToString(calendar.getTimeInMillis()));
				Map<String, Object> mMap = new HashMap<String, Object>();
				mMap.put("date", turnMillsToDate(calendar.getTimeInMillis()));
				mMap.put("dateLong", calendar.getTimeInMillis());
				mMap.put("amount", ex.doubleValue());
				mExpenseData.add(mMap);
				
				Map<String, Object> mMap1 = new HashMap<String, Object>();
				mMap1.put("date", turnMillsToDate(calendar.getTimeInMillis()));
				mMap1.put("dateLong", calendar.getTimeInMillis());
				mMap1.put("amount", in.doubleValue());
				mIncomeData.add(mMap1);
				
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			} while (calendar.getTimeInMillis() <= MainActivity.endDate);

		}

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
	
	public String turnMillsToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	public String turnMillsToMonth(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
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
				rangeType = arg2;
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
		}else if (rangPosition == 6)  {
			
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
								new ContextThemeWrapper(
										mActivity,
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
								new ContextThemeWrapper(
										mActivity,
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
							public void onClick(DialogInterface dialog,
									int which) {
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
							public void onClick(DialogInterface dialog,
									int which) {
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 1:
			Log.v("mtest", "onActivityResult11");
			if (data != null) {
				mHandler.post(mTask);
				Log.v("mtest", "onActivityResult22");
			}
			break;
		}
		Log.v("mtest", "onActivityResult");
	}
	
	
}
