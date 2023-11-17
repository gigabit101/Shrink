package net.gigabit101.shrink;

import net.gigabit101.shrink.init.ModItems;

public class Shrink
{
    public static final String MOD_ID = "shrink";

    public static void init()
    {
        ModItems.CREATIVE_MODE_TABS.register();
        ModItems.ITEMS.register();
    }
}
