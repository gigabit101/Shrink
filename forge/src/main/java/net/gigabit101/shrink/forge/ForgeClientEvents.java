package net.gigabit101.shrink.forge;

import net.gigabit101.shrink.polylib.LivingEntityRenderEvents;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeClientEvents
{
    @SubscribeEvent
    public void onRenderPlayerPre(RenderLivingEvent.Pre event)
    {
        LivingEntityRenderEvents.PRE.invoker().pre(event.getEntity(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());

    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderLivingEvent.Post event)
    {
        LivingEntityRenderEvents.POST.invoker().post(event.getEntity(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
    }
}
