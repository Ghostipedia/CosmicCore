package com.ghostipedia.cosmiccore.api.capability;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CosmicCapabilities {

    public static Capability<ISoulContainer> CAPABILITY_SOUL_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<IHeatContainer> CAPABILITY_HEAT_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ISoulContainer.class);
        event.register(IHeatContainer.class);
    }
}
