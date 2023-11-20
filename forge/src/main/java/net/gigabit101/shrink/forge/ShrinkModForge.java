package net.gigabit101.shrink.forge;

import dev.architectury.platform.forge.EventBuses;
import net.gigabit101.shrink.Shrink;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Shrink.MOD_ID)
public class ShrinkModForge
{
    public ShrinkModForge()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Shrink.MOD_ID, eventBus);
        eventBus.addListener(this::clientSetup);

        Shrink.init();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
    }
}
