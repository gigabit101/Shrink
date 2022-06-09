package net.gigabit101.shrink.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import javax.annotation.Nullable;
import java.util.List;

public class ItemModBottle extends Item
{
    public ItemModBottle(Item.Properties properties)
    {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Level worldIn = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (player.level.isClientSide) return InteractionResult.FAIL;
        if (!containsEntity(stack)) return InteractionResult.FAIL;
        Entity entity = getEntityFromItemStack(stack, worldIn);
        BlockPos blockPos = pos.relative(facing);
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        player.setItemInHand(context.getHand(), new ItemStack(Items.GLASS_BOTTLE, 1));
        stack.setTag(new CompoundTag());
        worldIn.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }

    public static ItemStack setContainedEntity(ItemStack stack, LivingEntity entity)
    {
        if (containsEntity(stack)) return stack;
        if (entity.level.isClientSide) return stack;
        if (entity instanceof Player || !entity.isAlive()) return stack;

        CompoundTag nbt = new CompoundTag();
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.save(nbt);
        ItemStack mobBottle = new ItemStack(ShrinkItems.MOB_BOTTLE.get(), 1);
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
        EntityType type = EntityType.byString(stack.getTag().getString("entity")).orElse(null);
        if (type != null)
        {
            Entity entity = type.create(world);
            entity.load(stack.getTag());
            return entity;
        }
        return null;
    }

    @Override
    public boolean isFoil(ItemStack itemStack)
    {
        return containsEntity(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (containsEntity(stack))
        {
            tooltip.add(Component.literal("Contains : " + getEntityID(stack)));
        }
        else
        {
            tooltip.add(Component.literal("Right-click on a shrunken entity with a glass bottle to capture"));
        }
    }
}
