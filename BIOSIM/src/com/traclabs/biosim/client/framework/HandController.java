
package biosim.client.framework;

import java.util.*;
import java.io.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import biosim.idl.framework.BioDriver;
import biosim.idl.framework.BioDriverHelper;
import biosim.idl.framework.BioModule;
import biosim.idl.sensor.air.*;
import biosim.idl.sensor.crew.*;
import biosim.idl.sensor.environment.*;
import biosim.idl.sensor.food.*;
import biosim.idl.sensor.framework.*;
import biosim.idl.sensor.power.*;
import biosim.idl.sensor.water.*;
import biosim.idl.actuator.air.*;
import biosim.idl.actuator.crew.*;
import biosim.idl.actuator.environment.*;
import biosim.idl.actuator.food.*;
import biosim.idl.actuator.framework.*;
import biosim.idl.actuator.power.*;
import biosim.idl.actuator.water.*;
import biosim.client.util.*;
import biosim.idl.simulation.crew.*;
import biosim.idl.simulation.water.*;
import biosim.idl.simulation.air.*;
import biosim.idl.simulation.food.*;
import biosim.idl.simulation.power.*;
import biosim.idl.simulation.environment.*;
import biosim.idl.simulation.framework.*;

/**
 * @author    Theresa Klein
 To compile:
 1) run make-client.sh
 2) javac -classpath .:/home/tjk2/JNL/Classes:$BIOSIM_HOME/lib/jacorb/jacorb.jar:$BIOSIM_HOME/generated/client/classes HandController.java

 javac - the compiler
 jacorb.jar - the library that has the ORB and various CORBA utilities
 generated/client/classes - the generated client stubs
 TestBiosim - this file

 
 To run:
 1)run run-nameserver.sh
 2)run run-server.sh
 3)
 java -classpath .:/home/tjk2/JNL/Classes:$BIOSIM_HOME/lib/jacorb/jacorb.jar:$BIOSIM_HOME/lib/jacorb:$BIOSIM_HOME/generated/client/classes -Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton -DORBInitRef.NameService=file:$BIOSIM_HOME/generated/ns/ior.txt HandController
 (all the above on one line)
 
 -Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB - overriding Sun's default ORB (using Jacorb instead)
 -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton - overriding Sun's default ORB (using Jacorb instead)
 -DORBInitRef.NameService=file:$BIOSIM_HOME/generated/ns/ior.txt - telling the client where to look for the ior (serialized nameservice object, produced by run-nameserver.sh)
 
 Good Luck!  If you have any questions, email me at:
 scott@traclabs.com

 */

public class HandController{

	//feedback loop stuff.
	float CrewO2Level = .20f; 
	float CrewCO2Level = 0.01f; 
	float PlantO2Level =  .2f; 
	float PlantCO2Level = .001f;
	float crewO2integral, crewCO2integral, plantO2integral, plantCO2integral;
 

	// hand controller stuff;
	int water = 0;
	int gwater =0; 
	int CO2 = 0;
	int potable = 0;


	StateMap ContinuousState; 
	ActionMap currentAction;
	Map ClassifiedState;

	static int Runs = 0;

	static Map ThresholdMap = new TreeMap();

	static BioDriver myBioDriver;
	static BioHolder myBioHolder;
	
	static int ATMOSPHERIC_PERIOD =1; 
	static int CORE_PERIOD_MULT = 10; 
	static int PLANT_PERIOD_MULT = 10; 
	
	public static String[] stateNames = {"carbondioxide", "dirtywater", "greywater", "hydrogen", "oxygen", "potablewater"}; 
	
	public static String[] actuatorNames = {"airRSCO2", "airRSpotable", "waterRSdirty", "waterRSgrey"};
	

	File OutFile = new File("output.txt");
	FileWriter fw;
	PrintWriter pw;
	


	public static void main(String[] args){

		HandController myController = new HandController();
		while (Runs<1) {
			myController.runSim();
		}

	}

	

