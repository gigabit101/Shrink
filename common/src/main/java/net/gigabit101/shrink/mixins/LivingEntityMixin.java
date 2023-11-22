package net.gigabit101.shrink.mixins;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.polylib.AttributeEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void addAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info)
    {
        info.getReturnValue().add(ShrinkAPI.SCALE_ATTRIBUTE);
    }
}
