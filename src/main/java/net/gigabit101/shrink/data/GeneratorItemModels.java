package net.gigabit101.shrink.data;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.items.ShrinkItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GeneratorItemModels extends ItemModelProvider
{
    public GeneratorItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, Shrink.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        singleTexture(ShrinkItems.SHRINKING_DEVICE.get().getRegistryName().getPath(), mcLoc("item/handheld"), "layer0", modLoc("item/" + ShrinkItems.SHRINKING_DEVICE.get().getRegistryName().getPath()));
        singleTexture(ShrinkItems.MOB_BOTTLE.get().getRegistryName().getPath(), mcLoc("item/handheld"), "layer0", mcLoc("item/" + Items.GLASS_BOTTLE.getRegistryName().getPath()));
    }

    private void registerBlockModel(Block block)
    {
        String path = block.getRegistryName().getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    @Override
    public String getName() {
        return "Item Models";
    }

}
