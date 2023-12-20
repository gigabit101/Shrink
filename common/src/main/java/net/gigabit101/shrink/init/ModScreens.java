package net.gigabit101.shrink.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.gigabit101.shrink.client.ShrinkScreen;

public class ModScreens
{
    public static void init()
    {
        MenuRegistry.registerScreenFactory(ModContainers.SHRINKING_DEVICE.get(), ShrinkScreen::create);
    }
}
