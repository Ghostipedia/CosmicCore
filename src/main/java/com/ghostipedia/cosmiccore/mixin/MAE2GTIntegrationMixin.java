package com.ghostipedia.cosmiccore.mixin;


import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stone.mae2.integration.GregTechIntegration;
import stone.mae2.core.MAE2Items;
import stone.mae2.integration.GregTechIntegration;
import stone.mae2.parts.p2p.EUP2PTunnelPart;
import stone.mae2.parts.p2p.multi.EUMultiP2PPart;

@Debug(export = true)
@Mixin(value = GregTechIntegration.class, remap = false)
public abstract class MAE2GTIntegrationMixin {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;addListener(Ljava/util/function/Consumer;)V"), cancellable = true, remap = false)
    private static void init(IEventBus bus, CallbackInfo callbackInfo) {
        callbackInfo.cancel();
    }

}
