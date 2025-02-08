package com.ghostipedia.cosmiccore.api.pipe;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import lombok.Getter;

import java.util.Objects;

public class HeatPipeProperties implements IMaterialProperty {

    @Getter
    private final float maxTemp;
    @Getter
    private final float conductance;
    @Getter
    private final float conductanceEnvironment;
    @Getter
    private final float thermalCapacity = 100f;

    private final int hash;

    public HeatPipeProperties(float temp, float rate) {
        assert rate > 0;
        this.maxTemp = temp;
        this.conductance = rate;
        this.conductanceEnvironment = rate * 0.1f;
        hash = Objects.hash(maxTemp, conductance, thermalCapacity);
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {

    }

    @Override
    public int hashCode() {
        return hash;
    }
}
