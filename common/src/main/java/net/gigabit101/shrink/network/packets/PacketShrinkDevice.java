package net.gigabit101.shrink.network.packets;

import dev.architectury.networking.NetworkManager;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class PacketShrinkDevice
{
    InteractionHand hand;
    double scale;

    public PacketShrinkDevice(InteractionHand hand, double scale)
    {
        this.hand = hand;
        this.scale = scale;
    }

    public PacketShrinkDevice(FriendlyByteBuf friendlyByteBuf)
    {
        hand = friendlyByteBuf.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        scale = friendlyByteBuf.readDouble();
    }

    public void write(FriendlyByteBuf friendlyByteBuf)
    {
        friendlyByteBuf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        friendlyByteBuf.writeDouble(scale);
    }

    public void handle(Supplier<NetworkManager.PacketContext> context)
    {
        context.get().queue(() ->
        {
            Player player = context.get().getPlayer();
            if(player == null) return;
            ItemStack stack = player.getItemInHand(hand);
            if(!stack.isEmpty() && stack.getItem() instanceof ItemShrinkDevice itemShrinkDevice)
            {
                itemShrinkDevice.writeScale(stack, scale);
            }
        });
    }
}
