package net.gigabit101.shrink.mixins;

import net.gigabit101.shrink.polylib.EntitySizeEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow private EntityDimensions dimensions;

    @Shadow protected abstract float getEyeHeight(Pose pose, EntityDimensions entityDimensions);

    @Shadow private float eyeHeight;

    @Shadow public abstract Pose getPose();

    @Shadow public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow protected abstract void reapplyPosition();

    @Shadow public abstract Level level();

    @Shadow protected boolean firstTick;

    @Shadow public boolean noPhysics;

    @Shadow public abstract Vec3 position();

    @Shadow public abstract void setPos(Vec3 vec3);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityType<?> entityType, Level level, CallbackInfo ci)
    {
        EntitySizeEvents.UpdatedSize updatedSize = EntitySizeEvents.SIZE.invoker().size((Entity) (Object)this, Pose.STANDING, this.dimensions, this.getEyeHeight(Pose.STANDING, this.dimensions));
        this.dimensions = updatedSize.getNewSize();
        this.eyeHeight = updatedSize.getNewEyeHeight();
    }

    /**
     * @author Gigabit101
     * @reason Adding SizeChange events
     */
    @Overwrite
    public void refreshDimensions()
    {
        EntityDimensions entityDimensions = this.dimensions;
        Pose pose = this.getPose();
        EntityDimensions entityDimensions2 = this.getDimensions(pose);
        EntitySizeEvents.UpdatedSize updatedSize = EntitySizeEvents.SIZE.invoker().size((Entity) (Object)this, pose, entityDimensions, this.getEyeHeight(pose, entityDimensions2));
        entityDimensions2 = updatedSize.getNewSize();
        this.dimensions = entityDimensions2;
        this.eyeHeight = updatedSize.getNewEyeHeight();
        this.reapplyPosition();
        boolean bl = (double)entityDimensions2.width <= 4.0 && (double)entityDimensions2.height <= 4.0;
        if (!this.level().isClientSide && !this.firstTick && !this.noPhysics && bl && (entityDimensions2.width > entityDimensions.width || entityDimensions2.height > entityDimensions.height) && !((Entity) (Object)this instanceof Player)) {
            Vec3 vec3 = this.position().add(0.0, (double)entityDimensions.height / 2.0, 0.0);
            double d = (double)Math.max(0.0F, entityDimensions2.width - entityDimensions.width) + 1.0E-6;
            double e = (double)Math.max(0.0F, entityDimensions2.height - entityDimensions.height) + 1.0E-6;
            VoxelShape voxelShape = Shapes.create(AABB.ofSize(vec3, d, e, d));
            EntityDimensions finalEntitydimensions = entityDimensions2;
            this.level().findFreePosition((Entity) (Object)this, voxelShape, vec3, (double)entityDimensions2.width, (double)entityDimensions2.height, (double)entityDimensions2.width).ifPresent((vec3x) -> {
                this.setPos(vec3x.add(0.0, (double)(-finalEntitydimensions.height) / 2.0, 0.0));
            });
        }
    }

//    @Inject(method = "refreshDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", shift = At.Shift.AFTER))
//    public void refresh(CallbackInfo ci)
//    {
//        EntitySizeEvents.UpdatedSize updatedSize = EntitySizeEvents.SIZE.invoker().size((Entity) (Object)this, Pose.STANDING, this.dimensions, this.getEyeHeight(Pose.STANDING, this.dimensions));
//        this.eyeHeight = updatedSize.getNewEyeHeight();
//        this.dimensions = updatedSize.getNewSize();
//        System.out.println(this.dimensions);
//        System.out.println(this.eyeHeight);
//
//    }

//    @Redirect(method = "refreshDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
//    private EntityDimensions injected(Entity instance, Pose pose)
//    {
//        EntitySizeEvents.UpdatedSize updatedSize = EntitySizeEvents.SIZE.invoker().size((Entity) (Object)this, Pose.STANDING, this.dimensions, this.getEyeHeight(Pose.STANDING, this.dimensions));
//        return updatedSize.getNewSize();
//    }
}
