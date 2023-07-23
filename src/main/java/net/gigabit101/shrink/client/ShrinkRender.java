package net.gigabit101.shrink.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class ShrinkRender
{
    public static void onEntityRenderPre(LivingEntity livingEntity, PoseStack poseStack)
    {
        try
        {
            if(livingEntity != null && livingEntity instanceof Mob)
            {
                livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                {
                    if(iShrinkProvider.isShrunk())
                    {
                        poseStack.pushPose();
                        poseStack.scale(iShrinkProvider.scale(), iShrinkProvider.scale(), iShrinkProvider.scale());
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void onEntityRenderPost(LivingEntity livingEntity, PoseStack poseStack)
    {
        try
        {
            if(livingEntity != null && livingEntity instanceof Mob)
            {
                if(livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).isPresent())
                {
                    livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                    {
                        if(iShrinkProvider.isShrunk())
                        {
                            poseStack.popPose();
                        }
                    });
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
