package com.aruloci.demagnetizer.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class SparkParticle extends TextureSheetParticle {
    private static final int FADE_TICKS = 6;

    private final SpriteSet sprites;

    private SparkParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        lifetime = 20 + random.nextInt(12);
        quadSize = 0.08F + random.nextFloat() * 0.04F;
        xd = (random.nextDouble() - 0.5) * 0.003;
        yd = 0.002 + random.nextDouble() * 0.004;
        zd = (random.nextDouble() - 0.5) * 0.003;
        friction = 1F;
        gravity = 0F;
        hasPhysics = false;
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void tick() {
        super.tick();
        if (!removed) {
            setSpriteFromAge(sprites);
            alpha = Math.min(1F, (lifetime - age) / (float) FADE_TICKS);
        }
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new SparkParticle(level, x, y, z, sprites);
        }
    }
}
