package net.gigabit101.shrink.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

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

    public static boolean canCaptureEntity(LivingEntity livingEntity)
    {
        //TODO
        return isEntityShrunk(livingEntity); //&& !livingEntity.getType().is(CAPTURING_NOT_SUPPORTED);
    }
}
