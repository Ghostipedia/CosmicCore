package com.ghostipedia.cosmiccore.common.data;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.api.material.CosmicPropertyKeys;
import com.ghostipedia.cosmiccore.common.block.pipelike.HeatPipeBlock;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeType;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.MaterialPipeBlock;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.MaterialPipeBlockItem;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;

public class CosmicMaterialBlocks {

    static ImmutableTable.Builder<TagPrefix, Material, BlockEntry<HeatPipeBlock>> HEAT_PIPE_BLOCKS_BUILDER = ImmutableTable.builder();

    public static Table<TagPrefix, Material, BlockEntry<HeatPipeBlock>> HEAT_PIPE_BLOCKS;

    public static void generateHeatPipeBlocks() {
        CosmicCore.LOGGER.debug("generating cosmic core material blocks...");

        for(var heatPipeType : HeatPipeType.values()) {
            for(MaterialRegistry registry : GTCEuAPI.materialManager.getRegistries()) {
                GTRegistrate registrate = registry.getRegistrate();
                for(Material mat : registry.getAllMaterials()) {
                    if(allowHeatPipeBlock(mat, heatPipeType)) {
                        registerHeatPipeBlock(mat, heatPipeType, registrate);
                    }
                }
            }
        }
        HEAT_PIPE_BLOCKS = HEAT_PIPE_BLOCKS_BUILDER.build();
    }

    private static boolean allowHeatPipeBlock(Material mat, HeatPipeType type) {
        return mat.hasProperty(CosmicPropertyKeys.HEAT) && !type.getTagPrefix().isIgnored(mat);
    }

    private static void registerHeatPipeBlock(Material mat, HeatPipeType heatPipeType, GTRegistrate registrate) {
        var entry = registrate
                .block("%s_%s_heat_pipe".formatted(mat.getName(), heatPipeType.name().toLowerCase(Locale.ROOT)),
                        p -> new HeatPipeBlock(p, heatPipeType, mat))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> {
                    return p.dynamicShape().noOcclusion().noLootTable().forceSolidOn();
                })
                .transform(GTBlocks.unificationBlock(heatPipeType.getTagPrefix(), mat))
                .blockstate(NonNullBiConsumer.noop())
                .setData(ProviderType.LANG, NonNullBiConsumer.noop())
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop())
                .addLayer(()-> RenderType::cutoutMipped)
                .color(() -> MaterialPipeBlock::tintedColor)
                .item(MaterialPipeBlockItem::new)
                .color(() -> MaterialPipeBlockItem::tintColor)
                .model(NonNullBiConsumer.noop())
                .build()
                .register();
        HEAT_PIPE_BLOCKS_BUILDER.put(heatPipeType.getTagPrefix(), mat, entry);
    }
}
