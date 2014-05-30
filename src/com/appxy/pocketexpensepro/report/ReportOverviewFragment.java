package com.appxy.pocketexpensepro.report;

import java.util.ArrayList;
import java.util.List;
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

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				linearLayout.removeAllViews();
				dataset = getDataset1();
				lChart = (GraphicalView) ChartFactory.getLineChartView(mActivity, dataset,getRenderer());
				lChart.repaint();
				linearLayout.addView(lChart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				
				break;

			case MSG_FAILURE:
				Toast.makeText(mActivity, "Exception", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	
	public ReportOverviewFragment () {
		
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_report_overview,
				container, false);
		linearLayout = (LinearLayout) view.findViewById(R.id.LineChartLayout);
		pieLayout = (LinearLayout) view.findViewById(R.id.PieChartLayout);
		
		dataset = getDataset();
		lChart = (GraphicalView) ChartFactory.getLineChartView(mActivity, dataset,getRenderer());
		linearLayout.addView(lChart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		    List<double[]> values = new ArrayList<double[]>();
		    values.add(new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		    List<String[]> titles = new ArrayList<String[]>();
		    titles.add(new String[] { "P1", "P2", "P3", "P4", "P5", "P1", "P2", "P3", "P4", "P5"});
		    int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };

		    DefaultRenderer renderer = buildCategoryRenderer(colors);
		    pChart = (GraphicalView)ChartFactory.getDoughnutChartView(mActivity, buildMultipleCategoryDataset("Project budget", titles, values), renderer);
		    pieLayout.addView(pChart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		    
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
			try {
				mThread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
		}
	};

	 protected MultipleCategorySeries buildMultipleCategoryDataset(String title,List<String[]> titles, List<double[]> values) {//圆环设值
		    MultipleCategorySeries series = new MultipleCategorySeries(title);
		    int k = 0;
		    for (double[] value : values) {
		      series.add(2007 + k + "", titles.get(k), value);
		      k++;
		    }
		    return series;
		  }
	 
	 protected DefaultRenderer buildCategoryRenderer(int[] colors) {
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
		final int nr = 10;// 每个系列种包含10个随机数
		Random r = new Random();
		for (int i = 0; i < 2; i++) {
			XYSeries series = new XYSeries("Series" + (i + 1));
			for (int k = 0; k < nr; k++) {
				int x = r.nextInt() % 10;// x：0-10之间的随机整数
				int y = 50 + r.nextInt() % 50;// y:50-100之间的随机整数
				series.add(x, y);// 往系列中加入一个随机分布的点
			}
			dataset.addSeries(series);
		}
		return dataset;
	}
	
	private XYMultipleSeriesDataset getDataset1() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = 10;// 每个系列种包含10个随机数
		Random r = new Random();
		for (int i = 0; i < 2; i++) {
			XYSeries series = new XYSeries("Series" + (i + 1));
			for (int k = 0; k < nr; k++) {
				int x = k*10;// x：0-10之间的随机整数
				int y = k*13;// y:50-100之间的随机整数
				series.add(x, y);// 往系列中加入一个随机分布的点
			}
			dataset.addSeries(series);
		}
		return dataset;
	}
	
	public XYMultipleSeriesRenderer getRenderer() {
		
		 XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		 renderer.setAxisTitleTextSize(16);// 设置坐标轴标题文本大小
		 renderer.setChartTitleTextSize(20); // 设置图表标题文本大小
		 renderer.setLabelsTextSize(15); // 设置轴标签文本大小
		 renderer.setLegendTextSize(15); // 设置图例文本大小
		 renderer.setMargins(new int[] {20, 30, 15,0}); // 设置4边留白
		 renderer.setApplyBackgroundColor(true);
		 renderer.setBackgroundColor(Color.WHITE);
		 renderer.setMarginsColor(Color.rgb(248, 251, 253));
		 renderer.setZoomEnabled(false, false); //设置是否可缩放XY
		 renderer.setShowGrid(true);
		 renderer.setPanEnabled(true, false);//设置XY轴的滑动
			
		 XYSeriesRenderer r = new XYSeriesRenderer();
		 r.setColor(Color.argb(255, 243, 61, 36));
		 r.setPointStyle(PointStyle.CIRCLE);
		 r.setFillPoints(true); 
		 renderer.addSeriesRenderer(r);
		
		 
		 r = new XYSeriesRenderer();
		 r.setPointStyle(PointStyle.CIRCLE);
		 r.setFillPoints(true); 
		 r.setColor(Color.argb(255, 102, 175, 54));
         renderer.addSeriesRenderer(r);
         
         XYSeriesRenderer xyRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
         FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
         fill.setColor(Color.argb(39, 243, 61, 36));
         xyRenderer.addFillOutsideLine(fill);
         
         XYSeriesRenderer xyRenderer1 = (XYSeriesRenderer) renderer.getSeriesRendererAt(1);
         FillOutsideLine fill1 = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
         fill1.setColor(Color.argb(39, 102, 175, 54));
         xyRenderer1.addFillOutsideLine(fill1);
         
		 return renderer;
	}
	
}
