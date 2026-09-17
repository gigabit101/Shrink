package net.gigabit101.shrink.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(Level.class)
public abstract class HappyGhastInteractionMixin {
    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Inject(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("RETURN")
    )
    private void shrink$includeEnlargedHappyGhasts(Entity except, AABB box, Predicate<? super Entity> matching,
                                                  CallbackInfoReturnable<List<Entity>> cir) {
        if (!(except instanceof Player)) return;

        List<Entity> entities = cir.getReturnValue();
        // Section searches assume entities are at most four blocks wide/high. An enlarged
        // ghast's sides and roof can overlap a player while its owning section is outside
        // the search. Include these hits for both targeting and client/server collisions.
        for (Entity entity : this.getEntities().getAll()) {
            if (entity instanceof HappyGhast ghast && ghast.getScale() > 1.0F
                    && !entity.isRemoved() && entity.getBoundingBox().intersects(box)
                    && !entities.contains(entity) && matching.test(entity)) {
                entities.add(entity);
            }
        }
        // Keep vanilla's collision eligibility, ray intersection, obstruction and reach rules.
    }
}
