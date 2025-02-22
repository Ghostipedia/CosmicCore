package com.ghostipedia.cosmiccore.common.data;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.api.machine.multiblock.IPBFMachine;
import com.ghostipedia.cosmiccore.api.machine.part.CosmicPartAbility;
import com.ghostipedia.cosmiccore.api.machine.part.SteamFluidHatchPartMachine;
import com.ghostipedia.cosmiccore.api.registries.CosmicRegistration;
import com.ghostipedia.cosmiccore.client.renderer.machine.SidedWorkableHullRenderer;
import com.ghostipedia.cosmiccore.common.block.WorkableSteamHullType;
import com.ghostipedia.cosmiccore.common.block.debug.CreativeThermiaContainerMachine;
import com.ghostipedia.cosmiccore.common.data.materials.CosmicMaterials;
import com.ghostipedia.cosmiccore.common.data.recipe.CosmicRecipeModifiers;
import com.ghostipedia.cosmiccore.common.machine.multiblock.electric.MagneticFieldMachine;
import com.ghostipedia.cosmiccore.common.machine.multiblock.multi.WirelessDataBankMachine;
import com.ghostipedia.cosmiccore.common.machine.multiblock.part.CosmicParallelHatchPartMachine;
import com.ghostipedia.cosmiccore.common.machine.multiblock.part.SoulHatchPartMachine;
import com.ghostipedia.cosmiccore.common.machine.multiblock.part.ThermiaHatchPartMachine;
import com.ghostipedia.cosmiccore.common.machine.multiblock.part.WirelessDataHatchPartMachine;
import com.ghostipedia.cosmiccore.common.machine.multiblock.steam.WeakSteamParallelMultiBlockMachine;
import com.ghostipedia.cosmiccore.gtbridge.CosmicRecipeTypes;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.renderer.machine.LargeBoilerRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableSteamMachineRenderer;
import com.gregtechceu.gtceu.common.block.BoilerFireboxType;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.common.registry.GTRegistration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import it.unimi.dsi.fastutil.Pair;

import java.util.*;
import java.util.function.BiFunction;

import static com.ghostipedia.cosmiccore.api.pattern.CosmicPredicates.magnetCoils;
import static com.ghostipedia.cosmiccore.api.registries.CosmicRegistration.REGISTRATE;
import static com.ghostipedia.cosmiccore.common.data.CosmicBlocks.*;
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.*;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.CREATIVE_TOOLTIPS;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.FUSION_REACTOR;

public class CosmicMachines {

    static {
        CosmicRegistration.REGISTRATE.creativeModeTab(() -> CosmicCreativeModeTabs.COSMIC_CORE);
    }

    public static final int[] HIGH_TIERS = GTValues.tiersBetween(GTValues.IV,
            GTCEuAPI.isHighTier() ? GTValues.OpV : GTValues.UHV);

    public final static MachineDefinition[] SOUL_IMPORT_HATCH = registerSoulTieredHatch(
            "soul_input_hatch", "Soul Input Hatch", "soul_hatch.import",
            IO.IN, HIGH_TIERS, CosmicPartAbility.IMPORT_SOUL);

    public static final MachineDefinition[] SOUL_EXPORT_HATCH = registerSoulTieredHatch(
            "soul_output_hatch", "Soul Output Hatch", "soul_hatch.export",
            IO.OUT, HIGH_TIERS, CosmicPartAbility.EXPORT_SOUL);
    public static final MachineDefinition[] THERMIA_VENT = registerThermiaTieredHatch(
            "thermia_export_hatch", "Thermia Vent", "thermia_hatch.export",
            IO.OUT, HIGH_TIERS, CosmicPartAbility.EXPORT_THERMIA);
    public static final MachineDefinition[] THERMIA_SOCKET = registerThermiaTieredHatch(
            "thermia_import_hatch", "Thermia Socket", "thermia_hatch.import",
            IO.IN, HIGH_TIERS, CosmicPartAbility.IMPORT_THERMIA);
    public static final MachineDefinition[] NAQUAHINE_MINI_REACTOR = registerSimpleGenerator("naquahine_mini_reactor",
            CosmicRecipeTypes.MINI_NAQUAHINE_REACTOR, genericGeneratorTankSizeFunction, 0.0f, GTValues.IV, GTValues.LuV,
            GTValues.ZPM, GTValues.UV, GTValues.UHV);
    public static final Pair<MachineDefinition, MachineDefinition> STEAM_BENDER = registerSteamMachines(
            "steam_bender", SimpleSteamMachine::new, (pressure, builder) -> builder
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(GTRecipeTypes.BENDER_RECIPES)
                    .recipeModifier(SimpleSteamMachine::recipeModifier)
                    .addOutputLimit(ItemRecipeCapability.CAP, 1)
                    .renderer(() -> new WorkableSteamMachineRenderer(pressure, GTCEu.id("block/machines/bender")))
                    .register());
    public static final Pair<MachineDefinition, MachineDefinition> STEAM_WIREMILL = registerSteamMachines(
            "steam_wiremill", SimpleSteamMachine::new, (pressure, builder) -> builder
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(GTRecipeTypes.WIREMILL_RECIPES)
                    .recipeModifier(SimpleSteamMachine::recipeModifier)
                    .addOutputLimit(ItemRecipeCapability.CAP, 1)
                    .renderer(() -> new WorkableSteamMachineRenderer(pressure, GTCEu.id("block/machines/wiremill")))
                    .register());

