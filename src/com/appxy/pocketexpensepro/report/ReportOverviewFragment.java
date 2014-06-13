package com.appxy.pocketexpensepro.report;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.SpinnerContext;
import com.appxy.pocketexpensepro.accounts.AccountsFragment;
import com.appxy.pocketexpensepro.accounts.CreatNewAccountActivity;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.overview.OverViewFragmentMonth;
import com.appxy.pocketexpensepro.overview.OverviewFragment;
import com.appxy.pocketexpensepro.setting.SettingActivity;

import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;

public class ReportOverviewFragment extends Fragment {

	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILURE = 0;
	private LinearLayout linearLayout;
	private FragmentActivity mActivity;
	private LinearLayout pieLayout;
	private Thread mThread;
	private GraphicalView lChart;
	private XYMultipleSeriesDataset dataset;
	private GraphicalView pChart;
	private Button rangButton;
	public static MenuItem item;
	private LayoutInflater mInflater;
	private PopupWindow mPopupWindow;
	private ListView lvPopupList;

	private ArrayList<Double> expenseArrayList;
	private ArrayList<Double> incomeArrayList;
	private ArrayList<String> labelArrayList;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				linearLayout.removeAllViews();

				dataset = getDataset();
				lChart = (GraphicalView) ChartFactory.getLineChartView(mActivity,
						dataset, getRenderer(labelArrayList));
				lChart.repaint();
				linearLayout.addView(lChart, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				
				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public ReportOverviewFragment() {

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
		mInflater = inflater;
		View view = inflater.inflate(R.layout.fragment_report_overview,
				container, false);
		iniPopupWindow();

		linearLayout = (LinearLayout) view.findViewById(R.id.LineChartLayout);
		pieLayout = (LinearLayout) view.findViewById(R.id.PieChartLayout);


		List<double[]> values = new ArrayList<double[]>();
		values.add(new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		List<String[]> titles = new ArrayList<String[]>();
		titles.add(new String[] { "P1", "P2", "P3", "P4", "P5", "P1", "P2",
				"P3", "P4", "P5" });

		DefaultRenderer renderer = buildCategoryRenderer();
		pChart = (GraphicalView) ChartFactory.getDoughnutChartView(mActivity,
				buildMultipleCategoryDataset("Project budget", titles, values),
				renderer);
		pieLayout.addView(pChart, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

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
			List<Map<String, Object>> mDataList = OverViewDao
					.selectTransactionByTimeBE(mActivity,
							MainActivity.startDate, MainActivity.endDate);
			Log.v("mtest", "selectTransactionByTimeBE"+mDataList);
			filterData(mDataList);
			
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	private void filterData(List<Map<String, Object>> mData) {
		expenseArrayList.clear();
		incomeArrayList.clear();
		labelArrayList.clear();
		
			if (MainActivity.rangePositon == 4 || MainActivity.rangePositon == 5) {
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(MainActivity.startDate);
				Log.v("mtest", "筛选开始时间"+MEntity.turnToDateString(MainActivity.startDate));
				Log.v("mtest", "筛选开始时间"+MEntity.turnToDateString(MainActivity.endDate));
				
				
				do {
					
					BigDecimal ex = new BigDecimal("0");
					BigDecimal in = new BigDecimal("0");
					
					for (Map<String, Object> iMap : mData) {
						String amount = (String) iMap.get("amount");
						long dateTime = (Long) iMap.get("dateTime");
						int expenseAccount = (Integer) iMap.get("expenseAccount");
						int incomeAccount = (Integer) iMap.get("incomeAccount");
						
						BigDecimal amountBig = new BigDecimal(amount);
						
						if ( MEntity.getFirstDayOfMonthMillis(calendar.getTimeInMillis()) <=dateTime && MEntity.getLastDayOfMonthMillis(calendar.getTimeInMillis()) >dateTime  ) {
							
						if (expenseAccount >0) {
							ex = ex.add(amountBig);
						}
						
						if (incomeAccount > 0) {
							in = in.add(amountBig);
						}
						
					  }
					
				    }
					expenseArrayList.add(ex.doubleValue());
					incomeArrayList.add(in.doubleValue());
					labelArrayList.add(turnDateToMonthString(calendar.getTimeInMillis()));
					
					calendar.add(Calendar.MONTH, 1);
				}
				while (calendar.getTimeInMillis() <= MainActivity.endDate);
				
				
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
						
						if (dateTime == calendar.getTimeInMillis()) {
							
						if (expenseAccount >0) {
							ex = ex.add(amountBig);
						}
						
						if (incomeAccount > 0) {
							in = in.add(amountBig);
						}
						
					  }
					
				    }
					expenseArrayList.add(ex.doubleValue());
					incomeArrayList.add(in.doubleValue());
					labelArrayList.add(turnDateToString(calendar.getTimeInMillis()));
					
					calendar.add(Calendar.DAY_OF_MONTH, 1);
				}
				while (calendar.getTimeInMillis() <= MainActivity.endDate);

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
	

	protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
			List<String[]> titles, List<double[]> values) {// 圆环设值
		MultipleCategorySeries series = new MultipleCategorySeries(title);
		int k = 0;
		for (double[] value : values) {
			series.add(2007 + k + "", titles.get(k), value);
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
		renderer.setScale(1.4f);

		for (int i = 0; i < 10; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(Common.IncomeColors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	private XYMultipleSeriesDataset getDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

			
			XYSeries series = new XYSeries("Expense");
			for (int k = 0; k < expenseArrayList.size(); k++) {
				
				series.add(k, expenseArrayList.get(k));
			}
			dataset.addSeries(series);
			
			
			 series = new XYSeries("Income");
			for (int k = 0; k < incomeArrayList.size(); k++) {
				
				series.add(k, incomeArrayList.get(k));
			}
			dataset.addSeries(series);
			
		return dataset;
	}
	
	public XYMultipleSeriesRenderer getRenderer( ArrayList<String> xLable) {
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);// 设置坐标轴标题文本大小
		renderer.setChartTitleTextSize(20); // 设置图表标题文本大小
		renderer.setLabelsTextSize(15); // 设置轴标签文本大小
		renderer.setLegendTextSize(15); // 设置图例文本大小
		renderer.setMargins(new int[] { MEntity.dip2px(mActivity, 10),
				MEntity.dip2px(mActivity, 25), MEntity.dip2px(mActivity, 8), 0 }); // 设置4边留白上左下右
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.rgb(248, 251, 253));
		renderer.setZoomEnabled(false, false); // 设置是否可缩放XY
		renderer.setShowGrid(true);
		renderer.setPanEnabled(true, false);// 设置XY轴的滑动
		
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.argb(255, 243, 61, 36));
		r.setPointStyle(PointStyle.CIRCLE);
//		r.setLineWidth(Dp2Px(context, 1)); //设置线条宽度
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

		xyRenderer = (XYSeriesRenderer) renderer
				.getSeriesRendererAt(1);
		fill = new FillOutsideLine(
				FillOutsideLine.Type.BOUNDS_ALL);
		fill.setColor(Color.argb(39, 102, 175, 54));
		xyRenderer.addFillOutsideLine(fill);
		
		renderer.setYAxisMin(0);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(7);
		renderer.setXLabels(0);
		
		Log.v("mtest", "xLabel.length"+xLable.size());
		
		for (int i = 0; i < xLable.size(); i++) {
			// 添加X轴便签
			renderer.addXTextLabel(i,xLable.get(i));
		}
		renderer.setPanLimits(new double[] {0, xLable.size()+0.4, 0, xLable.size() }); // 设置左右拉伸的界限

		return renderer;
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

	public void setRangDate(int rangPosition) {

		if (rangPosition == 0) {
			Calendar c0 = Calendar.getInstance();
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c0
					.getTimeInMillis());
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c0
					.getTimeInMillis());

		} else if (rangPosition == 1) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MONTH, -1);
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c1
					.getTimeInMillis());
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c1
					.getTimeInMillis());

		} else if (rangPosition == 2) {
			Calendar c2 = Calendar.getInstance();
			GetQuarter(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH));
			Log.v("mtest", "Calendar.MONTH" + Calendar.MONTH);

		} else if (rangPosition == 3) {
			Calendar c3 = Calendar.getInstance();
			c3.add(Calendar.MONTH, -3);
			GetQuarter(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH));

		} else if (rangPosition == 4) {
			Calendar c4 = Calendar.getInstance();
			c4.set(Calendar.MONTH, 0);
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c4
					.getTimeInMillis());
			c4.set(Calendar.MONTH, 11);
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c4
					.getTimeInMillis());

		} else if (rangPosition == 5) {

			Calendar c5 = Calendar.getInstance();
			c5.add(Calendar.YEAR, -1);
			c5.set(Calendar.MONTH, 0);
			MainActivity.startDate = MEntity.getFirstDayOfMonthMillis(c5
					.getTimeInMillis());
			c5.set(Calendar.MONTH, 11);
			MainActivity.endDate = MEntity.getLastDayOfMonthMillis(c5
					.getTimeInMillis());
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
				if (arg2 == 6) {

				}
				mHandler.post(mTask);
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

}
