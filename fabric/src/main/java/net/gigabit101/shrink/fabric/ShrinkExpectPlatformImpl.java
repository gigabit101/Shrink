package net.gigabit101.shrink.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ShrinkExpectPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }
}
