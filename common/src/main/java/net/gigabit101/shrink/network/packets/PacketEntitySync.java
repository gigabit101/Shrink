package net.gigabit101.shrink.network.packets;

import dev.architectury.networking.NetworkManager;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class PacketEntitySync
{
    double scale;
    int id;
    boolean shrink;

    public PacketEntitySync(double scale, int id, boolean shrink)
    {
        this.scale = scale;
        this.id = id;
        this.shrink = shrink;
    }

    public PacketEntitySync(FriendlyByteBuf buf)
    {
        scale = buf.readDouble();
        id = buf.readInt();
        shrink = buf.readBoolean();
    }

    public void write(FriendlyByteBuf friendlyByteBuf)
    {
        friendlyByteBuf.writeDouble(scale);
        friendlyByteBuf.writeInt(id);
        friendlyByteBuf.writeBoolean(shrink);
    }

    public void handle(Supplier<NetworkManager.PacketContext> context)
    {
        context.get().queue(() ->
        {
            Player player = context.get().getPlayer();
            Level level = player.level();
            if(level != null && level.getEntity(id) != null)
            {
                LivingEntity livingEntity = (LivingEntity) level.getEntity(id);
                if(!ShrinkAPI.isEntityShrunk(livingEntity))
                {
                    livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).addPermanentModifier(ItemShrinkDevice.createModifier(scale));
                }
                else
                {
                    livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).removeModifier(ItemShrinkDevice.SHRINKING_DEVICE_ID);
                }
                livingEntity.refreshDimensions();
            }
            else
            {
                System.out.println("entity with id " + id + " does not exist");
            }
        });
    }
}
