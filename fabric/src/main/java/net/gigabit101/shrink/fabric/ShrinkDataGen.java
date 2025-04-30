package net.gigabit101.shrink.fabric;

import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.providers.PolyLanguageProvider;
import net.creeperhost.polylib.fabric.datagen.providers.PolyRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;


public class ShrinkDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider((output, registriesFuture) ->
        {
            PolyLanguageProvider provider = new PolyLanguageProvider(output, ModuleType.COMMON, registriesFuture);
            provider.add("itemGroup.shrink", "Shrink", ModuleType.COMMON);
            provider.add(ModItems.SHRINKING_DEVICE.get(), "Personal Shrinking Device", ModuleType.COMMON);
            provider.add(ModItems.SHRINK_BOTTLE.get(), "Glass Bottle", ModuleType.COMMON);
            provider.add("item.mob_bottle.tooltip_empty", "Right-click on a shrunken entity with a glass bottle to capture", ModuleType.COMMON);
            provider.add("item.mob_bottle.tooltip","Contains : %s", ModuleType.COMMON);
            provider.add("item.mob_bottle.tooltip_with_name","Contains : %s (%s)", ModuleType.COMMON);
            provider.add("key.shrink.category", "Shrink", ModuleType.COMMON);
            provider.add("key.shrink.shrink", "Toggle Shrink", ModuleType.COMMON);
            provider.add("shrink.deny_shrink", "Blocked shrinking of entity due to deny tag", ModuleType.COMMON);
            provider.add("shrink.message.already_shrunk","Unable to open while already shrunk", ModuleType.COMMON);
            provider.add("shrink.message.power", "Not enough power", ModuleType.COMMON);
            provider.add("shrink.message.missing", "Shrink Attribute Missing", ModuleType.COMMON);
            return provider;
        });

//        pack.addProvider((output, registriesFuture) ->
//        {
//            PolyRecipeProvider provider = new PolyRecipeProvider(output, ModuleType.COMMON, registriesFuture);
//            var enderPearl = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ender_pearls"));
//
//            provider.add(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHRINKING_DEVICE.get())
//                    .pattern("iei")
//                    .pattern("igi")
//                    .pattern("ibi")
//                    .define('b', Items.STONE_BUTTON)
//                    .define('e', enderPearl)
//                    .define('g', Items.GLASS)
//                    .define('i', Items.IRON_INGOT)
//                    .group(Shrink.MOD_ID)
//                    .unlockedBy("has_item", has(Items.ENDER_PEARL)), ModuleType.COMMON);
//
//            return provider;
//        });
    }
}
