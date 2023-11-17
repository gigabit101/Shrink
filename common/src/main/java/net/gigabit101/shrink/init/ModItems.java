package net.gigabit101.shrink.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.gigabit101.shrink.Shrink;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Shrink.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Shrink.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("creative_tab", () -> CreativeTabRegistry.create(Component.translatable("itemGroup." + Shrink.MOD_ID), () -> new ItemStack(ModItems.SHRINKING_DEVICE.get())));
    public static final RegistrySupplier<Item> SHRINKING_DEVICE = ITEMS.register("shrinking_device", () -> new Item(new Item.Properties()));

    static
    {
        CreativeTabRegistry.append(CREATIVE_TAB, SHRINKING_DEVICE);
    }
}
