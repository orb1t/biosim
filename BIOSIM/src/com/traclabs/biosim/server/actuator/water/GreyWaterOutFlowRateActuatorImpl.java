package biosim.server.actuator.water;

import biosim.server.actuator.framework.*;
import biosim.idl.actuator.water.*;
import biosim.idl.simulation.water.*;

public class GreyWaterOutFlowRateActuatorImpl extends GenericActuatorImpl implements GreyWaterOutFlowRateActuatorOperations{
	private GreyWaterProducer myProducer;
	private int myIndex;
	
	public GreyWaterOutFlowRateActuatorImpl(int pID){
		super(pID);
	}

	protected void processData(){
		float myFilteredValue = randomFilter(myValue);
		getOutput().setGreyWaterOutputDesiredFlowRate(myFilteredValue, myIndex);
	}
	
	protected void notifyListeners(){
		//does nothing right now
	}

	public void setOutput(GreyWaterProducer pProducer, int pIndex){
		myProducer = pProducer;
		myIndex = pIndex;
	}
	
	public GreyWaterProducer getOutput(){
		return myProducer;
	}
	
	public int getIndex(){
		return myIndex;
	}
	
	/**
	* Returns the name of this module (GreyWaterOutFlowRateActuator)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "GreyWaterOutFlowRateActuator"+getID();
	}
}