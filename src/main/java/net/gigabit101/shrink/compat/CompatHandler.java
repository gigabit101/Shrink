package net.gigabit101.shrink.compat;

import net.gigabit101.shrink.Shrink;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class CompatHandler
{
    public static void init()
    {
        if(ModList.get().isLoaded("geckolib"))
        {
            Shrink.LOGGER.info("gekolib detected, registering GekoLibCompat");
            MinecraftForge.EVENT_BUS.register(new GekoLibCompat());
        }
    }
}
