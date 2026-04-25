package net.gigabit101.shrink;

import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.polylib.init.DataComps;
import net.creeperhost.polylib.platform.Services;
import net.gigabit101.shrink.init.ShrinkComponentTypes;
import net.gigabit101.shrink.init.ShrinkContainers;
import net.gigabit101.shrink.init.ShrinkCreativeTabs;
import net.gigabit101.shrink.init.ShrinkItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShrinkCommon
{
    public static final String MOD_ID = "shrink";
    public static final String MOD_NAME = "Shrink";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static ConfigBuilder configBuilder;
    public static ShrinkConfig shrinkConfig;

    public static void init()
    {
        // initialize PolyLib's item components
        DataComps.registerData();
        ShrinkCreativeTabs.init();
        ShrinkItems.init();
        ShrinkContainers.init();
        ShrinkComponentTypes.init();

        configBuilder = new ConfigBuilder(ShrinkCommon.MOD_ID, Services.PLATFORM.getConfigFolder().resolve(ShrinkCommon.MOD_ID + ".json5"), new ShrinkConfig());
        shrinkConfig = (ShrinkConfig) configBuilder.getConfigData();

        if (Services.PLATFORM.isClient()) {
            ShrinkClient.init();
        }
    }
}