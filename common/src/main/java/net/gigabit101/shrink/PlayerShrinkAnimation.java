package net.gigabit101.shrink;

import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class PlayerShrinkAnimation
{
    private static final int DURATION_TICKS = 12;
    private static final Identifier ANIMATION_ID = Identifier.fromNamespaceAndPath(ShrinkCommon.MOD_ID, "size_animation");
    private static final Map<ServerPlayer, Transition> ACTIVE = new WeakHashMap<>();

    private PlayerShrinkAnimation() {}

    public static boolean toggle(ServerPlayer player, double amount)
    {
        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        if (scale == null || !player.isAlive() || !Double.isFinite(amount)) return false;
        amount = Math.clamp(amount, ShrinkCommon.shrinkConfig.minSize, ShrinkCommon.shrinkConfig.maxSize);
        Transition transition = new Transition(scale, amount);
        if (transition.offset == 0) ACTIVE.remove(player);
        else ACTIVE.put(player, transition);
        player.needsSync = true;
        return true;
    }

    /** Runs before player ticks so dimensions and tracking packets use the same scale. */
    public static void tick(MinecraftServer server)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            Transition transition = ACTIVE.get(player);
            if (transition == null) continue;
            AttributeInstance scale = player.getAttribute(Attributes.SCALE);
            if (scale == null || !player.isAlive() || player.isRemoved())
            {
                if (scale != null) scale.removeModifier(ANIMATION_ID);
                ACTIVE.remove(player);
            }
            else if (transition.tick(scale))
            {
                ACTIVE.remove(player);
            }
            // SCALE is syncable. Vanilla sends its dirty attributes to tracking players AND self.
            // Force the tracker to flush each step instead of waiting for its movement interval.
            player.needsSync = true;
        }
    }

    private static final class Transition
    {
        private final AttributeModifier target;
        private final double offset;
        private int elapsed;

        private Transition(AttributeInstance scale, double amount)
        {
            AttributeModifier previous = scale.getModifier(ItemShrinkDevice.SHRINKING_DEVICE_ID);
            AttributeModifier animation = scale.getModifier(ANIMATION_ID);
            double current = (previous == null ? 0 : previous.amount()) + (animation == null ? 0 : animation.amount());
            target = previous == null ? ItemShrinkDevice.createModifier(amount) : null;
            double destination = target == null ? 0 : target.amount();
            offset = current - destination;

            // Save the final size immediately. The temporary animation offset is never persisted,
            // so logging out or restarting mid-transition cannot leave a player stuck halfway.
            scale.removeModifier(ItemShrinkDevice.SHRINKING_DEVICE_ID);
            if (target != null) scale.addPermanentModifier(target);
            if (offset == 0) scale.removeModifier(ANIMATION_ID);
            else applyOffset(scale, offset);
        }

        private boolean tick(AttributeInstance scale)
        {
            // Respect another mod or command replacing/removing our target during the animation.
            if (!Objects.equals(scale.getModifier(ItemShrinkDevice.SHRINKING_DEVICE_ID), target)
                    || ++elapsed >= DURATION_TICKS)
            {
                scale.removeModifier(ANIMATION_ID);
                return true;
            }
            double progress = (double) elapsed / DURATION_TICKS;
            double eased = progress * progress * (3 - 2 * progress);
            applyOffset(scale, offset * (1 - eased));
            return false;
        }

        private static void applyOffset(AttributeInstance scale, double offset)
        {
            scale.addOrUpdateTransientModifier(new AttributeModifier(ANIMATION_ID, offset, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
