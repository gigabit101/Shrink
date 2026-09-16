package net.gigabit101.shrink.api;

import net.gigabit101.shrink.init.ShrinkTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

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
        return livingEntity != null
                && !(livingEntity instanceof Player)
                && livingEntity.isAlive()
                && !livingEntity.isRemoved()
                && livingEntity.getScale() < 1.0F
                && livingEntity.getType().canSerialize()
                && !livingEntity.getType().onlyOpCanSetNbt()
                && !livingEntity.isPassenger()
                && !livingEntity.isVehicle()
                && !livingEntity.typeHolder().is(ShrinkTags.CAPTURING_NOT_SUPPORTED);
    }
}
