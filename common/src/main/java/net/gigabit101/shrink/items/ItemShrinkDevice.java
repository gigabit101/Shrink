package net.gigabit101.shrink.items;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemShrinkDevice extends Item
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
            double scale = player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE).getValue();

            if (!level.isClientSide())
            {
                ItemStack stack = player.getItemInHand(interactionHand);
                AttributeInstance shrink = player.getAttribute(ShrinkAPI.SCALE_ATTRIBUTE);
                if (shrink == null)
                {
                    player.displayClientMessage(Component.literal("No Attribute"), false);
                    return InteractionResultHolder.fail(stack);
                }

                if (player.isShiftKeyDown())
                {
                    if (shrink.getModifier(SHRINKING_DEVICE_ID) == null)
                    {
                        shrink.addPermanentModifier(createModifier(-1.0D));
                    }
                }
                else
                {
                    shrink.removePermanentModifier(SHRINKING_DEVICE_ID);
                }
                player.displayClientMessage(Component.literal("SERVER: Scale: " + scale), false);
            }
            player.displayClientMessage(Component.literal("CLIENT: Scale: " + scale), false);
        }
        return super.use(level, player, interactionHand);
    }

    public static AttributeModifier createModifier(double value)
    {
        return new AttributeModifier(SHRINKING_DEVICE_ID, "shrinking_device", value, AttributeModifier.Operation.ADDITION);
    }
}
