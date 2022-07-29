package net.gigabit101.shrink.events;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.cap.ShrinkImpl;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.gigabit101.shrink.items.ItemModBottle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Shrink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents
{
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone evt)
    {
        evt.getOriginal().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(old ->
        {
            CompoundTag compoundTag = old.serializeNBT();
            evt.getEntity().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.deserializeNBT(compoundTag));
        });
    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent evt)
    {
        evt.getEntity().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(evt.getEntity()));
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        event.getEntity().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(event.getEntity()));
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt)
    {
        if (evt.getObject() instanceof LivingEntity livingEntity)
        {
            try
            {
                evt.addCapability(ShrinkImpl.Provider.NAME, new ShrinkImpl.Provider(livingEntity));
            } catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(player));
    }

    @SubscribeEvent
    public static void playerStartTracking(PlayerEvent.StartTracking event)
    {
        Entity target = event.getTarget();
        Player player = event.getEntity();

        if (player instanceof ServerPlayer && target instanceof LivingEntity livingEntity)
        {
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> iShrinkProvider.sync(livingEntity));
        }
    }

    @SubscribeEvent
    public static void joinWorldEvent(EntityJoinLevelEvent event)
    {
        if(!event.getLevel().isClientSide() && event.getEntity() instanceof LivingEntity livingEntity)
        {
            livingEntity.refreshDimensions();
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> iShrinkProvider.sync(livingEntity));
        }
    }

    @SubscribeEvent
    public static void itemInteractionForEntity(PlayerInteractEvent.EntityInteract event)
    {
        if(!ShrinkConfig.ENABLE_MOB_BOTTLES.get()) return;

        if(!event.getLevel().isClientSide() && event.getTarget() instanceof LivingEntity && !(event.getTarget() instanceof Player))
        {
            Player playerEntity = event.getEntity();

            if(event.getTarget() instanceof LivingEntity livingEntity)
            {
                if(playerEntity.getItemInHand(event.getHand()).getItem() == Items.GLASS_BOTTLE)
                {
                    livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                    {
                        if(iShrinkProvider.isShrunk())
                        {
                            playerEntity.getItemInHand(event.getHand()).shrink(1);
                            ItemStack output = ItemModBottle.setContainedEntity(event.getItemStack(), livingEntity);
                            boolean added = playerEntity.getInventory().add(output);
                            if(!added)
                            {
                                ItemEntity itemEntity = new ItemEntity(event.getLevel(), playerEntity.blockPosition().getX(), playerEntity.blockPosition().getY(), playerEntity.blockPosition().getZ(), output);
                                event.getLevel().addFreshEntity(itemEntity);
                            }
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void changeSize(EntityEvent.Size event)
    {
        if(event.getEntity() instanceof LivingEntity livingEntity)
        {
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if(iShrinkProvider.isShrunk())
                {
                    event.setNewSize(event.getNewSize().scale(iShrinkProvider.scale()));
                    event.setNewEyeHeight(event.getNewEyeHeight() * iShrinkProvider.scale());
                }
            });
        }
    }
}
