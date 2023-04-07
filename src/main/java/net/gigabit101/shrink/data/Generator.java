package net.gigabit101.shrink.data;

import net.gigabit101.shrink.Shrink;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Shrink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Generator
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(event.includeServer(), new GeneratorRecipes(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new GeneratorEntityTags(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));

        generator.addProvider(event.includeClient(), new GeneratorItemModels(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new GeneratorLanguage(generator));
    }
}
