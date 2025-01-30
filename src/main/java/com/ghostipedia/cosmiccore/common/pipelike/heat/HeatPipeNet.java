package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.gregtechceu.gtceu.api.pipenet.LevelPipeNet;
import com.gregtechceu.gtceu.api.pipenet.PipeNet;
import net.minecraft.nbt.CompoundTag;

public class HeatPipeNet extends PipeNet<HeatPipeProperties> {

    public HeatPipeNet(LevelPipeNet<HeatPipeProperties, HeatPipeNet> world) {
        super(world);
    }

    @Override
    protected void writeNodeData(HeatPipeProperties nodeData, CompoundTag tagCompound) {

    }

    @Override
    protected HeatPipeProperties readNodeData(CompoundTag tagCompound) {
        return null;
    }
}
