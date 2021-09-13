package net.gigabit101.shrink.mixins;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    @Shadow
    public abstract EntityDimensions getDimensions(Pose p_213305_1_);

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow public Level level;

    @Inject(at = @At("RETURN"), method = "canEnterPose", cancellable = true)
    public void isPoseClear(Pose pose, CallbackInfoReturnable<Boolean> cir)
    {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof Player)
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if(iShrinkProvider.isShrunk())
                {
                    float scale = iShrinkProvider.scale();
                    EntityDimensions entitysize = this.getDimensions(pose);
                    entitysize = entitysize.scale(scale);
                    float f = entitysize.width / 2.0F;
                    Vec3 vector3d = new Vec3(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
                    Vec3 vector3d1 = new Vec3(this.getX() + (double)f, this.getY() + (double)entitysize.height, this.getZ() + (double)f);
                    AABB box = new AABB(vector3d, vector3d1);
                    cir.setReturnValue(this.level.noCollision(livingEntity, box.deflate(1.0E-7D)));
                }
            });
        }
    }
}
