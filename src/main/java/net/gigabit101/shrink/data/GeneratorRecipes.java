package net.gigabit101.shrink.data;

import net.gigabit101.shrink.items.ShrinkItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class GeneratorRecipes extends RecipeProvider
{

    public GeneratorRecipes(PackOutput packOutput)
    {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        //        ShapedRecipeBuilder.shaped(ShrinkItems.SHRINKING_DEVICE.get())
        //                .define('e', Tags.Items.ENDER_PEARLS)
        //                .define('g', Tags.Items.GLASS)
        //                .define('i', Tags.Items.INGOTS_IRON)
        //                .define('b', Items.STONE_BUTTON)
        //                .pattern("iei")
        //                .pattern("igi")
        //                .pattern("ibi")
        //                .unlockedBy("has_enderpearls", has(Tags.Items.ENDER_PEARLS))
        //                .save(consumer);

    }
}
