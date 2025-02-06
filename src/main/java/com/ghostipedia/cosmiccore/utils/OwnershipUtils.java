package com.ghostipedia.cosmiccore.utils;

import com.gregtechceu.gtceu.common.machine.owner.ArgonautsOwner;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import com.gregtechceu.gtceu.common.machine.owner.IMachineOwner;
import com.gregtechceu.gtceu.common.machine.owner.PlayerOwner;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.common.UsernameCache;
import wayoftime.bloodmagic.core.util.PlayerUtil;
import wayoftime.bloodmagic.util.helper.PlayerHelper;

import java.util.List;
import java.util.UUID;

public class OwnershipUtils {

    public static UUID getOwnerUUID(IMachineOwner owner) {
        if (owner instanceof PlayerOwner playerOwner) {
            return playerOwner.getPlayerUUID();
        } else if (owner instanceof FTBOwner ftOwner) {
            return ftOwner.getTeam().getId();
        } else return null;
    }

    public static String getName(IMachineOwner owner) {
        if (owner instanceof PlayerOwner playerOwner) {
            return UsernameCache.getLastKnownUsername(playerOwner.getPlayerUUID());
        } else if (owner instanceof FTBOwner ftOwner) {
            return ftOwner.getTeam().getName().getString();
        } else return "NaN";
    }

    public static void addOwnerLine(List<Component> textList, IMachineOwner owner) {
        if (owner instanceof PlayerOwner playerOwner) {
            var name = UsernameCache.getLastKnownUsername(playerOwner.getPlayerUUID());
            textList.add(Component.translatable("behavior.wireless_data.owner.player", name));
        } else if (owner instanceof FTBOwner ftOwner) {
            var team = ftOwner.getTeam().getName();
            textList.add(Component.translatable("behavior.wireless_data.owner.team").append(team));
        }
    }
}
