package net.gigabit101.shrink.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.gigabit101.shrink.ShrinkCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ShrinkCreativeTabs
{
    public static final PolyRegistry<CreativeModeTab> TABS = PolyRegistry.create(Registries.CREATIVE_MODE_TAB, ShrinkCommon.MOD_ID);

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = TABS.registerCreativeTab(
            "creative_tab",
            "Shrink",
            () -> new ItemStack(ShrinkItems.SHRINKING_DEVICE.get()),
            (params, output) -> {
                output.accept(ShrinkItems.SHRINKING_DEVICE.get());
            }
    );

    public static void init() {
        TABS.init();
    }
}
