package net.gigabit101.shrink.network;

import com.google.common.collect.ImmutableMap;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.cap.ShrinkImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ShrinkPacket
{
    private final CompoundNBT nbt;
    private final int entityID;

    public ShrinkPacket(int entityID, CompoundNBT nbt)
    {
        this.nbt = nbt;
        this.entityID = entityID;
    }

    public static void encode(ShrinkPacket msg, PacketBuffer buf)
    {
        buf.writeInt(msg.entityID);
        buf.writeCompoundTag(msg.nbt);
    }

    public static ShrinkPacket decode(PacketBuffer buf)
    {
        return new ShrinkPacket(buf.readInt(), buf.readCompoundTag());
    }

    public static class Handler
    {
        public static void handle(final ShrinkPacket message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                ClientWorld world = Minecraft.getInstance().world;

                if (world != null)
                {
                    Entity entity = world.getEntityByID(message.entityID);

                    if (entity instanceof LivingEntity)
                    {
                        entity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(cap ->
                        {
                            cap.deserializeNBT(message.nbt);
                        });
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
