package net.gigabit101.shrink.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModBottle extends Item
{
    public ItemModBottle(Properties properties)
    {
        super(properties.stacksTo(1));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        World worldIn = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (player.level.isClientSide) return ActionResultType.FAIL;
        if (!containsEntity(stack)) return ActionResultType.FAIL;
        Entity entity = getEntityFromItemStack(stack, worldIn);
        BlockPos blockPos = pos.relative(facing);
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        player.setItemInHand(context.getHand(), new ItemStack(Items.GLASS_BOTTLE, 1));
        stack.setTag(new CompoundNBT());
        worldIn.addFreshEntity(entity);
        return ActionResultType.SUCCESS;
    }

    public static ItemStack setContainedEntity(ItemStack stack, LivingEntity entity)
    {
        if (containsEntity(stack)) return stack;
        if (entity.level.isClientSide) return stack;
        if (entity instanceof PlayerEntity || !entity.isAlive()) return stack;

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.save(nbt);
        ItemStack mobBottle = new ItemStack(ShrinkItems.MOB_BOTTLE.get(), 1);
        mobBottle.setTag(nbt);
        entity.remove(true);

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
    public Entity getEntityFromItemStack(ItemStack stack, World world)
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (containsEntity(stack))
        {
            tooltip.add(new StringTextComponent("Contains : " + getEntityID(stack)));
        }
        else
        {
            tooltip.add(new StringTextComponent("Right-click on a shrunken entity with a glass bottle to capture"));
        }
    }
}
