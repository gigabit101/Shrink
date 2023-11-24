package net.gigabit101.shrink;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.polylib.config.ConfigBuilder;
import net.fabricmc.api.EnvType;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.init.ModContainers;
import net.gigabit101.shrink.init.ModItems;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.polylib.AttributeEvents;
import net.gigabit101.shrink.polylib.EntitySizeEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public class Shrink
{
    public static final String MOD_ID = "shrink";

    public static final DeferredRegister<Attribute> ATTRIBUTES_DEFERRED_REGISTER = DeferredRegister.create(Shrink.MOD_ID, Registries.ATTRIBUTE);

    public static final RegistrySupplier<Attribute> SHRINK_ATTRIBUTE = ATTRIBUTES_DEFERRED_REGISTER.register("shrink_scale", () -> ShrinkAPI.SCALE_ATTRIBUTE);
    public static ConfigBuilder configBuilder;
    public static ShrinkConfig shrinkConfig;

    public static void init()
    {
        configBuilder = new ConfigBuilder(MOD_ID, Platform.getConfigFolder().resolve(MOD_ID + ".json5"), new ShrinkConfig());
        shrinkConfig = (ShrinkConfig) configBuilder.getConfigData();

        ATTRIBUTES_DEFERRED_REGISTER.register();

        ModItems.CREATIVE_MODE_TABS.register();
        ModItems.ITEMS.register();
        ModContainers.CONTAINERS.register();
        PacketHandler.init();
        if(Platform.getEnv() == EnvType.CLIENT)
        {
            ClientLifecycleEvent.CLIENT_SETUP.register(instance -> ShrinkClient.init());
        }

        EntitySizeEvents.SIZE.register((entity, pose, size, eyeHeight) ->
        {
            if(entity == null) return new EntitySizeEvents.UpdatedSize(size, eyeHeight, size, eyeHeight);

            if(entity instanceof LivingEntity livingEntity)
            {
                if (livingEntity.getAttributes() != null)
                {
                    if (livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE) == null) return new EntitySizeEvents.UpdatedSize(size, eyeHeight, size, eyeHeight);

                    boolean canShrink = ShrinkAPI.canEntityShrink(livingEntity);

                    if(canShrink)
                    {
                        float scale = (float) livingEntity.getAttributeValue(ShrinkAPI.SCALE_ATTRIBUTE);
                        if(!ShrinkAPI.isEntityShrunk(livingEntity))
                        {
                            return new EntitySizeEvents.UpdatedSize(entity.getDimensions(pose), entity.getEyeHeight(pose), entity.getDimensions(pose), entity.getEyeHeight(pose));
                        }
                        return new EntitySizeEvents.UpdatedSize(entity.getDimensions(pose), entity.getEyeHeight(pose), entity.getDimensions(pose).scale(scale), entity.getEyeHeight(pose) * scale);
                    }
                }
            }
            return new EntitySizeEvents.UpdatedSize(size, eyeHeight, size, eyeHeight);
        });

        //Force the players size to update on login
        PlayerEvent.PLAYER_JOIN.register(player -> player.setPose(Pose.CROUCHING));

        LifecycleEvent.SETUP.register(() ->
        {
            AttributeEvents.ADD.register(builder -> builder.add(ShrinkAPI.SCALE_ATTRIBUTE));

        });
    }
}
