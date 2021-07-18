package net.gigabit101.shrink.items;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.cap.EnergyStorageItemImpl;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemShrinkRay extends Item
{
    public ItemShrinkRay(Properties properties)
    {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
    {
        playerEntity.startUsingItem(hand);
        return super.use(world, playerEntity, hand);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand)
    {
        if(livingEntity != null && !playerEntity.level.isClientSide())
        {
            AtomicReference<Float> scale = new AtomicReference<>(0.1F);
            playerEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> scale.set(iShrinkProvider.scale()));

            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if(canUse(itemStack, playerEntity))
                {
                    iShrinkProvider.setScale(scale.get());

                    if (iShrinkProvider.isShrunk())
                    {
                        iShrinkProvider.deShrink(livingEntity);
                    }
                    else
                    {
                        iShrinkProvider.shrink(livingEntity);
                    }
                }
            });
        }
        return super.interactLivingEntity(itemStack, playerEntity, livingEntity, hand);
    }

    public boolean canUse(ItemStack stack, PlayerEntity playerEntity)
    {
        if(!ShrinkConfig.POWER_REQUIREMENT.get()) return true;
        if(playerEntity.isCreative()) return true;

        LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
        if(optional.isPresent())
        {
            IEnergyStorage energyStorage = optional.orElseThrow(IllegalStateException::new);
            if(energyStorage.getEnergyStored() >= ShrinkConfig.POWER_COST.get())
            {
                energyStorage.extractEnergy(ShrinkConfig.POWER_COST.get(), false);
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new ICapabilityProvider()
        {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
            {
                if (cap == CapabilityEnergy.ENERGY) return LazyOptional.of(() -> new EnergyStorageItemImpl(stack, ShrinkConfig.POWER_CAPACITY.get(), ShrinkConfig.POWER_CAPACITY.get(), ShrinkConfig.POWER_CAPACITY.get())).cast();
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return ShrinkConfig.POWER_REQUIREMENT.get();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1 - getChargeRatio(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return MathHelper.hsvToRgb((1 + getChargeRatio(stack)) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
        if (optional.isPresent())
        {
            IEnergyStorage energyStorage = optional.orElseThrow(IllegalStateException::new);
            tooltip.add(new StringTextComponent(energyStorage.getEnergyStored() + " FE / " + energyStorage.getMaxEnergyStored() + " FE"));
        }
    }

    public static float getChargeRatio(ItemStack stack)
    {
        LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
        if (optional.isPresent())
        {
            IEnergyStorage energyStorage = optional.orElseThrow(IllegalStateException::new);
            return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
        }
        return 0;
    }
}
