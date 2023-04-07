package net.gigabit101.shrink.data;

import net.gigabit101.shrink.Shrink;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Shrink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Generator
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        if(event.includeServer()) registerServerProviders(event.getGenerator());

        if(event.includeClient()) registerClientProviders(event.getGenerator(), event);
    }

    private static void registerServerProviders(DataGenerator generator)
    {
        generator.addProvider(true, new GeneratorRecipes(generator.getPackOutput()));
    }

    private static void registerClientProviders(DataGenerator generator, GatherDataEvent event)
    {
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(true, new GeneratorItemModels(generator, helper));
        generator.addProvider(true, new GeneratorLanguage(generator));
    }
}
