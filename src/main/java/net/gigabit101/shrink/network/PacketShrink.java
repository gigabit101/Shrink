package net.gigabit101.shrink.network;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        buf.writeNbt(msg.nbt);
    }

    public static PacketShrink decode(PacketBuffer buf)
    {
        return new PacketShrink(buf.readInt(), buf.readNbt());
    }

    public static class Handler
    {
        public static void handle(final PacketShrink message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                ClientWorld world = Minecraft.getInstance().level;

                if (world != null)
                {
                    Entity entity = world.getEntity(message.entityID);

                    if (entity instanceof LivingEntity)
                    {
                        entity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                        {
                            iShrinkProvider.deserializeNBT(message.nbt);

                            entity.refreshDimensions();
                            entity.setPos(entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ());

                            if(!(entity instanceof PlayerEntity))
                            {
                                if (iShrinkProvider.isShrunk())
                                {
                                    entity.dimensions = new EntitySize(iShrinkProvider.scale(), iShrinkProvider.scale() * 2, true);
                                    entity.eyeHeight = iShrinkProvider.defaultEyeHeight() * iShrinkProvider.scale();
                                }
                                else
                                {
                                    entity.dimensions = iShrinkProvider.defaultEntitySize();
                                    entity.eyeHeight = iShrinkProvider.defaultEyeHeight();
                                }
                            }
                        });
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
