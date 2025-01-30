package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.gregtechceu.gtceu.api.pipenet.IRoutePath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PipeRoutePath implements IRoutePath<IHeatContainer> {

    @Override
    public @NotNull BlockPos getTargetPipePos() {
        return null;
    }

    @Override
    public @NotNull Direction getTargetFacing() {
        return null;
    }

    @Override
    public int getDistance() {
        return 0;
    }

    @Override
    public @Nullable IHeatContainer getHandler(Level world) {
        return null;
    }
}
