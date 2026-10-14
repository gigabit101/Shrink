package net.gigabit101.shrink;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

public class ShrinkExpectPlatform
{
    @ExpectPlatform
    public static Path getConfigDirectory()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ItemStack findEquippedShrinkDevice(Player player)
    {
        throw new AssertionError();
    }
}