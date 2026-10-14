package net.gigabit101.shrink.fabric;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

public class ShrinkExpectPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static ItemStack findEquippedShrinkDevice(Player player)
    {
        if(!FabricLoader.getInstance().isModLoaded("trinkets")) return ItemStack.EMPTY;

        return TrinketsApi.getTrinketComponent(player)
                .flatMap(component -> component.getEquipped(stack -> stack.getItem() instanceof ItemShrinkDevice).stream().findFirst())
                .map(pair -> pair.getB())
                .orElse(ItemStack.EMPTY);
    }
}