package net.gigabit101.shrink.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class ShrinkConfig
{
    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<Boolean> POWER_REQUIREMENT;
    public static ForgeConfigSpec.ConfigValue<Integer> POWER_COST;
    public static ForgeConfigSpec.ConfigValue<Integer> POWER_CAPACITY;

    static
    {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        POWER_REQUIREMENT = COMMON_BUILDER.comment("Set to true to enable power requirements for personal shrinking device")
                .define("enablePowerRequirements",  true);

        POWER_COST = COMMON_BUILDER.comment("Set the amount of power required to use use the personal shrinking device")
                .define("setPowerUsage",  5000);

        POWER_CAPACITY = COMMON_BUILDER.comment("Set the amount of power the personal shrinking device can store")
                .define("setShrinkingDeviceCapacity",  100000);

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {}

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {}
}
