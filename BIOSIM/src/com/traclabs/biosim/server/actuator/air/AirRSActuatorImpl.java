package biosim.server.actuator.air;

import biosim.server.actuator.framework.*;
import biosim.idl.actuator.air.*;
import biosim.idl.simulation.air.*;

public abstract class AirRSActuatorImpl extends GenericActuatorImpl implements AirRSActuatorOperations{
	protected AirRS myAirRS;
	
	public AirRSActuatorImpl(int pID){
		super(pID);
	}

	protected abstract void processData();
	protected abstract void notifyListeners();

	public void setOutput(AirRS source){
		myAirRS = source;
	}
	
	public AirRS getOutput(){
		return myAirRS;
	}
	
	/**
	* Returns the name of this module (AirRSActuator)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "AirRSActuator"+getID();
	}
}