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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemShrinkBottle extends Item
{
    public ItemShrinkBottle()
    {
        super(new Properties().stacksTo(1));
    }

    // if a player somehow gets an empty mob bottle, let it work like a regular bottle for capture.
    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        InteractionResult result = onInteractWithEntity(stack,player,interactionTarget,usedHand);
        if (result.consumesAction()) return result;
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    // Handles usages with items that should generate a bottled mob
    public static InteractionResult onInteractWithEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if(ShrinkAPI.canCaptureEntity(livingEntity))
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

        if (!containsEntity(stack)) return super.useOn(context); // if the bottle doesnt have an entity in it
        if (stack.isEmpty()) return super.useOn(context); // if something called this method with an empty stack
        if (!(world instanceof ServerLevel serverWorld)) return InteractionResult.SUCCESS; // clientside or custom Level implementation + casting

        // Spawn the entity if the entity type lookup succeeds, otherwise fail the usage
        Optional<Entity> maybeEntity = ShrinkComponentUtils.getEntityType(stack)
            .map(type -> type.spawn(serverWorld, stack, player, blockPos, MobSpawnType.MOB_SUMMONED, false, false));
        if (maybeEntity.isEmpty()) return InteractionResult.FAIL;
        // reduce the stack in hand and swap
        // replace hand stack with glass bottle if hand is now empty
        // otherwise insert into inventory or drop
        if (player != null) {
            ItemStack handStack = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE),true);
            player.setItemInHand(hand, handStack);
        }else{
            // handle player-less usage in case a fake player isn't used for machines
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    public static ItemStack setContainedEntity(ItemStack emptyBottle, LivingEntity entity)
    {
        if (containsEntity(emptyBottle)) return emptyBottle;// bottle is already filled
        if (entity.level().isClientSide()) return emptyBottle; // don't modify itemstacks on client
        if (entity instanceof Player || // don't store players
            !entity.isAlive() ||  // don't store dead things (or non-living things)
            !entity.getType().canSerialize() // don't attempt to store entities that can't be turned to nbt
        ) return emptyBottle;

        CompoundTag entityNbt = new CompoundTag();
        if (!entity.save(entityNbt)) return emptyBottle; // failed to serialize the entity into nbt
        ShrinkComponentUtils.stripTag(entityNbt); // remove position and some other state information. See ShrinkComponentUtils.IGNORED_ENTITY_TAGS

        // create the item and set its components.
        ItemStack mobBottle = new ItemStack(ModItems.SHRINK_BOTTLE.get(), 1);
        mobBottle.set(DataComponents.ENTITY_DATA, CustomData.of(entityNbt));

        // if the entity has a visible custom name, set a component for optimized lookup in tooltips
        if (entity.hasCustomName() && entity.isCustomNameVisible()) {
            mobBottle.set(ShrinkComponentTypes.ENTITY_NAME.get(), entity.getCustomName());
        } else{
            mobBottle.remove(ShrinkComponentTypes.ENTITY_NAME.get()); // not necessary at the time of writing but prevents accidental issues in the future
        }

        entity.remove(Entity.RemovalReason.KILLED);

        return mobBottle;
    }

    public static boolean containsEntity(ItemStack stack)
    {
        return stack.getOrDefault(DataComponents.ENTITY_DATA,CustomData.EMPTY).contains("id");
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemStack)
    {
        return containsEntity(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, tooltipContext, list, tooltipFlag);
        if (containsEntity(stack))
        {
            Component entityTypeName = ShrinkComponentUtils.getEntityType(stack).map(EntityType::getDescription).orElseGet(Component::empty);
            Component name = stack.get(ShrinkComponentTypes.ENTITY_NAME.get());
            if (name != null) {
                list.add(Component.translatable("item.mob_bottle.tooltip_with_name", name, entityTypeName));
            }else{
                list.add(Component.translatable("item.mob_bottle.tooltip", entityTypeName));
            }
        }
        else
        {
            list.add(Component.translatable("item.mob_bottle.tooltip_empty"));
        }
    }

    // Remove legacy component and replace it with vanilla's ENTITY_DATA component
    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        // TODO: remove legacy component support
        ShrinkComponentUtils.convertLegacyDataComponent(stack);
        CustomData data = stack.get(DataComponents.ENTITY_DATA);
        if (data != null && !data.contains("id")) stack.remove(DataComponents.ENTITY_DATA);
        Component name = stack.get(ShrinkComponentTypes.ENTITY_NAME.get());
        if (!stack.has(DataComponents.ENTITY_DATA) || (name != null && name.getString().isBlank())) stack.remove(ShrinkComponentTypes.ENTITY_NAME.get());
    }
}
