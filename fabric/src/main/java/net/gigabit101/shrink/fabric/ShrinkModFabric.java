package net.gigabit101.shrink.fabric;

import net.fabricmc.api.ModInitializer;
import net.gigabit101.shrink.Shrink;

public class ShrinkModFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Shrink.init();
    }
}
