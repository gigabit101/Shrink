package net.gigabit101.shrink;

import net.gigabit101.shrink.cap.ShrinkImpl;
import net.gigabit101.shrink.client.KeyBindings;
import net.gigabit101.shrink.client.screen.ShrinkScreen;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.gigabit101.shrink.events.RenderEvents;
import net.gigabit101.shrink.items.ItemShrinkingDevice;
import net.gigabit101.shrink.items.ShrinkItems;
import net.gigabit101.shrink.network.PacketHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

@Mod(Shrink.MOD_ID)
public class Shrink
{
    public static final String MOD_ID = "shrink";
    public static Shrink INSTANCE;

    public Shrink()
    {
        INSTANCE = this;
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::registerCreativeTab);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->  eventBus.addListener(this::registerKeybinding));
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        ShrinkItems.ITEMS.register(eventBus);
        ModContainers.CONTAINERS.register(eventBus);

        ShrinkConfig.loadConfig(ShrinkConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }

    public void registerKeybinding(RegisterKeyMappingsEvent event)
    {
        KeyBindings.shrink = KeyBindings.createBinding("shrink", GLFW.GLFW_KEY_G);
        event.register(KeyBindings.shrink);
    }

    public void registerCommands(RegisterCommandsEvent event)
    {
//        event.getDispatcher().register(ShrinkCommand.register());
//        event.getDispatcher().register(ShrinkResetCommand.register());
    }


    public void registerCreativeTab(CreativeModeTabEvent.Register event)
    {
        event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "creative_tab"), builder -> builder.icon(() -> new ItemStack(ShrinkItems.SHRINKING_DEVICE.get()))
                .title(Component.translatable("key.shrink.category"))
                .displayItems((features, output, hasPermissions) -> ShrinkItems.ITEMS.getEntries().forEach(itemRegistryObject -> output.accept(new ItemStack(itemRegistryObject.get())))));

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

        MenuScreens.register(ModContainers.SHRINK_CONTAINER.get(), ShrinkScreen::new);

        event.enqueueWork(() ->
        {
            ItemProperties.register(ShrinkItems.SHRINKING_DEVICE.get(), new ResourceLocation(MOD_ID, "has_power"), (stack, level, living, id) ->
            {
                ItemShrinkingDevice itemShrinkingDevice = (ItemShrinkingDevice) stack.getItem();
                return itemShrinkingDevice.hasPower(stack) ? 1.0F : 0.0F;
            });
        });
    }
}
