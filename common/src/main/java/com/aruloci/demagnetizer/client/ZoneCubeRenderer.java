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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class ZoneCubeRenderer {
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucentEmissive(DemagnetizerMod.id("textures/block/range.png"));
    private static final float[] ACTIVE_TINT = {1F, 0.3F, 0.3F};
    private static final float[] POWERED_TINT = {0.8F, 0.8F, 0.8F};
    private static final Map<ResourceKey<Level>, Set<BlockPos>> SHOWN = new HashMap<>();

    private ZoneCubeRenderer() {
    }

    public static boolean isShown(Level level, BlockPos pos) {
        return shown(level).contains(pos);
    }

    public static void toggle(Level level, BlockPos pos) {
        Set<BlockPos> shown = shown(level);
        if (!shown.remove(pos)) {
            shown.add(pos.immutable());
        }
    }

    private static Set<BlockPos> shown(Level level) {
        return SHOWN.computeIfAbsent(level.dimension(), key -> new HashSet<>());
    }

    public static void render(Level level, PoseStack poseStack, MultiBufferSource.BufferSource buffers, Vec3 camera) {
        Iterator<BlockPos> shown = shown(level).iterator();
        boolean drewSomething = false;
        while (shown.hasNext()) {
            BlockPos pos = shown.next();
            if (!(level.getBlockEntity(pos) instanceof DemagnetizerBlockEntity be)) {
                if (level.hasChunkAt(pos)) {
                    shown.remove();
                }
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            renderZone(be, poseStack.last(), buffers.getBuffer(RENDER_TYPE));
            poseStack.popPose();
            drewSomething = true;
        }
        if (drewSomething) {
            buffers.endBatch();
        }
    }

    private static void renderZone(DemagnetizerBlockEntity be, PoseStack.Pose pose, VertexConsumer buffer) {
        float[] tint = DemagnetizerBlock.isActive(be.getBlockState()) ? ACTIVE_TINT : POWERED_TINT;
        float min = -be.getRange();
        float max = be.getRange() + 1;
        float size = max - min;
        for (Face face : Face.cube(min, max)) {
            vertex(buffer, pose, face, 0, 0, tint);
            vertex(buffer, pose, face, size, 0, tint);
            vertex(buffer, pose, face, size, size, tint);
            vertex(buffer, pose, face, 0, size, tint);
        }
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, Face face, float u, float v, float[] tint) {
        buffer.addVertex(pose.pose(), face.ox + u * face.ux + v * face.vx, face.oy + u * face.uy + v * face.vy, face.oz + u * face.uz + v * face.vz)
                .setColor(tint[0], tint[1], tint[2], 1F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0, 1, 0);
    }

    private record Face(float ox, float oy, float oz, float ux, float uy, float uz, float vx, float vy, float vz) {
        static Face[] cube(float min, float max) {
            return new Face[] {
                    new Face(min, min, min, 1, 0, 0, 0, 0, 1),
                    new Face(min, max, min, 1, 0, 0, 0, 0, 1),
                    new Face(min, min, min, 1, 0, 0, 0, 1, 0),
                    new Face(min, min, max, 1, 0, 0, 0, 1, 0),
                    new Face(min, min, min, 0, 0, 1, 0, 1, 0),
                    new Face(max, min, min, 0, 0, 1, 0, 1, 0),
            };
        }
    }
}
