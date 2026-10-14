package net.gigabit101.shrink.neoforge;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.network.packets.PacketToggleShrink;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(
        modid = Shrink.MOD_ID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.GAME
)
public class ClientEvents
{
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        while (ClientRegistration.TOGGLE_SHRINK.consumeClick())
        {
            new PacketToggleShrink().sendToServer();
        }
    }
}