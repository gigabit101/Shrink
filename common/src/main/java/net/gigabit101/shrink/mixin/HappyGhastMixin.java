package net.gigabit101.shrink.mixin;

import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhast.class)
public abstract class HappyGhastMixin {
    @Inject(method = "sanitizeScale(F)F", at = @At("HEAD"), cancellable = true)
    private void shrink$allowEnlargement(float scale, CallbackInfoReturnable<Float> cir) {
        // Keep the normal scale attribute limits, without the happy ghast's extra 1x cap.
        cir.setReturnValue(scale);
    }
}
