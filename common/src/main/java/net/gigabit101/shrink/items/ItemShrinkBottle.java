package net.gigabit101.shrink.items;

import net.gigabit101.shrink.ShrinkCommon;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.init.ShrinkComponentTypes;
import net.gigabit101.shrink.init.ShrinkItems;
import net.gigabit101.shrink.init.ShrinkTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.List;
import java.util.function.Consumer;

public class ItemShrinkBottle extends Item
{
    // A released mob gets a new identity and position, without stale movement or attachments.
    private static final List<String> TRANSIENT_ENTITY_TAGS = List.of(
            "id", "UUID", "Pos", "Motion", "Rotation", "Passengers", "leash",
            "sleeping_pos", "SleepingX", "SleepingY", "SleepingZ", "fall_distance", "FallDistance",
            "OnGround", "PortalCooldown");

    public ItemShrinkBottle(Properties properties)
    {
        super(properties);
    }

    /** Called by both loaders before normal entity interactions (trading, milking, etc.). */
    public static InteractionResult capture(Player player, Entity target, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        if (player.isSpectator() || !player.getAbilities().mayBuild || held.isEmpty()
                || !held.is(Items.GLASS_BOTTLE)
                || !(target instanceof LivingEntity living) || !ShrinkAPI.canCaptureEntity(living))
        {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide()) return InteractionResult.SUCCESS;
        if (!player.level().mayInteract(player, target.blockPosition())) return InteractionResult.FAIL;

        ItemStack filled;
        try
        {
            ProblemReporter.Collector problems = new ProblemReporter.Collector();
            TagValueOutput output = TagValueOutput.createWithContext(problems, living.registryAccess());
            if (!living.save(output)) return InteractionResult.FAIL;
            if (!problems.isEmpty())
            {
                ShrinkCommon.LOG.warn("Unable to capture mob: {}", problems.getReport());
                return InteractionResult.FAIL;
            }
            CompoundTag data = output.buildResult();
            TRANSIENT_ENTITY_TAGS.forEach(data::remove);
            filled = new ItemStack(ShrinkItems.SHRINK_BOTTLE.get());
            filled.set(DataComponents.ENTITY_DATA, TypedEntityData.of(living.getType(), data));
            if (living.hasCustomName())
            {
                filled.set(ShrinkComponentTypes.ENTITY_NAME.get(), living.getCustomName());
            }
        }
        catch (RuntimeException exception)
        {
            ShrinkCommon.LOG.warn("Unable to save mob into a bottle", exception);
            return InteractionResult.FAIL;
        }

        // Commit only after serialization succeeds. Discarding does not kill the mob or drop loot.
        if (living instanceof Leashable leashable) leashable.dropLeash();
        living.discard();
        exchangeOne(player, hand, filled);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack stack = context.getItemInHand();
        TypedEntityData<EntityType<?>> stored = stack.get(DataComponents.ENTITY_DATA);
        if (stored == null) return InteractionResult.PASS;

        Player player = context.getPlayer();
        if (player == null || player.isSpectator() || !player.getAbilities().mayBuild) return InteractionResult.FAIL;
        BlockPos clickedPos = context.getClickedPos();
        BlockPos spawnPos = context.getLevel().getBlockState(clickedPos).getCollisionShape(context.getLevel(), clickedPos).isEmpty()
                ? clickedPos : clickedPos.relative(context.getClickedFace());
        if (!player.mayUseItemAt(spawnPos, context.getClickedFace(), stack)
                || !context.getLevel().mayInteract(player, spawnPos)) return InteractionResult.FAIL;
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.SUCCESS;

        EntityType<?> type = stored.type();
        if (!type.canSerialize() || type.onlyOpCanSetNbt() || type.builtInRegistryHolder().is(ShrinkTags.CAPTURING_NOT_SUPPORTED)
                || !type.canSpawn(level) || !level.isInWorldBounds(spawnPos)) return InteractionResult.FAIL;

        LivingEntity released;
        try
        {
            Entity entity = type.create(level, EntitySpawnReason.MOB_SUMMONED);
            if (!(entity instanceof LivingEntity living) || entity instanceof Player) return InteractionResult.FAIL;
            CompoundTag data = stored.copyTagWithoutId();
            TRANSIENT_ENTITY_TAGS.forEach(data::remove);
            ProblemReporter.Collector problems = new ProblemReporter.Collector();
            living.load(TagValueInput.create(problems, level.registryAccess(), data));
            if (!problems.isEmpty() || !living.isAlive())
            {
                ShrinkCommon.LOG.warn("Unable to restore bottled mob: {}", problems.getReport());
                return InteractionResult.FAIL;
            }
            living.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), 0);
            if (!level.getWorldBorder().isWithinBounds(living.getBoundingBox())
                    || !level.noCollision(living, living.getBoundingBox())) return InteractionResult.FAIL;
            released = living;
        }
        catch (RuntimeException exception)
        {
            ShrinkCommon.LOG.warn("Unable to restore mob from a bottle", exception);
            return InteractionResult.FAIL;
        }

        // Unlike EntityType.spawn, this reports rejected spawns so the filled bottle is retained.
        if (!level.addFreshEntity(released)) return InteractionResult.FAIL;
        exchangeOne(player, context.getHand(), new ItemStack(Items.GLASS_BOTTLE));
        level.gameEvent(player, GameEvent.ENTITY_PLACE, released.position());
        return InteractionResult.SUCCESS;
    }

    private static void exchangeOne(Player player, InteractionHand hand, ItemStack replacement)
    {
        ItemStack held = player.getItemInHand(hand);
        // Consume filled bottles even in creative: they represent an existing mob, not a spawn egg.
        held.shrink(1);
        if (held.isEmpty()) player.setItemInHand(hand, replacement);
        else if (!player.getInventory().add(replacement)) player.drop(replacement, false, Prediction.SERVER_ONLY);
    }

    public static boolean containsEntity(ItemStack stack)
    {
        return stack.has(DataComponents.ENTITY_DATA);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return containsEntity(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltipAdder, TooltipFlag flag)
    {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        TypedEntityData<EntityType<?>> stored = stack.get(DataComponents.ENTITY_DATA);
        if (stored == null)
        {
            tooltipAdder.accept(Component.translatable("item.mob_bottle.tooltip_empty"));
            return;
        }
        Component name = stack.get(ShrinkComponentTypes.ENTITY_NAME.get());
        tooltipAdder.accept(name == null
                ? Component.translatable("item.mob_bottle.tooltip", stored.type().getDescription())
                : Component.translatable("item.mob_bottle.tooltip_with_name", name, stored.type().getDescription()));
    }
}
