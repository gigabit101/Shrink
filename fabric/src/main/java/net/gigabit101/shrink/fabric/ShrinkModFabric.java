package net.gigabit101.shrink.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.mixin.client.rendering.EntityRenderersMixin;
import net.gigabit101.shrink.Shrink;

public class ShrinkModFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Shrink.init();
    }
}
