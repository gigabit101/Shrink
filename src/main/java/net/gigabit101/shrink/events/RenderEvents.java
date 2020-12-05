package net.gigabit101.shrink.events;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderEvents
{
    float scale = 1.0F;
    int delay = 0;

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

                    if(iShrinkProvider.isShrinking())
                    {
                        delay++;
                        if(delay >= 5)
                        {
                            scale -= 0.1F;
                            delay = 0;
                        }
                    }

                    if(scale <= 0.1F)
                    {
                        iShrinkProvider.setShrinking(false);
                        scale = 0.1F;
                        delay = 0;
                    }

                    event.getMatrixStack().scale(scale, scale, scale);
                    event.getRenderer().shadowSize = 0.08F;
                    if(event.getEntity().isCrouching() && !iShrinkProvider.isShrinking())
                    {
                        event.getMatrixStack().translate(0, 1.0, 0);
                    }
                }
                else if(!iShrinkProvider.isShrunk())
                {
                    event.getRenderer().shadowSize = 0.5F;
                    scale = 1.0F;
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
