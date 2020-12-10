package net.gigabit101.shrink.events;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.cap.ShrinkImpl;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.ShrinkPacket;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Shrink.MOD_ID)
public class PlayerEvents {
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone evt) {
        evt.getOriginal().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(old -> {
            CompoundNBT knowledge = old.serializeNBT();
            evt.getEntityLiving().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.deserializeNBT(knowledge));
        });
    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent evt) {
        evt.getPlayer().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync((ServerPlayerEntity) evt.getPlayer()));
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        event.getPlayer().getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync((ServerPlayerEntity) event.getPlayer()));
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof PlayerEntity) {
            evt.addCapability(ShrinkImpl.Provider.NAME, new ShrinkImpl.Provider((PlayerEntity) evt.getObject()));
        }
        if(evt.getObject() instanceof MobEntity) {
            evt.addCapability(ShrinkImpl.Provider.NAME, new ShrinkImpl.Provider((MobEntity) evt.getObject()));
        }
    }

    @SubscribeEvent
    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
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
            LivingEntity livingBase = (LivingEntity) target;
            livingBase.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                PacketHandler.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ShrinkPacket(target.getEntityId(), iShrinkProvider.serializeNBT()));
            });
        }
    }

    @SubscribeEvent
    public static void joinWorldEvent(EntityJoinWorldEvent event)
    {
        if(!event.getWorld().isRemote && event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof PlayerEntity))
        {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(c -> c.sync(livingEntity));
        }
    }

    @SubscribeEvent
    public static void changeSize(EntityEvent.Size event)
    {
        World world = event.getEntity().world;

        if(event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) event.getEntity();
            playerEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                double x = event.getEntity().getPosX();
                double y = event.getEntity().getPosY();
                double z = event.getEntity().getPosZ();

                if(iShrinkProvider.isShrunk() && event.getPose() == Pose.STANDING)
                {
                    event.setNewSize(new EntitySize(0.1F, 0.2F, true));
                    event.setNewEyeHeight(0.16F);
                    event.getEntity().setPosition(x, y, z);
                }
                else if(iShrinkProvider.isShrunk() && event.getPose() == Pose.CROUCHING)
                {
                    event.setNewSize(new EntitySize(0.1F, 0.14F, true));
                    event.setNewEyeHeight(0.11F);
                    event.getEntity().setPosition(x, y, z);
                }
                else if(!iShrinkProvider.isShrunk() && event.getPose() == Pose.STANDING)
                {
                    event.setNewSize(new EntitySize(0.6F, 1.8F, false));
                    event.setNewEyeHeight(ShrinkImpl.defaultEyeHeight);
                    event.getEntity().setPosition(x, y, z);
                }
            });
        }
    }
}
