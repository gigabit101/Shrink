package net.gigabit101.shrink.network;

import io.netty.buffer.ByteBuf;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketShrink
{
    private final CompoundTag nbt;
    private final int entityID;

    public PacketShrink(int entityID, CompoundTag nbt)
    {
        this.nbt = nbt;
        this.entityID = entityID;
    }

    public static void encode(PacketShrink msg, FriendlyByteBuf buf)
    {
        buf.writeInt(msg.entityID);
        buf.writeNbt(msg.nbt);
    }

    public static PacketShrink decode(FriendlyByteBuf buf)
    {
        return new PacketShrink(buf.readInt(), buf.readNbt());
    }

    public static class Handler
    {
        public static void handle(final PacketShrink message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                ClientLevel world = Minecraft.getInstance().level;

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

                            if(!(entity instanceof Player))
                            {
                                if (iShrinkProvider.isShrunk())
                                {
//                                    entity.dimensions = new EntityDimensions(iShrinkProvider.scale(), iShrinkProvider.scale() * 2, true);
//                                    entity.eyeHeight = iShrinkProvider.defaultEyeHeight() * iShrinkProvider.scale();
                                }
                                else
                                {
//                                    entity.dimensions = iShrinkProvider.defaultEntitySize();
//                                    entity.eyeHeight = iShrinkProvider.defaultEyeHeight();
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
