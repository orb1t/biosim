package biosim.server.sensor.food;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.food.*;
import biosim.idl.simulation.food.*;

public class FoodStoreLevelSensorImpl extends FoodStoreSensorImpl implements FoodStoreLevelSensorOperations{
	public FoodStoreLevelSensorImpl(int pID){
		super(pID);
	}

	protected void gatherData(){
		float preFilteredValue = getInput().getLevel();
		myValue = randomFilter(preFilteredValue);
	}
	
	protected void notifyListeners(){
	}
	
	/**
	* Returns the name of this module (FoodStoreLevelSensor)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "FoodStoreLevelSensor"+getID();
	}
}