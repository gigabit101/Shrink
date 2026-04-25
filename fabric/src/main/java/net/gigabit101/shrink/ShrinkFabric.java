package net.gigabit101.shrink;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.gigabit101.shrink.network.PacketShrink;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.nio.ByteBuffer;

public class ShrinkFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        ShrinkCommon.init();
        PayloadTypeRegistry.serverboundPlay().register(PacketShrink.TYPE, PacketShrink.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketShrink.TYPE, (payload, context) -> {
            context.server().execute(() -> PacketShrink.handle(context.player(), payload));
        });
    }
}
