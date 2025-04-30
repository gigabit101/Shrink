package net.gigabit101.shrink.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.items.ItemShrinkBottle;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Shrink.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Shrink.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("creative_tab", () -> CreativeTabRegistry.create(Component.translatable("itemGroup." + Shrink.MOD_ID), () -> new ItemStack(ModItems.SHRINKING_DEVICE.get())));
    public static final RegistrySupplier<Item> SHRINKING_DEVICE = ITEMS.register("shrinking_device", () -> new ItemShrinkDevice(new Item.Properties().stacksTo(1).setId(modKey("shrinking_device"))));
    public static final RegistrySupplier<Item> SHRINK_BOTTLE = ITEMS.register("shrink_bottle", () -> new ItemShrinkBottle(new Item.Properties().stacksTo(1).setId(modKey("shrink_bottle"))));

    static ResourceKey<Item> modKey (String name)
    {
        return ResourceKey.create(Registries.ITEM, modLoc(name));
    }

    static ResourceLocation modLoc (String name)
    {
        return ResourceLocation.fromNamespaceAndPath(Shrink.MOD_ID, name);
    }

    static
    {
        //noinspection unchecked,UnstableApiUsage
        CreativeTabRegistry.append(CREATIVE_TAB, SHRINKING_DEVICE, SHRINK_BOTTLE);
    }
}
