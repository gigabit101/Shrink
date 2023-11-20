package net.gigabit101.shrink.items;

import dev.architectury.registry.menu.MenuRegistry;
import net.gigabit101.shrink.ShrinkingDeviceContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemShrinkDevice extends Item implements MenuProvider
{
    public static final UUID SHRINKING_DEVICE_ID = UUID.fromString("e4388c41-4cf8-4631-98b4-b26eeaedcbdc");
    public ItemShrinkDevice(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand)
    {
        if (player.getAttributes() != null && player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE) != null)
        {
            if (!level.isClientSide())
            {
                ItemStack stack = player.getItemInHand(interactionHand);
                AttributeInstance shrink = player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE);
                if (shrink == null)
                {
                    player.displayClientMessage(Component.literal("Shrink Attribute Missing???"), false);
                    return InteractionResultHolder.fail(stack);
                }

                if (player.isShiftKeyDown())
                {
                    if(!ShrinkAPI.isEntityShrunk(player))
                    {
                        if (shrink.getModifier(SHRINKING_DEVICE_ID) == null)
                        {
                            shrink.addPermanentModifier(createModifier(-1.0D));
                            return InteractionResultHolder.success(stack);
                        }
                    }
                    else
                    {
                        shrink.removePermanentModifier(SHRINKING_DEVICE_ID);
                        return InteractionResultHolder.success(stack);
                    }
                }
                else
                {
                    MenuRegistry.openMenu((ServerPlayer) player, this);
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return super.use(level, player, interactionHand);
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
}
