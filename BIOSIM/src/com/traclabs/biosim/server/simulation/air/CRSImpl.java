package com.traclabs.biosim.server.simulation.air;

import com.traclabs.biosim.idl.framework.MalfunctionIntensity;
import com.traclabs.biosim.idl.framework.MalfunctionLength;
import com.traclabs.biosim.idl.simulation.air.CRSOperations;
import com.traclabs.biosim.idl.simulation.framework.CO2ConsumerDefinition;
import com.traclabs.biosim.idl.simulation.framework.CO2ConsumerOperations;
import com.traclabs.biosim.idl.simulation.framework.H2ConsumerDefinition;
import com.traclabs.biosim.idl.simulation.framework.H2ConsumerOperations;
import com.traclabs.biosim.idl.simulation.framework.O2ProducerDefinition;
import com.traclabs.biosim.idl.simulation.framework.O2ProducerOperations;
import com.traclabs.biosim.idl.simulation.framework.PotableWaterProducerDefinition;
import com.traclabs.biosim.idl.simulation.framework.PotableWaterProducerOperations;
import com.traclabs.biosim.idl.simulation.framework.PowerConsumerDefinition;
import com.traclabs.biosim.idl.simulation.framework.PowerConsumerOperations;
import com.traclabs.biosim.server.simulation.framework.CO2ConsumerDefinitionImpl;
import com.traclabs.biosim.server.simulation.framework.H2ConsumerDefinitionImpl;
import com.traclabs.biosim.server.simulation.framework.O2ProducerDefinitionImpl;
import com.traclabs.biosim.server.simulation.framework.PotableWaterProducerDefinitionImpl;
import com.traclabs.biosim.server.simulation.framework.PowerConsumerDefinitionImpl;
import com.traclabs.biosim.server.simulation.framework.SimBioModuleImpl;
import com.traclabs.biosim.server.util.OrbUtils;

/**
 * The Air Revitalization System Implementation. Takes in Air (O2, CO2, other)
 * from the environment and power from the Power Production System and produces
 * air with less CO2 and more O2.
 * 
 * @author Scott Bell
 */

public class CRSImpl extends SimBioModuleImpl implements CRSOperations,
        PowerConsumerOperations, PotableWaterProducerOperations, O2ProducerOperations,
        CO2ConsumerOperations, H2ConsumerOperations {
    //Consumers, Producers
    private PowerConsumerDefinitionImpl myPowerConsumerDefinitionImpl;
    private PotableWaterProducerDefinitionImpl myPotableWaterProducerDefinitionImpl;
    private O2ProducerDefinitionImpl myO2ProducerDefinitionImpl;
    private CO2ConsumerDefinitionImpl myCO2ConsumerDefinitionImpl;
    private H2ConsumerDefinitionImpl myH2ConsumerDefinitionImpl;
    
    private float currentPowerConsumed = 0f;
    
    private float currentCO2Consumed;

    private float currentH2Consumed;

    private float currentH2OProduced;

    private float currentCH4Produced;
    
    private float CH4Produced = 0f;

    private float currentO2Produced;
    
    //multiply times power to determine how much air/H2/water we're consuming
    private static final float LINEAR_MULTIPLICATIVE_FACTOR = 100;

    public CRSImpl(int pID, String pName) {
        super(pID, pName);
        myPowerConsumerDefinitionImpl = new PowerConsumerDefinitionImpl();
        myPotableWaterProducerDefinitionImpl = new PotableWaterProducerDefinitionImpl();
        myO2ProducerDefinitionImpl = new O2ProducerDefinitionImpl();
        myCO2ConsumerDefinitionImpl = new CO2ConsumerDefinitionImpl();
        myH2ConsumerDefinitionImpl = new H2ConsumerDefinitionImpl();
    }
    
    public PowerConsumerDefinition getPowerConsumerDefinition(){
        return (PowerConsumerDefinition)(OrbUtils.poaToCorbaObj(myPowerConsumerDefinitionImpl));
    }
    
    public PotableWaterProducerDefinition getPotableWaterProducerDefinition(){
        return (PotableWaterProducerDefinition)(OrbUtils.poaToCorbaObj(myPotableWaterProducerDefinitionImpl));
    }
    
    public CO2ConsumerDefinition getCO2ConsumerDefinition(){
        return (CO2ConsumerDefinition)(OrbUtils.poaToCorbaObj(myCO2ConsumerDefinitionImpl));
    }
    
    public O2ProducerDefinition getO2ProducerDefinition(){
        return (O2ProducerDefinition)(OrbUtils.poaToCorbaObj(myO2ProducerDefinitionImpl));
    }
    
    public H2ConsumerDefinition getH2ConsumerDefinition(){
        return (H2ConsumerDefinition)(OrbUtils.poaToCorbaObj(myH2ConsumerDefinitionImpl));
    }
    
    private void gatherPower() {
        currentPowerConsumed = myPowerConsumerDefinitionImpl.getMostResourceFromStore();
    }
    /**
     * Processes a tick by collecting referernces (if needed), resources, and
     * pushing the new air out.
     */
    public void tick() {
        super.tick();
        gatherPower();
        gatherH2andCO2();
        pushWaterAndMethane();
    }
    
    private void gatherH2andCO2() {
        float CO2Needed = currentPowerConsumed * LINEAR_MULTIPLICATIVE_FACTOR;
        float H2Needed = CO2Needed * 4f;
        float filteredCO2Needed = randomFilter(CO2Needed);
        float filteredH2Needed = randomFilter(H2Needed);
        currentCO2Consumed = myCO2ConsumerDefinitionImpl.getResourceFromStore(filteredCO2Needed);
        currentH2Consumed = myH2ConsumerDefinitionImpl.getResourceFromStore(filteredH2Needed);
    }

    private void pushWaterAndMethane() {
        if ((currentH2Consumed <= 0) || (currentCO2Consumed <= 0)) {
            currentH2OProduced = 0f;
            currentCH4Produced = 0f;
            myH2ConsumerDefinitionImpl.pushResourceToStore(currentH2Consumed);
            myCO2ConsumerDefinitionImpl.pushResourceToStore(currentCO2Consumed);
        } else {
            // CO2 + 4H2 --> CH4 + 2H20
            float limitingReactant = Math.min(currentH2Consumed / 4f,
                    currentCO2Consumed);
            if (limitingReactant == currentH2Consumed)
                myCO2ConsumerDefinitionImpl.pushResourceToStore(currentCO2Consumed - limitingReactant); 
            else
                myH2ConsumerDefinitionImpl.pushResourceToStore(currentH2Consumed - 4f * limitingReactant);
            float waterMolesProduced = 2f * limitingReactant;
            float waterLitersProduced = (waterMolesProduced * 18.01524f) / 1000f; //1000g/liter,
            // 18.01524g/mole
            float methaneMolesProduced = limitingReactant;
            currentH2OProduced = randomFilter(waterLitersProduced);
            currentCH4Produced = randomFilter(methaneMolesProduced);
        }
        float distributedWaterLeft = myPotableWaterProducerDefinitionImpl.pushResourceToStore(currentH2OProduced);
        CH4Produced += currentCH4Produced;
    }
    
    protected String getMalfunctionName(MalfunctionIntensity pIntensity,
            MalfunctionLength pLength) {
        return "None";
    }

    protected void performMalfunctions() {
    }

    /**
     * Resets production/consumption levels.
     */
    public void reset() {
        super.reset();
        CH4Produced = 0f;
    }

}