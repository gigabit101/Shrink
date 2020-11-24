package net.gigabit101.shrink.events;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderEvents
{
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        try
        {
            PlayerEntity player = event.getPlayer();

            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if (iShrinkProvider.isShrunk())
                {
                    event.getMatrixStack().push();
                    event.getMatrixStack().scale(0.1F, 0.1F, 0.1F);
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        try
        {
            PlayerEntity player = event.getPlayer();
            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if (iShrinkProvider.isShrunk())
                {
                    event.getMatrixStack().pop();
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
