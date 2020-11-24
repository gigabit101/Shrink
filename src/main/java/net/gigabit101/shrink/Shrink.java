package net.gigabit101.shrink;

import net.gigabit101.shrink.events.RenderEvents;
import net.gigabit101.shrink.items.ShrinkItems;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.proxy.ClientProxy;
import net.gigabit101.shrink.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Shrink.MOD_ID)
public class Shrink
{
    public static final String MOD_ID = "shrink";
    public static Shrink INSTANCE;
    private static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Shrink()
    {
        INSTANCE = this;
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::doClientStuff);

        ShrinkItems.ITEMS.register(eventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.register();
    }

    void doClientStuff(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
    }
}
