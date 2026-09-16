package net.gigabit101.shrink.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.gigabit101.shrink.ShrinkCommon;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.gigabit101.shrink.items.ItemShrinkBottle;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ShrinkItems
{
    public static final PolyRegistry<Item> ITEMS = PolyRegistry.create(Registries.ITEM, ShrinkCommon.MOD_ID);
    public static final Supplier<Item> SHRINKING_DEVICE = ITEMS.registerItem(
            "shrinking_device", "Personal Shrinking Device", ItemShrinkDevice::new);
    public static final Supplier<Item> SHRINK_BOTTLE = ITEMS.registerItem(
            "shrink_bottle", "Glass Bottle", properties -> new ItemShrinkBottle(properties.stacksTo(1)));

    public static void init()
    {
        ITEMS.init();
    }

}
