package com.aruloci.demagnetizer.fabric.client;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.client.DemagnetizerRenderer;
import com.aruloci.demagnetizer.client.SparkParticle;
import com.aruloci.demagnetizer.client.ZoneCubeRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class DemagnetizerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(DemagnetizerMod.BLOCK_ENTITY.get(), DemagnetizerRenderer::new);
        ParticleFactoryRegistry.getInstance().register(DemagnetizerMod.SPARK.get(), SparkParticle.Provider::new);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (context.matrixStack() != null) {
                ZoneCubeRenderer.render(context.world(), context.matrixStack(), Minecraft.getInstance().renderBuffers().bufferSource(), context.camera().getPosition());
            }
        });
    }
}
