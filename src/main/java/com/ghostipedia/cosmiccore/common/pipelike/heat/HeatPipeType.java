package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.api.data.CosmicCustomTags;
import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.pipenet.IMaterialPipeType;
import com.gregtechceu.gtceu.api.pipenet.IPipeType;
import com.gregtechceu.gtceu.client.model.PipeModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum HeatPipeType implements IMaterialPipeType<HeatPipeProperties>, StringRepresentable {
    NORMAL(CosmicCustomTags.heatNormal);

    public static final ResourceLocation TYPE = CosmicCore.id("heat");
    private final TagPrefix prefix;

    HeatPipeType(TagPrefix prefix) {
        this.prefix = prefix;
    }


    @Override
    public float getThickness() {
        return 0.375f;
    }

    @Override
    public HeatPipeProperties modifyProperties(HeatPipeProperties baseProperties) {
        return baseProperties;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    public PipeModel createPipeModel(Material mat) {
        return new PipeModel(getThickness(), () -> GTCEu.id("block/pipe/pipe_side"), () -> GTCEu.id("block/pipe/pipe_normal_in"), null, null);
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public TagPrefix getTagPrefix() {
        return prefix;
    }
}
