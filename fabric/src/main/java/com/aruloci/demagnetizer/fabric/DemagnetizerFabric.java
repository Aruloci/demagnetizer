package com.aruloci.demagnetizer.fabric;

import com.aruloci.demagnetizer.DemagnetizerMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public final class DemagnetizerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DemagnetizerMod.init();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> entries.accept(DemagnetizerMod.ITEM.get()));
    }
}
