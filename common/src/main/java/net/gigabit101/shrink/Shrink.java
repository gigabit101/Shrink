package net.gigabit101.shrink;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.init.ModItems;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.gigabit101.shrink.polylib.EntitySizeEvents;
import net.gigabit101.shrink.polylib.LivingEntityRenderEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public class Shrink
{
    public static final String MOD_ID = "shrink";

    public static final DeferredRegister<Attribute> ATTRIBUTES_DEFERRED_REGISTER = DeferredRegister.create(Shrink.MOD_ID, Registries.ATTRIBUTE);

    public static final RegistrySupplier<Attribute> SHRINK_ATTRIBUTE = ATTRIBUTES_DEFERRED_REGISTER.register("shrink_scale", () -> ShrinkAPI.SCALE_ATTRIBUTE);


    public static void init()
    {
        ATTRIBUTES_DEFERRED_REGISTER.register();

        ModItems.CREATIVE_MODE_TABS.register();
        ModItems.ITEMS.register();

        if(Platform.getEnv() == EnvType.CLIENT)
        {
            LivingEntityRenderEvents.PRE.register((livingEntity, f, g, poseStack, multiBufferSource, i) ->
            {
                boolean shrunk = livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue() != 1.0D;
                if(shrunk)
                {
                    poseStack.pushPose();
                    float scale = (float) livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue();
                    poseStack.scale(scale, scale, scale);
                }
            });

            LivingEntityRenderEvents.POST.register((livingEntity, f, g, poseStack, multiBufferSource, i) ->
            {
                boolean shrunk = livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).hasModifier(ItemShrinkDevice.createModifier(1.0F));
                if(shrunk)
                {
                    poseStack.popPose();
                }
            });
        }

        EntitySizeEvents.SIZE.register((entity, pose, size, eyeHeight) ->
        {
            if(entity == null) return new EntitySizeEvents.UpdatedSize(size, eyeHeight, size, eyeHeight);

            if(entity instanceof Player livingEntity)
            {
                if (livingEntity.getAttributes() != null)
                {
                    if (livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE) == null) return new EntitySizeEvents.UpdatedSize(size, eyeHeight, size, eyeHeight);

                    boolean canShrink = livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue() != 1.0D;

                    System.out.println("SIDE: " + (entity.level().isClientSide() ? "CLIENT " : " SERVER ")  + "canShrink:" + canShrink + " " + livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue());

                    if(canShrink)
                    {
                        float scale = (float) livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue();
                        return new EntitySizeEvents.UpdatedSize(entity.getDimensions(pose), entity.getEyeHeight(pose), entity.getDimensions(pose).scale(scale), entity.getEyeHeight(pose) * scale);
                    }
                }
            }
            return new EntitySizeEvents.UpdatedSize(size, eyeHeight, size, eyeHeight);
        });

    }
}
