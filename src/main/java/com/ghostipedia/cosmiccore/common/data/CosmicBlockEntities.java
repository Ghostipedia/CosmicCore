package com.ghostipedia.cosmiccore.common.data;

import com.ghostipedia.cosmiccore.api.registries.CosmicRegistration;
import com.ghostipedia.cosmiccore.client.renderer.block.NebulaeCoilRenderer;
import com.ghostipedia.cosmiccore.common.blockentity.CosmicCoilBlockEntity;
import com.ghostipedia.cosmiccore.common.blockentity.pipelike.HeatPipeBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

@SuppressWarnings("Convert2MethodRef")
public class CosmicBlockEntities {

    public static final BlockEntityEntry<CosmicCoilBlockEntity> CAUSAL_FABRIC_COIL_BLOCK_ENTITY = REGISTRATE
            .blockEntity("causal_fabric_coil", CosmicCoilBlockEntity::new)
            .renderer(() -> NebulaeCoilRenderer.createBlockEntityRenderer())
            .validBlocks(CosmicBlocks.COIL_CAUSAL_FABRIC)
            .register();

    public static final BlockEntityEntry<HeatPipeBlockEntity> HEAT_PIPE = CosmicRegistration.REGISTRATE
            .blockEntity("heat_pipe", HeatPipeBlockEntity::new)
            .validBlocks(CosmicMaterialBlocks.HEAT_PIPE_BLOCKS.values().toArray(BlockEntry[]::new))
            .register();

    public static void init() {

    }
}
