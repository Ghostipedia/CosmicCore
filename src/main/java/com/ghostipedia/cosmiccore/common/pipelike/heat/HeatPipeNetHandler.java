package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.ghostipedia.cosmiccore.common.blockentity.pipelike.HeatPipeBlockEntity;
import lombok.Getter;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class HeatPipeNetHandler implements IHeatContainer {

    private final HeatPipeBlockEntity pipe;
    private final HeatPipeProperties properties;
    private double energy;
    private double lastEnergy;
    private int lastUpdateTick = -1;

    public HeatPipeNetHandler(@NotNull HeatPipeBlockEntity pipe, HeatPipeProperties properties) {
        this.pipe = pipe;
        this.properties = properties;
    }

    @Override
    public double acceptHeatFromNetwork(Direction side, double thermalEnergy) {
        update();
        energy += thermalEnergy;
        return thermalEnergy;
    }

    @Override
    public double getBaseTemperature() {
        return pipe.getEnvironmentalTemperature();
    }

    @Override
    public boolean inputsHeat(Direction side) {
        return pipe.isConnected(side);
    }

    @Override
    public boolean outputsHeat(Direction side) {
        return pipe.isConnected(side);
    }

    @Override
    public double changeHeat(double thermalEnergy) {
        update();
        energy += thermalEnergy;
        if (energy < 0) {
            thermalEnergy += energy;
            energy = 0;
        }
        return thermalEnergy;
    }

    @Override
    public float getOverloadLimit() {
        return properties.getMaxTemp();
    }

    @Override
    public double getCurrentEnergy() {
        update();
        return lastEnergy;
    }

    @Override
    public float getHeatCapacity() {
        return properties.getThermalCapacity();
    }

    @Override
    public float getConductance() {
        return properties.getConductance();
    }

    private void update() {
        int tick = pipe.getLevel().getServer().getTickCount();
        int update = tick - lastUpdateTick;
        if (update == 0 || update < 0) {
            lastUpdateTick = tick;
            return;
        }
        lastUpdateTick = tick;
        lastEnergy = pipe.loseEnergy(energy, pipe.getEnvironmentalConductivity() * properties.getConductanceEnvironment(), update);
        energy = lastEnergy;
    }
}
