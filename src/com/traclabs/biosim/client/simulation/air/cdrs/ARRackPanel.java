package com.traclabs.biosim.client.simulation.air.cdrs;

import javax.swing.JLabel;

import com.traclabs.biosim.client.util.BioHolderInitializer;


public class ARRackPanel extends GridButtonPanel {
	
	private CDRSPanel myCDRSPanel = new CDRSPanel();
	private MCAPanel myMCAPanel = new MCAPanel();
	private SmokeDetectorPanel mySmokeDetectorPanel = new SmokeDetectorPanel();
	private RpcmM1Panel myRpcmM1Panel = new RpcmM1Panel();
	private RpcmMAPanel myRpcmMAPanel = new RpcmMAPanel();
	private JLabel myPressureLabel = new JLabel();
	
	public ARRackPanel(){
		setName("AR Rack");
		addButton(myCDRSPanel);
		addButton(myMCAPanel);
		addButton(mySmokeDetectorPanel);
		addButton(myRpcmM1Panel);
		addButton(myRpcmMAPanel);
		myPressureLabel.setText("LSSM Cabin Pressure: ");
		addLabel(myPressureLabel);
	}

	@Override
	public void refresh() {
		super.refresh();
		myPressureLabel.setText("LSSM Cabin Pressure: " + BioHolderInitializer.getBioHolder().theSimEnvironments.get(0).getTotalPressure());
		myCDRSPanel.refresh();
		myMCAPanel.refresh();
		mySmokeDetectorPanel.refresh();
		myRpcmM1Panel.refresh();
		myRpcmMAPanel.refresh();
	}
}