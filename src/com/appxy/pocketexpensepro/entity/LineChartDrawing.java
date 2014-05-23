package com.appxy.pocketexpensepro.entity;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * 在android中画折线图、柱状图、饼图等统计图，可以用achartengine这个工具，可通过下载achartengine.jar 主要通过设置几个对象
 * 1、XYSeries对象：用于存储一条线的数据信息；
 * 2、XYMultipleSeriesDataset对象：即数据集，可添加多个XYSeries对象，因为一个折线图中可能有多条线。
 * 3、XYSeriesRenderer对象：主要是用来设置一条线条的风格，颜色啊，粗细之类的。
 * 4、XYMultipleSeriesRenderer对象：主要用来定义一个图的整体风格
 * ，设置xTitle,yTitle,chartName等等整体性的风格， 可添加多个XYSeriesRenderer对象，因为一个图中可以有多条折线。
 * 设置完那些对象之后，可通过 org.achartengine.ChartFactory调用数据集XYMultipleSeriesDataset对象
 * 与XYMultipleSeriesRenderer对象来画图并将图加载到GraphicalView中，
 * ChartFactory有多种api，通过这些api调用来决定是画折线图还是柱状图。
 * */
public class LineChartDrawing {
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer multiRenderer;

	public XYMultipleSeriesRenderer getMultiRenderer() {
		return multiRenderer;
	}

	public XYMultipleSeriesDataset getDataset() {
		return dataset;
	}

	public LineChartDrawing() {
		
		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		this.multiRenderer = new XYMultipleSeriesRenderer();
		// Creating a dataset to hold each series
		this.dataset = new XYMultipleSeriesDataset();
	}

}
