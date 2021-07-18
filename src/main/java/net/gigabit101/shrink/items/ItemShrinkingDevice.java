package net.gigabit101.shrink.items;

import net.gigabit101.shrink.ShrinkContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.cap.EnergyStorageItemImpl;
import net.gigabit101.shrink.client.KeyBindings;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class ItemShrinkingDevice extends Item implements INamedContainerProvider
{
    public ItemShrinkingDevice(Properties properties)
    {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if(!player.isCrouching())
        {
            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
               if(!iShrinkProvider.isShrunk())
               {
                   if (!world.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, this);
               }
               else
               {
                   if (!world.isClientSide()) player.displayClientMessage(new StringTextComponent("Can't open while shrunk"), false);
               }
            });
        }

        if (!world.isClientSide() && player.isCrouching())
        {
            player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if (!iShrinkProvider.isShrunk() && canUse(stack, player))
                {
                    iShrinkProvider.shrink((ServerPlayerEntity) player);
                }
                else if(iShrinkProvider.isShrunk() && canUse(stack, player))
                {
                    iShrinkProvider.deShrink((ServerPlayerEntity) player);
                }
                else if(!canUse(stack, player) && ShrinkConfig.POWER_REQUIREMENT.get())
                {
                    player.displayClientMessage(new StringTextComponent("Not enough power in device"), false);
                }
            });
        }

        if(world.isClientSide() && player.isCrouching())
        {
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 5F, 0F);
            spawnParticle(world, player.getX(), player.getY(), player.getZ() -1, world.random);
        }
        return new ActionResult<>(ActionResultType.PASS, player.getItemInHand(hand));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity)
    {
        AtomicReference<Float> scale = new AtomicReference<>(0.1F);
        player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> scale.set(iShrinkProvider.scale()));

        if(entity instanceof LivingEntity && !entity.level.isClientSide)
        {
            entity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                iShrinkProvider.setScale(scale.get());

                if (!iShrinkProvider.isShrunk() && canUse(stack, player))
                {
                    iShrinkProvider.shrink((LivingEntity) entity);
                }
                else if (iShrinkProvider.isShrunk() && canUse(stack, player))
                {
                    iShrinkProvider.deShrink((LivingEntity) entity);
                }
            });
            return true;
        }
        return false;
    }

    public void spawnParticle(World worldIn, double posX, double posY, double posZ, Random rand)
    {
        for (int i = 0; i < 16; ++i)
        {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = posX + 0.5D + 0.25D * (double) j;
            double d1 = ((float) posY + rand.nextFloat());
            double d2 = posZ + 0.5D + 0.25D * (double) k;
            double d3 = (rand.nextFloat() * (float) j);
            double d4 = (rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = (rand.nextFloat() * (float) k);
            worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(KeyBindings.shrink != null) tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Sneak-Click " + TextFormatting.WHITE + "or press " + TextFormatting.DARK_PURPLE + KeyBindings.shrink.getKey().getDisplayName().getString() + TextFormatting.WHITE + " to active"));
        LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
        if (optional.isPresent())
        {
            IEnergyStorage energyStorage = optional.orElseThrow(IllegalStateException::new);
            tooltip.add(new StringTextComponent(energyStorage.getEnergyStored() + " FE / " + energyStorage.getMaxEnergyStored() + " FE"));
        }
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent(this.getOrCreateDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player)
    {
        return new ShrinkContainer(id, inv);
    }
}
