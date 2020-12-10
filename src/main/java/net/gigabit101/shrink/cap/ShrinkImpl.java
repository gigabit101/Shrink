package net.gigabit101.shrink.cap;

import com.google.common.collect.ImmutableMap;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.api.IShrinkProvider;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.events.PlayerEvents;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.ShrinkPacket;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public final class ShrinkImpl
{
    public static final float defaultEyeHeight = 1.62F;

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
        @Nullable
        private final LivingEntity livingEntity;
        private boolean isShrunk = false;
        private boolean isShrinking = false;
        private EntitySize defaultEntitySize;
        private float defaultEyeHeight;

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
        public boolean isShrinking()
        {
            return this.isShrinking;
        }

        @Override
        public void setShrinking(boolean shrinking)
        {
            this.isShrinking = shrinking;
        }

        @Override
        public void sync(@Nonnull LivingEntity livingEntity)
        {
            PacketHandler.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new ShrinkPacket(livingEntity.getEntityId(), serializeNBT()));
        }

        @Override
        public void shrink(@Nonnull LivingEntity livingEntity)
        {
            setShrunk(true);
            setShrinking(true);
            defaultEntitySize = livingEntity.size;
            defaultEyeHeight = livingEntity.eyeHeight;
            livingEntity.recalculateSize();
            sync(livingEntity);
        }

        @Override
        public void deShrink(@Nonnull LivingEntity livingEntity)
        {
            setShrunk(false);
            setShrinking(false);
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
        public CompoundNBT serializeNBT()
        {
            CompoundNBT properties = new CompoundNBT();
            properties.putBoolean("isshrunk", isShrunk);
            properties.putBoolean("isshrinking", isShrinking);
            properties.putFloat("width", defaultEntitySize.width);
            properties.putFloat("height", defaultEntitySize.height);
            properties.putBoolean("fixed", defaultEntitySize.fixed);
            properties.putFloat("defaulteyeheight", defaultEyeHeight);
            return properties;
        }

        @Override
        public void deserializeNBT(CompoundNBT properties)
        {
            isShrunk = properties.getBoolean("isshrunk");
            isShrinking = properties.getBoolean("isshrinking");
            defaultEntitySize = new EntitySize(properties.getFloat("width"), properties.getFloat("height"), properties.getBoolean("fixed"));
            defaultEyeHeight = properties.getFloat("defaulteyeheight");
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
