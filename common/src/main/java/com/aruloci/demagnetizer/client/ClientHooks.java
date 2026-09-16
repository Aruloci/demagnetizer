package com.aruloci.demagnetizer.client;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.block.DemagnetizerBlock;
import com.aruloci.demagnetizer.block.DemagnetizerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public final class ClientHooks {
    private static final int SPARK_INTERVAL_TICKS = 14;
    private static final double SPARK_MIN_RADIUS = 0.2;
    private static final double SPARK_MAX_RADIUS = 0.35;
    private static final double SPARK_MIN_HEIGHT = 0.5;
    private static final double SPARK_MAX_HEIGHT = 1.15;

    private ClientHooks() {
    }

    public static void openRangeScreen(DemagnetizerBlockEntity be) {
        Minecraft.getInstance().setScreen(new DemagnetizerRangeScreen(be));
    }

    public static void tickEffects(Level level, BlockPos pos, BlockState state) {
        RandomSource random = level.getRandom();
        if (!DemagnetizerBlock.isActive(state) || random.nextInt(SPARK_INTERVAL_TICKS) != 0) {
            return;
        }
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = SPARK_MIN_RADIUS + random.nextDouble() * (SPARK_MAX_RADIUS - SPARK_MIN_RADIUS);
        double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
        double y = pos.getY() + SPARK_MIN_HEIGHT + random.nextDouble() * (SPARK_MAX_HEIGHT - SPARK_MIN_HEIGHT);
        double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
        level.addParticle(DemagnetizerMod.SPARK.get(), x, y, z, 0.0, 0.0, 0.0);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(new ServerboundCustomPayloadPacket(payload));
        }
    }
}
