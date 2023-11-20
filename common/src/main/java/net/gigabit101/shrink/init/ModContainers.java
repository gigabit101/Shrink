package net.gigabit101.shrink.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.ShrinkingDeviceContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Shrink.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<ShrinkingDeviceContainer>> SHRINKING_DEVICE = CONTAINERS.register("shrinking_device", () -> MenuRegistry.ofExtended(ShrinkingDeviceContainer::new));
}
