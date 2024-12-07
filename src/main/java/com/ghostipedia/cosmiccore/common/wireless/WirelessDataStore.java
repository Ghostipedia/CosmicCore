package com.ghostipedia.cosmiccore.common.wireless;

import com.ghostipedia.cosmiccore.api.wireless.IWirelessStore;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ghostipedia.cosmiccore.common.wireless.GlobalWirelessVariableStorage.GlobalWirelessDataSticks;

public class WirelessDataStore implements IWirelessStore<List<ItemStack>> {

    private final ArrayList<ItemStack> dataSticks = new ArrayList<ItemStack>();

    @Override
    public void clearData() {
        dataSticks.clear();
    }

    @Override
    public void uploadData(List<ItemStack> data) {
        dataSticks.addAll(data);
    }

    @Override
    public List<ItemStack> downloadData() {
        return dataSticks;
    }

    public static WirelessDataStore getWirelessDataStore(UUID uuid) {
        //TODO: Implement Team logic here
        if (GlobalWirelessDataSticks.get(uuid) == null)
            GlobalWirelessDataSticks.put(uuid, new WirelessDataStore());
        return GlobalWirelessDataSticks.get(uuid);
    }
}
