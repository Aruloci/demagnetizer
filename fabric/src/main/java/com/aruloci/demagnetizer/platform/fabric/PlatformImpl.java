package com.aruloci.demagnetizer.platform.fabric;

import com.aruloci.demagnetizer.DemagnetizerMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
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

public final class PlatformImpl {
    private PlatformImpl() {
    }

    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        T registered = Registry.register(BuiltInRegistries.BLOCK, DemagnetizerMod.id(id), block.get());
        return () -> registered;
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        T registered = Registry.register(BuiltInRegistries.ITEM, DemagnetizerMod.id(id), item.get());
        return () -> registered;
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String id, BiFunction<BlockPos, BlockState, T> factory, Supplier<? extends Block> block) {
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory::apply, block.get()).build();
        BlockEntityType<T> registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DemagnetizerMod.id(id), type);
        return () -> registered;
    }

    public static Supplier<SimpleParticleType> registerParticleType(String id) {
        SimpleParticleType registered = Registry.register(BuiltInRegistries.PARTICLE_TYPE, DemagnetizerMod.id(id), FabricParticleTypes.simple());
        return () -> registered;
    }

    public static <T extends CustomPacketPayload> void registerServerboundPayload(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<ServerPlayer, T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(context.player(), payload));
    }
}
