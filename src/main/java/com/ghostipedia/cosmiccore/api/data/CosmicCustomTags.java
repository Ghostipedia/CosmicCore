package com.ghostipedia.cosmiccore.api.data;

import com.ghostipedia.cosmiccore.common.data.CosmicMaterialBlocks;
import com.ghostipedia.cosmiccore.common.data.tag.TagUtil;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.Conditions.hasOreProperty;

public class CosmicCustomTags {
    public static TagPrefix crushedLeached;
    public static TagPrefix prismaFrothed;

    public static TagPrefix heatNormal;

    public static final TagKey<Block> STAR_LADDER_BLOCKS = TagUtil.createBlockTag("starladder_blocks");
    public static final TagKey<Item> STAR_LADDER_ITEMS = TagUtil.createItemTag("starladder_items");
    public static void initTagPrefixes() {
        crushedLeached = new TagPrefix("leachedOre")
                .idPattern("leached_%s_ore")
                .defaultTagPath("leached_ores/%s")
                .defaultTagPath("leached_ores")
                .materialIconType(CosmicCoreMaterialIconType.crushedLeached)
                .unificationEnabled(true)
                .generateItem(true)
                .generationCondition(hasOreProperty);
        prismaFrothed = new TagPrefix("prismaFrothedOre")
                .idPattern("prisma_frothed_%s_ore")
                .defaultTagPath("prisma_frothed_ores/%s")
                .defaultTagPath("prisma_frothed_ores")
                .materialIconType(CosmicCoreMaterialIconType.prismaFrothed)
                .unificationEnabled(true)
                .generateItem(true)
                .generationCondition(hasOreProperty);

        heatNormal = new TagPrefix("heatNormal")
                .itemTable(() -> CosmicMaterialBlocks.HEAT_PIPE_BLOCKS).langValue("Normal %s Heat Pipe")
                .miningToolTag(GTToolType.WRENCH.harvestTags.get(0)).materialAmount(GTValues.M * 2)
                .unificationEnabled(true);
    }
}