	public void runSim(){
		// ticks the sim one step at a time, observes the state, updates policy and predictive model in
		// response to the current state and modifies actuators in response
		int i;
		float tmp2, oldvalue, newvalue;
		GenericActuator currentActuator; 	
		
		setThresholds();
		myBioHolder = BioHolderInitializer.getBioHolder();
		myBioDriver = myBioHolder.theBioDriver;
		
		try { 	fw = new FileWriter(OutFile) ; }
		catch (IOException e) {}
		pw = new PrintWriter(fw, true) ;
		
		myBioDriver.startSimulation();
		BiomassRS myBiomassRS = (BiomassRS)(myBioHolder.theBiomassRSModules.get(0));  
		myBiomassRS.setAutoHarvestAndReplantEnabled(false); 
		FoodProcessor myFoodProcessor = (FoodProcessor)myBioHolder.theFoodProcessors.get(0);

		
		CrewGroup myCrew = (CrewGroup)myBioHolder.theCrewGroups.get(0);
		WaterRS myWaterRS = (WaterRS)myBioHolder.theWaterRSModules.get(0);
		AirRS myAirRS = (AirRS)myBioHolder.theAirRSModules.get(0);
		//supply power
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.thePowerInFlowRateActuators, myWaterRS));
		currentActuator.setValue(500);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.thePowerInFlowRateActuators, myAirRS));
		currentActuator.setValue(300);
		//turn vccr off 
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theAirInFlowRateActuators, myAirRS));
		currentActuator.setValue(0);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2OutFlowRateActuators, myAirRS));
		currentActuator.setValue(0);
		//set values for other inputs and outputs
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theH2InFlowRateActuators, myAirRS));
		currentActuator.setValue(40);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.thePotableWaterOutFlowRateActuators, myAirRS));
		currentActuator.setValue(20);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.thePotableWaterOutFlowRateActuators, myWaterRS));
		currentActuator.setValue(10);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theH2OutFlowRateActuators, myAirRS));
		currentActuator.setValue(10);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theO2OutFlowRateActuators, myAirRS));
		currentActuator.setValue(10);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theBiomassInFlowRateActuators, myFoodProcessor));
		currentActuator.setValue(100);
		
		// initialize everything to off
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2InFlowRateActuators, myAirRS));
		currentActuator.setValue(0);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.thePotableWaterInFlowRateActuators, myAirRS));
		currentActuator.setValue(0);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theDirtyWaterInFlowRateActuators, myWaterRS));
		currentActuator.setValue(0);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theGreyWaterInFlowRateActuators, myWaterRS));
		currentActuator.setValue(0);

		
		

		System.out.println("starting run...");
		crewO2integral = 0f;
		crewCO2integral = 0f;
		plantO2integral = 0f;
		plantCO2integral = 0f;
		Runs ++;

		
		while (!myBioDriver.simulationHasStarted()) { 
			try {Thread.sleep(10000);}
			catch (InterruptedException e) { 
			}
		}
	//	for (i=0;i<20;i++) myBioDriver.advanceOneTick();

		ContinuousState = new StateMap();
		ContinuousState.updateState();
		ClassifiedState = classifyState(ContinuousState); 
		currentAction = handController(ClassifiedState); 
		setActuators(currentAction);
		//int max_ticks = (int)Math.pow(Runs, 2); 		
		while ((!myCrew.anyDead() ) /*&& myBioDriver.getTicks() < max_ticks*/) {			
			if (((myBioDriver.getTicks())%(CORE_PERIOD_MULT*ATMOSPHERIC_PERIOD)) == 0) {
				System.out.println(myBioDriver.getTicks());
				ContinuousState.printMe();
				currentAction.printMe();
				ContinuousState.updateState();
				ClassifiedState = classifyState(ContinuousState); 
				currentAction = handController(ClassifiedState);
				setActuators(currentAction);
				doFoodProcessor();
				if (((myBioDriver.getTicks())%(PLANT_PERIOD_MULT*CORE_PERIOD_MULT*ATMOSPHERIC_PERIOD)) == 0) {
					doPlants();
				}
			}
			
			//advancing the sim n ticks 
			for (i=0;i<ATMOSPHERIC_PERIOD;i++) myBioDriver.advanceOneTick();
			doAccumulatorsInjectors();

		}
		System.out.println("crew dead at "+myBioDriver.getTicks()+" ticks");
		myBioDriver.endSimulation();


	}

	public static void setThresholds() {

		// sets up the threshold map variable
		int i;
		Map subMap;
		int DirtyWaterLowLevel = 200;
		int DirtyWaterHighLevel = 800;
		int GreyWaterLowLevel = 200;
		int GreyWaterHighLevel = 800;
		int PotableWaterLowLevel = 200;
		int PotableWaterHighLevel = 800;
		int O2StoreLowLevel = 400;
		int O2StoreHighLevel = 1600;
		int CO2StoreLowLevel = 200;
		int CO2StoreHighLevel = 950;
		int H2StoreLowLevel = 1000;
		int H2StoreHighLevel = 8000;


		subMap = new TreeMap();
		subMap.put("low", new Integer(DirtyWaterLowLevel));
		subMap.put("high", new Integer(DirtyWaterHighLevel));
		ThresholdMap.put("dirtywater", subMap);
		
		subMap = new TreeMap();
		subMap.put("low", new Integer(GreyWaterLowLevel));
		subMap.put("high", new Integer(GreyWaterHighLevel));
		ThresholdMap.put("greywater", subMap);

		subMap = new TreeMap();
		subMap.put("low", new Integer(PotableWaterLowLevel));
		subMap.put("high", new Integer(PotableWaterHighLevel));
		ThresholdMap.put("potablewater", subMap);
		
		subMap = new TreeMap();
		subMap.put("low", new Integer(O2StoreLowLevel));
		subMap.put("high", new Integer(O2StoreHighLevel));
		ThresholdMap.put("oxygen", subMap);

		subMap = new TreeMap();
		subMap.put("low", new Integer(CO2StoreLowLevel));
		subMap.put("high", new Integer(CO2StoreHighLevel));
		ThresholdMap.put("carbondioxide", subMap);
		
		subMap = new TreeMap();
		subMap.put("low", new Integer(H2StoreLowLevel));
		subMap.put("high", new Integer(H2StoreHighLevel));
		ThresholdMap.put("hydrogen", subMap);


	}

	public Map classifyState(StateMap instate) {

		int i;

		Map state = new TreeMap();

		Map thisSet;
		GenericSensor currentSensor;
		String fileoutput;
		DirtyWaterStore myDirtyWaterStore = (DirtyWaterStore)myBioHolder.theDirtyWaterStores.get(0);
		PotableWaterStore myPotableWaterStore = (PotableWaterStore)myBioHolder.thePotableWaterStores.get(0);
		GreyWaterStore myGreyWaterStore = (GreyWaterStore)myBioHolder.theGreyWaterStores.get(0);

		O2Store myO2Store = (O2Store)myBioHolder.theO2Stores.get(0);
		CO2Store myCO2Store = (CO2Store)myBioHolder.theCO2Stores.get(0);
		H2Store myH2Store = (H2Store)myBioHolder.theH2Stores.get(0);


		fileoutput = myBioDriver.getTicks()+"   ";


		for (i =0;i<stateNames.length;i++) {

			thisSet = (Map)ThresholdMap.get(stateNames[i]);
			fileoutput += instate.getStateValue(stateNames[i]) + "   ";
			if (instate.getStateValue(stateNames[i])< ((Integer)thisSet.get("low")).intValue())
				state.put(stateNames[i], "low");
			else if (instate.getStateValue(stateNames[i]) > ((Integer)thisSet.get("high")).intValue())
				state.put(stateNames[i], "high");
			else state.put(stateNames[i], "normal");
		}
		
		SimEnvironment myCrewEnvironment = (SimEnvironment)myBioHolder.theSimEnvironments.get(0);
		SimEnvironment myPlantEnvironment = (SimEnvironment)myBioHolder.theSimEnvironments.get(1);
		BiomassStore myBiomassStore = (BiomassStore)myBioHolder.theBiomassStores.get(0);
		FoodStore myFoodStore = (FoodStore)myBioHolder.theFoodStores.get(0);

		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theBiomassStoreLevelSensors, myBiomassStore));
		fileoutput += currentSensor.getValue()+"   ";

		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theFoodStoreLevelSensors, myFoodStore));
		fileoutput += currentSensor.getValue()+"   ";

		
		
		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theO2AirConcentrationSensors, myCrewEnvironment));
