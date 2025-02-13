package com.ghostipedia.cosmiccore.api.capability;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import static com.ghostipedia.cosmiccore.api.capability.CosmicCapabilities.CAPABILITY_HEAT_CONTAINER;

public class HeatCapabilityProvider implements ICapabilityProvider {
    private final MetaMachineBlockEntity machine;
    private LazyOptional<IHeatContainer> container = null;

    public HeatCapabilityProvider(MetaMachineBlockEntity machine) {
        this.machine = machine;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return CAPABILITY_HEAT_CONTAINER.orEmpty(capability, getContainer());
    }

    public LazyOptional<IHeatContainer> getContainer() {
        if (container == null) {
            if (machine.metaMachine instanceof IHeatContainer heatContainer) {
                container = LazyOptional.of(() -> heatContainer);
            } else if (machine.metaMachine instanceof IMultiController){
                container = LazyOptional.of(HeatContainerWrapper::new);
            } else {
                container = LazyOptional.empty();
            }
        }
        return container;
    }

    @Getter
    @Setter
    public static class HeatContainerWrapper implements IHeatContainer {
        private double currentEnergy = 0;

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
            currentEnergy += thermalEnergy;
            double fit = getHeatChangeToFitWithinTempLimits();
            currentEnergy -= fit;
            return thermalEnergy - fit;
        }

        @Override
        public float getOverloadLimit() {
            return Float.MAX_VALUE;
        }

        @Override
        public float getHeatCapacity() {
            return 1000f;
        }

        @Override
        public float getConductance() {
            return 0;
        }
    }
}
