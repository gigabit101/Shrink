package net.gigabit101.shrink.network;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketShrinkScreen
{
    private final float scale;

    public PacketShrinkScreen(float scale)
    {
        this.scale = scale;
    }

    public static void encode(PacketShrinkScreen msg, PacketBuffer buffer)
    {
        buffer.writeFloat(msg.scale);
    }

    public static PacketShrinkScreen decode(PacketBuffer buffer)
    {
        return new PacketShrinkScreen(buffer.readFloat());
    }

    public static class Handler
    {
        public static void handle(final PacketShrinkScreen msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                ServerPlayerEntity player = ctx.get().getSender();
                player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                {
                    iShrinkProvider.setScale(msg.scale);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
