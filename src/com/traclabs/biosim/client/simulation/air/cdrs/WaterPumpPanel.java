package com.traclabs.biosim.client.simulation.air.cdrs;

public class WaterPumpPanel extends GridButtonPanel {
	public WaterPumpPanel(){
		setName("Water Pump LSSMP1");
		addButton(new WaterPumpDetailsPanel());
		addButton(new LssmArRtStatus());
	}
}
