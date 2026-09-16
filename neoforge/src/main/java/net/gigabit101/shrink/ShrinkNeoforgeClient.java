package net.gigabit101.shrink;

import net.creeperhost.polylib.neoforge.registry.NeoPolyScreens;
import net.gigabit101.shrink.client.ShrinkBottleRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

public class ShrinkNeoforgeClient
{
    public static void init(IEventBus eventBus)
    {
        NeoPolyScreens.registerToBus(eventBus);
        eventBus.addListener((RegisterSpecialModelRendererEvent event) ->
                event.register(ShrinkBottleRenderer.ID, ShrinkBottleRenderer.Unbaked.MAP_CODEC));
    }
}
