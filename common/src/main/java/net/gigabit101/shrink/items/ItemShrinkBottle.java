package net.gigabit101.shrink.items;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.init.ModItems;
import net.gigabit101.shrink.init.ShrinkComponentTypes;
import net.gigabit101.shrink.items.components.ShrinkComponentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemShrinkBottle extends Item
{
    public ItemShrinkBottle(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand)
    {
        InteractionResult result = onInteractWithEntity(stack, player, interactionTarget, usedHand);
        if (result.consumesAction()) return result;
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    public static InteractionResult onInteractWithEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand)
    {
        if (ShrinkAPI.canCaptureEntity(livingEntity))
        {
            ItemStack output = ItemShrinkBottle.setContainedEntity(new ItemStack(ModItems.SHRINK_BOTTLE), livingEntity);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, output, true));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context)
    {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());

        if (!containsEntity(stack)) return super.useOn(context);
        if (stack.isEmpty()) return super.useOn(context);
        if (!(world instanceof ServerLevel serverWorld)) return InteractionResult.SUCCESS;

        Optional<Entity> maybeEntity = ShrinkComponentUtils.getEntityType(stack).map(type -> type.spawn(serverWorld, stack, player, blockPos, EntitySpawnReason.MOB_SUMMONED, false, false));
        if (maybeEntity.isEmpty()) return InteractionResult.FAIL;
        if (player != null)
        {
            ItemStack handStack = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE), true);
            player.setItemInHand(hand, handStack);
        }
        else
        {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    public static ItemStack setContainedEntity(ItemStack emptyBottle, LivingEntity entity)
    {
        if (containsEntity(emptyBottle)) return emptyBottle;
        if (entity.level().isClientSide()) return emptyBottle;
        if (entity instanceof Player || !entity.isAlive() || !entity.getType().canSerialize()) return emptyBottle;

        TagValueOutput tagValueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
        entity.save(tagValueOutput);
        ShrinkComponentUtils.IGNORED_ENTITY_TAGS.forEach(tagValueOutput::discard);
        CompoundTag entityNbt = tagValueOutput.buildResult();

        ItemStack mobBottle = new ItemStack(ModItems.SHRINK_BOTTLE.get(), 1);
        mobBottle.set(DataComponents.ENTITY_DATA, CustomData.of(entityNbt));

        if (entity.hasCustomName() && entity.isCustomNameVisible())
        {
            mobBottle.set(ShrinkComponentTypes.ENTITY_NAME.get(), entity.getCustomName());
        }
        else
        {
            mobBottle.remove(ShrinkComponentTypes.ENTITY_NAME.get());
        }

        entity.remove(Entity.RemovalReason.KILLED);

        return mobBottle;
    }

    public static boolean containsEntity(ItemStack stack)
    {
        return stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY).contains("id");
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemStack)
    {
        return containsEntity(itemStack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag)
    {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        if (containsEntity(stack))
        {
            Component entityTypeName = ShrinkComponentUtils.getEntityType(stack).map(EntityType::getDescription).orElseGet(Component::empty);
            Component name = stack.get(ShrinkComponentTypes.ENTITY_NAME.get());
            if (name != null)
            {
                tooltipAdder.accept(Component.translatable("item.mob_bottle.tooltip_with_name", name, entityTypeName));
            }
            else
            {
                tooltipAdder.accept(Component.translatable("item.mob_bottle.tooltip", entityTypeName));
            }
        }
        else
        {
            tooltipAdder.accept(Component.translatable("item.mob_bottle.tooltip_empty"));
        }
    }
}
