package net.gigabit101.shrink.network;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.items.ItemShrinkingDevice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketShrinkKeybind
{
    public static void encode(PacketShrinkKeybind msg, FriendlyByteBuf buffer) {}

    public static PacketShrinkKeybind decode(FriendlyByteBuf buffer)
    {
        return new PacketShrinkKeybind();
    }

    public static class Handler
    {
        public static void handle(final PacketShrinkKeybind msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;
                for (int i = 0; i < player.getInventory().getContainerSize(); i++)
                {
                    ItemStack stack = player.getInventory().getItem(i);
                    if(stack.getItem() instanceof ItemShrinkingDevice itemShrinkingDevice)
                    {
                        if(itemShrinkingDevice.canUse(stack, player))
                        {
                            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                            {
                                if(!iShrinkProvider.isShrunk())
                                {
                                    iShrinkProvider.shrink(player);
                                }
                                else
                                {
                                    iShrinkProvider.deShrink(player);
                                }
                            });
                            break;
                        }
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
