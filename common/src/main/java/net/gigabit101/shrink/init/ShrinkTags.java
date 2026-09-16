package net.gigabit101.shrink.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ShrinkTags
{
    public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = TagKey.create(
            Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", "capturing_not_supported"));

    private ShrinkTags() {}
}
