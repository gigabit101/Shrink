package net.gigabit101.shrink.network;

import dev.architectury.networking.NetworkChannel;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.network.packets.PacketShrinkDevice;
import net.minecraft.resources.ResourceLocation;

public class PacketHandler
{
    public static final NetworkChannel HANDLER = NetworkChannel.create(new ResourceLocation(Shrink.MOD_ID, "main_channel"));

    public static void init()
    {
        HANDLER.register(PacketShrinkDevice.class, PacketShrinkDevice::write, PacketShrinkDevice::new, PacketShrinkDevice::handle);
    }
}
