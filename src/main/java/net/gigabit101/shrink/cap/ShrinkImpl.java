package net.gigabit101.shrink.cap;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.api.IShrinkProvider;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.PacketShrink;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ShrinkImpl
{
    public static void init()
    {
        CapabilityManager.INSTANCE.register(IShrinkProvider.class, new Capability.IStorage<IShrinkProvider>()
        {
            @Override
            public CompoundNBT writeNBT(Capability<IShrinkProvider> capability, IShrinkProvider instance, Direction side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IShrinkProvider> capability, IShrinkProvider instance, Direction side, INBT nbt)
            {
                if (nbt instanceof CompoundNBT)
                {
                    instance.deserializeNBT((CompoundNBT) nbt);
                }
            }
        }, () -> new DefaultImpl(null));
    }

    private static class DefaultImpl implements IShrinkProvider
    {
        private final LivingEntity livingEntity;
        private boolean isShrunk = false;
        private EntitySize defaultEntitySize;
        private float defaultEyeHeight;
        private float scale = 1F;

        private DefaultImpl(@Nullable LivingEntity livingEntity)
        {
            this.livingEntity = livingEntity;
            this.defaultEntitySize = livingEntity.size;
            this.defaultEyeHeight = livingEntity.eyeHeight;
        }

        @Override
        public boolean isShrunk()
        {
            return isShrunk;
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
            PacketHandler.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new PacketShrink(livingEntity.getEntityId(), serializeNBT()));
        }

        @Override
        public void shrink(@Nonnull LivingEntity livingEntity)
        {
            setShrunk(true);
            if(!(livingEntity instanceof PlayerEntity)) {
                if (defaultEntitySize == null) defaultEntitySize = livingEntity.size;
                if (defaultEyeHeight == 0F) defaultEyeHeight = livingEntity.eyeHeight;
            }
            livingEntity.setPose(livingEntity.getPose());
            livingEntity.recalculateSize();
            sync(livingEntity);
        }

        @Override
        public void deShrink(@Nonnull LivingEntity livingEntity)
        {
            setShrunk(false);
            livingEntity.recalculateSize();
            sync(livingEntity);
        }

        @Override
        public EntitySize defaultEntitySize()
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
        public CompoundNBT serializeNBT()
        {
            CompoundNBT properties = new CompoundNBT();
            properties.putBoolean("isshrunk", isShrunk);
            properties.putFloat("width", defaultEntitySize.width);
            properties.putFloat("height", defaultEntitySize.height);
            properties.putBoolean("fixed", defaultEntitySize.fixed);
            properties.putFloat("defaulteyeheight", defaultEyeHeight);
            properties.putFloat("scale", scale);
            return properties;
        }

        @Override
        public void deserializeNBT(CompoundNBT properties)
        {
            isShrunk = properties.getBoolean("isshrunk");
            defaultEntitySize = new EntitySize(properties.getFloat("width"), properties.getFloat("height"), properties.getBoolean("fixed"));
            defaultEyeHeight = properties.getFloat("defaulteyeheight");
            scale = properties.getFloat("scale");
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT>
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
        public CompoundNBT serializeNBT()
        {
            return impl.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            impl.deserializeNBT(nbt);
        }
    }
}
