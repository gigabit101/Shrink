//package net.gigabit101.shrink.mixins;
//
//import net.gigabit101.shrink.api.ShrinkAPI;
//
//@Mixin(Entity.class)
//public abstract class MixinEntity
//{
//    @Shadow public abstract EntitySize getDimensions(Pose p_213305_1_);
//
//    @Shadow public abstract double getX();
//
//    @Shadow public abstract double getY();
//
//    @Shadow public abstract double getZ();
//
//    @Shadow public World level;
//
//    @Inject(at = @At("RETURN"), method = "canEnterPose", cancellable = true)
//    public void isPoseClear(Pose pose, CallbackInfoReturnable<Boolean> cir)
//    {
//        Entity entity = (Entity) (Object) this;
//        if(entity instanceof PlayerEntity)
//        {
//            LivingEntity livingEntity = (LivingEntity) entity;
//            livingEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
//            {
//                if(iShrinkProvider.isShrunk())
//                {
//                    float scale = iShrinkProvider.scale();
//                    EntitySize entitysize = this.getDimensions(pose);
//                    entitysize = entitysize.scale(scale);
//                    float f = entitysize.width / 2.0F;
//                    Vector3d vector3d = new Vector3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
//                    Vector3d vector3d1 = new Vector3d(this.getX() + (double)f, this.getY() + (double)entitysize.height, this.getZ() + (double)f);
//                    AxisAlignedBB box = new AxisAlignedBB(vector3d, vector3d1);
//                    cir.setReturnValue(this.level.noCollision(livingEntity, box.deflate(1.0E-7D)));
//                }
//            });
//        }
//
//    }
//}
