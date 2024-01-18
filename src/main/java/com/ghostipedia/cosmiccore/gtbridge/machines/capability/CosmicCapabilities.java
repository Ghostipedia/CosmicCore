package com.ghostipedia.cosmiccore.gtbridge.machines.capability;

import com.ghostipedia.cosmiccore.gtbridge.machines.traits.AirRecipeCapability;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CosmicCapabilities {

    public static final AirRecipeCapability PRESSURE = AirRecipeCapability.CAP;

    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.register(PRESSURE.name, PRESSURE);
    }
}