package net.gigabit101.shrink.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.gigabit101.shrink.Shrink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.resources.ResourceLocation;

public class GuiBuilder
{
    public static final ResourceLocation GUI_SHEET = new ResourceLocation(Shrink.MOD_ID.toLowerCase() + ":" + "textures/gui/gui_sheet.png");

    public void drawDefaultBackground(GuiGraphics gui, int x, int y, int width, int height, int textureXSize, int textureYSize)
    {
        RenderSystem.setShaderTexture(0, GUI_SHEET);

        gui.blit(GUI_SHEET, x, y, 0, 0, width / 2, height / 2, textureXSize, textureYSize);
        gui.blit(GUI_SHEET, x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2, textureXSize, textureYSize );
        gui.blit(GUI_SHEET, x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2, textureXSize, textureYSize);
        gui.blit(GUI_SHEET, x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2, textureXSize, textureYSize);
    }

    public void drawBlackBox(GuiGraphics gui, int x, int y, int width, int height, int textureXSize, int textureYSize)
    {
//        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_SHEET);


        gui.blit(GUI_SHEET, x, y, 150, 32, width, height, textureXSize, textureYSize);
    }

    public void drawPlayerSlots(GuiGraphics gui, int posX, int posY, boolean center, int textureXSize, int textureYSize)
    {
        RenderSystem.setShaderTexture(0, GUI_SHEET);
        if (center)
        {
            posX -= 81;
        }
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                gui.blit(GUI_SHEET, posX + x * 18, posY + y * 18, 150, 0, 18, 18, textureXSize, textureYSize);
            }
        }
        for (int x = 0; x < 9; x++)
        {
            gui.blit(GUI_SHEET, posX + x * 18, posY + 58, 150, 0, 18, 18, textureXSize, textureYSize);
        }
    }

    public void drawSlot(GuiGraphics gui, int posX, int posY, int textureXSize, int textureYSize)
    {
        RenderSystem.setShaderTexture(0, GUI_SHEET);
        gui.blit(GUI_SHEET, posX, posY, 150, 0, 18, 18, textureXSize, textureYSize);
    }

//    public TextFormatting getPercentageColour(int percentage)
//    {
//        if (percentage <= 10)
//        {
//            return TextFormatting.RED;
//        } else if (percentage >= 75)
//        {
//            return TextFormatting.GREEN;
//        } else
//        {
//            return TextFormatting.YELLOW;
//        }
//    }

    public int percentage(int MaxValue, int CurrentValue)
    {
        if (CurrentValue == 0)
            return 0;
        return (int) ((CurrentValue * 100.0f) / MaxValue);
    }

    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }
}
