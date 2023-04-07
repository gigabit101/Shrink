package net.gigabit101.shrink.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ShrinkEvents
{
    public static class PlayerShrinkEvent extends LivingEvent
    {
        public PlayerShrinkEvent(LivingEntity livingEntity)
        {
            super(livingEntity);
        }
    }

    public static class PlayerDeShrinkEvent extends LivingEvent
    {
        public PlayerDeShrinkEvent(LivingEntity livingEntity)
        {
            super(livingEntity);
        }
    }
}
