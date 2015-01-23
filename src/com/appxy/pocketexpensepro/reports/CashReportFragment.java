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
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.expinterface.OnSyncFinishedListener;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
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

public class CashReportFragment extends Fragment implements
		OnSyncFinishedListener {
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private FragmentActivity mActivity;
	private TextView dateTextView;
	private LinearLayout barLayout;
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
	private int sMonth;
	private int sDay;
	private int eYear;
	private int eMonth;
	private int eDay;
	private long startDate;
	private long endDate;

	private GraphicalView barChart;
	private XYMultipleSeriesDataset dataset;
	private CashReportListAdapter mAdapter;
	private List<Map<String, Object>> mAllData;
	private int rangeType = 0;

	private final static long DAYMILLIS = 86400000L - 1L;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				barLayout.removeAllViews();

				mAdapter.setAdapterDate(mAllData);
				mAdapter.notifyDataSetChanged();

				dataset = getBarDataset();

				barChart = (GraphicalView) ChartFactory.getBarChartView(
						mActivity, dataset, getBarRenderer(labelArrayList),
						Type.DEFAULT);

				barChart.repaint();
				barLayout.addView(barChart, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				String dateString = MEntity
						.turnToDateString(MainActivity.startCashDate)
						+ " - "
						+ MEntity.turnToDateString(MainActivity.endCashDate);
				dateTextView.setText(dateString);

				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public CashReportFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (FragmentActivity) activity;
		MainActivity.attachFragment = this;
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
		View view = inflater.inflate(R.layout.fragment_report_cashv2,
				container, false);
		mInflater = inflater;

		iniPopupWindow();

		mAllData = new ArrayList<Map<String, Object>>();

		dateTextView = (TextView) view.findViewById(R.id.date_txt);
		barLayout = (LinearLayout) view.findViewById(R.id.LineChartLayout);
		mListView = (ListView) view.findViewById(R.id.mListView);
		mListView.setDividerHeight(0);
		mAdapter = new CashReportListAdapter(mActivity);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("dateLong",
						(Long) mAllData.get(arg2).get("dateLong"));
				intent.setClass(mActivity, ReCashListActivity.class);
				startActivityForResult(intent, 1);

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

			expenseArrayList.clear();
			incomeArrayList.clear();
			labelArrayList.clear();
			mAllData.clear();

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(MainActivity.startCashDate);

			do {

				double expenseAll = 0;
				double incomeAll = 0;

				List<Map<String, Object>> mSumList = ReportDao
						.selectCategoryAllSum(mActivity, MEntity.getFirstDayOfMonthMillis(calendar.getTimeInMillis()),
								MEntity.getLastDayOfMonthMillis(calendar.getTimeInMillis())); // 查询该时间段的总额

				if (mSumList.size() > 0) {

					for (int i = 0; i < mSumList.size(); i++) {
						double sum = (Double) mSumList.get(i).get("sum");
						int categoryType = (Integer) mSumList.get(i).get(
								"categoryType");
						if (categoryType == 0) {
							expenseAll = sum;
						} else if (categoryType == 1) {
							incomeAll = sum;
						}

					}
				} else {
					expenseAll = 0;
					incomeAll = 0;
				}

				expenseArrayList.add(expenseAll);
				incomeArrayList.add(incomeAll);
				labelArrayList.add(turnDateToMonthString(calendar
						.getTimeInMillis()));

				Map<String, Object> mMap = new HashMap<String, Object>();
				mMap.put("date", turnMillsToMonth(calendar.getTimeInMillis()));
				mMap.put("dateLong", calendar.getTimeInMillis());
				mMap.put("exAmount", expenseAll);
				mMap.put("inAmount", incomeAll);
				mAllData.add(mMap);

				calendar.add(Calendar.MONTH, 1);

			} while (calendar.getTimeInMillis() <= MainActivity.endCashDate);

			long en = System.currentTimeMillis();
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	private XYMultipleSeriesDataset getBarDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		XYSeries series = new XYSeries("Expense");

		for (int k = 0; k < expenseArrayList.size(); k++) {

			double everyDouble = expenseArrayList.get(k);

			series.add(k, everyDouble);

		}
		dataset.addSeries(series);

		series = new XYSeries("Income");
		for (int k = 0; k < incomeArrayList.size(); k++) {

			double everyDouble = incomeArrayList.get(k);
			series.add(k, everyDouble);
		}
		dataset.addSeries(series);

		return dataset;
	}

	public XYMultipleSeriesRenderer getBarRenderer(ArrayList<String> xLable) {

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.setAxisTitleTextSize(16);// 设置坐标轴标题文本大小
		renderer.setChartTitleTextSize(20); // 设置图表标题文本大小
		renderer.setLabelsTextSize(MEntity.dip2px(mActivity, 9)); // 设置轴标签文本大小
		renderer.setLegendTextSize(MEntity.dip2px(mActivity, 7)); // 设置图例文本大小
		renderer.setXLabelsColor(Color.parseColor("#acadb2"));
		renderer.setYLabelsColor(0, Color.parseColor("#acadb2"));
		renderer.setMargins(new int[] { MEntity.dip2px(mActivity, 10), 0,
				MEntity.dip2px(mActivity, 8), 0 }); // 设置4边留白上左下右
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setZoomEnabled(false, false); // 设置是否可缩放XY
		renderer.setPanEnabled(true, false);// 设置XY轴的滑动
		renderer.setYLabelsAlign(Align.RIGHT);

		renderer.setBarSpacing(1);
		renderer.setInScroll(true);
		renderer.setBarWidth(22);
		renderer.setShowLegend(false);
		renderer.setFitLegend(true);

		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.argb(255, 243, 61, 36));
		r.setPointStyle(PointStyle.CIRCLE);
		// r.setLineWidth(Dp2Px(context, 1)); //设置线条宽度
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);

		r = new XYSeriesRenderer();
		r.setColor(Color.argb(255, 102, 175, 54));
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);

		XYSeriesRenderer xyRenderer = (XYSeriesRenderer) renderer
				.getSeriesRendererAt(0);
		FillOutsideLine fill = new FillOutsideLine(
				FillOutsideLine.Type.BOUNDS_ALL);
		fill.setColor(Color.argb(39, 243, 61, 36));
		xyRenderer.addFillOutsideLine(fill);

		xyRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(1);
		fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
		fill.setColor(Color.argb(39, 102, 175, 54));
		xyRenderer.addFillOutsideLine(fill);

		renderer.setYAxisMin(0);
		renderer.setXAxisMin(-0.5);
		renderer.setXAxisMax(7);
		renderer.setYLabels(0);
		renderer.setXLabels(0);

		for (int i = 0; i < xLable.size(); i++) {
			// 添加X轴便签
			renderer.addXTextLabel(i, xLable.get(i));
		}
		renderer.setPanLimits(new double[] { -0.5, xLable.size() + 0.4, 0, 0 }); // 设置左右拉伸的界限

		return renderer;
	}

	private void filterData(List<Map<String, Object>> mData) {
		expenseArrayList.clear();
		incomeArrayList.clear();
		labelArrayList.clear();
		mAllData.clear();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(MainActivity.startCashDate);

		do {

			BigDecimal ex = new BigDecimal("0");
			BigDecimal in = new BigDecimal("0");

			for (Map<String, Object> iMap : mData) {
				String amount = (String) iMap.get("amount");
				long dateTime = (Long) iMap.get("dateTime");
				int expenseAccount = (Integer) iMap.get("expenseAccount");
				int incomeAccount = (Integer) iMap.get("incomeAccount");

				BigDecimal amountBig = new BigDecimal(amount);

				if (MEntity
						.getFirstDayOfMonthMillis(calendar.getTimeInMillis()) <= dateTime
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
			labelArrayList
					.add(turnDateToMonthString(calendar.getTimeInMillis()));

			Map<String, Object> mMap = new HashMap<String, Object>();
			mMap.put("date", turnMillsToMonth(calendar.getTimeInMillis()));
			mMap.put("dateLong", calendar.getTimeInMillis());
			mMap.put("exAmount", ex.doubleValue());
			mMap.put("inAmount", in.doubleValue());
			mAllData.add(mMap);

			calendar.add(Calendar.MONTH, 1);
		} while (calendar.getTimeInMillis() <= MainActivity.endCashDate);

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
		map.put("item", "This Year");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "Last Year");
		moreList.add(map);
		map = new HashMap<String, String>();
		map.put("item", "Last 12 Months");
		moreList.add(map);

		return moreList;
	}

	private void iniPopupWindow() {

		View layout = mInflater.inflate(R.layout.popupwindow_drop_list, null);
		lvPopupList = (ListView) layout.findViewById(R.id.mList);
		mPopupWindow = new PopupWindow(layout);
		mPopupWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		PopupListViewAdapter mAdapter = new PopupListViewAdapter(mActivity);
		mAdapter.setAdapterDate(getPopupData());
		lvPopupList.setAdapter(mAdapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				MainActivity.rangeCashPositon = arg2;
				rangButton.setText(getPopupData().get(
						MainActivity.rangeCashPositon).get("item"));
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
			Calendar c4 = Calendar.getInstance();
			c4.set(Calendar.MONTH, 0);
			MainActivity.startCashDate = MEntity.getFirstDayOfMonthMillis(c4
					.getTimeInMillis());
			c4.set(Calendar.MONTH, 11);
			MainActivity.endCashDate = MEntity.getLastDayOfMonthMillis(c4
					.getTimeInMillis());
			mHandler.post(mTask);
		} else if (rangPosition == 1) {

			Calendar c5 = Calendar.getInstance();
			c5.add(Calendar.YEAR, -1);
			c5.set(Calendar.MONTH, 0);
			MainActivity.startCashDate = MEntity.getFirstDayOfMonthMillis(c5
					.getTimeInMillis());
			c5.set(Calendar.MONTH, 11);
			MainActivity.endCashDate = MEntity.getLastDayOfMonthMillis(c5
					.getTimeInMillis());
			mHandler.post(mTask);
		} else if (rangPosition == 2) {

			Calendar c6 = Calendar.getInstance();
			MainActivity.endCashDate = MEntity.getLastDayOfMonthMillis(c6
					.getTimeInMillis());
			c6.set(Calendar.MONTH, -11);
			MainActivity.startCashDate = MEntity.getFirstDayOfMonthMillis(c6
					.getTimeInMillis());
			mHandler.post(mTask);

		}
	}

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
		rangButton.setText(getPopupData().get(MainActivity.rangeCashPositon)
				.get("item"));
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
			if (data != null) {
				mHandler.post(mTask);
			}
			break;
		}
	}

	@Override
	public void onSyncFinished() {
		// TODO Auto-generated method stub
		mHandler.post(mTask);
	}

}