//		System.out.println("Crew O2..."+currentSensor.getValue());
		fileoutput += currentSensor.getValue()+"   ";

		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theCO2AirConcentrationSensors, myCrewEnvironment));
//		System.out.println("Crew CO2..."+currentSensor.getValue());
		fileoutput += currentSensor.getValue()+"   ";

		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theWaterAirConcentrationSensors, myCrewEnvironment));
//		System.out.println("Crew Water Vapor..."+currentSensor.getValue());
		fileoutput += currentSensor.getValue()+"   ";


		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theO2AirConcentrationSensors, myPlantEnvironment));
		System.out.println("Plant O2..."+currentSensor.getValue());
		fileoutput += currentSensor.getValue()+"   ";


		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theCO2AirConcentrationSensors, myPlantEnvironment));
		System.out.println("Plant CO2..."+currentSensor.getValue());
		fileoutput += currentSensor.getValue()+"   ";
		
		currentSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theWaterAirConcentrationSensors, myPlantEnvironment));
//		System.out.println("Plant Water Vapor..."+currentSensor.getValue());
		fileoutput += currentSensor.getValue()+"   ";
		
		pw.println(fileoutput);

		return state;
	}


	
	public void setActuators(ActionMap currentAction) {

		// sets actuator values according to the current policy

		GenericActuator currentActuator;
		int i;

		String[] names = ActionMap.actuatorNames;
		
		for (i = 0;i<names.length;i++) {
			currentActuator = (GenericActuator)(ActionMap.actuators[i]);
			currentActuator.setValue((float)currentAction.getActuatorValue(names[i]));
//			System.out.println("Setting "+names[i]+" to "+currentAction.getActuatorValue(names[i]));
		}



	}

	private void doAccumulatorsInjectors() { 
	
	// a crude feedback controller for the accumulators and injectors 
		GenericSensor levelSensor, rateSensor;  
		GenericActuator currentActuator; 
		
		float delta, signal; 
		float crewO2p, crewCO2p, plantO2p, plantCO2p; 
		float crewO2i, crewCO2i, plantO2i, plantCO2i; 
		float crewO2, crewCO2, plantO2, plantCO2; 
		SimEnvironment myCrewEnvironment = (SimEnvironment)myBioHolder.theSimEnvironments.get(0);
		SimEnvironment myPlantEnvironment = (SimEnvironment)myBioHolder.theSimEnvironments.get(1);
		Injector myInjector = (Injector)myBioHolder.theInjectors.get(0);
		Accumulator myAccumulator = (Accumulator)myBioHolder.theAccumulators.get(0);
		
		//crew O2 feedback control
		crewO2p = 200f;
		crewO2i = 0.2f;
		levelSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theO2AirConcentrationSensors, myCrewEnvironment));
		crewO2 = levelSensor.getValue();
		delta = (float)(CrewO2Level - crewO2);
		crewO2integral += delta;
		signal = delta*crewO2p + crewO2i*crewO2integral;
