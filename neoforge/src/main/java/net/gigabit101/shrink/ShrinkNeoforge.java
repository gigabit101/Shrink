package net.gigabit101.shrink;

import net.creeperhost.polylib.neoforge.registry.NeoPolyRegistry;
import net.gigabit101.shrink.network.PacketShrink;
import net.gigabit101.shrink.items.ItemShrinkBottle;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(ShrinkCommon.MOD_ID)
public class ShrinkNeoforge
{
    public ShrinkNeoforge(IEventBus eventBus)
    {
        ShrinkCommon.init();
        NeoPolyRegistry.registerToBus(eventBus, ShrinkCommon.MOD_ID);
        eventBus.addListener(ShrinkNeoforge::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(ShrinkNeoforge::onEntityInteract);
        if (FMLLoader.getCurrent().getDist().isClient()) {
            ShrinkNeoforgeClient.init(eventBus);
        }
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        InteractionResult result = ItemShrinkBottle.capture(event.getEntity(), event.getTarget(), event.getHand());
        if (result != InteractionResult.PASS)
        {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(ShrinkCommon.MOD_ID);
        registrar.playToServer(PacketShrink.TYPE, PacketShrink.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> PacketShrink.handle(context.player(),  payload));
        });
    }
}
