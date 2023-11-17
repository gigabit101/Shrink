package net.gigabit101.shrink;

import com.google.common.base.Suppliers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class Shrink
{
    public static final String MOD_ID = "shrink";
    // We can use this if we don't want to use DeferredRegister
//    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
//
//    // Registering a new creative tab
//    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
//    public static final RegistrySupplier<CreativeModeTab> EXAMPLE_TAB = TABS.register("example_tab", () ->
//            CreativeTabRegistry.create(Component.translatable("itemGroup." + MOD_ID + ".example_tab"),
//                    () -> new ItemStack(Shrink.EXAMPLE_ITEM.get())));
//
//    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
//    public static final RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () ->
//            new Item(new Item.Properties().arch$tab(Shrink.EXAMPLE_TAB)));
//
    public static void init() {
//        TABS.register();
//        ITEMS.register();

//        System.out.println(ShrinkExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
