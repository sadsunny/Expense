package com.appxy.pocketexpensepro;

import java.text.Format;
import java.util.Arrays;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

@SuppressLint({ "NewApi", "ResourceAsColor" })
/**
 * 在android中画折线图、柱状图、饼图等统计图，可以用achartengine这个工具，可通过下载achartengine.jar
 * 主要通过设置几个对象
 * 1、XYSeries对象：用于存储一条线的数据信息；
 * 2、XYMultipleSeriesDataset对象：即数据集，可添加多个XYSeries对象，因为一个折线图中可能有多条线。
 * 3、XYSeriesRenderer对象：主要是用来设置一条线条的风格，颜色啊，粗细之类的。
 * 4、XYMultipleSeriesRenderer对象：主要用来定义一个图的整体风格，设置xTitle,yTitle,chartName等等整体性的风格，
 *    可添加多个XYSeriesRenderer对象，因为一个图中可以有多条折线。
 * 设置完那些对象之后，可通过 org.achartengine.ChartFactory调用数据集XYMultipleSeriesDataset对象
 * 与XYMultipleSeriesRenderer对象来画图并将图加载到GraphicalView中，
 * ChartFactory有多种api，通过这些api调用来决定是画折线图还是柱状图。
 * */
public class ChartDrawing {

	private String xTitle, yTitle, chartTitle;
	private String xLabel[];
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer multiRenderer;

	public XYMultipleSeriesRenderer getMultiRenderer() {
		return multiRenderer;
	}

	public XYMultipleSeriesDataset getDataset() {
		return dataset;
	}
	

	public ChartDrawing(String xTitle, String yTitle, String chartTitle,
			String xLabel[]) {

		this.xTitle = xTitle;
		this.yTitle = yTitle;
		this.xLabel = Arrays.copyOf(xLabel, xLabel.length);
		this.chartTitle = chartTitle;
		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		this.multiRenderer = new XYMultipleSeriesRenderer();
		// Creating a dataset to hold each series
		this.dataset = new XYMultipleSeriesDataset();
	}
	
	/**
	 * 给XYSeries对象赋值。并将其加到数据集 XYMultipleSeriesDataset对象中去
	 * */
	public void set_XYSeries(double value[], String lineName) {
		// 创建一个XYSeries存放线为lineName上的数据
		XYSeries oneSeries = new XYSeries(lineName);
		// Adding data to Series
		for (int i = 0; i < value.length; i++) {
			oneSeries.add(i+1, value[i]);
		}
		// Adding Series to the dataset
		this.dataset.addSeries(oneSeries);
	}
   
	
	public XYSeriesRenderer set_XYSeriesRender_Style(Context context) {
		XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
		//设置线条的颜色
		seriesRenderer.setColor(Color.rgb(51, 181, 229));
		seriesRenderer.setFillPoints(true);
		//设置线条的宽度
		seriesRenderer.setLineWidth(Dp2Px(context, 1));
		seriesRenderer.setDisplayChartValues(false);
//		seriesRenderer.setChartValuesTextSize(20);
		seriesRenderer.setPointStyle(PointStyle.CIRCLE);
		seriesRenderer.setFillPoints(true); 
		
//		  FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
//	    fill.setColor(Color.BLUE);
//	    seriesRenderer.addFillOutsideLine(fill);
		return seriesRenderer;

	}
	
	
	public int Dp2Px(Context context, double dp) {  //将dp转换为pix
		
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (dp * scale + 0.5f); 
	    
	}

	public void set_XYMultipleSeriesRenderer_Style(XYSeriesRenderer renderer ,double Max ,Context context) {
		// 设置 X 轴不显示数字(改用我们手动添加的文字标签)
		
		this.multiRenderer.setMargins(new int[] {Dp2Px(context, 10), Dp2Px(context, 70), 0, 0}); //设置边距
		this.multiRenderer.setXLabels(0);
		//设置Y轴的结点数
		
		this.multiRenderer.setYLabels(5);
		this.multiRenderer.setShowLegend(false);
		this.multiRenderer.setShowGrid(true);
		this.multiRenderer.setGridColor(Color.rgb(213, 215, 217));
		this.multiRenderer.setLabelsTextSize(Dp2Px(context, 14)); 
		this.multiRenderer.setYLabelsAlign(Align.RIGHT);
//		this.multiRenderer.setZoomEnabled(true);
		
		this.multiRenderer.setApplyBackgroundColor(true);//设置背景网格
		this.multiRenderer.setAxesColor(Color.rgb(213, 215, 217));//设置坐标轴的颜色
		this.multiRenderer.setBackgroundColor(Color.rgb(248, 251, 253));//设置背景的颜色
		this.multiRenderer.setMarginsColor(Color.rgb(248, 251, 253));//设置边缘背景的颜色
		
		this.multiRenderer.setXLabelsColor(Color.rgb(51, 181, 229));//设置横坐标刻度的颜色
		this.multiRenderer.setYLabelsColor(0, Color.rgb(51, 181, 229));//设置纵坐标刻度的颜色
		this.multiRenderer.setPointSize(Dp2Px(context, 2));//设置点的大小
		this.multiRenderer.setYLabelsPadding(11); //Y轴显示的字离Y轴的距离
		
		this.multiRenderer.setYAxisMin(0);
		this.multiRenderer.setYAxisMax(Max+Max/5);
		this.multiRenderer.setXAxisMax(6.3);
		this.multiRenderer.setXAxisMin(0);
		this.multiRenderer.setPanEnabled(true, false);//设置XY轴的滑动
		this.multiRenderer.setPanLimits(new double[] {0,12.4,0,12}); //设置左右拉伸的界限
//		this.multiRenderer.setXLabelsAlign(Align.LEFT);
		this.multiRenderer.setZoomEnabled(false, false); //设置是否可缩放XY
//		this.multiRenderer.setXRoundedLabels(false);
		
//		for (int i = 0; i < 5; i++) {
//			//添加X轴便签
//			this.multiRenderer.addYTextLabel(i+1, Max/(5-i)+"");
//		}
		
		for (int i = 0; i < xLabel.length; i++) {
			//添加X轴便签
			this.multiRenderer.addXTextLabel(i+1, this.xLabel[i]);
		}
//		this.multiRenderer.setFitLegend(false);// 调整合适的位置
//		this.multiRenderer.setClickEnabled(false);//设置是否可以滑动及放大缩小;
//		this.multiRenderer.setInScroll(true);
		
		this.multiRenderer.addSeriesRenderer(renderer);

	}

}
