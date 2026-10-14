package net.gigabit101.shrink.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.gigabit101.shrink.ShrinkExpectPlatform;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.gigabit101.shrink.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PacketToggleShrink extends BaseC2SMessage
{
    public PacketToggleShrink()
    {
    }

    public PacketToggleShrink(FriendlyByteBuf buf)
    {
    }

    @Override
    public MessageType getType()
    {
        return PacketHandler.TOGGLE_SHRINK;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf)
    {
    }

    @Override
    public void handle(NetworkManager.PacketContext context)
    {
        context.queue(() ->
        {
            Player player = context.getPlayer();

            if (player == null)
            {
                return;
            }

            ItemStack stack = findShrinkDevice(player);

            if (!stack.isEmpty() && stack.getItem() instanceof ItemShrinkDevice itemShrinkDevice)
            {
                itemShrinkDevice.toggleShrink(player, stack);
            }
        });
    }

    private static ItemStack findShrinkDevice(Player player)
    {
        for (ItemStack stack : player.getInventory().items)
        {
            if (stack.getItem() instanceof ItemShrinkDevice)
            {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().offhand)
        {
            if (stack.getItem() instanceof ItemShrinkDevice)
            {
                return stack;
            }
        }

        ItemStack equippedStack = ShrinkExpectPlatform.findEquippedShrinkDevice(player);

        if (!equippedStack.isEmpty() && equippedStack.getItem() instanceof ItemShrinkDevice)
        {
            return equippedStack;
        }

        return ItemStack.EMPTY;
    }
}