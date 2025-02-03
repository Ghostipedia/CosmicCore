package com.ghostipedia.cosmiccore.common.block.debug;

import com.ghostipedia.cosmiccore.api.capability.CosmicCapabilities;
import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.ghostipedia.cosmiccore.api.machine.trait.NotifiableThermiaContainer;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.ghostipedia.cosmiccore.common.machine.multiblock.part.ThermiaHatchPartMachine.getThermiaLimits;

public class CreativeThermiaContainerMachine extends MetaMachine implements IHeatContainer, IUIMachine {

    //FieldHolder
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CreativeThermiaContainerMachine.class, MetaMachine.MANAGED_FIELD_HOLDER);
    @Persisted
    private long heat = 0;
    @Persisted
    private boolean active = false;
    @Persisted
    private boolean source = true;
    private long lastAverageHeatIOPerTick = 0;

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
    @Persisted
    @DescSynced
    private final NotifiableThermiaContainer thermiaContainer;
    public CreativeThermiaContainerMachine(IMachineBlockEntity holder) {
        super(holder);
        long currentTemp = 0;
        this.thermiaContainer = new NotifiableThermiaContainer(this, IO.BOTH, getThermiaLimits(GTValues.MAX), currentTemp);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        subscribeServerTick(this::update);
    }



    // This outputs heat to any adjacent container
    protected void update() {
        if(!source || !active) return;

        for(var facing : Direction.values()) {
            var opposite = facing.getOpposite();
            var neighbor = getLevel().getBlockEntity(this.getPos().relative(facing));
            if(neighbor == null) continue;

            IHeatContainer container = neighbor.getCapability(CosmicCapabilities.CAPABILITY_HEAT_CONTAINER, opposite).resolve().orElse(null);
            if(container != null && container.inputsHeat(opposite)) {
                container.acceptHeatFromNetwork(opposite, heat);
            }
        }

        lastAverageHeatIOPerTick = heat;
    }

    @Override
    public long acceptHeatFromNetwork(Direction side, long heatDiff) {
        return 0;
    }

    @Override
    public boolean inputsHeat(Direction side) {
        return !source;
    }

    @Override
    public boolean outputsHeat(Direction side) {
        return source;
    }

    @Override
    public long changeHeat(long heatDifference) {
        if(source || !active) {
            return 0;
        }
        return 0;
    }

    @Override
    public long getOverloadLimit() {
        return 0;
    }

    @Override
    public long getCurrentTemperature() {
        return heat;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(176, 166, this, entityPlayer)
                .background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(7, 32, "cosmiccore.recipe.temperature"))
                .widget(new TextFieldWidget(9, 47, 152, 16, () -> String.valueOf(heat),
                        value -> {
                            heat = Long.parseLong(value);
                        }).setNumbersOnly(0L, Long.MAX_VALUE))
                .widget(new LabelWidget(7, 110,
                        () -> "Average Energy I/O per tick: " + this.lastAverageHeatIOPerTick))
                .widget(new SwitchWidget(7, 139, 77, 20, (clickData, value) -> active = value)
                        .setTexture(
                                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON,
                                        new TextTexture("gtceu.creative.activity.off")),
                                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON,
                                        new TextTexture("gtceu.creative.activity.on")))
                        .setPressed(active))
                .widget(new SwitchWidget(85, 139, 77, 20, (clickData, value) -> {
                    source = value;
                    if (source) {
                        heat = 0;
                    } else {
                        heat = Long.MIN_VALUE;
                    }
                }).setTexture(
                                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON,
                                        new TextTexture("gtceu.creative.energy.sink")),
                                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON,
                                        new TextTexture("gtceu.creative.energy.source")))
                        .setPressed(source));
    }

    @Override
    public float getThermalConductance() {
        return 1.0f;
    }
}


