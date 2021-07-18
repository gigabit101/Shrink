package net.gigabit101.shrink.items;

import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class ItemShrinkRay extends Item
{
    public ItemShrinkRay(Properties properties)
    {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }
}
