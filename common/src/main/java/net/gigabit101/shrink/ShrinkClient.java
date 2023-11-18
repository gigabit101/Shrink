package net.gigabit101.shrink;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.gigabit101.shrink.polylib.LivingEntityRenderEvents;

public class ShrinkClient
{
    public static void init()
    {
        LivingEntityRenderEvents.PRE.register((livingEntity, f, g, poseStack, multiBufferSource, i) ->
        {
            boolean shrunk = ShrinkAPI.isEntityShrunk(livingEntity);//livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue() != 1.0D;
            if(shrunk)
            {
                poseStack.pushPose();
                float scale = (float) livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue();
                poseStack.scale(scale, scale, scale);
            }
        });

        LivingEntityRenderEvents.POST.register((livingEntity, f, g, poseStack, multiBufferSource, i) ->
        {
            boolean shrunk = ShrinkAPI.isEntityShrunk(livingEntity);//livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).hasModifier(ItemShrinkDevice.createModifier(1.0F));
            if(shrunk)
            {
                poseStack.popPose();
            }
        });
    }
}
