package net.gigabit101.shrink.events;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderEvents
{
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        try
        {
            Player player = event.getPlayer();

            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if (iShrinkProvider.isShrunk())
                {
                    event.getPoseStack().pushPose();

                    float scale = iShrinkProvider.scale();

                    event.getPoseStack().scale(scale, scale, scale);
//                    event.getRenderer().shadowRadius = 0.08F;
                    if(event.getEntity().isCrouching() && scale < 0.2F)
                    {
                        event.getPoseStack().translate(0, 1.0, 0);
                    }
                }
                else if(!iShrinkProvider.isShrunk())
                {
//                    event.getRenderer().shadowRadius = 0.5F;
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
            Player player = event.getPlayer();
            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if (iShrinkProvider.isShrunk())
                {
                    event.getPoseStack().popPose();
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onLivingRenderPre(RenderLivingEvent.Pre event)
    {
        try
        {
            LivingEntity livingEntity = event.getEntity();
            if(livingEntity != null && livingEntity instanceof Mob)
            {
                livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                {
                    if(iShrinkProvider.isShrunk())
                    {
                        event.getPoseStack().pushPose();

                        event.getPoseStack().scale(iShrinkProvider.scale(), iShrinkProvider.scale(), iShrinkProvider.scale());
//                        event.getRenderer().shadowRadius = 0.08F;
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onLivingRenderPost(RenderLivingEvent.Post event)
    {
        try
        {
            LivingEntity livingEntity = event.getEntity();
            if(livingEntity != null && livingEntity instanceof Mob)
            {
                if(livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).isPresent())
                {
                    livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                    {
                        if(iShrinkProvider.isShrunk())
                        {
                            event.getPoseStack().popPose();
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
