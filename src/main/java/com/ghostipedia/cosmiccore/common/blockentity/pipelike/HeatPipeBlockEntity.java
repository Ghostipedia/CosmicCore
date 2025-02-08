package com.ghostipedia.cosmiccore.common.blockentity.pipelike;

import com.ghostipedia.cosmiccore.api.capability.CosmicCapabilities;
import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeNet;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeNetHandler;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeType;
import com.ghostipedia.cosmiccore.common.pipelike.heat.LevelHeatPipeNet;
import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDataInfoProvider;
import com.gregtechceu.gtceu.common.item.PortableScannerBehavior;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.List;

public class HeatPipeBlockEntity extends PipeBlockEntity<HeatPipeType, HeatPipeProperties> implements IDataInfoProvider {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(HeatPipeBlockEntity.class, PipeBlockEntity.MANAGED_FIELD_HOLDER);

    EnumMap<Direction, IHeatContainer> neighbors = new EnumMap<>(Direction.class);
    protected WeakReference<HeatPipeNet> currentHeatNet = new WeakReference<>(null);
    public HeatPipeNetHandler heatContainer;

    private TickableSubscription updateSubs;

    public HeatPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(updateSubs == null) {
            updateSubs = this.subscribeServerTick(this::update);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if(updateSubs != null) {
            this.unsubscribe(updateSubs);
            updateSubs = null;
        }
    }

    @Override
    public boolean canAttachTo(Direction side) {
        if(level != null) {
            if(level.getBlockEntity(getBlockPos().relative(side)) instanceof HeatPipeBlockEntity) {
                return false;
            }
            return getBlockEntityCapability(CosmicCapabilities.CAPABILITY_HEAT_CONTAINER, level, getBlockPos().relative(side), side.getOpposite()) != null;
        }
        return false;
    }

    @Nullable
    private static <T> T getBlockEntityCapability(Capability<T> capability, Level level, BlockPos pos,
                                                  @Nullable Direction side) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return blockEntity.getCapability(capability, side).resolve().orElse(null);
            }
        }
        return null;
    }

    private HeatPipeNet getHeatNet() {
        if(!(level instanceof ServerLevel serverLevel))
            return null;
        HeatPipeNet currentNet = this.currentHeatNet.get();
        if(currentNet != null && currentNet.isValid() && currentNet.containsNode(getBlockPos()))
            return currentNet;
        LevelHeatPipeNet worldNet = LevelHeatPipeNet.getOrCreate(serverLevel);
        currentNet = worldNet.getNetFromPos(getBlockPos());
        if(currentNet != null) {
            this.currentHeatNet = new WeakReference<>(currentNet);
        }
        return currentNet;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CosmicCapabilities.CAPABILITY_HEAT_CONTAINER) {
            return CosmicCapabilities.CAPABILITY_HEAT_CONTAINER.orEmpty(cap, LazyOptional.of(this::getHeatContainer));
        }
        return super.getCapability(cap, side);
    }

    @NotNull
    public HeatPipeNetHandler getHeatContainer() {
        if (heatContainer == null) {
            heatContainer = new HeatPipeNetHandler(this, this.getNodeData());
        }
        return heatContainer;
    }
    //todo; make map of temps and dims
    public double getEnvironmentalTemperature() {
        return 300;
    }

    public float getEnvironmentalConductivity() {
        return 0.5f;
    }

    public double loseEnergy(double thermalEnergy, double environmentalFactor, int ticksPassed) {
        for (int i = 0; i < ticksPassed; i++) {
            thermalEnergy -= Math.pow(Math.abs(thermalEnergy), 0.3) * environmentalFactor * Math.signum(thermalEnergy);
        }
        return thermalEnergy;
    }

    public void removeNeighborCache(Direction direction) {
        neighbors.remove(direction);
    }

    public void update() {
        if (getLevel().isClientSide) return;
        for (Direction direction : Direction.values()) {
            if (isConnected(direction)) {
                if (!neighbors.containsKey(direction)) {
                    BlockEntity neighbor = getLevel().getBlockEntity(getBlockPos().relative(direction));
                    if (neighbor == null) {
                        neighbors.put(direction, null);
                        continue;
                    }
                    LazyOptional<IHeatContainer> opt = neighbor.getCapability(CosmicCapabilities.CAPABILITY_HEAT_CONTAINER);
                    if (!opt.isPresent()) {
                        neighbors.put(direction, null);
                        continue;
                    }
                    neighbors.put(direction, opt.orElse(null));
                }
                IHeatContainer neighbor = neighbors.get(direction);
                if (neighbor == null) continue;
                double selfTemp = getHeatContainer().getCurrentTemperature();
                double neighborTemp = neighbor.getCurrentTemperature();
                if (neighborTemp >= selfTemp) continue;
                double transfer = (selfTemp - neighborTemp) * harmonicMean(neighbor.getConductance(), getHeatContainer().getConductance());
                transfer = neighbor.acceptHeatFromNetwork(direction, transfer);
                getHeatContainer().removeHeat(transfer);
            }
        }
        double current = getHeatContainer().getCurrentTemperature();
        double max = getHeatContainer().getOverloadLimit();
        if (current > max) {
            onOverload(current, max);
        }
    }

    protected void onOverload(double currentTemp, double tempLimit) {
        if (currentTemp * 1.2 > tempLimit) {
            getLevel().setBlock(getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private float harmonicMean(float a, float b) {
        return 1 / (1 / a + 1 / b);
    }

    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        tag.putDouble("Thermal", getHeatContainer().getCurrentEnergy());
        super.saveCustomPersistedData(tag, forDrop);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        getHeatContainer().setCurrentEnergy(tag.getDouble("Thermal"));
        super.loadCustomPersistedData(tag);
    }

    @Override
    public GTToolType getPipeTuneTool() {
        return GTToolType.MINING_HAMMER;
    }

    @NotNull
    @Override
    public List<Component> getDataInfo(PortableScannerBehavior.DisplayMode mode) {
        //runningTemp += 1000;
        return List.of(Component.literal("Current Temp: " + FormattingUtil.formatNumber2Places(getHeatContainer().getCurrentTemperature())));
    }
}
