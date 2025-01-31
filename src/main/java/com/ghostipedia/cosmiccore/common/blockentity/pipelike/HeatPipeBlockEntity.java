package com.ghostipedia.cosmiccore.common.blockentity.pipelike;

import com.ghostipedia.cosmiccore.api.capability.CosmicCapabilities;
import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeNet;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeNetHandler;
import com.ghostipedia.cosmiccore.common.pipelike.heat.HeatPipeType;
import com.ghostipedia.cosmiccore.common.pipelike.heat.LevelHeatPipeNet;
import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDataInfoProvider;
import com.gregtechceu.gtceu.common.item.PortableScannerBehavior;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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
import java.util.Map;

public class HeatPipeBlockEntity extends PipeBlockEntity<HeatPipeType, HeatPipeProperties> implements IDataInfoProvider {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(HeatPipeBlockEntity.class, PipeBlockEntity.MANAGED_FIELD_HOLDER);

    protected WeakReference<HeatPipeNet> currentHeatNet = new WeakReference<>(null);
    private final EnumMap<Direction, HeatPipeNetHandler> handlers = new EnumMap<>(Direction.class);
    public IHeatContainer heatContainer;
    private HeatPipeNetHandler defaultHandler;

    private long currentTemp;
    private long runningTemp;
    public byte lastReceivedFrom = 0, oldLastReceivedFrom = 0;

    private TickableSubscription updateSubs;

    public HeatPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        currentTemp = 230;
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
            return CosmicCapabilities.CAPABILITY_HEAT_CONTAINER.orEmpty(cap, LazyOptional.of(() -> getHeatContainer(side)));
        }
        return super.getCapability(cap, side);
    }

    public void updateNetwork() {
        if(defaultHandler != null) {
            HeatPipeNet current = getHeatNet();
            if(defaultHandler.getNet() != current) {
                defaultHandler.updateNet(current);
                for(HeatPipeNetHandler handler : handlers.values()) {
                    handler.updateNet(current);
                }
            }
        }
    }

    public IHeatContainer getHeatContainer(Direction side) {
        if(heatContainer == null) {
            if(side != null && !isConnected(side)) return null;
            if(isRemote()) return IHeatContainer.DEFAULT;
            if(handlers.isEmpty())
                initHandlers();

            return handlers.getOrDefault(side, defaultHandler);
        }
        return heatContainer;
    }

    private void initHandlers() {
        HeatPipeNet net = getHeatNet();
        if(net == null)
            return;
        for(Direction facing : Direction.values()) {
            handlers.put(facing, new HeatPipeNetHandler(net, this, facing));
        }
        defaultHandler = new HeatPipeNetHandler(net, this, null);
    }


    public void update() {
        Map<Direction, IHeatContainer> neighborHeats = new EnumMap<>(Direction.class);

        if (!level.isClientSide) {
            for(byte i = 0; i < 6; i++) {
                byte side = (byte) (i % 6);
                Direction facing = Direction.values()[i];

                // horse
                BlockEntity neighbor = getNeighbor(facing);
                if (neighbor == null) { // maybe check for air dissipation?

                    continue;
                }
                IHeatContainer neighborHeatContainer = neighbor.getCapability(CosmicCapabilities.CAPABILITY_HEAT_CONTAINER, facing.getOpposite()).resolve().orElse(null);
                if (neighborHeatContainer != null)
                    neighborHeats.put(facing, neighborHeatContainer);
                else
                    neighborHeats.put(facing, getHeatContainer(null));
            }

            float surrounding = (neighborHeats.get(Direction.NORTH).getHeatInfo().stored() +
                    neighborHeats.get(Direction.SOUTH).getHeatInfo().stored() +
                    neighborHeats.get(Direction.EAST).getHeatInfo().stored() +
                    neighborHeats.get(Direction.WEST).getHeatInfo().stored() +
                    neighborHeats.get(Direction.UP).getHeatInfo().stored() +
                    neighborHeats.get(Direction.DOWN).getHeatInfo().stored() -
                    (6 * getHeatContainer(null).getHeatInfo().stored())) / 3.f;

            if(surrounding >= 5.0f)
                runningTemp = this.currentTemp + (int)(getNodeData().getMaxTransferRate() * surrounding * .2f);

            /*for(byte i = 0; i < 6; i++) {
                byte side = (byte) (i % 6);
                Direction facing = Direction.values()[i];

                if(!isConnected(facing) || (lastReceivedFrom & (1 << side)) != 0) {
                    continue;
                }

                // horse
                BlockEntity neighbor = getNeighbor(facing);
                if(neighbor == null) { // maybe check for air dissipation?

                    continue;
                }
                IHeatContainer neighborHeatContainer = neighbor.getCapability(CosmicCapabilities.CAPABILITY_HEAT_CONTAINER, facing.getOpposite()).resolve().orElse(null);
                if(neighborHeatContainer == null) continue;

                long neighborTemp = neighborHeatContainer.getHeatInfo().stored();
                currentTemp = getHeatContainer(null).getHeatInfo().stored();
                long deltaHeat = currentTemp - neighborTemp;
                float transferRate = 0.1f;
                long tempA = currentTemp - (long)(deltaHeat * transferRate);
                long tempB = neighborTemp + (long)(deltaHeat * transferRate);

                getHeatContainer(null).removeHeat((long)(deltaHeat * transferRate)); // remove heat from self
                neighborHeatContainer.addHeat((long)(deltaHeat * transferRate)); // add heat from neighbor
            }*/
        }
    }

    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        super.loadCustomPersistedData(tag);
    }

    @Override
    public GTToolType getPipeTuneTool() {
        return GTToolType.MINING_HAMMER;
    }

    @NotNull
    @Override
    public List<Component> getDataInfo(PortableScannerBehavior.DisplayMode mode) {

        return null;
    }
}
