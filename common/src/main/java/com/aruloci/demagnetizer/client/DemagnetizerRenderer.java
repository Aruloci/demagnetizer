package com.aruloci.demagnetizer.client;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.block.DemagnetizerBlock;
import com.aruloci.demagnetizer.block.DemagnetizerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.Level;

import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class DemagnetizerRenderer implements BlockEntityRenderer<DemagnetizerBlockEntity> {
    private static final RenderType[] ARC_FRAMES = IntStream.range(0, 4)
            .mapToObj(i -> RenderType.entityTranslucentEmissive(DemagnetizerMod.id("textures/effect/arc_" + i + ".png")))
            .toArray(RenderType[]::new);
    private static final int TICKS_PER_FRAME = 2;
    private static final float X0 = 6.475F / 16;
    private static final float X1 = 9.525F / 16;
    private static final float Y0 = 16 / 16F;
    private static final float Y1 = 18 / 16F;
    private static final float Z = 8 / 16F;

    public DemagnetizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(DemagnetizerBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        Level level = be.getLevel();
        if (level == null || !DemagnetizerBlock.isActive(be.getBlockState())) {
            return;
        }
        VertexConsumer buffer = buffers.getBuffer(ARC_FRAMES[(int) (level.getGameTime() / TICKS_PER_FRAME % ARC_FRAMES.length)]);
        PoseStack.Pose pose = poseStack.last();
        vertex(buffer, pose, X0, Y0, 0, 1);
        vertex(buffer, pose, X1, Y0, 1, 1);
        vertex(buffer, pose, X1, Y1, 1, 0);
        vertex(buffer, pose, X0, Y1, 0, 0);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v) {
        buffer.addVertex(pose.pose(), x, y, Z)
                .setColor(1F, 1F, 1F, 1F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0, 0, 1);
    }
}
