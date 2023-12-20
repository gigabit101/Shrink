package net.gigabit101.shrink.mixins;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Shadow public abstract EntityDimensions getDimensions(Pose pose);

    @Inject(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("RETURN"), cancellable = true)
    private void isPoseClear (Pose pose, CallbackInfoReturnable<Boolean> cir)
    {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity livingEntity)
        {
            if (ShrinkAPI.isEntityShrunk(livingEntity))
            {
                double scale = livingEntity.getAttributeValue(ShrinkAPI.SCALE_ATTRIBUTE);
                EntityDimensions entitysize = this.getDimensions(pose);
                entitysize = entitysize.scale((float) scale);
                float f = entitysize.width / 2.0F;
                Vec3 vector3d = new Vec3(livingEntity.getX() - (double)f, livingEntity.getY(), livingEntity.getZ() - (double)f);
                Vec3 vector3d1 = new Vec3(livingEntity.getX() + (double)f, livingEntity.getY() + (double)entitysize.height, livingEntity.getZ() + (double)f);
                AABB box = new AABB(vector3d, vector3d1);
                cir.setReturnValue(livingEntity.level().noCollision(livingEntity, box.deflate(1.0E-7D)));
            }
        }
    }
}
