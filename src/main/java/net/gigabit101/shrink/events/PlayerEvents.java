package net.gigabit101.shrink.events;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.cap.ShrinkImpl;
import net.gigabit101.shrink.items.ItemModBottle;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Shrink.MOD_ID)
public class PlayerEvents
{
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone evt)
    {
        evt.getOriginal().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(old ->
        {
            CompoundNBT knowledge = old.serializeNBT();
            evt.getEntityLiving().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.deserializeNBT(knowledge));
        });
    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent evt)
    {
        evt.getPlayer().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(evt.getPlayer()));
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        event.getPlayer().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(event.getPlayer()));
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt)
    {
        if(evt.getObject() instanceof LivingEntity)
        {
            evt.addCapability(ShrinkImpl.Provider.NAME, new ShrinkImpl.Provider((LivingEntity) evt.getObject()));
        }
    }

    @SubscribeEvent
    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(player));
    }

    @SubscribeEvent
    public static void playerStartTracking(PlayerEvent.StartTracking event)
    {
        Entity target = event.getTarget();
        PlayerEntity player = event.getPlayer();

        if (player instanceof ServerPlayerEntity && target instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) target;
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> iShrinkProvider.sync(livingEntity));
        }
    }

    @SubscribeEvent
    public static void joinWorldEvent(EntityJoinWorldEvent event)
    {
        if(!event.getWorld().isRemote && event.getEntity() instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            livingEntity.recalculateSize();
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> iShrinkProvider.sync(livingEntity));
        }
    }

    @SubscribeEvent
    public static void itemInteractionForEntity(PlayerInteractEvent.EntityInteract event)
    {
        if(!event.getWorld().isRemote && event.getTarget() instanceof LivingEntity && !(event.getTarget() instanceof PlayerEntity))
        {
            PlayerEntity playerEntity = event.getPlayer();

            if(event.getTarget() instanceof LivingEntity)
            {
                LivingEntity livingEntity = (LivingEntity) event.getTarget();

                if(playerEntity.getHeldItem(event.getHand()).getItem() == Items.GLASS_BOTTLE.getItem())
                {
                    livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
                    {
                        if(iShrinkProvider.isShrunk())
                        {
                            playerEntity.getHeldItem(event.getHand()).shrink(1);
                            ItemStack output = ItemModBottle.setContainedEntity(event.getItemStack(), livingEntity);
                            playerEntity.inventory.addItemStackToInventory(output);
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void changeSize(EntityEvent.Size event)
    {
        if(event.getEntity() instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                double x = event.getEntity().getPosX();
                double y = event.getEntity().getPosY();
                double z = event.getEntity().getPosZ();

                if(iShrinkProvider.isShrunk() && (event.getPose() == Pose.STANDING || event.getPose() == Pose.SWIMMING))
                {
                    event.setNewSize(new EntitySize(0.1F, 0.2F, true));
                    if(event.getPose() != Pose.STANDING) event.getEntity().setPose(Pose.STANDING);
                    event.setNewEyeHeight(0.16F);
                    event.getEntity().setPosition(x, y, z);
                }
                else if(iShrinkProvider.isShrunk() && event.getPose() == Pose.CROUCHING && livingEntity instanceof PlayerEntity)
                {
                    event.setNewSize(new EntitySize(0.1F, 0.14F, true));
                    event.setNewEyeHeight(0.11F);
                    event.getEntity().setPosition(x, y, z);
                }
                else if(!iShrinkProvider.isShrunk() && event.getPose() == Pose.STANDING && livingEntity instanceof PlayerEntity)
                {
                    event.setNewSize(new EntitySize(0.6F, 1.8F, false));
                    event.setNewEyeHeight(ShrinkImpl.defaultEyeHeight);
                    event.getEntity().setPosition(x, y, z);
                }
            });
        }
    }
}
