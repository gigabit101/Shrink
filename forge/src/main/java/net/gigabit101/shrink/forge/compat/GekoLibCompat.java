package net.gigabit101.shrink.forge.compat;

import net.gigabit101.shrink.client.ShrinkRender;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib.event.GeoRenderEvent;

public class GekoLibCompat
{
    @SubscribeEvent
    public void onGekoRenderEventPre(GeoRenderEvent.Entity.Pre event)
    {
        if(event.getEntity() instanceof LivingEntity livingEntity)
            ShrinkRender.onEntityRenderPre(livingEntity, event.getPoseStack());
    }

    @SubscribeEvent
    public void onGekoRenderEventPost(GeoRenderEvent.Entity.Post event)
    {
        if(event.getEntity() instanceof LivingEntity livingEntity)
            ShrinkRender.onEntityRenderPost(livingEntity, event.getPoseStack());
    }
}
