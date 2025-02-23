package com.ghostipedia.cosmiccore.api.machine.multiblock;

import com.ghostipedia.cosmiccore.api.data.wireless.WirelessEnergySavedData;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
 import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DimensionalEnergyCapacitorInterface extends WorkableMultiblockMachine {

    protected static final long ticks_between_save_data_operations = 60L * 20L; // Once per minute

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DimensionalEnergyCapacitorInterface.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    protected IMaintenanceMachine maintenance;
    protected EnergyContainerList inputHatches;
    protected EnergyContainerList outputHatches;

    protected IEnergyContainer energyBuffer;

    protected ConditionalSubscriptionHandler tickSubscription;

    public DimensionalEnergyCapacitorInterface(IMachineBlockEntity holder) {
        super(holder);
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::transferEnergyTick, this::isFormed);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IEnergyContainer> inputs = new ArrayList<>();
        List<IEnergyContainer> outputs = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);
        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;
            if (part instanceof IMaintenanceMachine maintenanceMachine) this.maintenance = maintenanceMachine;
            for (var handler : part.getRecipeHandlers()) {
                var handlerIO = handler.getHandlerIO();
                if (io != IO.BOTH && handlerIO != IO.BOTH && io != handlerIO) continue;
                if (handler.getCapability() == EURecipeCapability.CAP && handler instanceof IEnergyContainer container) {
                    if (handlerIO == IO.IN) inputs.add(container);
                    else if (handlerIO == IO.OUT) outputs.add(container);
                    traitSubscriptions.add(handler.addChangedListener(tickSubscription::updateSubscription));
                }
            }
        }
        this.inputHatches = new EnergyContainerList(inputs);
        this.outputHatches = new EnergyContainerList(outputs);

        setEnergyBuffer();
    }

    private void setEnergyBuffer() {
        long ioCapacity = inputHatches.getEnergyCapacity() + outputHatches.getEnergyCapacity();
        // Size is the capacity over the duration between operations with a safety factor of 6s of operations (10%)
        long bufferSize = ioCapacity * (ticks_between_save_data_operations + (6L * 20L));
        if (bufferSize < 0L) throw new RuntimeException("DimensionalEnergyCapacitor: Calculated buffer size is too big.");
        this.energyBuffer = new NotifiableEnergyContainer(this, bufferSize, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    @Override
    public void onStructureInvalid() {
        if (getLevel() instanceof ServerLevel serverLevel) { // Transfer buffer content to avoid losses
            var data = WirelessEnergySavedData.getOrCreate(serverLevel);
            data.addEUToGlobalWirelessEnergy(getHolder().getOwner().getUUID(), energyBuffer.getEnergyStored());
        }

        this.inputHatches = null;
        this.outputHatches = null;
        this.energyBuffer = null;

        super.onStructureInvalid();
    }

    protected void transferEnergyTick() {
        if (getLevel() instanceof ServerLevel serverLevel) {
            // TODO: handle UI stuff here

            if(isWorkingEnabled() && isFormed()) {
                // Handle inputs
                long energyBuffered = energyBuffer.addEnergy(inputHatches.getEnergyStored());
                inputHatches.changeEnergy(-energyBuffered);

                // Handle outputs
                long energyNeed = outputHatches.getEnergyCapacity() - outputHatches.getEnergyStored();
                long energyDeBuffered = energyBuffer.removeEnergy(energyNeed);
                outputHatches.changeEnergy(energyDeBuffered);

                // Handle buffer transfer to WirelessEnergySavedData
                if (getOffsetTimer() % ticks_between_save_data_operations == 0) {
                    var data = WirelessEnergySavedData.getOrCreate(serverLevel);
                    var owner = getHolder().getOwner().getUUID();
                    // After operation buffer should aim to be 50% full
                    var euToTransfer = (energyBuffer.getEnergyCapacity() / 2) - energyBuffer.getEnergyStored();
                    var euTransferred = data.addEUToGlobalWirelessEnergy(owner, euToTransfer);
                    energyBuffer.changeEnergy(euTransferred);
                }
            }
        }
    }
}
