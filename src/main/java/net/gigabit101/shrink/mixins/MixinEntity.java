package net.gigabit101.shrink.mixins;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    @Shadow public World world;

    @Shadow protected abstract AxisAlignedBB getBoundingBox(Pose pose);

    @Shadow public abstract EntitySize getSize(Pose poseIn);

    @Shadow public abstract double getPosX();

    @Shadow public abstract double getPosY();

    @Shadow public abstract double getPosZ();

    @Inject(at = @At("RETURN"), method = "isPoseClear", cancellable = true)
    public void isPoseClear(Pose pose, CallbackInfoReturnable<Boolean> cir)
    {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof PlayerEntity)
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
            {
                if(iShrinkProvider.isShrunk())
                {
                    float scale = iShrinkProvider.scale();
                    EntitySize entitysize = this.getSize(pose);
                    entitysize = entitysize.scale(scale);
                    float f = entitysize.width / 2.0F;
                    Vector3d vector3d = new Vector3d(this.getPosX() - (double)f, this.getPosY(), this.getPosZ() - (double)f);
                    Vector3d vector3d1 = new Vector3d(this.getPosX() + (double)f, this.getPosY() + (double)entitysize.height, this.getPosZ() + (double)f);
                    AxisAlignedBB box = new AxisAlignedBB(vector3d, vector3d1);
                    cir.setReturnValue(this.world.hasNoCollisions(livingEntity, box.shrink(1.0E-7D)));
                }
            });
        }

    }
}
