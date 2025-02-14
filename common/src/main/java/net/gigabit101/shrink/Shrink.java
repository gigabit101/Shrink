package net.gigabit101.shrink;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.config.ConfigBuilder;
import net.fabricmc.api.EnvType;
import net.gigabit101.shrink.init.ModContainers;
import net.gigabit101.shrink.init.ModItems;
import net.gigabit101.shrink.init.ModTagKeys;
import net.gigabit101.shrink.init.ShrinkComponentTypes;
import net.gigabit101.shrink.items.ItemShrinkBottle;
import net.gigabit101.shrink.network.PacketHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Shrink
{
    public static final String MOD_ID = "shrink";

    public static ConfigBuilder configBuilder;
    public static ShrinkConfig shrinkConfig;

    public static void init()
    {
        // initialize PolyLib's item components
        PolyLib.initPolyItemData();

        configBuilder = new ConfigBuilder(MOD_ID, Platform.getConfigFolder().resolve(MOD_ID + ".json5"), new ShrinkConfig());
        shrinkConfig = (ShrinkConfig) configBuilder.getConfigData();

        PolyLib.initPolyItemData();

        ModItems.CREATIVE_MODE_TABS.register();
        ModItems.ITEMS.register();
        ModContainers.CONTAINERS.register();
        ShrinkComponentTypes.COMPONENTS.register();
        PacketHandler.init();
        ModTagKeys.init();
        if(Platform.getEnv() == EnvType.CLIENT)
        {
            ClientLifecycleEvent.CLIENT_SETUP.register(instance -> ShrinkClient.init());
        }

        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) ->
        {
            ItemStack stack = player.getItemInHand(hand);
            if(stack.is(Items.GLASS_BOTTLE) && entity instanceof LivingEntity livingEntity) {
                if (ItemShrinkBottle.onInteractWithEntity(stack, player, livingEntity, hand).consumesAction()) {
                    return EventResult.interruptTrue();
                }
            }
            return EventResult.pass();
        });
    }
}
