package biosim.server.actuator.power;

import biosim.server.actuator.framework.*;
import biosim.idl.actuator.power.*;
import biosim.idl.simulation.power.*;

public class PowerOutFlowRateActuatorImpl extends GenericActuatorImpl implements PowerOutFlowRateActuatorOperations{
	private PowerProducer myProducer;
	private int myIndex;
	
	public PowerOutFlowRateActuatorImpl(int pID){
		super(pID);
	}

	protected void processData(){
		float myFilteredValue = randomFilter(myValue);
		getOutput().setPowerOutputDesiredFlowRate(myFilteredValue, myIndex);
	}
	
	protected void notifyListeners(){
		//does nothing right now
	}

	public void setOutput(PowerProducer pProducer, int pIndex){
		myProducer = pProducer;
		myIndex = pIndex;
	}
	
	public PowerProducer getOutput(){
		return myProducer;
	}
	
	public int getIndex(){
		return myIndex;
	}
	
	/**
	* Returns the name of this module (PowerOutFlowRateActuator)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "PowerOutFlowRateActuator"+getID();
	}
}