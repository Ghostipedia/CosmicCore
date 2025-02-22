package com.ghostipedia.cosmiccore.common.machine.multiblock.part;

import com.ghostipedia.cosmiccore.common.wireless.WirelessDataStore;
import com.ghostipedia.cosmiccore.utils.OwnershipUtils;

import com.gregtechceu.gtceu.api.capability.IDataAccessHatch;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import com.gregtechceu.gtceu.common.machine.owner.IMachineOwner;
import com.gregtechceu.gtceu.common.machine.owner.PlayerOwner;
import com.gregtechceu.gtceu.common.recipe.condition.ResearchCondition;

import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.MethodsReturnNonnullByDefault;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WirelessDataHatchPartMachine extends MultiblockPartMachine
                                          implements IDataAccessHatch {

    public WirelessDataHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public GTRecipe modifyRecipe(GTRecipe recipe) {
        return IDataAccessHatch.super.modifyRecipe(recipe);
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isRecipeAvailable(@NotNull GTRecipe recipe, @NotNull Collection<IDataAccessHatch> seen) {
        seen.add(this);
        var dataStore = WirelessDataStore.getWirelessDataStore(OwnershipUtils.getOwnerUUID(getHolder().getOwner()));
        return recipe.conditions.stream().noneMatch(ResearchCondition.class::isInstance) ||
                dataStore.isRecipeAvailable(recipe, seen);
    }
    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 128, 63);
        IMachineOwner owner = MetaMachine.getMachine(this.getLevel(),this.getPos()).holder.getOwner();
        group.addWidget(new ImageWidget(4, 4, 120, 55, GuiTextures.DISPLAY));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        if (owner instanceof PlayerOwner playerOwner) {
            var name = UsernameCache.getLastKnownUsername(playerOwner.getPlayerUUID());
            group.addWidget(new LabelWidget(8, 28, () -> I18n.get("behavior.wireless_data.owner.player", name)));
        } else if (owner instanceof FTBOwner ftOwner) {
            var team = ftOwner.getTeam().getName();
            group.addWidget(new LabelWidget(8, 38, () -> I18n.get("behavior.wireless_data.owner.team")));
            group.addWidget(new LabelWidget(8, 48, () -> I18n.get(team.getString())));
        }
        return group;
    }
}
