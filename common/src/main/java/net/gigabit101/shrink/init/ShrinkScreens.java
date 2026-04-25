package net.gigabit101.shrink.init;

import net.creeperhost.polylib.registry.PolyScreens;
import net.gigabit101.shrink.client.ShrinkScreen;

public class ShrinkScreens
{
    public static void init()
    {
        PolyScreens.register(ShrinkContainers.SHRINKING_DEVICE,
                ShrinkScreen::create);
    }
}
