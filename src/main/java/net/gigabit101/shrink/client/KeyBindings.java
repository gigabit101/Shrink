package net.gigabit101.shrink.client;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.items.ShrinkItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings
{
    private static final KeyConflictShrink CONFLICT_CONTEXT_GADGET = new KeyConflictShrink();
    public static KeyBinding shrink;

    public static void init()
    {
        shrink = createBinding("shrink", GLFW.GLFW_KEY_G);
    }

    private static KeyBinding createBinding(String name, int key)
    {
        KeyBinding keyBinding = new KeyBinding(getKey(name), CONFLICT_CONTEXT_GADGET, InputMappings.Type.KEYSYM.getOrMakeInput(key), getKey("category"));
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
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
            PlayerEntity player = Minecraft.getInstance().player;
            return !KeyConflictContext.GUI.isActive() && player != null
                    && (player.inventory.hasItemStack(new ItemStack(ShrinkItems.SHRINKING_DEVICE.get())));
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return other == this || other == KeyConflictContext.IN_GAME;
        }
    }

}
