package net.gigabit101.shrink.network;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.network.packets.PacketShrinkDevice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class PacketHandler
{
    public static final NetworkChannel HANDLER = NetworkChannel.create(new ResourceLocation(Shrink.MOD_ID, "main_channel"));
    private static final ResourceLocation GUI_CONTAINER_S2C = new ResourceLocation(Shrink.MOD_ID, "gui_container_s2c");
    private static final ResourceLocation GUI_CONTAINER_C2S = new ResourceLocation(Shrink.MOD_ID, "gui_container_c2s");

    public static void init()
    {
        HANDLER.register(PacketShrinkDevice.class, PacketShrinkDevice::write, PacketShrinkDevice::new, PacketShrinkDevice::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GUI_CONTAINER_S2C, (buf, context) -> context.queue(() -> ModularGuiContainerMenu.handlePacketFromServer(context.getPlayer(), buf)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, GUI_CONTAINER_C2S, (buf, context) -> context.queue(() -> ModularGuiContainerMenu.handlePacketFromClient(context.getPlayer(), buf)));
    }

    public static void sendContainerPacketToServer(Consumer<FriendlyByteBuf> packetWriter)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToServer(GUI_CONTAINER_C2S, buf);
    }

    public static void sendContainerPacketToClient(ServerPlayer player, Consumer<FriendlyByteBuf> packetWriter)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToPlayer(player, GUI_CONTAINER_S2C, buf);
    }

}
