package net.gigabit101.shrink.cap;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.api.IShrinkProvider;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.PacketShrink;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ShrinkImpl
{
    public static void init(RegisterCapabilitiesEvent event)
    {
        event.register(IShrinkProvider.class);
    }

    private static class DefaultImpl implements IShrinkProvider
    {
        private final LivingEntity livingEntity;
        private boolean isShrunk = false;
        private EntityDimensions defaultEntitySize;
        private float defaultEyeHeight;
        private float scale = 1F;
        private boolean isShrinking = false;

        private DefaultImpl(@Nonnull LivingEntity livingEntity)
        {
            this.livingEntity = livingEntity;
        }

        @Override
        public boolean isShrunk()
        {
            return isShrunk;
        }

        @Override
        public boolean isShrinking()
        {
            return isShrinking;
        }

        @Override
        public void setShrinking(boolean value)
        {
            isShrinking = value;
        }

        @Override
        public void setShrunk(boolean isShrunk)
        {
            if(this.isShrunk != isShrunk)
            {
                this.isShrunk = isShrunk;
                sync(livingEntity);
            }
        }

        @Override
        public void sync(@Nonnull LivingEntity livingEntity)
        {
            PacketHandler.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new PacketShrink(livingEntity.getId(), serializeNBT()));
        }

        @Override
        public void shrink(@Nonnull LivingEntity livingEntity)
        {
            setShrinking(true);
            livingEntity.refreshDimensions();
            setShrunk(true);
            sync(livingEntity);
        }

        @Override
        public void deShrink(@Nonnull LivingEntity livingEntity)
        {
            setShrunk(false);
            livingEntity.refreshDimensions();
            sync(livingEntity);
        }

        @Override
        public EntityDimensions defaultEntitySize()
        {
            return defaultEntitySize;
        }

        @Override
        public float defaultEyeHeight()
        {
            return defaultEyeHeight;
        }

        @Override
        public float scale()
        {
            return scale;
        }

        @Override
        public void setScale(float scale)
        {
            if(this.scale != scale)
            {
                this.scale = scale;
                sync(livingEntity);
            }
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag properties = new CompoundTag();
            properties.putBoolean("isshrunk", isShrunk);
            if(defaultEntitySize != null) {
                properties.putFloat("width", defaultEntitySize.width);
                properties.putFloat("height", defaultEntitySize.height);
                properties.putBoolean("fixed", defaultEntitySize.fixed);
            }
            properties.putFloat("defaulteyeheight", defaultEyeHeight);
            properties.putFloat("scale", scale);
            properties.putBoolean("isshrinking", isShrinking);


            return properties;
        }

        @Override
        public void deserializeNBT(CompoundTag properties)
        {
            isShrunk = properties.getBoolean("isshrunk");
            defaultEntitySize = new EntityDimensions(properties.getFloat("width"), properties.getFloat("height"), properties.getBoolean("fixed"));
            defaultEyeHeight = properties.getFloat("defaulteyeheight");
            scale = properties.getFloat("scale");
            isShrinking = properties.getBoolean("isshrinking");
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag>
    {
        public static final ResourceLocation NAME = new ResourceLocation(Shrink.MOD_ID, "shrunk");

        private final DefaultImpl impl;
        private final LazyOptional<IShrinkProvider> cap;

        public Provider(LivingEntity livingEntity)
        {
            impl = new DefaultImpl(livingEntity);
            cap = LazyOptional.of(() -> impl);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing)
        {
            if (capability == ShrinkAPI.SHRINK_CAPABILITY)
            {
                return cap.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability)
        {
            if (capability == ShrinkAPI.SHRINK_CAPABILITY)
            {
                return cap.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return impl.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            impl.deserializeNBT(nbt);
        }
    }
}
