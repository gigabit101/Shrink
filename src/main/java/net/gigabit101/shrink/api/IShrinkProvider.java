package net.gigabit101.shrink.api;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public interface IShrinkProvider extends INBTSerializable<CompoundNBT>
{
    boolean isShrunk();

    void setShrunk(boolean set);

    void sync(@Nonnull ServerPlayerEntity player);

    void shrink(@Nonnull ServerPlayerEntity player);

    void deShrink(@Nonnull ServerPlayerEntity player);
}
