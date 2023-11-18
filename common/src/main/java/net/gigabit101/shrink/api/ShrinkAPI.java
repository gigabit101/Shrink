package net.gigabit101.shrink.api;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ShrinkAPI
{
    public static final Attribute SCALE_ATTRIBUTE = new RangedAttribute("shrink_scale", 1.0D, 0.25D, 10).setSyncable(true);
}
