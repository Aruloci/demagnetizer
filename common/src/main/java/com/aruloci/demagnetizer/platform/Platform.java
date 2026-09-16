package com.aruloci.demagnetizer.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Platform {
    private Platform() {
    }

    @ExpectPlatform
    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String id, BiFunction<BlockPos, BlockState, T> factory, Supplier<? extends Block> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Supplier<SimpleParticleType> registerParticleType(String id) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends CustomPacketPayload> void registerServerboundPayload(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<ServerPlayer, T> handler) {
        throw new AssertionError();
    }
}
