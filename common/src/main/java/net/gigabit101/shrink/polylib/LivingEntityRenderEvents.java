package net.gigabit101.shrink.polylib;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityRenderEvents
{
    Event<LivingEntityRenderEvents.PRE> PRE = EventFactory.createEventResult();
    Event<LivingEntityRenderEvents.POST> POST = EventFactory.createEventResult();

    interface PRE
    {
        void pre(LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i);
    }

    interface POST
    {
        void post(LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i);
    }
}
