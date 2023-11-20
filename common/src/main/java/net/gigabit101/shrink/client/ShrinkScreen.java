package net.gigabit101.shrink.client;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.ShrinkingDeviceContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.client.widgets.ShrinkButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ShrinkScreen extends AbstractContainerScreen<ShrinkingDeviceContainer>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Shrink.MOD_ID, "textures/gui/shrinking_device.png");
    private float xMouse;
    private float yMouse;
    private float scale;

    private float MAX_SIZE = 10.0F;
    private float MIN_SIZE = 0.12F;

    public ShrinkScreen(ShrinkingDeviceContainer abstractContainerMenu, Inventory inventory, Component component)
    {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init()
    {
        super.init();
        int x = width / 2;
        this.scale = (float) minecraft.player.getAttributeValue(ShrinkAPI.SCALE_ATTRIBUTE);

        this.addRenderableWidget(new ShrinkButton(x - 20, topPos + 10, 40, 20, Component.literal("^"), b ->
        {
            if (minecraft.player == null) return;
            if(scale <= MAX_SIZE)
            {
                if(Screen.hasShiftDown())
                {
                    scale += 1.0F;

                }
                else
                {
                    scale += 0.1F;
                }
            }
        }));

        this.addRenderableWidget(new ShrinkButton(x - 20, topPos + 50, 40, 20, Component.literal("v"), b ->
        {
            if (minecraft.player == null) return;
            if(scale >= MIN_SIZE)
            {
                if(Screen.hasShiftDown())
                {
                    scale -= 1.0F;

                }
                else
                {
                    scale -= 0.1F;
                }
            }
            if(scale < MIN_SIZE) scale = 0.21F;
        }));
    }

    @Override
    public void onClose()
    {
        super.onClose();
        //TODO send packet to the server to add the target size to itemstack
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j)
    {
        int relX = this.leftPos;
        int relY = this.topPos;
        guiGraphics.blit(TEXTURE, relX - 23, relY, 0, 0, this.imageWidth + 23, this.imageHeight);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, relX + 26, relY + 8, relX + 60, relY + 120, 30, 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, relX + 26, relY + 8, relX + 250, relY + 120, (int) (30 * scale), 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);

        //        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics,i + 30, j + 70, (int) (30 * scale), (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.minecraft.player);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f)
    {
        super.render(guiGraphics, i, j, f);
        renderTooltip(guiGraphics, i, j);

        String scaleString = ("" + scale).substring(0, 3);
        guiGraphics.drawCenteredString(font, scaleString, this.width / 2, this.topPos + 35, 0xFFFFFF);

        this.xMouse = (float)i;
        this.yMouse = (float)j;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {}
}
