package net.gigabit101.shrink.data;

import net.gigabit101.shrink.items.ShrinkItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class GeneratorRecipes extends RecipeProvider
{
    public GeneratorRecipes(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(ShrinkItems.SHRINKING_DEVICE.get())
                .define('e', Tags.Items.ENDER_PEARLS)
                .define('g', Tags.Items.GLASS)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('b', Items.STONE_BUTTON)
                .pattern("iei")
                .pattern("igi")
                .pattern("ibi")
                .unlockedBy("has_enderpearls", has(Tags.Items.ENDER_PEARLS))
                .save(consumer);
    }
}
