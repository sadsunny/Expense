package com.appxy.pocketexpensepro.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.appxy.pocketexpensepro.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ReportOverviewFragment extends Fragment {

	private LinearLayout linearLayout;
	private FragmentActivity mActivity;
	private LinearLayout pieLayout;
	
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
		
		GraphicalView lChart = (GraphicalView) ChartFactory.getLineChartView(mActivity, getDataset(),getRenderer());
		linearLayout.addView(lChart);
		
		   List<double[]> values = new ArrayList<double[]>();
		    values.add(new double[] { 12, 14, 11, 10, 19 });
		    List<String[]> titles = new ArrayList<String[]>();
		    titles.add(new String[] { "P1", "P2", "P3", "P4", "P5" });
		    int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };

		    DefaultRenderer renderer = buildCategoryRenderer(colors);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setBackgroundColor(Color.rgb(222, 222, 200));
		    renderer.setLabelsColor(Color.GRAY);
		    renderer.setStartAngle(180);
		    GraphicalView pChart = (GraphicalView)ChartFactory.getDoughnutChartView(mActivity, buildMultipleCategoryDataset("Project budget", titles, values), renderer);
		    pieLayout.addView(pChart, new LayoutParams(LayoutParams.MATCH_PARENT,
	                LayoutParams.MATCH_PARENT));
		    
		return view;
	}

	 protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
		      List<String[]> titles, List<double[]> values) {
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
		    renderer.setLabelsTextSize(10);
		    renderer.setLegendTextSize(10);
		    renderer.setZoomRate(13);
		    renderer.setMargins(new int[] { 20, 30, 15, 0 });
		    renderer.setScale(1.4f);
		    
		    for (int color : colors) {
		      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		      r.setColor(color);
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
	
	public XYMultipleSeriesRenderer getRenderer() {
		
		 XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		 renderer.setAxisTitleTextSize(16);// 设置坐标轴标题文本大小
		 renderer.setChartTitleTextSize(20); // 设置图表标题文本大小
		 renderer.setLabelsTextSize(15); // 设置轴标签文本大小
		 renderer.setLegendTextSize(15); // 设置图例文本大小
		 renderer.setMargins(new int[] {20, 30, 15,0}); // 设置4边留白
		 renderer.setApplyBackgroundColor(true);
		 renderer.setBackgroundColor(Color.rgb(222, 222, 200));
		    
		 XYSeriesRenderer r = new XYSeriesRenderer();
		 r.setColor(Color.argb(255, 243, 61, 36));
		 renderer.addSeriesRenderer(r);
		 r = new XYSeriesRenderer();
		 r.setColor(Color.argb(255, 102, 175, 54));
         renderer.addSeriesRenderer(r);
         
         XYSeriesRenderer xyRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
         FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
         fill.setColor(Color.argb(120, 251, 224, 220));
         xyRenderer.addFillOutsideLine(fill);
         
         XYSeriesRenderer xyRenderer1 = (XYSeriesRenderer) renderer.getSeriesRendererAt(1);
         FillOutsideLine fill1 = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
         fill.setColor(Color.argb(120, 102, 175, 54));
         xyRenderer1.addFillOutsideLine(fill1);
         
		 return renderer;
	}
	
}
