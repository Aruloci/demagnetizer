package com.aruloci.demagnetizer.platform.neoforge;

import com.aruloci.demagnetizer.DemagnetizerMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PlatformImpl {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, DemagnetizerMod.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DemagnetizerMod.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DemagnetizerMod.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, DemagnetizerMod.MOD_ID);
    private static final List<Consumer<PayloadRegistrar>> PAYLOADS = new ArrayList<>();

    private PlatformImpl() {
    }

    public static void attach(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        PARTICLE_TYPES.register(modBus);
        modBus.addListener((RegisterPayloadHandlersEvent event) -> {
            PayloadRegistrar registrar = event.registrar("1");
            PAYLOADS.forEach(registration -> registration.accept(registrar));
        });
    }

    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        return BLOCKS.register(id, block);
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return ITEMS.register(id, item);
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String id, BiFunction<BlockPos, BlockState, T> factory, Supplier<? extends Block> block) {
        return BLOCK_ENTITY_TYPES.register(id, () -> BlockEntityType.Builder.of(factory::apply, block.get()).build(null));
    }

    public static Supplier<SimpleParticleType> registerParticleType(String id) {
        return PARTICLE_TYPES.register(id, () -> new SimpleParticleType(false));
    }

    public static <T extends CustomPacketPayload> void registerServerboundPayload(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<ServerPlayer, T> handler) {
        PAYLOADS.add(registrar -> registrar.playToServer(type, codec,
                (payload, context) -> handler.accept((ServerPlayer) context.player(), payload)));
    }
}
