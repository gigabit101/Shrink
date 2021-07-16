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
        super(properties.maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        World worldIn = context.getWorld();
        ItemStack stack = context.getItem();

        if (player.getEntityWorld().isRemote) return ActionResultType.FAIL;
        if (!containsEntity(stack)) return ActionResultType.FAIL;
        Entity entity = getEntityFromItemStack(stack, worldIn);
        BlockPos blockPos = pos.offset(facing);
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        player.setHeldItem(context.getHand(), new ItemStack(Items.GLASS_BOTTLE, 1));
        stack.setTag(new CompoundNBT());
        worldIn.addEntity(entity);
        return ActionResultType.SUCCESS;
    }

    public static ItemStack setContainedEntity(ItemStack stack, LivingEntity entity)
    {
        if (containsEntity(stack)) return stack;
        if (entity.getEntityWorld().isRemote) return stack;
        if (entity instanceof PlayerEntity || !entity.isNonBoss() || !entity.isAlive()) return stack;

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.writeWithoutTypeId(nbt);
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
        EntityType type = EntityType.byKey(stack.getTag().getString("entity")).orElse(null);
        if (type != null)
        {
            Entity entity = type.create(world);
            entity.read(stack.getTag());
            return entity;
        }
        return null;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return containsEntity(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
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
