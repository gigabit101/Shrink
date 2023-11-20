package net.gigabit101.shrink.polylib;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public interface EntitySizeEvents
{
    Event<EntitySizeEvents.Size> SIZE = EventFactory.createEventResult();

    interface Size
    {
        UpdatedSize size(Entity entity, Pose pose, EntityDimensions size, float eyeHeight);
    }

    public static class UpdatedSize
    {
        EntityDimensions size;
        float eyeHeight;

        EntityDimensions newSize;
        float newEyeHeight;
        public UpdatedSize(EntityDimensions size, float eyeHeight, EntityDimensions newSize, float newEyeHeight)
        {
            this.size = size;
            this.eyeHeight = eyeHeight;
            this.newSize = newSize;
            this.newEyeHeight = newEyeHeight;
        }

        public EntityDimensions getSize()
        {
            return size;
        }

        public float getEyeHeight()
        {
            return eyeHeight;
        }

        public EntityDimensions getNewSize()
        {
            if(this.newSize != size) return newSize;
            return this.size;
        }

        public float getNewEyeHeight()
        {
            if(this.newEyeHeight != eyeHeight) return this.newEyeHeight;
            return eyeHeight;
        }
    }
}
