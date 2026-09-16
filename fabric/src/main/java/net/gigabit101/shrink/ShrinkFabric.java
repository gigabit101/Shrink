package net.gigabit101.shrink;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.gigabit101.shrink.network.PacketShrink;
import net.gigabit101.shrink.items.ItemShrinkBottle;

public class ShrinkFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        ShrinkCommon.init();
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                ItemShrinkBottle.capture(player, entity, hand));
        PayloadTypeRegistry.serverboundPlay().register(PacketShrink.TYPE, PacketShrink.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketShrink.TYPE, (payload, context) -> {
            context.server().execute(() -> PacketShrink.handle(context.player(), payload));
        });
    }
}
