package biosim.server.actuator.environment;

import biosim.server.actuator.framework.*;
import biosim.idl.actuator.environment.*;
import biosim.idl.simulation.environment.*;

public class O2AirStoreInFlowRateActuatorImpl extends GenericActuatorImpl implements O2AirStoreInFlowRateActuatorOperations{
	private O2AirConsumer myConsumer;
	private int myIndex;
	
	public O2AirStoreInFlowRateActuatorImpl(int pID){
		super(pID);
	}

	protected void processData(){
		float myFilteredValue = randomFilter(myValue);
		getOutput().setO2AirStoreInputDesiredFlowRate(myFilteredValue, myIndex);
	}
	
	protected void notifyListeners(){
		//does nothing right now
	}

	public void setOutput(O2AirConsumer pConsumer, int pIndex){
		myConsumer = pConsumer;
		myIndex = pIndex;
	}
	
	public O2AirConsumer getOutput(){
		return myConsumer;
	}
	
	public int getIndex(){
		return myIndex;
	}
	
	/**
	* Returns the name of this module (O2AirStoreInFlowRateActuator)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "O2AirStoreInFlowRateActuator"+getID();
	}
}