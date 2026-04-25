package net.gigabit101.shrink.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.gigabit101.shrink.ShrinkCommon;
import net.gigabit101.shrink.ShrinkingDeviceContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ShrinkContainers
{
    public static final PolyRegistry<MenuType<?>> CONTAINERS = PolyRegistry.create(Registries.MENU, ShrinkCommon.MOD_ID);

    public static final Supplier<MenuType<ShrinkingDeviceContainer>> SHRINKING_DEVICE =
            CONTAINERS.registerMenu("shrinking_device", ShrinkingDeviceContainer::new);

    public static void init(){
        CONTAINERS.init();
    }
}
