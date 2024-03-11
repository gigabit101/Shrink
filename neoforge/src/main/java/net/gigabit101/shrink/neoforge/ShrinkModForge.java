package net.gigabit101.shrink.neoforge;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.neoforge.compat.GekoLibCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Shrink.MOD_ID)
public class ShrinkModForge
{
    public ShrinkModForge(IEventBus bus)
    {
        bus.addListener(this::clientSetup);
        Shrink.init();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.register(new NeoForgeClientEvents());
        if(ModList.get().isLoaded("gekolib"))
        {
            NeoForge.EVENT_BUS.register(new GekoLibCompat());
        }
    }
}
