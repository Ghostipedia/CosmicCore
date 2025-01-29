package com.ghostipedia.cosmiccore.utils;

import com.gregtechceu.gtceu.common.machine.owner.ArgonautsOwner;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import com.gregtechceu.gtceu.common.machine.owner.IMachineOwner;
import com.gregtechceu.gtceu.common.machine.owner.PlayerOwner;

import java.util.UUID;

public class OwnershipUtils {

    public static UUID getOwnerUUID(IMachineOwner owner) {
        UUID uuid = null;
        if (owner instanceof PlayerOwner playerOwner) {
            uuid = playerOwner.getPlayerUUID();
        } else if (owner instanceof FTBOwner ftOwner) {
            uuid = ftOwner.getPlayerUUID();
        } else if (owner instanceof ArgonautsOwner argonautsOwner) {
            uuid = argonautsOwner.getPlayerUUID();
        }
        return uuid;
    }

}
