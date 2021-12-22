package net.gigabit101.shrink.items;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.client.ItemGroupShrink;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ShrinkItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Shrink.MOD_ID);
    public static final Item.Properties ITEM_GROUP = new Item.Properties().tab(ItemGroupShrink.INSTANCE);

    public static final RegistryObject<Item> SHRINKING_DEVICE = ITEMS.register("shrinking_device", () -> new ItemShrinkingDevice(ITEM_GROUP));
//    public static final RegistryObject<Item> SHRINK_RAY = ITEMS.register("shrink_ray", () -> new ItemShrinkRay(ITEM_GROUP));

    public static final RegistryObject<Item> MOB_BOTTLE = ITEMS.register("mob_bottle", () -> new ItemModBottle(ITEM_GROUP));
}
