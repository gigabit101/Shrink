package net.gigabit101.shrink.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.gigabit101.shrink.network.packets.PacketToggleShrink;
import net.minecraft.client.KeyMapping;

public class ShrinkClientFabric implements ClientModInitializer
{
    private static final KeyMapping TOGGLE_SHRINK = new KeyMapping(
            "key.shrink.shrink",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.shrink.category"
    );

    @Override
    public void onInitializeClient()
    {
        KeyBindingHelper.registerKeyBinding(TOGGLE_SHRINK);

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            while (TOGGLE_SHRINK.consumeClick())
            {
                new PacketToggleShrink().sendToServer();
            }
        });
    }
}