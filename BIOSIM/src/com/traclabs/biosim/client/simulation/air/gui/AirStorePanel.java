package com.traclabs.biosim.client.simulation.air.gui;

import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.TextTitle;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.data.DefaultCategoryDataset;

import com.traclabs.biosim.client.framework.gui.GraphPanel;
import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.simulation.air.CO2Store;
import com.traclabs.biosim.idl.simulation.air.H2Store;
import com.traclabs.biosim.idl.simulation.air.NitrogenStore;
import com.traclabs.biosim.idl.simulation.air.O2Store;

/**
 * This is the JPanel that displays a chart about the Air Stores
 *
 * @author    Scott Bell
 */
public class AirStorePanel extends GraphPanel
{
	private O2Store myO2Store;
	private CO2Store myCO2Store;
	private H2Store myH2Store;
	private NitrogenStore myNitrogenStore;
	private DefaultCategoryDataset myDataset;
	private ValueAxis rangeAxis;

	protected void createGraph(){
		// create the chart...
		BioHolder myBioHolder = BioHolderInitializer.getBioHolder();
		myO2Store = (O2Store)(myBioHolder.theO2Stores.get(0));
		myCO2Store = (CO2Store)(myBioHolder.theCO2Stores.get(0));
		myH2Store = (H2Store)(myBioHolder.theH2Stores.get(0));
		myNitrogenStore = (NitrogenStore)(myBioHolder.theNitrogenStores.get(0));
		refresh();
		JFreeChart myChart = ChartFactory.createBarChart3D(
		                  "Gas Store Levels",  // chart title
		                  "Stores",              // domain axis label
		                  "Gas Level (L)",                 // range axis label
		                  myDataset, PlotOrientation.VERTICAL,                 // data
		                  true,                     // include legend
				  true, //tooltips cool
				  false
		          );
		// add the chart to a panel...
		CategoryPlot myPlot = myChart.getCategoryPlot();
		rangeAxis = myPlot.getRangeAxis();
		rangeAxis.setAutoRange(false);
		rangeAxis.setRange(0.0, myO2Store.getCapacity());
		CategoryItemRenderer renderer = myPlot.getRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.ORANGE);
		renderer.setSeriesPaint(3, Color.RED);
		TextTitle myTextTitle = (TextTitle)(myChart.getTitle());
		myTextTitle.setFont(myTextTitle.getFont().deriveFont(13.0f));
		myChartPanel = new ChartPanel(myChart);
		myChartPanel.setMinimumDrawHeight(250);
		myChartPanel.setMinimumDrawWidth(250);
		myChartPanel.setPreferredSize(new Dimension(200, 200));
	}

	public void refresh() {
		if (myDataset == null){
			myDataset = new DefaultCategoryDataset();
			String series1 = "O2";
			String series2 = "CO2";
			String series3 = "H2";
			String series4 = "N";
			String category = "";
			myDataset.addValue(myO2Store.getLevel(),series1, category);
			myDataset.addValue(myCO2Store.getLevel(), series2, category);
			myDataset.addValue(myH2Store.getLevel(), series3, category);
			myDataset.addValue(myNitrogenStore.getLevel(), series4, category);
		}
		else{
			float capacity1 = Math.max(myO2Store.getCapacity(), myCO2Store.getCapacity());
			float capacity2 = Math.max(capacity1, myH2Store.getCapacity());
			float capacity3 = Math.max(capacity2, myNitrogenStore.getCapacity());
			if ((rangeAxis.getRange().getUpperBound() != capacity3) && (capacity3 > 0)){
				rangeAxis.setRange(0.0, capacity3);
				myChartPanel.repaint();
			}
			myDataset.setValue(new Float(myO2Store.getLevel()), "O2", "");
			myDataset.setValue(new Float(myCO2Store.getLevel()), "CO2", "");
			myDataset.setValue(new Float(myH2Store.getLevel()), "H2", "");
			myDataset.setValue(new Float(myNitrogenStore.getLevel()), "N", "");
		}
	}

}
