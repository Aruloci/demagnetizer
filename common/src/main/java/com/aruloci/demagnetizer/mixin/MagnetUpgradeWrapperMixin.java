package com.aruloci.demagnetizer.mixin;

import com.aruloci.demagnetizer.zone.DemagnetizerZones;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MagnetUpgradeWrapper.class, remap = false)
public abstract class MagnetUpgradeWrapperMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void demagnetizer$suppressInZone(Entity entity, Level level, BlockPos pos, CallbackInfo ci) {
        if (!level.isClientSide() && DemagnetizerZones.isDemagnetized(level, pos)) {
            ci.cancel();
        }
    }
}
