package com.ghostipedia.cosmiccore.common.machine.multiblock.multi;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.common.wireless.WirelessDataStore;
import com.ghostipedia.cosmiccore.utils.OwnershipUtils;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.ITickSubscription;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.IDataAccessHatch;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.DataBankMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.owner.ArgonautsOwner;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import com.gregtechceu.gtceu.common.machine.owner.IMachineOwner;
import com.gregtechceu.gtceu.common.machine.owner.PlayerOwner;
import com.gregtechceu.gtceu.config.ConfigHolder;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WirelessDataBankMachine extends WorkableElectricMultiblockMachine
        implements IFancyUIMachine, IDisplayUIMachine, IControllable {

    public static final int EUT_PER_HATCH_CHAINED = GTValues.VA[GTValues.LuV];

    private IMaintenanceMachine maintenance;
    private IEnergyContainer energyContainer;

    private TickableSubscription wirelessProviderSubscription;

    public WirelessDataBankMachine(IMachineBlockEntity holder) {
        super(holder);
        energyContainer = new EnergyContainerList(new ArrayList<>());
    }

    public void updateSubscriptions() {
        if (isSubscriptionActive()) {
            wirelessProviderSubscription = subscribeServerTick(wirelessProviderSubscription, this::transmitWirelessData);
        } else if (wirelessProviderSubscription != null) {
            wirelessProviderSubscription.unsubscribe();
            wirelessProviderSubscription = null;
            removeHatchesFromWirelessNetwork();
        } else {
            removeHatchesFromWirelessNetwork();
        }
    }

    public void unsubscribe() {
        if (wirelessProviderSubscription != null) {
            wirelessProviderSubscription.unsubscribe();
            wirelessProviderSubscription = null;
        }
    }

    protected void transmitWirelessData() {
        if (isWorkingEnabled()) {
            getRecipeLogic().setStatus(isSubscriptionActive() ? RecipeLogic.Status.WORKING : RecipeLogic.Status.SUSPEND);
            energyContainer.removeEnergy(calculateEnergyUsage());
            addHatchesToWirelessNetwork();
        } else removeHatchesFromWirelessNetwork();
        updateSubscriptions();
    }

    protected boolean isSubscriptionActive() {
        if (!isFormed()) return false;
        if (energyContainer == null || energyContainer.getEnergyStored() <= 0) return false;
        return energyContainer.getEnergyStored() > calculateEnergyUsage();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IEnergyContainer> energyContainers = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for(IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (part instanceof IMaintenanceMachine maintenanceMachine)
                this.maintenance = maintenanceMachine;
            if (io == IO.NONE || io == IO.OUT) continue;
            for (var handler : part.getRecipeHandlers()) {
                var handlerIO = handler.getHandlerIO();
                if (io != IO.BOTH && handlerIO != IO.BOTH && io != handlerIO) continue;
                if (handler.getCapability() == EURecipeCapability.CAP && handler instanceof IEnergyContainer container) {
                    energyContainers.add(container);
                    traitSubscriptions.add(handler.addChangedListener(this::updateSubscriptions));
                }
            }
        }

        if (this.maintenance == null) {
            onStructureInvalid();
            return;
        }

        this.energyContainer = new EnergyContainerList(new ArrayList<>(energyContainers));

        updateSubscriptions();
    }

    private int calculateEnergyUsage() {
        int receivers = getOpticalHatches().size();
        var maintenanceIssues = getMaintenanceIssues();
        return receivers * maintenanceIssues * EUT_PER_HATCH_CHAINED;
    }

    @Override
    public void onStructureInvalid() {
        if (isWorkingEnabled() && getRecipeLogic().getStatus() == RecipeLogic.Status.WORKING)
            removeHatchesFromWirelessNetwork();
        super.onStructureInvalid();
        this.energyContainer = new EnergyContainerList(new ArrayList<>());
        getRecipeLogic().setStatus(RecipeLogic.Status.SUSPEND);
        unsubscribe();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(true, isActive() && isWorkingEnabled()) // transform into two-state system for display
                .setWorkingStatusKeys(
                        "gtceu.multiblock.idling",
                        "gtceu.multiblock.idling",
                        "gtceu.multiblock.data_bank.providing")
                .addEnergyUsageExactLine(calculateEnergyUsage())
                .addWorkingStatusLine();
    }

    private void addHatchesToWirelessNetwork() {
        var owner = getHolder().getOwner();
        var uuid = OwnershipUtils.getOwnerUUID(owner);
        var hatches = getOpticalHatches();
        WirelessDataStore.addHatches(uuid, hatches);
    }

    private void removeHatchesFromWirelessNetwork() {
        var owner = getHolder().getOwner();
        var uuid = OwnershipUtils.getOwnerUUID(owner);
        var hatches = getOpticalHatches();
        WirelessDataStore.removeHatches(uuid, hatches);
    }

    private List<IDataAccessHatch> getOpticalHatches() {
        List<IDataAccessHatch> hatches = new ArrayList<>();

        for (var part : getParts()) {
            Block block = part.self().getBlockState().getBlock();
            if (part instanceof IDataAccessHatch hatch && PartAbility.OPTICAL_DATA_RECEPTION.isApplicable(block)){
                hatches.add(hatch);
            }
        }

        return hatches;
    }

    private int getMaintenanceIssues() {
        for (var part : getParts())
            if (part instanceof IMaintenanceMachine maintenanceMachine)
                return maintenanceMachine.getNumMaintenanceProblems();
        return 0;
    }
}
