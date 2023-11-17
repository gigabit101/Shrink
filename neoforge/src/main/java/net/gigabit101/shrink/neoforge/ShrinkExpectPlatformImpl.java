package net.gigabit101.shrink.neoforge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ShrinkExpectPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }
}
