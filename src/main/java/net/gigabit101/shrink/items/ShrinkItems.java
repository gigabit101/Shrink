package net.gigabit101.shrink.items;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.client.ItemGroupShrink;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ShrinkItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Shrink.MOD_ID);
    public static final Item.Properties ITEM_GROUP = new Item.Properties().group(ItemGroupShrink.INSTANCE);

    public static final RegistryObject<Item> SHRINKING_DEVICE = ITEMS.register("shrinking_device", () -> new ItemShrinkingDevice(ITEM_GROUP));
}
