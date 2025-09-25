package net.gigabit101.shrink.neoforge;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.client.ShrinkScreen;
import net.gigabit101.shrink.init.ModContainers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Shrink.MOD_ID, value = Dist.CLIENT)
public class ClientRegistration
{
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.SHRINKING_DEVICE.get(), ShrinkScreen::create);
    }
}
