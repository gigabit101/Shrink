package net.gigabit101.shrink.items;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;


public class ItemShrinkingDevice extends Item
{
    public ItemShrinkingDevice(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        if(!world.isRemote() && player.isSneaking())
        {
            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if(!iShrinkProvider.isShrunk())
                {
                    iShrinkProvider.shrink((ServerPlayerEntity) player);
                }
                else
                {
                    iShrinkProvider.deShrink((ServerPlayerEntity) player);
                }
            });
        }
        return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
    }
}
