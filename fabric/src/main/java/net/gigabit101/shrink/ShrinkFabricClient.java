package net.gigabit101.shrink;

import net.creeperhost.polylib.registry.PolyScreens;
import net.fabricmc.api.ClientModInitializer;
import net.gigabit101.shrink.client.ShrinkBottleRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class ShrinkFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        PolyScreens.flush();
        SpecialModelRenderers.ID_MAPPER.put(ShrinkBottleRenderer.ID, ShrinkBottleRenderer.Unbaked.MAP_CODEC);
    }
}
