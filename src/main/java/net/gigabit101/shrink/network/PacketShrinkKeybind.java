package net.gigabit101.shrink.network;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketShrinkKeybind
{
    public static void encode(PacketShrinkKeybind msg, PacketBuffer buffer) {}

    public static PacketShrinkKeybind decode(PacketBuffer buffer)
    {
        return new PacketShrinkKeybind();
    }

    public static class Handler
    {
        public static void handle(final PacketShrinkKeybind msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                ServerPlayerEntity player = ctx.get().getSender();
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
