package biosim.server.sensor.water;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.water.*;
import biosim.idl.framework.*;

public class PotableWaterInFlowRateSensorImpl extends GenericSensorImpl implements PotableWaterInFlowRateSensorOperations{
	private PotableWaterConsumer myConsumer;
	private int myIndex;
	
	public PotableWaterInFlowRateSensorImpl(int pID){
		super(pID);
	}

	protected void gatherData(){
		float preFilteredValue = getInput().getPotableWaterInputActualFlowRate(myIndex);
		myValue = randomFilter(preFilteredValue);
	}
	
	protected void notifyListeners(){
		//does nothing right now
	}

	public void setInput(PotableWaterConsumer pConsumer, int pIndex){
		myConsumer = pConsumer;
		myIndex = pIndex;
	}
	
	public PotableWaterConsumer getInput(){
		return myConsumer;
	}
	
	public int getIndex(){
		return myIndex;
	}
	
	public float getMax(){
		return myConsumer.getPotableWaterInputMaxFlowRate(myIndex);
	}
	
	/**
	* Returns the name of this module (PotableWaterInFlowRateSensor)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "PotableWaterInFlowRateSensor"+getID();
	}
}
