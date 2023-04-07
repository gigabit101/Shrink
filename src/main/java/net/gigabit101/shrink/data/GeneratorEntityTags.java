package net.gigabit101.shrink.data;

import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorEntityTags extends EntityTypeTagsProvider
{
    public GeneratorEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, "shrink", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        this.tag(ShrinkAPI.SHRINK_DENYLIST).add(EntityType.ARMOR_STAND);
    }

    @Override
    public @NotNull String getName()
    {
        return "Shrink EntityType Tags";
    }
}
