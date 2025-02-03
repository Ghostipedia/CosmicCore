package com.ghostipedia.cosmiccore.api.pipe;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import lombok.Getter;

import java.util.Objects;

public class HeatPipeProperties implements IMaterialProperty {

    @Getter
    private final float maxTemp;
    // do not go above 1 or death occurs
    @Getter
    private final float maxTransferRate;

    public HeatPipeProperties(float temp, float rate) {
        this.maxTemp = temp;
        this.maxTransferRate = rate;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {

    }

    @Override
    public int hashCode() {
        return Objects.hash(maxTemp, maxTransferRate);
    }
}
