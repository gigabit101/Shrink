package net.gigabit101.shrink.neoforge;

import net.gigabit101.shrink.polylib.LivingEntityRenderEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

public class NeoForgeClientEvents
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
