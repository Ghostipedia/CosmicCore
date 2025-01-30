package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.gregtechceu.gtceu.api.pipenet.LevelPipeNet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class LevelHeatPipeNet extends LevelPipeNet<HeatPipeProperties, HeatPipeNet> {

    public static LevelHeatPipeNet getOrCreate(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(tag -> new LevelHeatPipeNet(world, tag),
                () -> new LevelHeatPipeNet(world), "cosmic_heat_pipe_net");
    }

    public LevelHeatPipeNet(ServerLevel serverLevel) {
        super(serverLevel);
    }

    public LevelHeatPipeNet(ServerLevel serverLevel, CompoundTag tag) {
        super(serverLevel, tag);
    }

    @Override
    protected HeatPipeNet createNetInstance() {
        return new HeatPipeNet(this);
    }
}
