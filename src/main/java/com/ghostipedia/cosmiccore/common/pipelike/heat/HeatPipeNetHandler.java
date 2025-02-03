package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.ghostipedia.cosmiccore.common.blockentity.pipelike.HeatPipeBlockEntity;
import lombok.Getter;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class HeatPipeNetHandler implements IHeatContainer {

    @Getter
    private HeatPipeNet net;
    private boolean transfer;
    private final HeatPipeBlockEntity pipe;
    private final Direction facing;

    public HeatPipeNetHandler(@NotNull HeatPipeNet net, @NotNull HeatPipeBlockEntity pipe, Direction facing) {
        this.net = net;
        this.pipe = pipe;
        this.facing  = facing;
    }

    public void updateNet(HeatPipeNet net) {
        this.net = net;
    }

    @Override
    public long acceptHeatFromNetwork(Direction side, long heatDiff) {
        return 0;
    }

    @Override
    public boolean inputsHeat(Direction side) {
        return false;
    }

    @Override
    public long changeHeat(long heatDifference) {
        return 0;
    }

    @Override
    public long getOverloadLimit() {
        return 0;
    }

    @Override
    public long getCurrentTemperature() {
        var container = pipe.getHeatContainer(null);
        if(container != null)
            return pipe.getCurrentTemp();
        return HeatPipeBlockEntity.AMBIENT_TEMP.getCurrentTemperature();
    }

    @Override
    public float getThermalConductance() {
        return pipe.getNodeData().getMaxTransferRate();
    }
}
