package com.aruloci.demagnetizer;

import com.aruloci.demagnetizer.block.DemagnetizerBlock;
import com.aruloci.demagnetizer.block.DemagnetizerBlockEntity;
import com.aruloci.demagnetizer.network.SetRangePayload;
import com.aruloci.demagnetizer.platform.Platform;
import com.aruloci.demagnetizer.zone.DemagnetizerZones;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;

import java.util.function.Supplier;

public final class DemagnetizerMod {
    public static final String MOD_ID = "demagnetizer";

    public static final Supplier<DemagnetizerBlock> BLOCK = Platform.registerBlock(MOD_ID, () -> new DemagnetizerBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
    public static final Supplier<BlockItem> ITEM = Platform.registerItem(MOD_ID, () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<DemagnetizerBlockEntity>> BLOCK_ENTITY =
            Platform.registerBlockEntityType(MOD_ID, DemagnetizerBlockEntity::new, BLOCK);
    public static final Supplier<SimpleParticleType> SPARK = Platform.registerParticleType("spark");

    private DemagnetizerMod() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        SetRangePayload.register();
        MagnetUpgradeWrapper.addMagnetPreventionChecker(entity ->
                !entity.level().isClientSide() && DemagnetizerZones.isDemagnetized(entity.level(), entity.position()));
    }
}
