package com.ghostipedia.cosmiccore.common.machine.multiblock.multi.modular;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.api.block.IMultiblockProvider;
import com.ghostipedia.cosmiccore.api.block.IMultiblockReciever;
import com.ghostipedia.cosmiccore.common.data.CosmicBlocks;
import com.ghostipedia.cosmiccore.common.data.CosmicModularMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.network.chat.Component;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import static com.ghostipedia.cosmiccore.api.registries.CosmicRegistration.REGISTRATE;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;

public class StarLadder extends WorkableElectricMultiblockMachine implements IMultiblockProvider {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarLadder.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    @Nullable
    protected EnergyContainerList inputEnergyContainers;
    // assuming a collection is just like a map, but it lost the actual mapping
    @Getter
    protected boolean isFuelable;
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

    private int getMaxModules() {
        if (this.tetherTier == 1) return 6;
        if (this.tetherTier == 2) return 12;
        if (this.tetherTier == 3) return 16;
        return 4;
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

    public void isfuelable(boolean fuelable) {
        this.isFuelable = fuelable;
    }
    // New Code ft. Ghost what the HELL are you doing...
    // @NotNull
    // protected static TraceabilityPredicate starLadderModulesOld() {
    // return new TraceabilityPredicate(MultiblockState -> {
    // var blockState = MultiblockState.getBlockState();
    // Block block = blockState.getBlock();
    // MetaMachine metaMachine = MetaMachine.getMachine(level, pos);
    // if (blockState.is(STAR_LADDER_BLOCKS)) {
    // if(block == CosmicBlocks.STAR_LADDER_CASING.get()) return true;
    // }
    // if(metaMachine == null) return false;
    // if (metaMachine instanceof IMultiblockProvider) return false;
    // if (!(metaMachine instanceof IMultiblockReciever starLadderReceiver)) return false;
    // if(starLadderReceiver.getModularMultiBlock() != this){
    // starLadderReceiver.setModularMultiBlock(this);
    // this.starLadderReceivers.add(starLadderReceiver);
    // }
    // return true;
    // }, () -> CosmicCoreAPI.STARLADDER_CASINGS.entrySet().stream()
    // // sort to make autogenerated jei previews not pick random coils each game load
    // .map(coil -> BlockInfo.fromBlockState(coil.getValue().get().defaultBlockState()))
    // .toArray(BlockInfo[]::new))
    // .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.coils"));
    // }

    public static final MultiblockMachineDefinition STAR_LADDER = REGISTRATE.multiblock("star_ladder", StarLadder::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .recipeModifier(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK))
            .appearanceBlock(CosmicBlocks.VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A   A", "A   A", "A   A", "A   A")
                    .aisle("AAAAA", "A B A", "Q   Q", "A   A")
                    .where(' ', any())
                    .where("B", controller(blocks(definition.getBlock())))
                    .where('A', blocks(CosmicBlocks.VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(16))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(16))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(16))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(16)))
                    .where("Q", blocks(CosmicModularMachines.STAR_LADDER_TEST_MODULE[GTValues.ZPM].get()))
                    // blocks(Arrays.stream(CosmicModularMachines.STAR_LADDER_TEST_MODULE).filter(Objects::nonNull).map(Supplier::get).toArray(IMachineBlock[]::new))
                    .build())
            .workableCasingRenderer(CosmicCore.id("block/casings/solid/vomahine_certified_chemically_resistant_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltips(Component.translatable("cosmiccore.multiblock.iris.tooltip.0"),
                    Component.translatable("cosmiccore.multiblock.iris.tooltip.1"),
                    Component.translatable("cosmiccore.multiblock.iris.tooltip.2"),
                    Component.translatable("cosmiccore.multiblock.iris.tooltip.3"))
            .hasTESR(true)
            .register();

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .textSupplier(this.getLevel().isClientSide ? null : this::addDisplayText)
                        .setMaxWidthLimit(150)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(new ButtonWidget(
                27,
                100,
                158,
                20,
                new GuiTextureGroup(
                        GuiTextures.BUTTON,
                        new TextTexture("cosmiccore.multiblock.send_orbit_data")),
                clickData -> isfuelable(true)));
        return group;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.translatable("cosmiccore.multiblock.advanced.star_ladder_tier", tetherTier,
                    getMaxModules()));
        }
    }

    public static void init() {}
}
