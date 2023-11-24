package net.gigabit101.shrink.client;

import net.gigabit101.shrink.Shrink;
import net.gigabit101.shrink.ShrinkingDeviceContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.client.widgets.ShrinkButton;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.packets.PacketShrinkDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class ShrinkScreen extends AbstractContainerScreen<ShrinkingDeviceContainer>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Shrink.MOD_ID, "textures/gui/shrinking_device.png");
    private float xMouse;
    private float yMouse;
    private double scale;
    private InteractionHand hand;

    private final double MAX_SIZE = Shrink.shrinkConfig.maxSize;
    private final double MIN_SIZE = Shrink.shrinkConfig.minSize;

    public ShrinkScreen(ShrinkingDeviceContainer abstractContainerMenu, Inventory inventory, Component component)
    {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init()
    {
        super.init();
        Player player = minecraft.player;
        if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemShrinkDevice itemShrinkDevice)
        {
            hand = InteractionHand.MAIN_HAND;
            this.scale = itemShrinkDevice.getScale(player.getItemInHand(InteractionHand.MAIN_HAND));
        }
        else
        {
            hand = InteractionHand.OFF_HAND;
        }

        int x = width / 2;

        this.addRenderableWidget(new ShrinkButton(x - 20, topPos + 10, 40, 20, Component.literal("^"), b ->
        {
            if (minecraft.player == null) return;
            if(scale <= MAX_SIZE)
            {
                if(Screen.hasShiftDown())
                {
                    scale += 1.0D;

                }
                else
                {
                    scale += 0.1D;
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
                    scale -= 1.0D;

                }
                else
                {
                    scale -= 0.1D;
                }
            }
            if(scale < MIN_SIZE) scale = MIN_SIZE;
        }));
    }

    @Override
    public void onClose()
    {
        super.onClose();
        PacketHandler.HANDLER.sendToServer(new PacketShrinkDevice(hand, scale));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j)
    {
        int relX = this.leftPos;
        int relY = this.topPos;
        guiGraphics.blit(TEXTURE, relX - 23, relY, 0, 0, this.imageWidth + 23, this.imageHeight);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, relX + 26, relY + 8, relX + 60, relY + 90, 30, 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, relX + 26, relY + 8, relX + 250, relY + 90, (int) (30 * scale), 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);
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
