package net.gigabit101.shrink.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gigabit101.shrink.ShrinkContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.PacketShrinkScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

public class ShrinkScreen extends AbstractContainerScreen<ShrinkContainer>
{
    private Button upButton;
    private Button downButton;
    private Button confirm;
    private Button cancel;
    private float scale;
    private GuiBuilder builder = new GuiBuilder();
    private float oldMouseX;
    private float oldMouseY;

    public ShrinkScreen(ShrinkContainer container, Inventory inventory, Component component)
    {
        super(container, inventory, component);
    }

    @Override
    public void init()
    {
        super.init();
        int x = width / 2;
        Minecraft.getInstance().player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> this.scale = iShrinkProvider.scale());

        this.addRenderableWidget(upButton = new Button(x - 20, topPos + 10, 40, 20, Component.m_237115_("^"), b ->
        {
            if (Minecraft.getInstance().player == null) return;
            if(scale <= ShrinkConfig.MAX_SIZE.get())
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

        this.addRenderableWidget(downButton = new Button(x - 20, topPos + 50, 40, 20, Component.m_237115_("v"), b ->
        {
            if (Minecraft.getInstance().player == null) return;
            if(scale >= ShrinkConfig.MIN_SIZE.get())
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
            if(scale < ShrinkConfig.MIN_SIZE.get()) scale = 0.21F;
        }));
    }

    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_)
    {
        scale += p_94688_;
        if(scale < ShrinkConfig.MIN_SIZE.get()) scale = 0.21F;
        if(scale > ShrinkConfig.MAX_SIZE.get()) scale = 10.0F;
        return super.mouseScrolled(p_94686_, p_94687_, p_94688_);
    }

    @Override
    public void onClose()
    {
        super.onClose();
        if (Minecraft.getInstance().player == null) return;
        PacketHandler.sendToServer(new PacketShrinkScreen(scale));
    }

    //Override to stop labels from rendering
    @Override
    protected void renderLabels(PoseStack p_230451_1_, int p_230451_2_, int p_230451_3_) {}

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y)
    {
        builder.drawDefaultBackground(this, matrixStack, leftPos, topPos, this.getXSize(), this.getYSize(), 256, 256);
        builder.drawPlayerSlots(this, matrixStack, leftPos + this.getXSize() / 2, topPos + 84, true, 256, 256);

        int i = this.leftPos;
        int j = this.topPos;
        EntityType entityType = EntityType.COW;
        LivingEntity livingEntity = (LivingEntity) entityType.create(this.minecraft.level);

        builder.drawBlackBox(this, matrixStack, i + 4, j + 4, 60, 80, 256, 256);
        builder.drawBlackBox(this, matrixStack, i + 120, j + 4, 60, 80, 256, 256);

        InventoryScreen.renderEntityInInventory(i + 30, j + 70, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.minecraft.player);
        InventoryScreen.renderEntityInInventory(i + 145, j + 70, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, livingEntity);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.renderLabels(matrixStack, mouseX, mouseY);

        String scaleString = ("" + scale).substring(0, 3);

        drawCenteredString(matrixStack, font, scaleString, this.width / 2, this.topPos + 35, 0xFFFFFF);

        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
