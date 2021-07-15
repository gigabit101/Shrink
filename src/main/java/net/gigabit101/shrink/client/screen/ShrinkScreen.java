package net.gigabit101.shrink.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.gigabit101.shrink.ShrinkContainer;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.gigabit101.shrink.config.ShrinkConfig;
import net.gigabit101.shrink.network.PacketHandler;
import net.gigabit101.shrink.network.PacketShrinkScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ShrinkScreen extends ContainerScreen<ShrinkContainer>
{
    private Button upButton;
    private Button downButton;
    private Button confirm;
    private Button cancel;
    private float scale;
    private GuiBuilder builder = new GuiBuilder();
    private float oldMouseX;
    private float oldMouseY;

    public ShrinkScreen(ShrinkContainer shrinkContainer, PlayerInventory playerInventory, ITextComponent iTextComponent)
    {
        super(shrinkContainer, playerInventory, new StringTextComponent(""));
    }

    @Override
    public void init()
    {
        super.init();
        int x = width / 2;
        Minecraft.getInstance().player.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider -> this.scale = iShrinkProvider.scale());

        this.addButton(upButton = new Button(x - 20, guiTop + 10, 40, 20, new TranslationTextComponent("^"), b ->
        {
            if (Minecraft.getInstance().player == null) return;
            if(scale <= ShrinkConfig.MAX_SIZE.get()) scale += 0.1F;
        }));

        this.addButton(downButton = new Button(x - 20, guiTop + 50, 40, 20, new TranslationTextComponent("v"), b ->
        {
            if (Minecraft.getInstance().player == null) return;
            if(scale >= ShrinkConfig.MIN_SIZE.get()) scale -= 0.1F;
            if(scale < ShrinkConfig.MIN_SIZE.get()) scale = 0.21F;
        }));
    }

    @Override
    public void onClose()
    {
        super.onClose();
        if (Minecraft.getInstance().player == null) return;
        PacketHandler.sendToServer(new PacketShrinkScreen(scale));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        builder.drawDefaultBackground(this, matrixStack, guiLeft, guiTop, xSize, ySize, 256, 256);
        builder.drawPlayerSlots(this, matrixStack, guiLeft + xSize / 2, guiTop + 84, true, 256, 256);

        int i = this.guiLeft;
        int j = this.guiTop;
        EntityType entityType = EntityType.COW;
        LivingEntity livingEntity = (LivingEntity) entityType.create(this.minecraft.world);

        builder.drawBlackBox(this, matrixStack, i + 4, j + 4, 60, 80, 256, 256);
        builder.drawBlackBox(this, matrixStack, i + 120, j + 4, 60, 80, 256, 256);

        drawEntityOnScreen(i + 30, j + 70, scale, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.minecraft.player);
        drawEntityOnScreen(i + 145, j + 70, scale, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, livingEntity);
    }

    public static void drawEntityOnScreen(int posX, int posY, float scale, float mouseX, float mouseY, LivingEntity livingEntity)
    {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float f1 = (float)Math.atan((double)(mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale(30, 30, 30);
        matrixstack.scale(scale, scale, scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = livingEntity.renderYawOffset;
        float f3 = livingEntity.rotationYaw;
        float f4 = livingEntity.rotationPitch;
        float f5 = livingEntity.prevRotationYawHead;
        float f6 = livingEntity.rotationYawHead;
        livingEntity.renderYawOffset = 180.0F + f * 20.0F;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        livingEntity.rotationPitch = -f1 * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
        });
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        livingEntity.renderYawOffset = f2;
        livingEntity.rotationYaw = f3;
        livingEntity.rotationPitch = f4;
        livingEntity.prevRotationYawHead = f5;
        livingEntity.rotationYawHead = f6;
        RenderSystem.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {}

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        String scaleString = ("" + scale).substring(0, 3);

        drawCenteredString(matrixStack, font, scaleString, this.width / 2, this.guiTop + 35, 0xFFFFFF);

        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
