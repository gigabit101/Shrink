package net.gigabit101.shrink;

import net.gigabit101.shrink.cap.ShrinkImpl;
import net.gigabit101.shrink.client.KeyBindings;
import net.gigabit101.shrink.client.screen.ShrinkScreen;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.gigabit101.shrink.events.RenderEvents;
import net.gigabit101.shrink.items.ShrinkItems;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.server.ShrinkCommand;
import net.gigabit101.shrink.server.ShrinkResetCommand;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ObjectHolder;

@Mod(Shrink.MOD_ID)
public class Shrink
{
    public static final String MOD_ID = "shrink";
    public static Shrink INSTANCE;

    @ObjectHolder(MOD_ID + ":" + "shrinkingdevice")
    public static ContainerType<ShrinkContainer> shrinkingdevice = null;

    public Shrink()
    {
        INSTANCE = this;
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        ShrinkItems.ITEMS.register(eventBus);

        eventBus.addGenericListener(ContainerType.class, Shrink::registerContainers);

        ShrinkConfig.loadConfig(ShrinkConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }

    public void registerCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(ShrinkCommand.register());
        event.getDispatcher().register(ShrinkResetCommand.register());
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().register(new ContainerType<>(ShrinkContainer::new).setRegistryName("shrinkingdevice"));
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.register();
        ShrinkImpl.init();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        KeyBindings.init();
        ScreenManager.registerFactory(Shrink.shrinkingdevice, ShrinkScreen::new);
    }
}
