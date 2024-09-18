package com.ghostipedia.cosmiccore.common.machine.multiblock.multi.modular;

import com.ghostipedia.cosmiccore.api.CosmicCoreAPI;
import com.ghostipedia.cosmiccore.api.block.IMultiblockProvider;
import com.ghostipedia.cosmiccore.api.block.IMultiblockReciever;
import com.ghostipedia.cosmiccore.api.machine.multiblock.MagnetWorkableElectricMultiblockMachine;
import com.ghostipedia.cosmiccore.client.renderer.machine.StarBallastMachineRenderer;
import com.ghostipedia.cosmiccore.common.data.CosmicBlocks;
import com.ghostipedia.cosmiccore.common.machine.multiblock.electric.MagneticFieldMachine;
import com.ghostipedia.cosmiccore.gtbridge.CosmicRecipeTypes;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

import static com.ghostipedia.cosmiccore.api.data.CosmicCustomTags.STAR_LADDER_BLOCKS;
import static com.ghostipedia.cosmiccore.api.registries.CosmicRegistration.REGISTRATE;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GCyMBlocks.CASING_HIGH_TEMPERATURE_SMELTING;

public class StarLadder extends WorkableElectricMultiblockMachine implements IMultiblockProvider {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarLadder.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    @Nullable
    protected EnergyContainerList inputEnergyContainers;
    //assuming a collection is just like a map, but it lost the actual mapping
    private final Collection<IMultiblockReciever> starLadderReceivers = ConcurrentHashMap.newKeySet();
    public StarLadder(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }
    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
    protected int tetherTier = 0;
    @Override
    public int getModulatorTier() {
        return 0;
    }
    private int getMaxModules(){
        if (this.tetherTier == 1) return 6;
        if (this.tetherTier == 2) return 12;
        if (this.tetherTier == 3) return 16;
        return 0;
    }

    @Override
    public IEnergyContainer getEnergyContainersForModules() {
        return this.inputEnergyContainers;
    }

    @Override
    public boolean amIAModule(IMultiblockProvider receiver) {
        return this.starLadderReceivers.contains(receiver);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.starLadderReceivers.forEach(IMultiblockReciever::sendWorkingDisabled);
        this.starLadderReceivers.forEach(s -> s.setModularMultiBlock(null));
    }
    //New Code ft. Ghost what the HELL are you doing...
    @NotNull
    protected TraceabilityPredicate starLadderModules(Level level, BlockPos pos) {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            Block block = blockState.getBlock();
            MetaMachine metaMachine = MetaMachine.getMachine(level, pos);
            if (blockState.is(STAR_LADDER_BLOCKS)) {
                if(block == CosmicBlocks.STAR_LADDER_CASING.get()) return true;
            }
            if(metaMachine == null) return false;
            if (metaMachine instanceof IMultiblockProvider) return false;
            if (!(metaMachine instanceof IMultiblockReciever starLadderReceiver)) return false;
            if(starLadderReceiver.getModularMultiBlock() != this){
                starLadderReceiver.setModularMultiBlock(this);
                this.starLadderReceivers.add(starLadderReceiver);
            }
            return true;
        }, () -> CosmicCoreAPI.MAGNET_COILS.entrySet().stream()
                // sort to make autogenerated jei previews not pick random coils each game load
                .sorted(Comparator.comparingInt(value -> value.getKey().getMagnetFieldCapacity()))
                .map(coil -> BlockInfo.fromBlockState(coil.getValue().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.coils"));
    }

    public final  MultiblockMachineDefinition STAR_LADDER = REGISTRATE.multiblock("star_ladder", StarLadder::new)
            .rotationState(RotationState.Y_AXIS)
            .recipeType(CosmicRecipeTypes.VOMAHINE_CORE_DRILL)
            .recipeModifier(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK))
            .appearanceBlock(CosmicBlocks.VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A   A", "A   A", "A   A", "A   A")
                    .aisle("AAAAA", "A B A", "Q   Q", "A   A")
                    .where(' ', any())
                    .where("B", controller(blocks(definition.getBlock())))
                    .where('A', blocks(CASING_HIGH_TEMPERATURE_SMELTING.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(16))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(16))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(16))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(16)))
                    .where("Q", starLadderModules(getLevel(), getPos()))
                    .build())
            .renderer(StarBallastMachineRenderer::new)
            .tooltips(Component.translatable("cosmiccore.multiblock.iris.tooltip.0"),
                    Component.translatable("cosmiccore.multiblock.iris.tooltip.1"),
                    Component.translatable("cosmiccore.multiblock.iris.tooltip.2"),
                    Component.translatable("cosmiccore.multiblock.iris.tooltip.3")
            )
            .hasTESR(true)
            .register();
    public static void init() {}
}
