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
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shapedRecipe(ShrinkItems.SHRINKING_DEVICE.get())
                .key('e', Tags.Items.ENDER_PEARLS)
                .key('g', Tags.Items.GLASS)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('b', Items.STONE_BUTTON)
                .patternLine("iei")
                .patternLine("igi")
                .patternLine("ibi")
                .addCriterion("has_enderpearls", hasItem(Tags.Items.ENDER_PEARLS))
                .build(consumer);
    }
}
