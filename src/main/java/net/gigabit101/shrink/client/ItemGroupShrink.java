package net.gigabit101.shrink.client;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.items.ShrinkItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupShrink extends ItemGroup
{
    public static ItemGroupShrink INSTANCE = new ItemGroupShrink();

    public ItemGroupShrink()
    {
        super(Shrink.MOD_ID);
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(ShrinkItems.SHRINKING_DEVICE.get());
    }
}
