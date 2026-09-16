package com.aruloci.demagnetizer.neoforge.client;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.client.DemagnetizerRenderer;
import com.aruloci.demagnetizer.client.SparkParticle;
import com.aruloci.demagnetizer.client.ZoneCubeRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = DemagnetizerMod.MOD_ID, dist = Dist.CLIENT)
public final class DemagnetizerNeoForgeClient {
    public DemagnetizerNeoForgeClient(IEventBus modBus) {
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
                event.registerBlockEntityRenderer(DemagnetizerMod.BLOCK_ENTITY.get(), DemagnetizerRenderer::new));
        modBus.addListener((RegisterParticleProvidersEvent event) ->
                event.registerSpriteSet(DemagnetizerMod.SPARK.get(), SparkParticle.Provider::new));
        NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && minecraft.level != null) {
                ZoneCubeRenderer.render(minecraft.level, event.getPoseStack(), minecraft.renderBuffers().bufferSource(), event.getCamera().getPosition());
            }
        });
    }
}
