package biosim.server.simulation.food;

import biosim.idl.simulation.food.*;
/**
 * Wheat
 * @author    Scott Bell
 */

public class Wheat extends PlantImpl{
	public Wheat(ShelfImpl pShelfImpl){
		super(pShelfImpl);
		canopyClosureConstants[0] = 95488f;;
		canopyClosureConstants[1] = 1068.6f;
		canopyClosureConstants[6] = 15.977f;
		canopyClosureConstants[10] = 0.3419f;
		canopyClosureConstants[11] = .00019733f;
		canopyClosureConstants[15] = -.00019076f;
		
		canopyQYConstants[6] = .044793f;
		canopyQYConstants[7] = .000051583f;
		canopyQYConstants[8] = -.000000020724f;
		canopyQYConstants[11] = -.0000051946f;
		canopyQYConstants[17] = -.0000000000049303f;
		canopyQYConstants[18] = .0000000000000022255f;
	}

	public PlantType getPlantType(){
		return PlantType.WHEAT;
	}
	
	public float getPPFNeeded(){
		return 4791667f;
	}
	
	protected float getBCF(){
		return 0.44f;
	}
	
	protected float getCarbonUseEfficiency24(){
		return 0.64f;
	}
	
	protected float getPhotoperiod(){
		return 20f;
	}
	
	protected float getN(){
		return 1.0f;
	}
	
	protected float getCQYMin(){
		return .001f;
	}
	
	protected float getTimeTillCanopySenescence(){
		return 33f;
	}
	
	protected float getTimeTillCropMaturity(){
		return 62f;
	}
	
	protected float getWaterNeeded(){
		return 0.1f;
	}
	
	protected float getOPF(){
		return 1.07f;
	}
	
	//FIX!!!
	private float calculateRelativeHumidity(){
		float molesOfWater = myShelfImpl.getBiomassRSImpl().getAirOutputs()[0].getWaterMoles();
		float waterPressure = myShelfImpl.getBiomassRSImpl().getAirOutputs()[0].getWaterPressure();
		return molesOfWater;
	}
	
	protected float calculateCanopyStomatalConductance(){
		float relativeHumdity = calculateRelativeHumidity();
		return 0.1389f + 15.32f * relativeHumdity * (calculateNetCanopyPhotosynthesis() / calculateCO2());
	}
	
	protected float calculateAtmosphericAeroDynamicConductance(){
		return 5.5f;
	}
}
