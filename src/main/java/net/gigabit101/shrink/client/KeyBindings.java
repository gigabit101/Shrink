package net.gigabit101.shrink.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.items.ItemShrinkingDevice;
import net.gigabit101.shrink.items.ShrinkItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings
{
    private static final KeyConflictShrink CONFLICT_CONTEXT_GADGET = new KeyConflictShrink();
    public static KeyMapping shrink;

    public static KeyMapping createBinding(String name, int key)
    {
        return new KeyMapping(getKey(name), CONFLICT_CONTEXT_GADGET, InputConstants.Type.KEYSYM.getOrCreate(key), getKey("category"));
    }

    private static String getKey(String name)
    {
        return String.join(".", "key", Shrink.MOD_ID, name);
    }

    public static class KeyConflictShrink implements IKeyConflictContext
    {
        @Override
        public boolean isActive()
        {
            Player player = Minecraft.getInstance().player;
            if(player == null) return false;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++)
            {
                if(player.getInventory().getItem(i).getItem() instanceof ItemShrinkingDevice)
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return other == this || other == KeyConflictContext.IN_GAME;
        }
    }

}
