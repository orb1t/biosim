package biosim.server.water;

import biosim.idl.water.*;

public class GreyWaterStoreImpl extends GreyWaterStorePOA {
	private float greyWaterLevel;
	private float greyWaterCapacity;

	public GreyWaterStoreImpl(){
		greyWaterLevel = 0.0f;
		greyWaterCapacity = 10.0f;
	}

	public GreyWaterStoreImpl (float initialGreyWaterLevel, float  initialGreyWaterCapacity){
		greyWaterLevel = initialGreyWaterLevel;
		greyWaterCapacity = initialGreyWaterCapacity;
	}
	
	public void setWaterCapacity(float liters){
		greyWaterCapacity = liters;
	}

	public void setWaterLevel(float liters){
		greyWaterLevel = liters;
	}

	public float addWater(float liters){
		if ((liters +greyWaterLevel) > greyWaterCapacity){
			float returnValue = (greyWaterCapacity - greyWaterLevel);
			greyWaterLevel = greyWaterCapacity;
			return returnValue;
		}
		else{
			greyWaterLevel = greyWaterLevel + liters;
			return liters;
		}
	}

	public float takeWater(float liters){
		if ((greyWaterLevel - liters) < 0){
			greyWaterLevel = 0;
			if (liters < 0)
				return 0;
			else
				return greyWaterLevel;
		}
		else{
			greyWaterLevel = greyWaterLevel - liters;
			return liters;
		}
	}
	public float getWaterLevel(){
		return greyWaterLevel;
	}

	public void tick(){
	}
	public String getModuleName(){
		return "GreyWaterStore";
	}
}
