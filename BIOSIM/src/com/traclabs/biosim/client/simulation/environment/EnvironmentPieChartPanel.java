package com.traclabs.biosim.client.simulation.environment;

import java.awt.Color;
import java.awt.Dimension;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import com.traclabs.biosim.client.framework.gui.GraphPanel;
import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.simulation.environment.SimEnvironment;

/**
 * This is the JPanel that displays a chart about the Environment
 * 
 * @author Scott Bell
 */
public class EnvironmentPieChartPanel extends GraphPanel {
    private SimEnvironment mySimEnvironment;

    private PiePlot3D myPlot;

    private JFreeChart myChart;

    private DefaultPieDataset myDataset;

    private String O2Category = "O2";

    private String CO2Category = "CO2";

    private String nitrogenCategory = "N";

    private String waterCategory = "H20";

    private String otherCategory = "Other";

    private String vacuumCategory = "Vacuum";

    private boolean isVacuum = false;

    private Logger myLogger;

    public EnvironmentPieChartPanel(String pEnvironmentName) {
        super(pEnvironmentName);
        myLogger = Logger.getLogger(this.getClass());
    }

    protected void createGraph() {
        // create the chart...
        refresh();
        String titleText = "Environment";
        if (mySimEnvironment.getModuleName().startsWith("Crew"))
            titleText = "Crew Environment";
        else if (mySimEnvironment.getModuleName().startsWith("Plant"))
            titleText = "Plant Environment";
        myChart = ChartFactory.createPieChart3D(titleText, myDataset, true,
                true, false);
        myPlot = (PiePlot3D) (myChart.getPlot());
        myPlot.setDepthFactor(0.1d);
        myPlot.setLabelLinksVisible(false);
        myPlot.setForegroundAlpha(0.3f);
        myPlot.setLabelGenerator(null);
        initDataset();
        TextTitle myTextTitle = (TextTitle) (myChart.getTitle());
        myTextTitle.setFont(myTextTitle.getFont().deriveFont(13.0f));
        myChartPanel = new ChartPanel(myChart);
        myChartPanel.setMinimumDrawHeight(250);
        myChartPanel.setMinimumDrawWidth(250);
        myChartPanel.setPreferredSize(new Dimension(250, 250));
    }

    protected void initializeDataSources(String dataSourceName) {
        BioHolder myBioHolder = BioHolderInitializer.getBioHolder();
        if (dataSourceName.startsWith("Crew"))
            mySimEnvironment = (SimEnvironment) (myBioHolder.theSimEnvironments
                    .get(0));
        else if (dataSourceName.startsWith("Plant"))
            mySimEnvironment = (SimEnvironment) (myBioHolder.theSimEnvironments
                    .get(1));
    }

    public void refresh() {
        if (myDataset == null) {
            myDataset = new DefaultPieDataset();
        } else if ((mySimEnvironment.getO2Moles() <= 0)
                && (mySimEnvironment.getCO2Moles() <= 0)
                && (mySimEnvironment.getNitrogenMoles() <= 0)
                && (mySimEnvironment.getOtherMoles() <= 0)
                && (mySimEnvironment.getWaterMoles() <= 0)) {
            //It isn't in a vacuum, set it up
            if (!isVacuum) {
                myDataset = new DefaultPieDataset();
                myPlot.setDataset(myDataset);
                myDataset.setValue(vacuumCategory, new Float(1f));
                myPlot.setSectionPaint(0, Color.DARK_GRAY);
                isVacuum = true;
            }
        } else {
            //In a vacuum, set it up for normal use
            if (isVacuum) {
                myDataset = new DefaultPieDataset();
                myPlot.setDataset(myDataset);
                myPlot.setSectionPaint(0, Color.BLUE);
                myPlot.setSectionPaint(1, Color.GREEN);
                myPlot.setSectionPaint(2, Color.CYAN);
                myPlot.setSectionPaint(3, Color.YELLOW);
                myPlot.setSectionPaint(4, Color.RED);
                isVacuum = false;
            }
            myDataset.setValue(O2Category, new Float(mySimEnvironment
                    .getO2Moles()));
            myDataset.setValue(CO2Category, new Float(mySimEnvironment
                    .getCO2Moles()));
            myDataset.setValue(waterCategory, new Float(mySimEnvironment
                    .getWaterMoles()));
            myDataset.setValue(otherCategory, new Float(mySimEnvironment
                    .getOtherMoles()));
            myDataset.setValue(nitrogenCategory, new Float(mySimEnvironment
                    .getNitrogenMoles()));
        }
    }

    private void initDataset() {
        if (mySimEnvironment == null)
            myLogger
                    .error("EnvironmentPieChartPanel: mySimEnvironment is null!");
        if ((mySimEnvironment.getO2Moles() <= 0)
                && (mySimEnvironment.getCO2Moles() <= 0)
                && (mySimEnvironment.getNitrogenMoles() <= 0)
                && (mySimEnvironment.getOtherMoles() <= 0)
                && (mySimEnvironment.getWaterMoles() <= 0)) {
            myDataset.setValue(vacuumCategory, new Float(1f));
            myPlot.setSectionPaint(0, Color.DARK_GRAY);
            isVacuum = true;
        } else {
            myPlot.setSectionPaint(0, Color.BLUE);
            myPlot.setSectionPaint(1, Color.GREEN);
            myPlot.setSectionPaint(2, Color.CYAN);
            myPlot.setSectionPaint(3, Color.YELLOW);
            myPlot.setSectionPaint(4, Color.RED);
            isVacuum = false;
        }
    }
}