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

                            if(cap.isShrunk())
                            {
                                entity.size = new EntitySize(0.1F, 0.1F, true);
                                EntitySize entitySize = PlayerEntity.STANDING_SIZE = EntitySize.flexible(0.1F, 0.2F);
                                PlayerEntity.SIZE_BY_POSE = ImmutableMap.<Pose, EntitySize>builder().put(Pose.STANDING, entitySize).put(Pose.SLEEPING, EntitySize.fixed(0.2F, 0.2F)).put(Pose.FALL_FLYING, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SWIMMING, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntitySize.flexible(0.6F, 0.6F)).put(Pose.CROUCHING, EntitySize.flexible(0.5F, 0.5F)).put(Pose.DYING, EntitySize.fixed(0.2F, 0.2F)).build();
                                entity.eyeHeight = 0.16F;
                                entity.recalculateSize();
                            }
                            else if(!cap.isShrunk())
                            {
                                entity.size = ShrinkImpl.defaultSize;
                                PlayerEntity.STANDING_SIZE =  EntitySize.flexible(0.6F, 1.8F);
                                PlayerEntity.SIZE_BY_POSE = ShrinkImpl.defaultSizes;
                                entity.eyeHeight = ShrinkImpl.defaultEyeHeight;
                                entity.recalculateSize();
                            }
                        });
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
