package net.gigabit101.shrink.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public interface IShrinkProvider extends INBTSerializable<CompoundTag>
{
    boolean isShrunk();

    boolean isShrinking();

    void setShrinking(boolean value);

    void setShrunk(boolean set);

    void sync(@Nonnull LivingEntity livingEntity);

    void shrink(@Nonnull LivingEntity livingEntity);

    void deShrink(@Nonnull LivingEntity livingEntity);

    EntityDimensions defaultEntitySize();

    float defaultEyeHeight();

    float scale();

    void setScale(float scale);
}
