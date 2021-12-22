package net.gigabit101.shrink;

import net.gigabit101.shrink.cap.ShrinkImpl;
import net.gigabit101.shrink.client.KeyBindings;
import net.gigabit101.shrink.client.screen.ShrinkScreen;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.gigabit101.shrink.events.RenderEvents;
import net.gigabit101.shrink.items.ShrinkItems;
import net.gigabit101.shrink.network.PacketHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Shrink.MOD_ID)
public class Shrink
{
    public static final String MOD_ID = "shrink";
    public static Shrink INSTANCE;

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Shrink.MOD_ID);

    public static final RegistryObject<MenuType<ShrinkContainer>> SHRINK_CONTAINER = CONTAINERS.register("shrinkcontainer",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                return new ShrinkContainer(windowId, inv);
            }));


    public Shrink()
    {
        INSTANCE = this;
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        ShrinkItems.ITEMS.register(eventBus);
        CONTAINERS.register(eventBus);

        eventBus.addGenericListener(MenuType.class, Shrink::registerContainers);

        ShrinkConfig.loadConfig(ShrinkConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }

    public void registerCommands(RegisterCommandsEvent event)
    {
//        event.getDispatcher().register(ShrinkCommand.register());
//        event.getDispatcher().register(ShrinkResetCommand.register());
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event)
    {
        event.getRegistry().register(new MenuType<>(ShrinkContainer::new).setRegistryName("shrinkingdevice"));
    }

    @SubscribeEvent
    public static void registerCap(RegisterCapabilitiesEvent event)
    {
        ShrinkImpl.init(event);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        KeyBindings.init();

        MenuScreens.register(Shrink.SHRINK_CONTAINER.get(), ShrinkScreen::new);
    }
}