//		System.out.println("O2 flow from tank to Crew environment: "+signal);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theO2AirEnvironmentOutFlowRateActuators, myInjector));
		currentActuator.setValue((float)(signal));
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theO2AirStoreInFlowRateActuators, myInjector));
		currentActuator.setValue((float)(signal));

		//crew CO2 feedback control
		crewCO2p = -200f;
		crewCO2i = -0.2f;
		levelSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theCO2AirConcentrationSensors, myCrewEnvironment));
		crewCO2 = levelSensor.getValue();
		delta = (float)(CrewCO2Level - crewCO2);
		crewCO2integral += delta;
		signal = delta*crewCO2p + crewCO2i*crewCO2integral;
//		System.out.println("CO2 flow from Crew environment to tank: "+signal);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2AirEnvironmentInFlowRateActuators, myAccumulator));
		currentActuator.setValue((float)(signal));
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2AirStoreOutFlowRateActuators, myAccumulator));
		currentActuator.setValue((float)(signal));

		//plant O2 feedback control
		plantO2p = -300f;
		plantO2i = -2f;
		levelSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theO2AirConcentrationSensors, myPlantEnvironment));
		plantO2 = levelSensor.getValue();
		delta = (float)(PlantO2Level - plantO2);
		plantO2integral += delta;
		signal = delta*plantO2p + plantO2i*plantO2integral;
