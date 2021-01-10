package net.gigabit101.shrink.events;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.client.KeyBindings;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.PacketShrinkKeybind;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Shrink.MOD_ID, value = Dist.CLIENT)
public class KeyInputEvents
{
    @SubscribeEvent
    public static void handleEventInput(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || event.phase == TickEvent.Phase.START) return;

        if (KeyBindings.shrink.isPressed())
        {
            PacketHandler.sendToServer(new PacketShrinkKeybind());
        }
    }
}
