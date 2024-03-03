package net.gigabit101.shrink.items;

import dev.architectury.registry.menu.MenuRegistry;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.creeperhost.polylib.inventory.power.PolyItemEnergyStorage;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.ShrinkingDeviceContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.packets.PacketEntitySync;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemShrinkDevice extends Item implements MenuProvider, PolyEnergyItem
{
    public static final UUID SHRINKING_DEVICE_ID = UUID.fromString("e4388c41-4cf8-4631-98b4-b26eeaedcbdc");

    public ItemShrinkDevice(Properties properties)
    {
        super(properties);
    }

    public void writeScale(ItemStack stack, double scale)
    {
        stack.getOrCreateTag().putDouble("scale", scale);
    }

    public double getScale(ItemStack stack)
    {
        if(stack.getTag() == null || !stack.getTag().contains("scale")) writeScale(stack, 1.0D);

        return stack.getTag().getDouble("scale");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand)
    {
        if (player.getAttributes() != null && player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE) != null)
        {
            if (!level.isClientSide())
            {
                ItemStack stack = player.getItemInHand(interactionHand);

                if (player.isShiftKeyDown())
                {
                    if(!hasPower(player, stack))
                    {
                        player.displayClientMessage(Component.translatable("shrink.message.power"), false);
                        return InteractionResultHolder.fail(stack);
                    }
                    if(!ShrinkAPI.isEntityShrunk(player))
                    {
                        player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).addPermanentModifier(createModifier(getScale(stack)));
                        usePower(player, stack);
                        return InteractionResultHolder.success(stack);
                    }
                    else
                    {
                        player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).removePermanentModifier(SHRINKING_DEVICE_ID);
                        usePower(player, stack);
                        return InteractionResultHolder.success(stack);
                    }
                }
                else
                {
                    if(!ShrinkAPI.isEntityShrunk(player))
                    {
                        MenuRegistry.openExtendedMenu((ServerPlayer) player, this, friendlyByteBuf -> friendlyByteBuf.writeDouble(0));
                    }
                    else
                    {
                        player.displayClientMessage(Component.translatable("shrink.message.already_shrunk"), false);
                    }
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        else {
            player.displayClientMessage(Component.translatable("shrink.message.missing"), false);
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand)
    {
        if(!livingEntity.level().isClientSide() && livingEntity != null)
        {
            if(ShrinkAPI.canEntityShrink(livingEntity))
            {
                if (hasPower(player, itemStack))
                {
                    List<ServerPlayer> players = livingEntity.level().getServer().getPlayerList().getPlayers();

                    if(!ShrinkAPI.isEntityShrunk(livingEntity))
                    {
                        livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).addPermanentModifier(ItemShrinkDevice.createModifier(getScale(itemStack)));
                        PacketHandler.HANDLER.sendToPlayers(players, new PacketEntitySync(getScale(itemStack), livingEntity.getId(), true));
                        usePower(player, itemStack);
                        livingEntity.refreshDimensions();
                        return InteractionResult.SUCCESS;
                    }
                    else
                    {
                        livingEntity.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).removeModifier(ItemShrinkDevice.SHRINKING_DEVICE_ID);
                        PacketHandler.HANDLER.sendToPlayers(players, new PacketEntitySync(getScale(itemStack), livingEntity.getId(), false));
                        usePower(player, itemStack);
                        livingEntity.refreshDimensions();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public boolean hasPower(Player player, ItemStack stack)
    {
        if(player.isCreative()) return true;
        return getEnergyStorage(stack).getEnergyStored() >= Shrink.shrinkConfig.shrinkingDeviceCost;
    }

    public void usePower(Player player, ItemStack stack)
    {
        if(!player.isCreative())
        {
            getEnergyStorage(stack).extractEnergy(Shrink.shrinkConfig.shrinkingDeviceCost, false);
        }
    }

    public static AttributeModifier createModifier(double value)
    {
        return new AttributeModifier(SHRINKING_DEVICE_ID, "shrinking_device", value, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.translatable("item.shrink.shrinking_device");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new ShrinkingDeviceContainer(id, inventory, null);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, level, list, tooltipFlag);
        list.add(Component.literal(getEnergyStorage(stack).getEnergyStored() + " / " + getEnergyStorage(stack).getMaxEnergyStored() + " RF"));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack itemStack)
    {
        return (getEnergyStorage(itemStack).getEnergyStored() < getEnergyStorage(itemStack).getMaxEnergyStored());
    }

    @Override
    public int getBarWidth(@NotNull ItemStack itemStack)
    {
        return (int) Math.min(13 * getEnergyStorage(itemStack).getEnergyStored() / getEnergyStorage(itemStack).getMaxEnergyStored(), 13);
    }

    @Override
    public IPolyEnergyStorage getEnergyStorage(ItemStack stack)
    {
        return new PolyItemEnergyStorage(stack, Shrink.shrinkConfig.shrinkingDeviceCapacity, 64);
    }
}
