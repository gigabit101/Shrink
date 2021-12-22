package net.gigabit101.shrink.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ShrinkAPI
{
    public static final Capability<IShrinkProvider> SHRINK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
