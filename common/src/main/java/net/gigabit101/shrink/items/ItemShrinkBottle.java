package net.gigabit101.shrink.items;

import net.gigabit101.shrink.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemShrinkBottle extends Item
{
    public ItemShrinkBottle()
    {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context)
    {

        if (context.getPlayer().level().isClientSide) return InteractionResult.FAIL;
        if (!containsEntity(context.getItemInHand())) return InteractionResult.FAIL;

        Entity entity = getEntityFromItemStack(context.getItemInHand(), context.getLevel());
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.GLASS_BOTTLE, 1));
        context.getItemInHand().setTag(new CompoundTag());
        context.getLevel().addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }

    public static ItemStack setContainedEntity(ItemStack stack, LivingEntity entity)
    {
        if (containsEntity(stack)) return stack;
        if (entity.level().isClientSide) return stack;
        if (entity instanceof Player || !entity.isAlive()) return stack;

        CompoundTag nbt = new CompoundTag();
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.save(nbt);
        ItemStack mobBottle = new ItemStack(ModItems.SHRINK_BOTTLE.get(), 1);
        mobBottle.setTag(nbt);
        entity.remove(Entity.RemovalReason.KILLED);

        return mobBottle;
    }

    public static boolean containsEntity(ItemStack stack)
    {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains("entity");
    }

    public String getEntityID(ItemStack stack)
    {
        return stack.getTag().getString("entity");
    }

    @Nullable
    public Entity getEntityFromItemStack(ItemStack stack, Level world)
    {
        EntityType<?> type = EntityType.byString(stack.getTag().getString("entity")).orElse(null);
        if (type != null)
        {
            Entity entity = type.create(world);
            entity.load(stack.getTag());
            return entity;
        }
        return null;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemStack)
    {
        return containsEntity(itemStack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (containsEntity(stack))
        {
            tooltip.add(Component.literal("Contains : " + getEntityID(stack)));
        }
        else
        {
            tooltip.add(Component.translatable("item.mob_bottle.tooltip_empty"));
        }
    }
}
