package com.ghostipedia.cosmiccore.common.machine.multiblock.multi;

import com.ghostipedia.cosmiccore.CosmicCore;
import com.ghostipedia.cosmiccore.common.wireless.WirelessDataStore;
import com.ghostipedia.cosmiccore.utils.OwnershipUtils;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.IDataAccessHatch;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
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
    private boolean transmitting = false;

    @Getter
    private int energyUsage = 0;

    @Nullable
    protected TickableSubscription tickSubs;

    public WirelessDataBankMachine(IMachineBlockEntity holder) {
        super(holder);
        energyContainer = new EnergyContainerList(new ArrayList<>());
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
                if (io != IO.BOTH && handler.getHandlerIO() != IO.BOTH && io != handler.getHandlerIO())
                    continue;
                if (handler.getCapability() == EURecipeCapability.CAP && handler instanceof IEnergyContainer container)
                    energyContainers.add(container);
            }
        }

        energyContainer = new EnergyContainerList(new ArrayList<>(energyContainers));
        energyUsage = calculateEnergyUsage();

        if (this.maintenance == null) {
            onStructureInvalid();
            return;
        }

        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateTickSubscription));
        }
    }

    private int calculateEnergyUsage() {
        int receivers = getOpticalHatches().size();
        return receivers * EUT_PER_HATCH_CHAINED;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.removeHatchesFromWirelessNetwork();
        this.energyContainer = new EnergyContainerList(new ArrayList<>());
        this.energyUsage = 0;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.isFormed() && getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateTickSubscription));
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (tickSubs != null) {
            tickSubs.unsubscribe();
            tickSubs = null;
        }
    }

    protected void updateTickSubscription() {
        if (isFormed) {
            tickSubs = subscribeServerTick(tickSubs, this::tick);
        } else if (tickSubs != null) {
            tickSubs.unsubscribe();
            tickSubs = null;
        }
    }

    public void tick() {
        int energyToConsume = this.getEnergyUsage();
        boolean hasMaintenance = ConfigHolder.INSTANCE.machines.enableMaintenance && this.maintenance != null;
        if (hasMaintenance) energyToConsume += maintenance.getNumMaintenanceProblems() * energyToConsume / 10;

        if (this.energyContainer.getEnergyStored() >= energyToConsume) {
            if (getRecipeLogic().isWaiting()) getRecipeLogic().setStatus(RecipeLogic.Status.IDLE);
            if (!getRecipeLogic().isWaiting()) {
                if (!transmitting) addHatchesToWirelessNetwork();
                long consumed = this.energyContainer.removeEnergy(energyToConsume);
                if (consumed == energyToConsume) {
                    getRecipeLogic().setStatus(RecipeLogic.Status.WORKING);
                } else {
                    getRecipeLogic().setWaiting(Component.translatable("gtceu.recipe_logic.insufficient_in")
                            .append(": ").append(EURecipeCapability.CAP.getName()));
                }
            }
        } else {
            getRecipeLogic()
                    .setWaiting(Component.translatable("gtceu.recipe_logic.insufficient_in").append(": ")
                    .append(EURecipeCapability.CAP.getName()));
            removeHatchesFromWirelessNetwork();
        }
        updateTickSubscription();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(true, isActive() && isWorkingEnabled()) // transform into two-state system for display
                .setWorkingStatusKeys(
                        "gtceu.multiblock.idling",
                        "gtceu.multiblock.idling",
                        "gtceu.multiblock.data_bank.providing")
                .addEnergyUsageExactLine(getEnergyUsage())
                .addWorkingStatusLine()
                .addEmptyLine();
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getMaxProgress() {
        return 0;
    }

    private void addHatchesToWirelessNetwork() {
        var owner = getHolder().getOwner();
        var uuid = OwnershipUtils.getOwnerUUID(owner);
        var hatches = getOpticalHatches();
        WirelessDataStore.addHatches(uuid, hatches);
        transmitting = true;
    }

    private void removeHatchesFromWirelessNetwork() {
        var owner = getHolder().getOwner();
        var uuid = OwnershipUtils.getOwnerUUID(owner);
        var hatches = getOpticalHatches();
        WirelessDataStore.removeHatches(uuid, hatches);
        transmitting = false;
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
}
