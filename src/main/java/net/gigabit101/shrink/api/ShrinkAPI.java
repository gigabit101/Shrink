package net.gigabit101.shrink.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ShrinkAPI
{
    public static final Capability<IShrinkProvider> SHRINK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final TagKey<EntityType<?>> SHRINK_DENYLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("shrink", "no_shrink"));
}
