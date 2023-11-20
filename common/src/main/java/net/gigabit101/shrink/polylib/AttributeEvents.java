package net.gigabit101.shrink.polylib;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public interface AttributeEvents
{
    Event<AttributeEvents.ADD> ADD = EventFactory.createEventResult();

    interface ADD
    {
        void add(AttributeSupplier.Builder builder);
    }
}
