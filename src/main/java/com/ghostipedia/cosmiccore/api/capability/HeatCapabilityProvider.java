package com.ghostipedia.cosmiccore.api.capability;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ghostipedia.cosmiccore.api.capability.CosmicCapabilities.CAPABILITY_HEAT_CONTAINER;

public class HeatCapabilityProvider implements ICapabilityProvider {
    private MetaMachineBlockEntity machine;

    public HeatCapabilityProvider(MetaMachineBlockEntity machine) {
        this.machine = machine;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if(machine.metaMachine instanceof IHeatContainer heatMachine) {
            return CAPABILITY_HEAT_CONTAINER.orEmpty(capability, LazyOptional.of(() -> heatMachine));
        }
        return LazyOptional.empty();
    }
}
