package net.gigabit101.shrink;

import dev.architectury.injectables.annotations.ExpectPlatform;
import java.nio.file.Path;

public class ShrinkExpectPlatform
{
    @ExpectPlatform
    public static Path getConfigDirectory()
    {
        throw new AssertionError();
    }
}
