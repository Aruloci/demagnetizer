package com.aruloci.demagnetizer.network;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.block.DemagnetizerBlockEntity;
import com.aruloci.demagnetizer.platform.Platform;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record SetRangePayload(BlockPos pos, int range) implements CustomPacketPayload {
    public static final Type<SetRangePayload> TYPE = new Type<>(DemagnetizerMod.id("set_range"));
    public static final StreamCodec<ByteBuf, SetRangePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetRangePayload::pos,
            ByteBufCodecs.VAR_INT, SetRangePayload::range,
            SetRangePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register() {
        Platform.registerServerboundPayload(TYPE, STREAM_CODEC, SetRangePayload::handle);
    }

    private static void handle(ServerPlayer player, SetRangePayload payload) {
        Level level = player.level();
        if (level.getBlockEntity(payload.pos()) instanceof DemagnetizerBlockEntity be
                && player.canInteractWithBlock(payload.pos(), 1.0)
                && level.mayInteract(player, payload.pos())) {
            be.setRange(payload.range());
        }
    }
}
