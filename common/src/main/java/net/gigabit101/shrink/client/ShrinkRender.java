package net.gigabit101.shrink.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.world.entity.LivingEntity;

public class ShrinkRender
{
    public static void onEntityRenderPre(LivingEntity livingEntity, PoseStack poseStack)
    {
        boolean shrunk = ShrinkAPI.isEntityShrunk(livingEntity);
        if(shrunk)
        {
            poseStack.pushPose();
            float scale = (float) livingEntity.getAttributeValue(ShrinkAPI.SCALE_ATTRIBUTE);
            poseStack.scale(scale, scale, scale);
        }
    }

    public static void onEntityRenderPost(LivingEntity livingEntity, PoseStack poseStack)
    {
        boolean shrunk = ShrinkAPI.isEntityShrunk(livingEntity);
        if(shrunk)
        {
            poseStack.popPose();
        }
    }
}
