package com.aruloci.demagnetizer.neoforge;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.platform.neoforge.PlatformImpl;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(DemagnetizerMod.MOD_ID)
public final class DemagnetizerNeoForge {
    public DemagnetizerNeoForge(IEventBus modBus) {
        DemagnetizerMod.init();
        PlatformImpl.attach(modBus);
        modBus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                event.accept(DemagnetizerMod.ITEM.get());
            }
        });
    }
}
