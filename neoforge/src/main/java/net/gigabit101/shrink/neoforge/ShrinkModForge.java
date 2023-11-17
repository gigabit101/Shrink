package net.gigabit101.shrink.neoforge;

//import dev.architectury.platform.neoforge.EventBuses;
import net.gigabit101.shrink.Shrink;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Shrink.MOD_ID)
public class ShrinkModForge
{
    public ShrinkModForge()
    {
//        EventBuses.registerModEventBus(Shrink.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Shrink.init();
    }
}
