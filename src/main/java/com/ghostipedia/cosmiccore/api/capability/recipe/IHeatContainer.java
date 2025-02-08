package com.ghostipedia.cosmiccore.api.capability.recipe;

import net.minecraft.core.Direction;

public interface IHeatContainer {
    /** This method is similar to {@link #changeHeat(double)}
     * <br>
     *
     *
     * This method is the logic for when this container RECIEVES heat from the specified direction,
     * used when heat goes between 2 blocks
    */
    double acceptHeatFromNetwork(Direction side, double thermalEnergy);

    //Returns: if this container can accept heat from this side.
    boolean inputsHeat(Direction side);

    // @returns if this container can eject heat from this side.
    default boolean outputsHeat(Direction side){
        return false;
    };

    /*  used for changing heat value internally in the block
     */
    double changeHeat(double thermalEnergy);

    /* Adds a set amount of heat to this heat container
     * Params : heatToAdd - amount of heat to add.
     * Returns : amount of heat added.
     * */
    default double addHeat(double heatToAdd) {
        return changeHeat(heatToAdd);
    }

    /* Removes a set amount of heat to this heat container
     * Params : heatToRemove - amount of heat to remove.
     * Returns : amount of heat removed.
     * */
    default double removeHeat(double heatToRemove) {
        return -changeHeat(-heatToRemove);
    }

    //Reports the Current Thermal Maximum a container can withstand
    float getOverloadLimit();

    double getCurrentEnergy();

    float getHeatCapacity();

    //Reports the Current Temperature.
    default double getCurrentTemperature() {
        return getCurrentEnergy() / getHeatCapacity() + getBaseTemperature();
    }

    default double getBaseTemperature() {
        return 300;
    }

    float getConductance();

    // Params needs to build the container.
    //This Abomination - Allows Going below Absolute Zero
    default boolean supportsImpossibleHeatValues() {
        return false;
    }

    default double getHeatChangeToFitWithinTempLimits() {
        double temp = getCurrentTemperature();
        if (temp > getOverloadLimit()) {
            return (getOverloadLimit() - getBaseTemperature()) * getHeatCapacity() - getCurrentEnergy();
        } else if (!supportsImpossibleHeatValues() && temp < 0) {
            return -getBaseTemperature() * getHeatCapacity() - getCurrentEnergy();
        } else return 0;
    }

    IHeatContainer DEFAULT = new IHeatContainer() {
        @Override
        public double acceptHeatFromNetwork(Direction side, double thermalEnergy) {
            return 0;
        }

        @Override
        public boolean inputsHeat(Direction side) {
            return false;
        }

        @Override
        public double changeHeat(double thermalEnergy) {
            return 0;
        }

        @Override
        public float getOverloadLimit() {
            return 0;
        }

        @Override
        public double getCurrentEnergy() {
            return 0;
        }

        @Override
        public float getHeatCapacity() {
            return 1;
        }

        @Override
        public float getConductance() {
            return 0;
        }
    };

}
