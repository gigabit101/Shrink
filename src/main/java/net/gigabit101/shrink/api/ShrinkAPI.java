package net.gigabit101.shrink.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ShrinkAPI
{
    @CapabilityInject(IShrinkProvider.class)
    public static Capability<IShrinkProvider> SHRINK_CAPABILITY = null;
}
