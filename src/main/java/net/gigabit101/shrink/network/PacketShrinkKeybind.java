package net.gigabit101.shrink.network;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
