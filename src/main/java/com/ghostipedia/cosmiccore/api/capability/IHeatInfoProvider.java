package com.ghostipedia.cosmiccore.api.capability;

public interface IHeatInfoProvider {

    record HeatInfo(Long capacity, Long currentTemp, boolean overload) {}
    //I need some way to store all dimensions temps, idk do it in the next interface.
    HeatInfo getHeatInfo();

    // this value should never go above 1 or you will get skinned alive
    default float getThermalConductance() {
        return 0f;
    }

    boolean supportsImpossibleHeatValues();
}
