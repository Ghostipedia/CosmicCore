package com.ghostipedia.cosmiccore.common.machine.multiblock.part;

import com.ghostipedia.cosmiccore.api.capability.recipe.IHeatContainer;
import com.ghostipedia.cosmiccore.api.machine.trait.NotifiableThermiaContainer;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class ThermiaHatchPartMachine extends TieredIOPartMachine implements IHeatContainer {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ThermiaHatchPartMachine.class, TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted @DescSynced
    private final NotifiableThermiaContainer thermiaContainer;
    public ThermiaHatchPartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.thermiaContainer = createThermiaContainer();
    }
    protected NotifiableThermiaContainer createThermiaContainer(){
        NotifiableThermiaContainer container;
        if (io == IO.OUT){
            container = new NotifiableThermiaContainer(this, IO.OUT,getThermiaLimits(tier),0);
            container.setSideOutputCondition(s -> s == getFrontFacing());
            container.setCapabilityValidator(s -> s == null || s == getFrontFacing());
        } else {
            container = new NotifiableThermiaContainer(this, IO.IN,getThermiaLimits(tier),0);
            container.setSideInputCondition(s -> s == getFrontFacing());
            container.setCapabilityValidator(s -> s == null || s == getFrontFacing());
        }
        return container;
    }


    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0,0,128,63);

        group.addWidget(new ImageWidget(4, 4, 120, 55, GuiTextures.DISPLAY));
        group.addWidget(new LabelWidget(8, 8, Component.translatable("gui.cosmiccore.thermia_hatch.label." + (this.io == IO.IN ? "import" : "export"))));
        group.addWidget(new LabelWidget(8, 18, () -> I18n.get("gui.cosmiccore.thermia_hatch.hatch_limit")));
        group.addWidget(new LabelWidget(8, 28, () -> I18n.get(FormattingUtil.formatNumbers(thermiaContainer.getOverloadLimit()), "K")).setClientSideWidget());
        group.addWidget(new LabelWidget(8, 38, () -> I18n.get("gui.cosmiccore.thermia_hatch.stored_temp")).setClientSideWidget());
        group.addWidget(new LabelWidget(8, 48, () -> I18n.get(FormattingUtil.formatNumbers(thermiaContainer.getCurrentTemperature()), "K")).setClientSideWidget());
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }
    public static int getThermiaLimits(int tier) {
        return switch (tier) {
            case GTValues.ZPM -> 95000;
            case GTValues.UV  -> 128000;
            case GTValues.UHV -> 108000;
            case GTValues.UEV -> 158000;
            case GTValues.UIV -> 198400;
            case GTValues.UXV -> 360000;
            case GTValues.OpV -> 2500000;
            case GTValues.MAX -> Integer.MAX_VALUE;
            default -> 0;
        };
    }

    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        tag.putDouble("Thermal", thermiaContainer.getCurrentEnergy());
        super.saveCustomPersistedData(tag, forDrop);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        thermiaContainer.setCurrentEnergy(tag.getDouble("Thermal"));
        super.loadCustomPersistedData(tag);
    }

    @Override
    public double acceptHeatFromNetwork(Direction side, double thermalEnergy) {
        return thermiaContainer.acceptHeatFromNetwork(side, thermalEnergy);
    }

    @Override
    public boolean inputsHeat(Direction side) {
        return thermiaContainer.inputsHeat(side);
    }

    @Override
    public boolean outputsHeat(Direction side) {
        return thermiaContainer.outputsHeat(side);
    }

    @Override
    public double changeHeat(double thermalEnergy) {
        return thermiaContainer.changeHeat(thermalEnergy);
    }

    @Override
    public float getOverloadLimit() {
        return thermiaContainer.getOverloadLimit();
    }

    @Override
    public double getCurrentEnergy() {
        return thermiaContainer.getCurrentEnergy();
    }

    @Override
    public void setCurrentEnergy(double energy) {
        thermiaContainer.setCurrentEnergy(energy);
    }

    @Override
    public float getHeatCapacity() {
        return thermiaContainer.getHeatCapacity();
    }

    @Override
    public double getCurrentTemperature() {
        return thermiaContainer.getCurrentTemperature();
    }

    @Override
    public double getBaseTemperature() {
        return thermiaContainer.getBaseTemperature();
    }

    @Override
    public float getConductance() {
        return thermiaContainer.getConductance();
    }

    @Override
    public boolean supportsImpossibleHeatValues() {
        return thermiaContainer.supportsImpossibleHeatValues();
    }
}
