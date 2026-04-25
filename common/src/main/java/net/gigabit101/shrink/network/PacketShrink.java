package net.gigabit101.shrink.network;

import net.gigabit101.shrink.ShrinkCommon;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record PacketShrink(int hand, double scale) implements CustomPacketPayload
{
    public static final Type<PacketShrink> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ShrinkCommon.MOD_ID, "shrink_sync"));

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PacketShrink> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketShrink::hand,
            ByteBufCodecs.DOUBLE, PacketShrink::scale,
            PacketShrink::new
    );

    public static void handle(Player player, PacketShrink message)
    {
        InteractionHand interactionHand = message.hand == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if(player == null) return;
        ItemStack stack = player.getItemInHand(interactionHand);
        if(!stack.isEmpty() && stack.getItem() instanceof ItemShrinkDevice itemShrinkDevice)
        {
            itemShrinkDevice.writeScale(stack, message.scale);
        }
    }
}
