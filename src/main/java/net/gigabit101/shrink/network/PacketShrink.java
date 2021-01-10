package net.gigabit101.shrink.network;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketShrink
{
    private final CompoundNBT nbt;
    private final int entityID;

    public PacketShrink(int entityID, CompoundNBT nbt)
    {
        this.nbt = nbt;
        this.entityID = entityID;
    }

    public static void encode(PacketShrink msg, PacketBuffer buf)
    {
        buf.writeInt(msg.entityID);
        buf.writeCompoundTag(msg.nbt);
    }

    public static PacketShrink decode(PacketBuffer buf)
    {
        return new PacketShrink(buf.readInt(), buf.readCompoundTag());
    }

    public static class Handler
    {
        public static void handle(final PacketShrink message, Supplier<NetworkEvent.Context> ctx)
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

                            if(cap.isShrunk())
                            {
                                entity.size = new EntitySize(0.1F, 0.2F, true);
                                entity.eyeHeight = 0.16F;
                            }
                            else
                            {
                                entity.size = cap.defaultEntitySize();
                                entity.eyeHeight = cap.defaultEyeHeight();
                            }
                        });
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
