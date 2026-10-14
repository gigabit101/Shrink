package net.gigabit101.shrink.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.client.ShrinkScreen;
import net.gigabit101.shrink.init.ModContainers;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Shrink.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistration
{
    public static final KeyMapping TOGGLE_SHRINK = new KeyMapping(
            "key.shrink.shrink",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.shrink.category"
    );

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.SHRINKING_DEVICE.get(), ShrinkScreen::create);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(TOGGLE_SHRINK);
    }
}