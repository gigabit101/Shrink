package net.gigabit101.shrink.neoforge;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.neoforge.compat.GekoLibCompat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@Mod(Shrink.MOD_ID)
public class ShrinkModForge
{
    public ShrinkModForge(IEventBus bus)
    {
        bus.addListener(this::clientSetup);
        bus.addListener(this::attachAttributeEvent);
        Shrink.init();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.register(new NeoForgeClientEvents());
        if(ModList.get().isLoaded("gekolib"))
        {
            NeoForge.EVENT_BUS.register(new GekoLibCompat());
        }
    }

    private void attachAttributeEvent(EntityAttributeModificationEvent event)
    {
        event.getTypes().forEach(entityType -> event.add(entityType, Shrink.SHRINK_ATTRIBUTE.get()));
    }
}