    public static final MachineDefinition[] COSMIC_PARALLEL_HATCH = registerTieredMachines("cosmic_parallel_hatch",
            CosmicParallelHatchPartMachine::new,
            (tier, builder) -> builder
                    .langValue(switch (tier) {
                        case 7 -> "Ultimate";
                        case 8 -> "Super";
                        case 9 -> "Extreme";
                        case 10 -> "WarpTech";
                        case 15 -> "Paradox";
                        default -> "Simple"; // Should never be hit.
                    } + " Parallel Control Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(CosmicPartAbility.COSMIC_PARALLEL_HATCH)
                    .workableTieredHullRenderer(GTCEu.id("block/machines/parallel_hatch_mk" + (tier - 4)))
                    .tooltips(Component.translatable("gtceu.machine.parallel_hatch_mk" + tier + ".tooltip"))
                    .register(),
            ZPM, UV, UHV, UEV, UIV);

    // Enable If needed Inside of Dev
    // public static final MultiblockMachineDefinition SOUL_TESTER = REGISTRATE.multiblock("soul_tester",
    // PrimitiveWorkableMachine::new)
    // .rotationState(RotationState.NON_Y_AXIS)
    // .recipeType(CosmicCoreRecipeTypes.SOUL_TESTER_RECIPES)
    // .appearanceBlock(GTBlocks.CASING_PRIMITIVE_BRICKS)
    // .pattern(definition -> FactoryBlockPattern.start()
    // .aisle("S", "C", "I")
    // .where("C", controller(blocks(definition.getBlock())))
    // .where("S", abilities(CosmicPartAbility.IMPORT_SOUL).or(abilities(CosmicPartAbility.EXPORT_SOUL)))
    // .where("I", abilities(PartAbility.EXPORT_ITEMS).or(abilities(PartAbility.IMPORT_ITEMS)))
    // .build())
    // .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_inert_ptfe"),
    // GTCEu.id("block/multiblock/coke_oven"))
    // .register();

    public static final MultiblockMachineDefinition STEAM_CASTER = GTRegistration.REGISTRATE
            .multiblock("steam_caster", WeakSteamParallelMultiBlockMachine::new)
            .rotationState(RotationState.ALL)
            .appearanceBlock(BRONZE_HULL)
            .recipeType(GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES)
            .recipeModifier(WeakSteamParallelMultiBlockMachine::recipeModifier, true)
            .addOutputLimit(ItemRecipeCapability.CAP, 1)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAA", "ABBA", "AAAA")
                    .aisle("AAAA", "BCCB", "AAAA")
                    .aisle("AAAA", "ADBA", "AAAA")
                    .where('D', Predicates.controller(blocks(definition.getBlock())))
                    .where('#', Predicates.air())
                    .where(' ', Predicates.any())
                    .where('A', blocks(CASING_BRONZE_BRICKS.get()))
                    .where('B', blocks(CASING_COKE_BRICKS.get())
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1)))
                    .where('C', blocks(CASING_BRONZE_PIPE.get()))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_coke_bricks"),
                    CosmicCore.id("block/multiblock/solidifier"))
            .register();
    public static final MultiblockMachineDefinition STEAM_MIXER = GTRegistration.REGISTRATE
            .multiblock("steam_mixing_vessel", WeakSteamParallelMultiBlockMachine::new)
            .rotationState(RotationState.ALL)
            .appearanceBlock(BRONZE_BRICKS_HULL)
            .recipeType(GTRecipeTypes.MIXER_RECIPES)
            .recipeModifier(WeakSteamParallelMultiBlockMachine::recipeModifier, true)
            .addOutputLimit(ItemRecipeCapability.CAP, 1)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "BCB", "BCB", " B ")
                    .aisle("AAA", "CEC", "CEC", "BBB")
                    .aisle("ADA", "BCB", "BCB", " B ")
                    .where('D', Predicates.controller(blocks(definition.getBlock())))
                    .where('#', Predicates.air())
                    .where(' ', Predicates.any())
                    .where('A', blocks(BRONZE_BRICKS_HULL.get())
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1)))
                    .where('B', blocks(CASING_BRONZE_BRICKS.get()))
                    .where('C', blocks(BRONZE_HULL.get()))
                    .where('E', blocks(CASING_BRONZE_GEARBOX.get()))
                    .build())
            .renderer(() -> new SidedWorkableHullRenderer(
                    GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"),
                    WorkableSteamHullType.BRONZE_BRICK_HULL,
                    CosmicCore.id("block/multiblock/mixing_vessel")))
            .register();
    public static final MultiblockMachineDefinition INDUSTRIAL_PRIMITIVE_BLAST_FURNACE = GTRegistration.REGISTRATE
            .multiblock("industrial_primitive_blast_furnace", IPBFMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(CosmicRecipeTypes.INDUSTRIAL_PRIMITIVE_BLAST_FURNACE_RECIPES)
            .recipeModifier(IPBFMachine::recipeModifier, true)
            .appearanceBlock(CASING_PRIMITIVE_BRICKS)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("QQQ", "XXX", "XXX", "XXX", "XXX")
                    .aisle("QQQ", "X#X", "X#X", "X#X", "X#X")
                    .aisle("QQQ", "XYX", "XXX", "XXX", "XXX")
                    .where('X', blocks(CASING_PRIMITIVE_BRICKS.get()))
                    .where('#', Predicates.air())
                    .where('Y', Predicates.controller(blocks(definition.getBlock())))
                    .where('Q', blocks(FIREBOX_STEEL.get()).setMinGlobalLimited(6)
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1)
                                    .setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1)
                                    .setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1).setExactLimit(1)))
                    .build())
            .renderer(() -> new LargeBoilerRenderer(GTCEu.id("block/casings/solid/machine_primitive_bricks"),
                    BoilerFireboxType.STEEL_FIREBOX,
                    GTCEu.id("block/multiblock/primitive_blast_furnace")))
            .tooltips(Component.translatable("cosmiccore.multiblock.ipbf.tooltip.0"),
                    Component.translatable("cosmiccore.multiblock.ipbf.tooltip.1"),
                    Component.translatable("cosmiccore.multiblock.ipbf.tooltip.2"),
                    Component.translatable("cosmiccore.multiblock.ipbf.tooltip.3"))
            .register();
    public static final MultiblockMachineDefinition HIGH_PRESSURE_ASSEMBLER = GTRegistration.REGISTRATE
            .multiblock("high_pressure_assembler", WeakSteamParallelMultiBlockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.ASSEMBLER_RECIPES)
            .recipeModifier(WeakSteamParallelMultiBlockMachine::recipeModifier, true)
            .appearanceBlock(STEEL_PLATED_BRONZE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAAA", "BBBBB", "BBBBB")
                    .aisle("AAAAA", "BDDDB", "BBBBB")
                    .aisle("AAAAA", "BYBBB", "BBBBB")
                    .where('B', blocks(STEEL_PLATED_BRONZE.get())
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1)
                                    .setExactLimit(2))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1)
                                    .setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1).setExactLimit(1)))
                    .where('#', Predicates.air())
                    .where('Y', Predicates.controller(blocks(definition.getBlock())))
                    .where('A', blocks(FIREBOX_STEEL.get()).setMinGlobalLimited(11)
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1)))
                    .where('D', blocks(CASING_STEEL_GEARBOX.get()))
                    .build())
            .renderer(() -> new LargeBoilerRenderer(CosmicCore.id("block/casings/solid/steel_plated_bronze_casing"),
                    BoilerFireboxType.STEEL_FIREBOX,
                    GTCEu.id("block/multiblock/implosion_compressor")))
            .tooltips(Component.translatable("cosmiccore.multiblock.hpsassem.tooltip.0"),
                    Component.translatable("cosmiccore.multiblock.hpsassem.tooltip.1"),
                    Component.translatable("cosmiccore.multiblock.hpsassem.tooltip.2"))
            .register();
    // Terrifying Recipe Modifiers half of this is moonruns to me :lets:
    public final static MultiblockMachineDefinition DRYGMY_GROVE = REGISTRATE
            .multiblock("drygmy_grove", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CosmicRecipeTypes.GROVE_RECIPES)
            // .recipeModifiers(true,
            // (machine, recipe, OCParams, OCResult) -> {
            // if (machine instanceof IRecipeCapabilityHolder holder) {
            // // Find all the items in the combined Item Input inventories and create oversized ItemStacks
            // Object2IntMap<ItemStack> ingredientStacks =
            // Objects.requireNonNullElseGet(holder.getCapabilitiesProxy().get(IO.IN, ItemRecipeCapability.CAP),
            // Collections::<IRecipeHandler<?>>emptyList)
            // .stream()
            // .map(container ->
            // container.getContents().stream().filter(ItemStack.class::isInstance).map(ItemStack.class::cast).toList())
            // .flatMap(container -> GTHashMaps.fromItemStackCollection(container).object2IntEntrySet().stream())
            // .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum, () -> new
            // Object2IntOpenCustomHashMap<>(ItemStackHashStrategy.comparingAllButCount())));
            // ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(new
            // ResourceLocation("ars_nouveau:drygmy_charm")));
            // //Never let the multiplier be 0 (THIS IS NOT ACTUALLY PARALLEL, It's just being used to to some goober
            // grade math)
            // if (ingredientStacks.getInt(stack) >= 1) {
            // var maxParallel = ingredientStacks.getInt(stack) / 2;
            // recipe = copyOutputs(recipe, ContentModifier.multiplier(maxParallel));
            // }
            // }
            // return recipe;
            // },
            // GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("##QQQ##", "##QQQ##", "#######", "#######", "#######", "##QQQ##", "##QQQ##")
                    .aisle("#QQQQQ#", "#QMMMQ#", "#FLBBF#", "#F#B#F#", "#F###F#", "#QGGGQ#", "#QQQQQ#")
                    .aisle("QQQQQQQ", "QMMMMMQ", "#B#####", "#B#####", "#######", "QGP#PGQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QMMMMMQ", "#B###B#", "#######", "#######", "QG###GQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QMMMMMQ", "####LB#", "#####B#", "#######", "QGP#PGQ", "QQQQQQQ")
                    .aisle("#QQQQQ#", "#QMMMQ#", "#F#BBF#", "#F##BF#", "#F###F#", "#QGGGQ#", "#QQQQQ#")
                    .aisle("##QQQ##", "##QCQ##", "#######", "#######", "#######", "##QQQ##", "##QQQ##")
                    .where('#', any())
                    .where("C", controller(blocks(definition.getBlock())))
                    .where('F', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.StainlessSteel)))
                    .where('M', blocks(Blocks.MOSS_BLOCK))
                    .where('B', blocks(Blocks.AZALEA_LEAVES)
                            .or(blocks(Blocks.FLOWERING_AZALEA_LEAVES)))
                    .where('L', blocks(Blocks.FLOWERING_AZALEA)
                            .or(blocks(Blocks.AZALEA)))
                    .where('P', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('G', blocks(Blocks.SEA_LANTERN)) // WHAT THE HELL IS A LAMP BRO - HALP
                    .where('Q', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS))
                            .or(abilities(PartAbility.EXPORT_FLUIDS))
                            .or(abilities(PartAbility.IMPORT_ITEMS))
                            .or(abilities(PartAbility.EXPORT_ITEMS))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.MAINTENANCE))
                            .or(abilities(CosmicPartAbility.IMPORT_SOUL)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/data_bank"))
            .register();
    public final static MultiblockMachineDefinition NAQUAHINE_PRESSURE_REACTOR = REGISTRATE
            .multiblock("naquahine_pressure_reactor", MagneticFieldMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CosmicRecipeTypes.NAQUAHINE_REACTOR)
            .recipeModifier(CosmicRecipeModifiers::vomahineReactorOC)
            .appearanceBlock(CosmicBlocks.NAQUADAH_PRESSURE_RESISTANT_CASING)
            .generator(true)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("##QQQ##", "##QQQ##", "###Q###", "#######", "#######", "#######", "#######", "#######",
                            "#######", "#######", "###Q###", "##QQQ##", "##QQQ##")
                    .aisle("#QQQQQ#", "#QQSQQ#", "#FQQQF#", "#FQFQF#", "#F###F#", "#F###F#", "#F###F#", "#F###F#",
                            "#F###F#", "#FQFQF#", "#FQQQF#", "#QQSQQ#", "#QQQQQ#")
                    .aisle("QQQQQQQ", "QQSSSQQ", "#QSSSQ#", "#QHGHQ#", "##HGH##", "##HGH##", "##HGH##", "##HGH##",
                            "##HGH##", "#QHGHQ#", "#QSSSQ#", "QQSSSQQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QSSSSSQ", "QQSSSQQ", "#FGSGF#", "##GSG##", "##GSG##", "##GSG##", "##GSG##",
                            "##GSG##", "#FGSGF#", "QQSSSQQ", "QSSSSSQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QQSSSQQ", "#QSSSQ#", "#QHGHQ#", "##HGH##", "##HGH##", "##HGH##", "##HGH##",
                            "##HGH##", "#QHGHQ#", "#QSSSQ#", "QQSSSQQ", "QQQQQQQ")
                    .aisle("#QQQQQ#", "#QQSQQ#", "#FQQQF#", "#FQFQF#", "#F###F#", "#F###F#", "#F###F#", "#F###F#",
                            "#F###F#", "#FQFQF#", "#FQQQF#", "#QQSQQ#", "#QQQQQ#")
                    .aisle("##QQQ##", "##QCQ##", "###Q###", "#######", "#######", "#######", "#######", "#######",
                            "#######", "#######", "###Q###", "##QQQ##", "##QQQ##")
                    .where('#', any())
                    .where("C", controller(blocks(definition.getBlock())))
                    .where('F', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.NaquadahAlloy)))
                    .where('S', magnetCoils())
                    .where('H', blocks(CosmicBlocks.RESONANTLY_TUNED_VIRTUE_MELD_CASING.get()))
                    .where('G', blocks(FUSION_GLASS.get()))
                    .where('Q', blocks(CosmicBlocks.NAQUADAH_PRESSURE_RESISTANT_CASING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS))
                            .or(abilities(PartAbility.EXPORT_FLUIDS))
                            .or(abilities(PartAbility.IMPORT_ITEMS))
                            .or(abilities(PartAbility.EXPORT_ITEMS))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.MAINTENANCE))
                            .or(abilities(PartAbility.OUTPUT_LASER))
                            .or(abilities(PartAbility.INPUT_LASER))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.OUTPUT_ENERGY)))
                    .build())
            .workableCasingRenderer(CosmicCore.id("block/casings/solid/naquadah_pressure_resistant_casing"),
                    GTCEu.id("block/multiblock/hpca"))
            .register();

    public final static MultiblockMachineDefinition CHROMATIC_DISTILLATION_PLANT = REGISTRATE
            .multiblock("chromatic_distillation_plant", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CosmicRecipeTypes.CHROMATIC_DISTILLATION_PLANT)
            .recipeModifier(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .pattern(definition -> FactoryBlockPattern.start(RIGHT, BACK, UP)
                    .aisle(" BCB ", "BBBBB", "BBBBB", "BBBBB", " BBB ")
                    .aisle(" A A ", "AGPGA", "APGPA", "AGPGA", " A A ").setRepeatable(1, 15)
                    .aisle(" AAA ", "AAAAA", "AAAAA", "AAAAA", " AAA ")
                    .where(' ', any())
                    .where("C", controller(blocks(definition.getBlock())))
                    .where('G', blocks(CASING_TEMPERED_GLASS.get()))
                    .where('P', blocks(CASING_TITANIUM_PIPE.get()))
                    .where('B', blocks(CASING_STAINLESS_CLEAN.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                    .setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1)))
                    .where('A', blocks(CASING_STAINLESS_CLEAN.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMinLayerLimited(1)
                                    .setMaxLayerLimited(1)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/generator/large_gas_turbine"))
            .register();

    public final static MultiblockMachineDefinition CHROMATIC_FLOTATION_PLANT = REGISTRATE
            .multiblock("chromatic_flotation_plant", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CosmicRecipeTypes.CHROMATIC_FLOTATION_PLANT)
            .recipeModifier(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(GCYMBlocks.CASING_WATERTIGHT)
            .generator(true)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAAAAA", "AAAAAAA", "AAAAAAA", "AAAAAAA", "AAAAAAA")
                    .aisle("AAAAAAA", "ABCBCBA", "ABBBBBA", "ABBBBBA", "ABBBBBA")
                    .aisle("AAAAAAA", "ABCBCBA", "ABBBBBA", "ABBBBBA", "ABBBBBA")
                    .aisle("AAAAAAA", "ABCBCBA", "ABBBBBA", "ABBBBBA", "ABBBBBA")
                    .aisle("AAAAAAA", "ABCBCBA", "ABBBBBA", "ABBBBBA", "ABBBBBA")
                    .aisle("AAAAAAA", "AACACAA", "AAAAAAA", "AAAAAAA", "AAAAAAA")
                    .aisle("       ", "  C C  ", "       ", "       ", "       ")
                    .aisle(" DDDDD ", " DCDCD ", " DDDDD ", "       ", "       ")
                    .aisle(" DDDDD ", " DEEED ", " DDDDD ", "       ", "       ")
                    .aisle(" DDDDD ", " DEEED ", " DDDDD ", "       ", "       ")
                    .aisle(" DDDDD ", " DDFDD ", " DDDDD ", "       ", "       ")
                    .aisle("       ", "       ", "       ", "       ", "       ")
                    .where(' ', any())
                    .where("F", controller(blocks(definition.getBlock())))
                    .where('C', blocks(CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('A', blocks(CASING_CORROSION_PROOF.get()))
                    .where('E', blocks(CASING_STEEL_SOLID.get()))
                    .where('B', blocks(Blocks.WATER))
                    .where('D', blocks(CASING_WATERTIGHT.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                    .setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/watertight_casing"),
                    GTCEu.id("block/multiblock/generator/large_gas_turbine"))
            .register();
    public final static MultiblockMachineDefinition ORBITAL_TEMPERING_FORGE = REGISTRATE.multiblock(
            "orbital_tempering_forge", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(CosmicRecipeTypes.CHROMATIC_FLOTATION_PLANT)
            .recipeModifier(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(CosmicBlocks.VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING)
            .generator(true)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("                                   ", "                                   ",
                            "   AAAAA                   AAAAA   ", "   BBBBB                   BBBBB   ",
                            "   AAAAA                   AAAAA   ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "  CA   AC      C   C      CA   AC  ", "  BB D BB      CDDDC      BB D BB  ",
                            "  CA   AC      C   C      CA   AC  ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "               CAAAC               ",
                            " CC     CC     DEEED     CC     CC ", " BB  D  BB     AEEEA     BB  D  BB ",
                            " CC     CC     DEEED     CC     CC ", "               CAAAC               ",
                            "                                   ")
                    .aisle("               C   C               ", "    AAA        DEEED        AAA    ",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "BB  CCC  BB    A   A    BB  CCC  BB",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "    AAA        DEEED        AAA    ",
                            "               C   C               ")
                    .aisle("               CDDDC               ", "   AAAAA       AEEEA       AAAAA   ",
                            "A  F   F  AABBBA   ABBBAA  F   F  A", "B  C   C  BBDDD     DDDBB  C   C  B",
                            "A  F   F  AABBBA   ABBBAA  F   F  A", "   AAAAA       AEEEA       AAAAA   ",
                            "               CDDDC               ")
                    .aisle("               C   C               ", "   AAAAA       AEEEA       AAAAA   ",
                            "A  F   F  ABDDD     DDDBA  F   F  A", "BDDC   CDDBA           ABDDC   CDDB",
                            "A  F   F  ABDDD     DDDBA  F   F  A", "   AAAAA       AEEEA       AAAAA   ",
                            "               C   C               ")
                    .aisle("               CDDDC               ", "   AAAAA       AEEEA       AAAAA   ",
                            "A  F   F  AABBBA   ABBBAA  F   F  A", "B  C   C  BBDDD     DDDBB  C   C  B",
                            "A  F   F  AABBBA   ABBBAA  F   F  A", "   AAAAA       AEEEA       AAAAA   ",
                            "               CDDDC               ")
                    .aisle("               C   C               ", "    AAA        DEEED        AAA    ",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "BB  CCC  BB    A   A    BB  CCC  BB",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "    AAA        DEEED        AAA    ",
                            "               C   C               ")
                    .aisle("                                   ", "               CAAAC               ",
                            " CC     CC     DEEED     CC     CC ", " BB  D  BB     AEEEA     BB  D  BB ",
                            " CC     CC     DEEED     CC     CC ", "               CAAAC               ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "  CA   AC      CAAAC      CA   AC  ", "  BB D BB      CAXAC      BB D BB  ",
                            "  CA   AC      CAAAC      CA   AC  ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "   AAAAA                   AAAAA   ", "   BBCBB                   BBCBB   ",
                            "   AAAAA                   AAAAA   ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    ABA                     ABA    ", "    BCB                     BCB    ",
                            "    ABA                     ABA    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    BDB                     BDB    ", "    DCD                     DCD    ",
                            "    BDB                     BDB    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    BDB                     BDB    ", "    DCD                     DCD    ",
                            "    BDB                     BDB    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    BDB                     BDB    ", "    DCD                     DCD    ",
                            "    BDB                     BDB    ", "                                   ",
                            "                                   ")
                    .aisle("   CCCCC                   CCCCC   ", "  CDAAADC                 CDAAADC  ",
                            " CDABABADC               CDABABADC ", " CAAAAAAAC               CAAAAAAAC ",
                            " CDABABADC               CDABABADC ", "  CDAAADC                 CDAAADC  ",
                            "   CCCCC                   CCCCC   ")
                    .aisle("    D D                     D D    ", "  AEEEEEA                 AEEEEEA  ",
                            "  E     EA               AE     E  ", " DE     EA               AE     ED ",
                            "  E     EA               AE     E  ", "  AEEEEEA                 AEEEEEA  ",
                            "    D D                     D D    ")
                    .aisle("    D D                     D D    ", "  AEEEEEA                 AEEEEEA  ",
                            "  E     EA               AE     E  ", " DE     EA               AE     ED ",
                            "  E     EA               AE     E  ", "  AEEEEEA                 AEEEEEA  ",
                            "    D D                     D D    ")
                    .aisle("    D D                     D D    ", "  AEEEEEA                 AEEEEEA  ",
                            "  E     EA               AE     E  ", " DE     EA               AE     ED ",
                            "  E     EA               AE     E  ", "  AEEEEEA                 AEEEEEA  ",
                            "    D D                     D D    ")
                    .aisle("   CCCCC                   CCCCC   ", "  CDAAADC                 CDAAADC  ",
                            " CDAA AADC               CDAA AADC ", " CAA   AAC               CAA   AAC ",
                            " CDAA AADC               CDAA AADC ", "  CDAAADC                 CDAAADC  ",
                            "   CCCCC                   CCCCC   ")
                    .aisle("                                   ", "                                   ",
                            "    BDB                     BDB    ", "    D D                     D D    ",
                            "    BDB                     BDB    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    BDB                     BDB    ", "    D D                     D D    ",
                            "    BDB                     BDB    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    BDB                     BDB    ", "    D D                     D D    ",
                            "    BDB                     BDB    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "    ABA                     ABA    ", "    BAB                     BAB    ",
                            "    ABA                     ABA    ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "   AAAAA                   AAAAA   ", "   BBBBB                   BBBBB   ",
                            "   AAAAA                   AAAAA   ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "  CA   AC      CAAAC      CA   AC  ", "  BB D BB      CAAAC      BB D BB  ",
                            "  CA   AC      CAAAC      CA   AC  ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "               CAAAC               ",
                            " CC     CC     DEEED     CC     CC ", " BB  D  BB     AEEEA     BB  D  BB ",
                            " CC     CC     DEEED     CC     CC ", "               CAAAC               ",
                            "                                   ")
                    .aisle("               C   C               ", "    AAA        DEEED        AAA    ",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "BB  CCC  BB    A   A    BB  CCC  BB",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "    AAA        DEEED        AAA    ",
                            "               C   C               ")
                    .aisle("               CDDDC               ", "   AAAAA       AEEEA       AAAAA   ",
                            "A  F   F  AABBBB   BBBBAA  F   F  A", "B  C   C  BBDDDA   ADDDBB  C   C  B",
                            "A  F   F  AABBBB   BBBBAA  F   F  A", "   AAAAA       AEEEA       AAAAA   ",
                            "               CDDDC               ")
                    .aisle("               C   C               ", "   AAAAA       AEEEA       AAAAA   ",
                            "A  F   F  ABDDDA   ADDDBA  F   F  A", "BDDC   CDDCCCCCA   ACCCCCDDC   CDDB",
                            "A  F   F  ABDDDA   ADDDBA  F   F  A", "   AAAAA       AEEEA       AAAAA   ",
                            "               C   C               ")
                    .aisle("               CDDDC               ", "   AAAAA       AEEEA       AAAAA   ",
                            "A  F   F  AABBBB   BBBBAA  F   F  A", "B  C   C  BBDDDA   ADDDBB  C   C  B",
                            "A  F   F  AABBBB   BBBBAA  F   F  A", "   AAAAA       AEEEA       AAAAA   ",
                            "               CDDDC               ")
                    .aisle("               C   C               ", "    AAA        DEEED        AAA    ",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "BB  CCC  BB    A   A    BB  CCC  BB",
                            "AA  FFF  AA    A   A    AA  FFF  AA", "    AAA        DEEED        AAA    ",
                            "               C   C               ")
                    .aisle("                                   ", "               CAAAC               ",
                            " CC     CC     DEEED     CC     CC ", " BB  D  BB     AEEEA     BB  D  BB ",
                            " CC     CC     DEEED     CC     CC ", "               CAAAC               ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "  CA   AC      CAAAC      CA   AC  ", "  BB D BB      CDDDC      BB D BB  ",
                            "  CA   AC      CAAAC      CA   AC  ", "                                   ",
                            "                                   ")
                    .aisle("                                   ", "                                   ",
                            "   AAAAA                   AAAAA   ", "   BBBBB                   BBBBB   ",
                            "   AAAAA                   AAAAA   ", "                                   ",
                            "                                   ")
                    .where(' ', any())
                    .where("X", controller(blocks(definition.getBlock())))
                    .where('C', blocks(VOMAHINE_CERTIFIED_INTERSTELLAR_GRADE_CASING.get()))
                    .where('A', blocks(VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING.get()))
                    .where('E', heatingCoils())
                    .where('B', blocks(VOMAHINE_ULTRA_POWERED_CASING.get()))
                    .where('D', blocks(VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_PIPE.get()))
                    .where('F', blocks(HEAT_VENT.get()))
                    .build())
            .workableCasingRenderer(CosmicCore.id("block/casings/solid/vomahine_certified_chemically_resistant_casing"),
                    CosmicCore.id("block/multiblock/vomahine_chemplant"))
            .register();
    public final static MultiblockMachineDefinition VOMAHINE_INDUSTRIAL_CHEMPLANT = REGISTRATE
            .multiblock("vomahine_industrial_chemical_plant", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeTypes(CosmicRecipeTypes.VOMAHINE_INDUSTRIAL_CHEMVAT, GTRecipeTypes.CRACKING_RECIPES)
            .recipeModifiers(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("##QQQ##", "##QQQ##", "###Q###", "#######", "#######", "#######", "#######", "#######",
                            "###Q###", "##QQQ##", "##QQQ##")
                    .aisle("#QQQQQ#", "#QQSQQ#", "#FQQQF#", "#FQ#QF#", "#F###F#", "#F###F#", "#F###F#", "#FQ#QF#",
                            "#FQQQF#", "#QQSQQ#", "#QQQQQ#")
                    .aisle("QQQQQQQ", "QQSSSQQ", "#QSSSQ#", "##HGH##", "##HGH##", "##HGH##", "##HGH##", "#QHGHQ#",
                            "#QSSSQ#", "QQSSSQQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QSSSSSQ", "QQSSSQQ", "##GSG##", "##GSG##", "##GSG##", "##GSG##", "##GSG##",
                            "QQSSSQQ", "QSSSSSQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QQSSSQQ", "#QSSSQ#", "##HGH##", "##HGH##", "##HGH##", "##HGH##", "#QHGHQ#",
                            "#QSSSQ#", "QQSSSQQ", "QQQQQQQ")
                    .aisle("#QQQQQ#", "#QQSQQ#", "#FQQQF#", "#FQ#QF#", "#F###F#", "#F###F#", "#F###F#", "#FQ#QF#",
                            "#FQQQF#", "#QQSQQ#", "#QQQQQ#")
                    .aisle("##QQQ##", "##QCQ##", "###Q###", "#######", "#######", "#######", "#######", "#######",
                            "###Q###", "##QQQ##", "##QQQ##")
                    .where('#', any())
                    .where("C", controller(blocks(definition.getBlock())))
                    .where('F', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.NaquadahAlloy)))
                    .where('S', blocks(CosmicBlocks.COIL_RESONANT_VIRTUE_MELD.get()))
                    .where('H', blocks(CosmicBlocks.VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_PIPE.get()))
                    .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where('Q', blocks(VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS))
                            .or(abilities(PartAbility.EXPORT_FLUIDS))
                            .or(abilities(PartAbility.IMPORT_ITEMS))
                            .or(abilities(PartAbility.EXPORT_ITEMS))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.MAINTENANCE))
                            .or(abilities(PartAbility.DATA_ACCESS))
                            .or(abilities(PartAbility.COMPUTATION_DATA_RECEPTION))
                            .or(abilities(PartAbility.OPTICAL_DATA_RECEPTION))
                            .or(abilities(PartAbility.PARALLEL_HATCH))
                            .or(abilities(CosmicPartAbility.COSMIC_PARALLEL_HATCH))
                            .or(abilities(PartAbility.INPUT_LASER))
                            .or(abilities(PartAbility.INPUT_ENERGY)))
                    .build())
            .workableCasingRenderer(CosmicCore.id("block/casings/solid/vomahine_certified_chemically_resistant_casing"),
                    CosmicCore.id("block/multiblock/vomahine_chemplant"))
            .register();
    public final static MultiblockMachineDefinition CELESTIAL_BORE = REGISTRATE.multiblock(
            "vomahine_celestial_laser_bore", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(CosmicRecipeTypes.CELESTIAL_BORE)
            .recipeModifiers(GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                            AAAAAAA                            ",
                            "                               A                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BBB                              ",
                            "                             BBBBB                             ",
                            "                            BBBBBBB                            ",
                            "                           ABBBBBBBA                           ",
                            "                            BBBBBBB                            ",
                            "                             BBBBB                             ",
                            "                              BBB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BBB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     BA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BBB                              ",
                            "                             BBBBB                             ",
                            "                            BBBBBBB                            ",
                            "                           ABBBBBBBA                           ",
                            "                            BBBBBBB                            ",
                            "                             BBBBB                             ",
                            "                              BBB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                              DAD                              ",
                            "                            AAACAAA                            ",
                            "                              DAD                              ",
                            "                               A                               ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                               C                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                               C                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               D                               ",
                            "                            DDDDDDD                            ",
                            "                                                               ",
                            "                            DDDDDDD                            ",
                            "                               D                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               D                               ",
                            "                            EEEEEEE                            ",
                            "                         DDDEEEEEEEDDD                         ",
                            "                            EEEEEEE                            ",
                            "                         DDDEEEEEEEDDD                         ",
                            "                            EEEEEEE                            ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                         EEE       EEE                         ",
                            "                       DDEEE       EEEDD                       ",
                            "                         EEE  FCF  EEE                         ",
                            "                       DDEEE       EEEDD                       ",
                            "                         EEE       EEE                         ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                       EE             EE                       ",
                            "                      DEE             EED                      ",
                            "                       EE     FCF     EE                       ",
                            "                      DEE             EED                      ",
                            "                       EE             EE                       ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                      E                 E                      ",
                            "                     DE                 ED                     ",
                            "                      E       FCF       E                      ",
                            "                     DE                 ED                     ",
                            "                      E                 E                      ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                     E                   E                     ",
                            "                    DE                   ED                    ",
                            "                     E        FCF        E                     ",
                            "                    DE                   ED                    ",
                            "                     E                   E                     ",
                            "                            GGGGGGG                            ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                            GGGGGGG                            ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                    E                     E                    ",
                            "                   DE       EEEEEEE       ED                   ",
                            "                    E F     EEFCFEE     F E                    ",
                            "                   DE       EEEEEEE       ED                   ",
                            "                    E         D D         E                    ",
                            "                          GGGGGGGGGGG                          ",
                            "                              EEE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EEE                              ",
                            "                          GGIIIIIIIGG                          ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            EEEEEEE                            ",
                            "                                                               ",
                            "                                                               ",
                            "                   E                       E                   ",
                            "                  DE      EE       EE      ED                  ",
                            "                   E F F  EE  FCF  EE  F F E                   ",
                            "                  DE      EE       EE      ED                  ",
                            "                   E                       E                   ",
                            "                        GGGGGGGGGGGGGGG                        ",
                            "                              EEE                              ",
                            "                              HJH                              ",
                            "                              HJH                              ",
                            "                              HJH                              ",
                            "                              EEE                              ",
                            "                        GGIIIIIIIIIIIGG                        ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                          EE       EE                          ",
                            "                                                               ",
                            "                                                               ",
                            "                  E                         E                  ",
                            "                 DE      E           E      ED                 ",
                            "                  E   F FE    FCF    EF F   E                  ",
                            "                 DE      E           E      ED                 ",
                            "                  E      D           D      E                  ",
                            "                       GGGGGGGGGGGGGGGGG                       ",
                            "                              EEE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EEE                              ",
                            "                       GIIIIIIIIIIIIIIIG                       ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                         E           E                         ",
                            "                                                               ",
                            "                                                               ",
                            "                  E                         E                  ",
                            "                 DE     E     EEE     E     ED                 ",
                            "                  E    FEF   EEEEE   FEF    E                  ",
                            "                 DE     E     EEE     E     ED                 ",
                            "                  E                         E                  ",
                            "                      GGGGGGGGGGGGGGGGGGG                      ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             DK KD                             ",
                            "                      GIIIIIIGGGGGIIIIIIG                      ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           EE     EE                           ",
                            "                                                               ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                        E     FCF     E                        ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                 E            FCF            E                 ",
                            "                DE     E      FCF      E     ED                ",
                            "                 E     EF FEE FCF EEF FE     E                 ",
                            "                DE     E               E     ED                ",
                            "                 E     D               D     E                 ",
                            "                      GGGGGGG D D GGGGGGG                      ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              K K                              ",
                            "                      GIIIIGG     GGIIIIG                      ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                          E   FCF   E                          ",
                            "                              FCF                              ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                       E               E                       ",
                            "                                                               ",
                            "                             EEEEE                             ",
                            "                 E                           E                 ",
                            "                DE    E                 E    ED                ",
                            "                 E    E  FE    C    EF  E    E                 ",
                            "                DE    E                 E    ED                ",
                            "                 E                           E                 ",
                            "                     GGGGGG   D D   GGGGGG                     ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             GGGGG                             ",
                            "                     GIIIIG         GIIIIG                     ")
                    .aisle("                               C                               ",
                            "                               C                               ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                         E           E                         ",
                            "                                                               ",
                            "                            ELLLLLE                            ",
                            "                                                               ",
                            "                       E               E                       ",
                            "                                                               ",
                            "                            E     E                            ",
                            "                 E                           E                 ",
                            "                DE    E                 E    ED                ",
                            " AAAAAAAAAAA     E    E  E     C     E  E    E     AAAAAAAAAAA ",
                            "                DE    E                 E    ED                ",
                            "                 E                           E                 ",
                            "                     GGGGG    D D    GGGGG                     ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            GIIIIIG                            ",
                            "                     GIIIG           GIIIG                     ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              EEE                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              EEE                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            ELLLLLE                            ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            ELLLLLE                            ",
                            "                                                               ",
                            "                         E           E                         ",
                            "                                                               ",
                            "                           ELMMMMMLE                           ",
                            "                                                               ",
                            "                      E                 E                      ",
                            "                                                               ",
                            "                           E  EEE  E                           ",
                            "                E             EEE             E                ",
                            " BBBBBBBBBBB   DE    E        EEE        E    ED   BBBBBBBBBBB ",
                            "ABCCCCCCCCBBA   E    E   E    EEE    E   E    E   ABCCCCCCCCCBA",
                            " BBBBBBBBBBB   DE    E        EEE        E    ED   BBBBBBBBBBB ",
                            "                E             EEE             E                ",
                            "                    GGGGGG   DEEED   GGGGGG                    ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           GIIIIIIIG                           ",
                            "                    GIIIIG           GIIIIG                    ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             ELLLE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             ELLLE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           ELLMMMLLE                           ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           ELLMMMLLE                           ",
                            "                                                               ",
                            "                        E             E                        ",
                            "                                                               ",
                            "                          ELMMNNNMMLE                          ",
                            "                                                               ",
                            "                      E                 EDD                    ",
                            "                              EEE          D                   ",
                            "                          E  CCCCC  E       DD                 ",
                            " BBBBBBBBBBB    E            CCCCC            E    BBBBBBBBBBB ",
                            " B         B   DE    E       CCCCC       E    ED   B         B ",
                            "AB         BA   E    E  E    CCCCC    E  E    E   AB         BA",
                            " B         B   DE    E       CCCCC       E    ED   B         B ",
                            " BBBBBBBBBBB    E            CCCCC            E    BBBBBBBBBBB ",
                            "                    GGGGG   DCCCCCD   GGGGG                    ",
                            "                    D   D    DEDED    D   D                    ",
                            "                    D   D             D   D                    ",
                            "                    D   D             D   D                    ",
                            "                    D   D             D   D                    ",
                            "                    D   D GIIIIIIIIIG D   D                    ",
                            "                    GIIIG             GIIIG                    ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           FELMMMLEF                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           FELMMMLEF                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                          FELMMNMMLEF                          ",
                            "                          F         F                          ",
                            "                          F         F                          ",
                            "                          F         F                          ",
                            "                          FELMMNMMLEF                          ",
                            "                          F         F                          ",
                            "                        E F         F E                        ",
                            "                          F         F                          ",
                            "                         FELMNNNNNMLEF                         ",
                            "                         F           F                         ",
                            "                    DDE  F           F  E                      ",
                            "                   D     F   EOOOE   F                         ",
                            " BBBBBBBBBBB     DD      FEDECOLOCEDEF             BBBBBBBBBBB ",
                            " B         B    E        F  ECOLOCE  F        E    B         B ",
                            " B         BDDDDE    E  EF  ECOLOCE  FE  E    EDDDDB         B ",
                            "AB         BA   EFFFFFFFEF  ECOLOCE  FEFFFFFFFE   AB         BA",
                            " B         BDDDDE    E  E   ECOLOCE   E  E    EDDDDB         B ",
                            " B         B    E    D      ECOLOCE      D    E    B         B ",
                            " BBBBBBBBBBB    DDDDGGGGGDDDECOLOCEDDDGGGGGDDDD    BBBBBBBBBBB ",
                            "                     EEE     EEEEE     EEE                     ",
                            "                     EHE      EEE      EHE                     ",
                            "                     EHE               EHE                     ",
                            "                     EHE               EHE                     ",
                            "                     EEEKKGIIIIIIIIIGKKEEE                     ",
                            "                    GIIIG             GIIIG                    ")
                    .aisle("                           C       C                           ",
                            "                           C       C                           ",
                            "                           C       C                           ",
                            "                           C       C                           ",
                            "                           CELMNMLEC                           ",
                            "                           C       C                           ",
                            "                           C       C                           ",
                            "                           C       C                           ",
                            "                           CELMNMLEC                           ",
                            "                           C       C                           ",
                            "                           C       C                           ",
                            "                           C       C                           ",
                            "                          CELMNNNMLEC                          ",
                            "                          C         C                          ",
                            "                          C         C                          ",
                            "                          C         C                          ",
                            "                          CELMNNNMLEC                          ",
                            "                          C         C                          ",
                            "                        E C         C E                        ",
                            "                          C         C                          ",
                            "                         CELMNNNNNMLEC                         ",
                            "                         C           C                         ",
                            "                      E  C           C  E                      ",
                            " AAAAAAAAAAA             C   EO OE   C             AAAAAAAAAAA ",
                            "ABCCCCCCCCBBA   D        CE ECL LCE EC        D   ABBCCCCCCCCBA",
                            "AB         BA  DE        C  ECL LCE  C        ED  AB         BA",
                            "AB         BA  DE    E  EC  ECL LCE  CE  E    ED  AB         BA",
                            "AB         BCCCCCCCCCCCCECCCECL LCECCCECCCCCCCECCCCB         BA",
                            "AB         BA  DE    E  E   ECL LCE   E  E    ED  AB         BA",
                            "AB         BA  DE           ECLGLCE           ED  AB         BA",
                            "ABCCCCCCCCCBA       GGGGG   ECL LCE   GGGGG       ABCCCCCCCCCBA",
                            " AAAAAAAAAAA         EEE     DE ED     EEE         AAAAAAAAAAA ",
                            "                     HJH      EEE      HJH                     ",
                            "                     HJH               HJH                     ",
                            "                     HJH               HJH                     ",
                            "                     EEE  GIIIIIIIIIG  EEE                     ",
                            "                    GIIIG             GIIIG                    ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           FELMMMLEF                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           FELMMMLEF                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                           F       F                           ",
                            "                          FELMMNMMLEF                          ",
                            "                          F         F                          ",
                            "                          F         F                          ",
                            "                          F         F                          ",
                            "                          FELMMNMMLEF                          ",
                            "                          F         F                          ",
                            "                        E F         F E                        ",
                            "                          F         F                          ",
                            "                         FELMNNNNNMLEF                         ",
                            "                         F           F                         ",
                            "                    DDE  F           F  EDD                    ",
                            "                   D     F   EOOOE   F     D                   ",
                            " BBBBBBBBBBB     DD      FEDECOLOCEDEF      DD     BBBBBBBBBBB ",
                            " B         B    E        F  ECOLOCE  F        E    B         B ",
                            " B         BDDDDE    E  EF  ECOLOCE  FE  E    EDDDDB         B ",
                            "AB         BA   EFFFFFFFEF  ECOLOCE  FEFFFFFFFE   AB         BA",
                            " B         BDDDDE    E  E   ECOLOCE   E  E    EDDDDB         B ",
                            " B         B    E    D      ECOLOCE      D    E    B         B ",
                            " BBBBBBBBBBB    DDDDGGGGGDDDECOLOCEDDDGGGGGDDDD    BBBBBBBBBBB ",
                            "                     EEE     EEEEE     EEE                     ",
                            "                     EHE      EPE      EHE                     ",
                            "                     EHE               EHE                     ",
                            "                     EHE               EHE                     ",
                            "                     EEEKKGIIIIIIIIIGKKEEE                     ",
                            "                    GIIIG             GIIIG                    ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             ELLLE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             ELLLE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           ELLMMMLLE                           ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           ELLMMMLLE                           ",
                            "                                                               ",
                            "                        E             E                        ",
                            "                                                               ",
                            "                          ELMMNNNMMLE                          ",
                            "                                                               ",
                            "                      E                 E                      ",
                            "                              EEE                              ",
                            "                          E  CCCCC  E                          ",
                            " BBBBBBBBBBB    E            CCCCC            E    BBBBBBBBBBB ",
                            " B         B   DE    E       CCCCC       E    ED   B         B ",
                            "AB         BA   E    E  E    CCCCC    E  E    E   AB         BA",
                            " B         B   DE    E       CCCCC       E    ED   B         B ",
                            " BBBBBBBBBBB    E            CCCCC            E    BBBBBBBBBBB ",
                            "                    GGGGG   DCCCCCD   GGGGG                    ",
                            "                    D   D    DEDED    D   D                    ",
                            "                    D   D             D   D                    ",
                            "                    D   D             D   D                    ",
                            "                    D   D             D   D                    ",
                            "                    D   D GIIIIIIIIIG D   D                    ",
                            "                    GIIIG             GIIIG                    ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              EEE                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              EEE                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            ELLLLLE                            ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            ELLLLLE                            ",
                            "                                                               ",
                            "                         E           E                         ",
                            "                                                               ",
                            "                           ELMMMMMLE                           ",
                            "                                                               ",
                            "                      E                 E                      ",
                            "                                                               ",
                            "                           E  EEE  E                           ",
                            "                E             EEE             E                ",
                            " BBBBBBBBBBB   DE    E        EEE        E    ED   BBBBBBBBBBB ",
                            "ABCCCCCCCCCBA   E    E   E    EEE    E   E    E   ABBCCCCCCCCBA",
                            " BBBBBBBBBBB   DE    E        EEE        E    ED   BBBBBBBBBBB ",
                            "                E             EEE             E                ",
                            "                    GGGGGG   DEEED   GGGGGG                    ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           GIIIIIIIG                           ",
                            "                    GIIIIG           GIIIIG                    ")
                    .aisle("                               C                               ",
                            "                               C                               ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                         E           E                         ",
                            "                                                               ",
                            "                            ELLLLLE                            ",
                            "                                                               ",
                            "                       E               E                       ",
                            "                                                               ",
                            "                            E D D E                            ",
                            "                 E                           E                 ",
                            "                DE    E                 E    ED                ",
                            " AAAAAAAAAAA     E    E  E     C     E  E    E     AAAAAAAAAAA ",
                            "                DE    E                 E    ED                ",
                            "                 E                           E                 ",
                            "                     GGGGG    D D    GGGGG                     ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            GIIIIIG                            ",
                            "                     GIIIG           GIIIG                     ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                          E   FCF   E                          ",
                            "                              FCF                              ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                       E               E                       ",
                            "                                                               ",
                            "                             EEEEE                             ",
                            "                 E                           E                 ",
                            "                DE    E                 E    ED                ",
                            "                 E    E  FE    C    EF  E    E                 ",
                            "                DE    E                 E    ED                ",
                            "                 E                           E                 ",
                            "                     GGGGGG   D D   GGGGGG                     ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             GGGGG                             ",
                            "                     GIIIIG         GIIIIG                     ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                           EE     EE                           ",
                            "                                                               ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                        E     FCF     E                        ",
                            "                              FCF                              ",
                            "                              FCF                              ",
                            "                 E            FCF            E                 ",
                            "                DE     E      FCF      E     ED                ",
                            "                 E     EF FEE FCF EEF FE     E                 ",
                            "                DE     E               E     ED                ",
                            "                 E     D               D     E                 ",
                            "                      GGGGGGG D D GGGGGGG                      ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              K K                              ",
                            "                      GIIIIGG     GGIIIIG                      ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                             EEEEE                             ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                         E           E                         ",
                            "                                                               ",
                            "                                                               ",
                            "                  E                         E                  ",
                            "                 DE     E     EEE     E     ED                 ",
                            "                  E    FEF   EEEEE   FEF    E                  ",
                            "                 DE     E     EEE     E     ED                 ",
                            "                  E                         E                  ",
                            "                      GGGGGGGGGGGGGGGGGGG                      ",
                            "                                 D                             ",
                            "                                 D                             ",
                            "                                 D                             ",
                            "                                 D                             ",
                            "                              K KD                             ",
                            "                      GIIIIIIGGGGGIIIIIIG                      ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                          EE       EE                          ",
                            "                                                               ",
                            "                                                               ",
                            "                  E                         E                  ",
                            "                 DE      E           E      ED                 ",
                            "                  E   F FE    FCF    EF F   E                  ",
                            "                 DE      E           E      ED                 ",
                            "                  E      D           D      E                  ",
                            "                       GGGGGGGGGGGGGGGGG                       ",
                            "                              EEE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EEE                              ",
                            "                       GIIIIIIIIIIIIIIIG                       ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                            EEEEEEE                            ",
                            "                                                               ",
                            "                                                               ",
                            "                   E                       E                   ",
                            "                  DE      EE       EE      ED                  ",
                            "                   E F F  EE  FCF  EE  F F E                   ",
                            "                  DE      EE       EE      ED                  ",
                            "                   E                       E                   ",
                            "                        GGGGGGGGGGGGGGG                        ",
                            "                              EEE                              ",
                            "                              HJH                              ",
                            "                              HJH                              ",
                            "                              HJH                              ",
                            "                              EEE                              ",
                            "                        GGIIIIIIIIIIIGG                        ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                    E                     E                    ",
                            "                   DE       EEEEEEE       ED                   ",
                            "                    E F     EEFCFEE     F E                    ",
                            "                   DE       EEEEEEE       ED                   ",
                            "                    E         D D         E                    ",
                            "                          GGGGGGGGGGG                          ",
                            "                              EEE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EHE                              ",
                            "                              EEE                              ",
                            "                          GGIIIIIIIGG                          ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                     E                   E                     ",
                            "                    DE                   ED                    ",
                            "                     E        FCF        E                     ",
                            "                    DE                   ED                    ",
                            "                     E                   E                     ",
                            "                            GGGGGGG                            ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                             D   D                             ",
                            "                            GGGGGGG                            ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                      E                 E                      ",
                            "                     DE                 ED                     ",
                            "                      E       FCF       E                      ",
                            "                     DE                 ED                     ",
                            "                      E                 E                      ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                       EE             EE                       ",
                            "                      DEE             EED                      ",
                            "                       EE     FCF     EE                       ",
                            "                      DEE             EED                      ",
                            "                       EE             EE                       ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                         EEE       EEE                         ",
                            "                       DDEEE       EEEDD                       ",
                            "                         EEE  FCF  EEE                         ",
                            "                       DDEEE       EEEDD                       ",
                            "                         EEE       EEE                         ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               D                               ",
                            "                            EEEEEEE                            ",
                            "                         DDDEEEEEEEDDD                         ",
                            "                            EEEEEEE                            ",
                            "                         DDDEEEEEEEDDD                         ",
                            "                            EEEEEEE                            ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               D                               ",
                            "                            DDDDDDD                            ",
                            "                               C                               ",
                            "                            DDDDDDD                            ",
                            "                               D                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                               C                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                              D D                              ",
                            "                               C                               ",
                            "                              D D                              ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                              DAD                              ",
                            "                            AAACAAA                            ",
                            "                              DAD                              ",
                            "                               A                               ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BBB                              ",
                            "                             BBBBB                             ",
                            "                            BBBBBBB                            ",
                            "                           ABBBBBBBA                           ",
                            "                            BBBBBBB                            ",
                            "                             BBBBB                             ",
                            "                              BBB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BBB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AB     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BCB                              ",
                            "                             B   B                             ",
                            "                            B     B                            ",
                            "                           AC     CA                           ",
                            "                            B     B                            ",
                            "                             B   B                             ",
                            "                              BCB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                              BBB                              ",
                            "                             BBBBB                             ",
                            "                            BBBBBBB                            ",
                            "                           ABBBBBBBA                           ",
                            "                            BBBBBBB                            ",
                            "                             BBBBB                             ",
                            "                              BBB                              ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .aisle("                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                            AAAAAAA                            ",
                            "                               A                               ",
                            "                               A                               ",
                            "                               A                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ",
                            "                                                               ")
                    .where(' ', any())
                    .where("A", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, CosmicMaterials.PsionicGalvorn)))
                    .where("B", blocks(CosmicBlocks.NAQUADAH_PRESSURE_RESISTANT_CASING.get()))
                    .where("C", blocks(CosmicBlocks.VOMAHINE_ULTRA_POWERED_CASING.get()))
                    .where("D", blocks(CosmicBlocks.VOMAHINE_CERTIFIED_INTERSTELLAR_GRADE_CASING.get()))
                    .where("E", blocks(VOMAHINE_CERTIFIED_CHEMICALLY_RESISTANT_CASING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS))
                            .or(abilities(PartAbility.EXPORT_FLUIDS))
                            .or(abilities(PartAbility.IMPORT_ITEMS))
                            .or(abilities(PartAbility.EXPORT_ITEMS))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.MAINTENANCE))
                            .or(abilities(PartAbility.DATA_ACCESS))
                            .or(abilities(PartAbility.COMPUTATION_DATA_RECEPTION))
                            .or(abilities(PartAbility.OPTICAL_DATA_RECEPTION))
                            .or(abilities(PartAbility.PARALLEL_HATCH))
                            .or(abilities(CosmicPartAbility.COSMIC_PARALLEL_HATCH))
                            .or(abilities(PartAbility.INPUT_LASER))
                            .or(abilities(PartAbility.INPUT_ENERGY)))
                    .where("F", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, CosmicMaterials.Trinavine)))
                    .where("G", blocks(CosmicBlocks.VOMAHINE_CERTIFIED_INTERSTELLAR_GRADE_CASING.get()))
                    .where("H", blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where("I", blocks(CosmicBlocks.CASING_DYSON_CELL.get()))
                    .where("J", blocks(CosmicBlocks.VOMAHINE_ULTRA_POWERED_CASING.get()))
                    .where("K", any())
                    .where("L", magnetCoils())
                    .where("M", blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where("N", blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where("O", blocks(CosmicBlocks.VOMAHINE_ULTRA_POWERED_CASING.get()))
                    .where("P", controller(blocks(definition.getBlock())))
                    .build())
            .workableCasingRenderer(CosmicCore.id("block/casings/solid/vomahine_certified_chemically_resistant_casing"),
                    CosmicCore.id("block/multiblock/vomahine_chemplant"))
            .register();

    private static MachineDefinition[] registerSoulTieredHatch(String name, String displayName, String model, IO io,
                                                               int[] tiers, PartAbility... abilities) {
        return registerTieredMachines(name,
                (holder, tier) -> new SoulHatchPartMachine(holder, tier, io),
                (tier, builder) -> builder
                        .langValue(GTValues.VNF[tier] + ' ' + displayName)
                        .abilities(abilities)
                        .rotationState(RotationState.ALL)
                        .overlayTieredHullRenderer(model)
                        .tooltipBuilder((item, tooltip) -> {
                            if (io == IO.IN)
                                tooltip.add(Component.translatable("tooltip.cosmiccore.soul_hatch.input",
                                        SoulHatchPartMachine.getMaxConsumption(tier)));
                            else
                                tooltip.add(Component.translatable("tooltip.cosmiccore.soul_hatch.output",
                                        SoulHatchPartMachine.getMaxCapacity(tier)));
                        }).register(),
                tiers);
    }

    private static MachineDefinition[] registerThermiaTieredHatch(String name, String displayName, String model, IO io,
                                                                  int[] tiers, PartAbility... abilities) {
        return registerTieredMachines(name,
                (holder, tier) -> new ThermiaHatchPartMachine(holder, tier, io),
                (tier, builder) -> builder
                        .langValue(GTValues.VNF[tier] + ' ' + displayName)
                        .abilities(abilities)
                        .rotationState(RotationState.ALL)
                        .overlayTieredHullRenderer(model)
                        .tooltipBuilder((item, tooltip) -> {
                            if (io == IO.IN)
                                tooltip.add(Component.translatable("tooltip.cosmiccore.thermia_hatch_limit",
                                        ThermiaHatchPartMachine.getThermiaLimits(tier)));
                            else
                                tooltip.add(Component.translatable("tooltip.cosmiccore.thermia_hatch_limit",
                                        ThermiaHatchPartMachine.getThermiaLimits(tier)));
                        }).register(),
                tiers);
    }

    public static final MachineDefinition CREATIVE_HEAT = REGISTRATE
            .machine("creative_thermal", CreativeThermiaContainerMachine::new)
            .rotationState(RotationState.NONE)
            .tooltipBuilder(CREATIVE_TOOLTIPS)
            .register();

    private static MachineDefinition[] registerTieredMachines(String name,
                                                              BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                              BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder,
                                                              int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE.machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                    holder -> factory.apply(holder, tier)).tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static final MachineDefinition STEAM_IMPORT_HATCH = GTRegistration.REGISTRATE
            .machine("steam_fluid_input_hatch", holder -> new SteamFluidHatchPartMachine(holder, IO.IN, 4000, 1))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_FLUIDS)
            .overlaySteamHullRenderer("fluid_hatch.import")
            .tooltips(Component.translatable("gtceu.machine.steam_fluid_hatch_notice"))
            .langValue("Fluid Input Hatch (Steam)")
            .register();
    public static final MachineDefinition STEAM_EXPORT_HATCH = GTRegistration.REGISTRATE
            .machine("steam_fluid_output_hatch", holder -> new SteamFluidHatchPartMachine(holder, IO.OUT, 4000, 1))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_FLUIDS)
            .overlaySteamHullRenderer("fluid_hatch.export")
            .langValue("Fluid Output Hatch (Steam)")
            .register();

    public static final MultiblockMachineDefinition WIRELESS_DATA_TRANSMITTER = REGISTRATE
            .multiblock("wireless_data_transmitter", WirelessDataBankMachine::new)
            .langValue("Wireless Data Transmitter")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("M", "A", "A")
                    .aisle("S", "C", "I")
                    .where("C", controller(blocks(definition.getBlock())))
                    .where("S", abilities(PartAbility.OPTICAL_DATA_RECEPTION))
                    .where("I", abilities(PartAbility.INPUT_ENERGY))
                    .where("M", abilities(PartAbility.MAINTENANCE))
                    .where("A", blocks(HIGH_POWER_CASING.get()))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/hpca/high_power_casing"),
                    CosmicCore.id("block/multiblock/wireless_data_transmitter"))
            .register();

    public static final MachineDefinition WIRELESS_DATA_HATCH = REGISTRATE
            .machine("wireless_data_hatch", WirelessDataHatchPartMachine::new)
            .langValue("Wireless Data Hatch")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.DATA_ACCESS)
            .tier(UEV)
            .overlayTieredHullRenderer("wireless_data_hatch")
            .register();

    public static void init() {
        for (MultiblockMachineDefinition definition : FUSION_REACTOR) {
            if (definition == null) continue;
            definition.setPatternFactory(() -> {
                var casing = blocks(FusionReactorMachine.getCasingState(definition.getTier()));
                return FactoryBlockPattern.start()
                        .aisle("###############", "######OGO######", "###############")
                        .aisle("######ICI######", "####GGAAAGG####", "######ICI######")
                        .aisle("####CC###CC####", "###EAAOGOAAE###", "####CC###CC####")
                        .aisle("###C#######C###", "##EKEG###GEKE##", "###C#######C###")
                        .aisle("##C#########C##", "#GAE#######EAG#", "##C#########C##")
                        .aisle("##C#########C##", "#GAG#######GAG#", "##C#########C##")
                        .aisle("#I###########I#", "OAO#########OAO", "#I###########I#")
                        .aisle("#C###########C#", "GAG#########GAG", "#C###########C#")
                        .aisle("#I###########I#", "OAO#########OAO", "#I###########I#")
                        .aisle("##C#########C##", "#GAG#######GAG#", "##C#########C##")
                        .aisle("##C#########C##", "#GAE#######EAG#", "##C#########C##")
                        .aisle("###C#######C###", "##EKEG###GEKE##", "###C#######C###")
                        .aisle("####CC###CC####", "###EAAOGOAAE###", "####CC###CC####")
                        .aisle("######ICI######", "####GGAAAGG####", "######ICI######")
                        .aisle("###############", "######OSO######", "###############")
                        .where('S', controller(blocks(definition.get())))
                        .where('G', blocks(FUSION_GLASS.get()).or(casing))
                        .where('E', casing.or(
                                blocks(PartAbility.INPUT_ENERGY.getBlockRange(definition.getTier(), UV)
                                        .toArray(Block[]::new))
                                        .setMinGlobalLimited(1).setPreviewCount(16)))
                        .where('C', casing)
                        .where('K', blocks(FusionReactorMachine.getCoilState(definition.getTier())))
                        .where('O', casing.or(abilities(PartAbility.EXPORT_FLUIDS))
                                .or(abilities(PartAbility.EXPORT_ITEMS)))
                        .where('A', air())
                        .where('I', casing.or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(2))
                                .or(abilities(PartAbility.IMPORT_ITEMS)))
                        .where('#', any())
                        .build();
            });
        }
    }
}
