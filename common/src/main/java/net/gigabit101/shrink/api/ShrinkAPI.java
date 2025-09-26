package net.gigabit101.shrink.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;

import static net.gigabit101.shrink.init.ModTagKeys.CAPTURING_NOT_SUPPORTED;

public class ShrinkAPI
{
    public static boolean canEntityShrink(LivingEntity livingEntity)
    {
        if(livingEntity == null) return false;
        return livingEntity.getAttribute(Attributes.SCALE) != null;
    }

    public static boolean isEntityShrunk(LivingEntity livingEntity)
    {
        if(livingEntity == null) return false;
        if(livingEntity.getAttributes() == null) return false;
        if(livingEntity.getAttribute(Attributes.SCALE) == null) return false;
        return !livingEntity.getAttribute(Attributes.SCALE).getModifiers().isEmpty();
    }

    public static boolean isEntityShrunk(LivingEntity livingEntity, ResourceLocation sourceID)
    {
        if(livingEntity == null) return false;
        if(livingEntity.getAttributes() == null) return false;
        if(livingEntity.getAttribute(Attributes.SCALE) == null) return false;
        return livingEntity.getAttribute(Attributes.SCALE).hasModifier(sourceID);
    }

    public static boolean canCaptureEntity(LivingEntity livingEntity)
    {
        return isEntityShrunk(livingEntity) && !livingEntity.getType().is(CAPTURING_NOT_SUPPORTED);
    }
}
