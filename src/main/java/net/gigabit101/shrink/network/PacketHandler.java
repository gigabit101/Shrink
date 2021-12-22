package net.gigabit101.shrink.network;

import net.gigabit101.shrink.Shrink;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler
{
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Shrink.MOD_ID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static int index;

    public static void register()
    {
        registerMessage(PacketShrink.class, PacketShrink::encode, PacketShrink::decode, PacketShrink.Handler::handle);
        registerMessage(PacketShrinkKeybind.class, PacketShrinkKeybind::encode, PacketShrinkKeybind::decode, PacketShrinkKeybind.Handler::handle);
        registerMessage(PacketShrinkScreen.class, PacketShrinkScreen::encode, PacketShrinkScreen::decode, PacketShrinkScreen.Handler::handle);
    }

    private static <MSG> void registerMessage(Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer)
    {
        HANDLER.registerMessage(index++, type, encoder, decoder, consumer);
    }

    public static void sendToServer(Object msg)
    {
        HANDLER.sendToServer(msg);
    }

    public static void send(PacketDistributor.PacketTarget target, PacketShrink message)
    {
        HANDLER.send(target, message);
    }
}
