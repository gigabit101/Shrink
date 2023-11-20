package net.gigabit101.shrink.client.widgets;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ShrinkButton extends Button
{
    public ShrinkButton(int i, int j, int k, int l, Component component, OnPress onPress)
    {
        super(i, j, k, l, component, onPress, DEFAULT_NARRATION);
    }
}
