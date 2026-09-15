package com.aruloci.demagnetizer.neoforge;

import net.neoforged.fml.common.Mod;

import com.aruloci.demagnetizer.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
