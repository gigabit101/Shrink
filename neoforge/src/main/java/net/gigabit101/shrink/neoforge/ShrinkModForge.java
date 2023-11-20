package net.gigabit101.shrink.neoforge;

//import dev.architectury.platform.neoforge.EventBuses;
import net.gigabit101.shrink.Shrink;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Shrink.MOD_ID)
public class ShrinkModForge
{
    public ShrinkModForge()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //TODO??
//        EventBuses.registerModEventBus(Shrink.MOD_ID, eventBus);
        eventBus.addListener(this::clientSetup);
        Shrink.init();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.register(new NeoForgeClientEvents());
    }
}
