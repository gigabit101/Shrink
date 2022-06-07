package net.gigabit101.shrink;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Shrink.MOD_ID);

    public static final RegistryObject<MenuType<ShrinkContainer>> SHRINK_CONTAINER = CONTAINERS.register("shrinkingdevice", () -> IForgeMenuType.create(ShrinkContainer::new));
}
