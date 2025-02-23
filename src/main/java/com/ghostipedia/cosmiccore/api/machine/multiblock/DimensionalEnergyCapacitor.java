package com.ghostipedia.cosmiccore.api.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.PowerSubstationMachine;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DimensionalEnergyCapacitor extends DimensionalEnergyCapacitorInterface{

    public static final int MAX_BATTERY_LAYER = 18;
    public static final int MIN_CASING = 14;

    public DimensionalEnergyCapacitor(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IBatteryData> batteries = new ArrayList<>();
        for (Map.Entry<String, Object> battery : getMultiblockState().getMatchContext().entrySet()) {
            if (battery.getKey().startsWith(PowerSubstationMachine.PMC_BATTERY_HEADER)
                    && battery.getValue() instanceof CosmicBatteryMatchWrapper wrapper) {
                for (int i = 0; i < wrapper.getAmount(); i++) {
                    batteries.add(wrapper.getPartType());
                }
            }
        }

        var cap = batteries.stream().mapToLong(IBatteryData::getCapacity).sum();
        System.out.println(cap);
    }

    @Getter
    public static class CosmicBatteryMatchWrapper extends PowerSubstationMachine.BatteryMatchWrapper {

        private final IBatteryData partType;
        private int amount;

        public CosmicBatteryMatchWrapper(IBatteryData partType) {
            super(partType);
            this.partType = partType;
        }

        public CosmicBatteryMatchWrapper increment() {
            amount++;
            return this;
        }
    }
}
