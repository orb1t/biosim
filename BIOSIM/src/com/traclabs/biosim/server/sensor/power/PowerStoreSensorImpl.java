package biosim.server.sensor.power;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.power.*;
import biosim.idl.framework.*;
import biosim.idl.simulation.power.*;

public abstract class PowerStoreSensorImpl extends GenericSensorImpl implements PowerStoreSensorOperations{
	protected PowerStore myPowerStore;
	
	public PowerStoreSensorImpl(int pID){
		super(pID);
	}

	protected abstract void gatherData();
	protected abstract void notifyListeners();

	public void setInput(PowerStore source){
		myPowerStore = source;
	}
	
	public PowerStore getInput(){
		return myPowerStore;
	}
	
	public float getMax(){
		return myPowerStore.getCapacity();
	}
	
	/**
	* Returns the name of this module (PowerStoreSensor)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "PowerStoreSensor"+getID();
	}
}