//		System.out.println("O2 flow from Plant environment to tank: "+signal);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theO2AirEnvironmentInFlowRateActuators, myAccumulator));
		currentActuator.setValue((float)(signal));
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theO2AirStoreOutFlowRateActuators, myAccumulator));
		currentActuator.setValue((float)(signal));

		//plant CO2 feedback control
		plantCO2p = 10000f;
		plantCO2i = 5f;
		levelSensor = (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theCO2AirConcentrationSensors, myPlantEnvironment));
		plantCO2 = levelSensor.getValue();
		delta = (float)(PlantCO2Level - plantCO2);
		plantCO2integral += delta;
		signal = delta*plantCO2p + plantCO2i*plantCO2integral;
		//System.out.println("CO2 flow from tank to Plant environment: "+signal);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2AirEnvironmentOutFlowRateActuators, myInjector));
		currentActuator.setValue((float)(signal));
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2AirStoreInFlowRateActuators, myInjector));
		currentActuator.setValue((float)(signal));
		
				
		currentActuator = (GenericActuator)myBioHolder.theWaterAirEnvironmentInFlowRateActuators.get(0); 
		currentActuator.setValue(2f);
		currentActuator = (GenericActuator)myBioHolder.theWaterAirEnvironmentInFlowRateActuators.get(1); 
		currentActuator.setValue(3.7f);
		currentActuator = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.theWaterAirStoreOutFlowRateActuators, myAccumulator));
		currentActuator.setValue(6f);
	
	} 

	private void doFoodProcessor() {
		GenericActuator FoodProcessorPower;
		GenericSensor BiomassStore, FoodStore;
		float food, biomass;
		boolean turnon = true;
		
		FoodProcessor myFoodProcessor = (FoodProcessor)myBioHolder.theFoodProcessors.get(0);
		BiomassStore myBiomassStore = (BiomassStore)myBioHolder.theBiomassStores.get(0);
		FoodStore myFoodStore = (FoodStore)myBioHolder.theFoodStores.get(0);
		GenericSensor myFoodStoreLevelSensor= (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theFoodStoreLevelSensors, myFoodStore));
		GenericSensor myBiomassStoreLevelSensor= (GenericSensor)(myBioHolder.getSensorAttachedTo(myBioHolder.theBiomassStoreLevelSensors, myBiomassStore));
		FoodProcessorPower = (GenericActuator)(myBioHolder.getActuatorAttachedTo(myBioHolder.thePowerInFlowRateActuators, myFoodProcessor));
		
		biomass = (float)myBiomassStoreLevelSensor.getValue();
		food = (float)myFoodStoreLevelSensor.getValue();

		
		if (biomass == 0) turnon = false;
		else if (food >= 1800) turnon = false;
		else turnon = true;

		System.out.println("Food: "+food+" Biomass "+biomass+ "     Food Processor Power: "+turnon);

		if (turnon)
			FoodProcessorPower.setValue(100);
		else FoodProcessorPower.setValue(0);

	}

	private ActionMap handController(Map SimState) {
		ActionMap myAction;
		if (SimState.get("potablewater") == "low") {
			water = 1;
		}
		else if (SimState.get("dirtywater") == "high" && SimState.get("potablewater") != "high") {
			water = 1;
		}
		else { 
			water = 0;
		}
		if (SimState.get("potablewater") == "low" && SimState.get("greywater") != "low") {
			gwater = 1;
		}
		else if (SimState.get("greywater") == "high" && SimState.get("potablewater") != "high") {
			gwater = 1;
		}
		else { 
			gwater = 0;
		}
		
		if (SimState.get("carbondioxide") == "low") {
			CO2 = 0;
		}
		if (SimState.get("hydrogen") == "low") { 
			CO2 = 0; 
		}
		if (SimState.get("hydrogen") == "high") { 
			CO2 = 1 ; 
			potable = 0; 
		}
		if (SimState.get("oxygen") == "low") {
			potable = 1;
		}
		if (SimState.get("oxygen") == "high" ) {
			potable = 0;
		}
		if (SimState.get("carbondioxide") == "high") {
			CO2 = 1;
		}
		
		System.out.println("CRS: "+CO2+" OGS: "+potable+" Dirty Water: "+water+" Grey Water: "+gwater);
		myAction = new ActionMap(new int[] {CO2, potable, water, gwater});
		return myAction;

	}
	
	private void doPlants() { 
		float overflow, new_overflow;
		float cropacres; 
		GenericSensor harvestSensor; 
		GenericSensor currentSensor; 
		PlantingActuator currentActuator;
		
		CO2Store myCO2Store = (CO2Store)myBioHolder.theCO2Stores.get(0);
		BiomassRS myBiomassRS = (BiomassRS)(myBioHolder.theBiomassRSModules.get(0));  
		currentActuator = PlantingActuatorHelper.narrow((myBioHolder.getActuatorAttachedTo(myBioHolder.thePlantingActuators, myBiomassRS)));
		HarvestingActuator harvestActuator = HarvestingActuatorHelper.narrow((myBioHolder.getActuatorAttachedTo(myBioHolder.theHarvestingActuators, myBiomassRS)));
		
		int i; 
		int num = myBioHolder.theHarvestSensors.size(); 
		
		
		for (i=0;i<num;i++) { 
//			currentSensor = (GenericSensor)myBioHolder.getShelfSensorAttachedTo(myBioHolder.theHarvestSensors, myBiomassRS, i); 
//			currentActuator = (PlantingActuator)myBioHolder.getShelfActuatorAttachedTo(myBioHolder.thePlantingActuators, myBiomassRS, i);
 			currentSensor = (HarvestSensor)myBioHolder.theHarvestSensors.get(i); 
			currentActuator = (PlantingActuator)myBioHolder.thePlantingActuators.get(i); 
			
			if (currentSensor.getValue() == 1f) { 
				System.out.println(" Harvest Sensor "+currentSensor.getValue());
				myBiomassRS.getShelf(i).harvest(); 
				cropacres = myBiomassRS.getShelf(i).getCropAreaTotal();
	//			System.out.println("Planting "+cropacres+" m^2."+((Shelf)myBiomassRS.getShelf(i)).getTypeString()); 
				currentActuator.setPlantType(myBiomassRS.getShelf(i).getCropType());
				currentActuator.setValue(cropacres);	
				plantCO2integral = 0;
			}
		}
	}
		

}

