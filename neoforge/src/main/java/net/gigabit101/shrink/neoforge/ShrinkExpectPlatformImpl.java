package net.gigabit101.shrink.neoforge;

import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import top.theillusivec4.curios.api.CuriosApi;

import java.nio.file.Path;

public class ShrinkExpectPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    public static ItemStack findEquippedShrinkDevice(Player player)
    {
        if(!ModList.get().isLoaded("curios")) return ItemStack.EMPTY;

        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof ItemShrinkDevice))
                .map(result -> result.stack())
                .orElse(ItemStack.EMPTY);
    }
}