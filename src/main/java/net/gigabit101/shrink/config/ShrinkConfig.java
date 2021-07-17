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
    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_MOB_BOTTLES;
    public static ForgeConfigSpec.DoubleValue MAX_SIZE;
    public static ForgeConfigSpec.DoubleValue MIN_SIZE;

    static
    {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        POWER_REQUIREMENT = COMMON_BUILDER.comment("Set to false to disable power requirements for personal shrinking device")
                .define("enablePowerRequirements",  true);

        POWER_COST = COMMON_BUILDER.comment("Set the amount of power required to use use the personal shrinking device")
                .define("setPowerUsage",  5000);

        POWER_CAPACITY = COMMON_BUILDER.comment("Set the amount of power the personal shrinking device can store")
                .define("setShrinkingDeviceCapacity",  100000);

        ENABLE_MOB_BOTTLES = COMMON_BUILDER.comment("Set to false to disable mobs being put in bottles")
                .define("enableMobBottles",  true);

        MAX_SIZE = COMMON_BUILDER.comment("Set the max size a player can grow too")
                .defineInRange("maxSize", 10, 0,  100D);

        MIN_SIZE = COMMON_BUILDER.comment("Set the min size a player can shrink too")
                .defineInRange("minSize", 0.21, 0.21D,  100D);

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
