package com.traclabs.biosim.client.framework.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.framework.BioModule;
import com.traclabs.biosim.idl.framework.StochasticIntensity;

/**
 * @author Scott Bell
 */
public class StochasticPanel extends TimedPanel {
    private JSlider mySlider;

    private JList moduleList;

    private JPanel myModulePanel;

    private JPanel myOperatorPanel;

    private ImageIcon myIcon;
    
    private Logger myLogger;

    /**
     * Default constructor.
     */
    public StochasticPanel() {
        super();
        myLogger = Logger.getLogger(this.getClass());
        buildGui();
        refresh();
    }

    public void refresh() {
        BioModule myModule = getSelectedModule();
        if ((myModule == null) || (mySlider == null))
            return;
        else {
            if (myModule.getStochasticIntensity() == StochasticIntensity.NONE_STOCH)
                mySlider.setValue(0);
            else if (myModule.getStochasticIntensity() == StochasticIntensity.LOW_STOCH)
                mySlider.setValue(1);
            else if (myModule.getStochasticIntensity() == StochasticIntensity.MEDIUM_STOCH)
                mySlider.setValue(2);
            else if (myModule.getStochasticIntensity() == StochasticIntensity.HIGH_STOCH)
                mySlider.setValue(3);
        }
    }

    protected void buildGui() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        createModuleSelectPanel();
        createOperatorPanel();

        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        gridbag.setConstraints(myModulePanel, c);
        add(myModulePanel);
        c.gridheight = 1;
        c.gridx = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(myOperatorPanel, c);
        add(myOperatorPanel);
    }

    private void createModuleSelectPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        myModulePanel = new JPanel();
        myModulePanel.setBorder(BorderFactory
                .createTitledBorder("Module Select"));
        myModulePanel.setLayout(gridbag);
        String[] myModuleNames = BioHolderInitializer.getBioHolder().theBioDriver
                .getModuleNames();
        Arrays.sort(myModuleNames);
        moduleList = new JList(myModuleNames);
        moduleList.addListSelectionListener(new ModuleListener());
        moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moduleList.setSelectedIndex(0);
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        JScrollPane scrollPane = new JScrollPane(moduleList);
        gridbag.setConstraints(scrollPane, c);
        myModulePanel.add(scrollPane);
    }

    private void createOperatorPanel() {
        myOperatorPanel = new JPanel();
        myOperatorPanel.setLayout(new BorderLayout());
        myOperatorPanel.setBorder(BorderFactory
                .createTitledBorder("Stochastic Intensity"));
        //Create the slider
        mySlider = new JSlider(JSlider.HORIZONTAL, 0, 3, 0);
        mySlider.setSnapToTicks(true);
        mySlider.addChangeListener(new SliderListener());
        mySlider.setMajorTickSpacing(1);
        mySlider.setPaintTicks(true);

        //Create the label table
        Dictionary labelDictionary = new Hashtable();
        labelDictionary.put(new Integer(0), new JLabel("None"));
        labelDictionary.put(new Integer(1), new JLabel("Low"));
        labelDictionary.put(new Integer(2), new JLabel("Medium"));
        labelDictionary.put(new Integer(3), new JLabel("High"));
        mySlider.setLabelTable(labelDictionary);
        mySlider.setPaintLabels(true);
        mySlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        myOperatorPanel.add(mySlider, BorderLayout.CENTER);
    }

    /**
     * Attempts to load the icons from the resource directory.
     */
    private void loadIcons() {
        if (myIcon != null)
            return;
        try {
            myIcon = new ImageIcon(ClassLoader.getSystemClassLoader()
                    .getResource("biosim/client/framework/gui/dice.jpg"));
        } catch (Exception e) {
            myLogger.debug("Couldn't find icon (" + e + "), skipping");
            e.printStackTrace();
        }
    }

    public ImageIcon getIcon() {
        loadIcons();
        return myIcon;
    }

    public static void main(String[] args) {
        BioFrame myFrame = new BioFrame("BioSIM Stochastic Controller", false);
        StochasticPanel myStochasticPanel = new StochasticPanel();
        myFrame.getContentPane().add(myStochasticPanel);
        myFrame.setSize(550, 350);
        myFrame.setVisible(true);
        myFrame.setIconImage(myStochasticPanel.getIcon().getImage());
        myStochasticPanel.visibilityChange(true);
    }

    private class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            int stochasticChoice = (int) mySlider.getValue();
            if (stochasticChoice == 0)
                getSelectedModule().setStochasticIntensity(
                        StochasticIntensity.NONE_STOCH);
            else if (stochasticChoice == 1)
                getSelectedModule().setStochasticIntensity(
                        StochasticIntensity.LOW_STOCH);
            else if (stochasticChoice == 2)
                getSelectedModule().setStochasticIntensity(
                        StochasticIntensity.MEDIUM_STOCH);
            else if (stochasticChoice == 3)
                getSelectedModule().setStochasticIntensity(
                        StochasticIntensity.HIGH_STOCH);
        }
    }

    private BioModule getSelectedModule() {
        String currentName = (String) (moduleList.getSelectedValue());
        return ((BioModule) (BioHolderInitializer.getBioHolder().theModulesMapped
                .get(currentName)));
    }

    private class ModuleListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            refresh();
        }
    }
}