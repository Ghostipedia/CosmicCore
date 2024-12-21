package com.ghostipedia.cosmiccore.common.machine.multiblock.multi.modular.modules;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.api.block.IMultiblockProvider;
import com.ghostipedia.cosmiccore.api.machine.multiblock.ModularizedWorkableElectricMultiblockMachine;
import com.ghostipedia.cosmiccore.client.renderer.machine.StarBallastMachineRenderer;
import com.ghostipedia.cosmiccore.common.data.CosmicBlocks;
import com.ghostipedia.cosmiccore.common.machine.multiblock.multi.modular.StarLadder;
import com.ghostipedia.cosmiccore.gtbridge.CosmicRecipeTypes;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.function.BiFunction;

import static com.ghostipedia.cosmiccore.api.registries.CosmicRegistration.REGISTRATE;
import static com.ghostipedia.cosmiccore.common.data.CosmicBlocks.*;
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_ATOMIC;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toRomanNumeral;

public class StarLadderDummy extends ModularizedWorkableElectricMultiblockMachine {
    public StarLadderDummy(IMachineBlockEntity holder, int tier, int moduleTier, int minModuleTier) {
        super(holder, tier, moduleTier, minModuleTier);
    }

    @Override
    public void sendWorkingDisabled() {
        this.recipeLogic.setWorkingEnabled(false);
    }

    @Override
    public void sendWorkingEnabled() {
        this.recipeLogic.setWorkingEnabled(true);
    }

    @Override
    public String getNameForDisplays() {
        return this.getDefinition().getId().toLanguageKey("block", "display_count");
    }






}
