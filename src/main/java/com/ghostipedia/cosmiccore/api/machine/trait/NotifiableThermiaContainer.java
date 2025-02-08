package com.ghostipedia.cosmiccore.api.machine.trait;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class NotifiableThermiaContainer extends NotifiableRecipeHandlerTrait<Integer> implements IHeatContainer {
    @Getter
    private final IO handlerIO;
    @Getter
    private final float overloadLimit;
    @Persisted
    @DescSynced
    @Getter
    private double energy;
    @Setter
    private Predicate<Direction> sideInputCondition;
    @Setter
    private Predicate<Direction> sideOutputCondition;
    public NotifiableThermiaContainer(MetaMachine machine, IO io, float overloadLimit, double thermalEnergy) {
        super(machine);
        this.handlerIO = io;
        this.overloadLimit = overloadLimit;
        this.energy = thermalEnergy;
    }

    @Override
    public double acceptHeatFromNetwork(Direction side, double thermalEnergy) {
        this.energy += thermalEnergy;
        double fit = getHeatChangeToFitWithinTempLimits();
        this.energy += fit;
        thermalEnergy -= fit;
        return thermalEnergy;
    }

    @Override
    public boolean inputsHeat(Direction side) {
        return handlerIO.support(IO.IN) && (sideInputCondition == null || sideInputCondition.test(side));
    }

    @Override
    public boolean outputsHeat(Direction side) {
        return handlerIO.support(IO.OUT) && (sideOutputCondition == null || sideOutputCondition.test(side));
    }

    @Override
    public double changeHeat(double thermalEnergy) {
        this.energy += thermalEnergy;
        double fit = getHeatChangeToFitWithinTempLimits();
        this.energy += fit;
        thermalEnergy -= fit;
        return thermalEnergy;
    }

    @Override
    public double getCurrentEnergy() {
        return energy;
    }

    @Override
    public float getHeatCapacity() {
        return 500f;
    }

    @Override
    public float getConductance() {
        return 1f;
    }

    @Override
    public List<Integer> handleRecipeInner(IO io, GTRecipe recipe, List<Integer> left, @Nullable String slotName, boolean simulate) {
        return null;
    }

    @Override
    public List<Object> getContents() {
        return null;
    }

    @Override
    public double getTotalContentAmount() {
        return 0;
    }

    @Override
    public RecipeCapability<Integer> getCapability() {
        return null;
    }
}
