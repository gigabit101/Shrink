package net.gigabit101.shrink.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin
{
    @Unique
    private static float shrink$value = 0;
    @Inject(method = "renderShadow", at = @At("HEAD"))
    private static void renderShadow(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, float f, float g, LevelReader levelReader, float h, CallbackInfo ci)
    {
        if(entity instanceof LivingEntity livingEntity)
        {
            if(ShrinkAPI.isEntityShrunk(livingEntity))
            {
                shrink$value = (float) (h * livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue());
            }
            else
            {
                shrink$value = h;
            }
        }
    }

    @ModifyVariable(method = "renderShadow", at = @At("HEAD"), ordinal = 2)
    private static float injected(float h)
    {
        return shrink$value;
    }
}
