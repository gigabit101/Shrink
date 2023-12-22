package net.gigabit101.shrink.client;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.DynamicTextures;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
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
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

public class ShrinkScreen extends ContainerGuiProvider<ShrinkingDeviceContainer> implements DynamicTextures
{
    private String BACKGROUND_TEXTURE;
    private double SCALE;

    @Override
    public void makeTextures(Function<DynamicTexture, String> textures)
    {
        BACKGROUND_TEXTURE = dynamicTexture(textures, new ResourceLocation(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla"),
                new ResourceLocation(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla"), 226, 220, 4);
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui)
    {
        GuiManipulable root = new GuiManipulable(gui)
                .addResizeHandles(4, false)
                .addMoveHandle(10);
        root.enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), PolyTextures.get("dynamic/gui_vanilla")).dynamicTexture();
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ShrinkingDeviceContainer> screenAccess)
    {
        ShrinkingDeviceContainer menu = screenAccess.getMenu();
        ItemStack stack = screenAccess.getMenu().inventory.player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemShrinkDevice itemShrinkDevice = (ItemShrinkDevice) stack.getItem();
        SCALE = itemShrinkDevice.getScale(stack);

        gui.initStandardGui(226, 220);
        gui.setGuiTitle(Component.literal("Shrinking Device"));
        GuiElement<?> root = gui.getRoot();
//        GuiTexture background = new GuiTexture(root, PolyTextures.get(BACKGROUND_TEXTURE));
//        Constraints.bind(background, root);

        GuiText title = new GuiText(root, gui.getGuiTitle())
                .setTextColour(0x404040)
                .setShadow(false)
                .constrain(TOP, relative(root.get(TOP), 8))
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(LEFT, relative(root.get(LEFT), 5))
                .constrain(RIGHT, relative(root.get(RIGHT), -5));

        var inventory = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);

        inventory.container
                .constrain(WIDTH, null)
                .constrain(LEFT, match(root.get(LEFT)))
                .constrain(RIGHT, match(root.get(RIGHT)))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -8));

        GuiText invLabel = new GuiText(root, Component.translatable("container.inventory"))
                .setTextColour(0x404040)
                .setShadow(false)
                .setAlignment(Align.LEFT)
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(BOTTOM, relative(inventory.container.get(TOP), -3))
                .constrain(LEFT, relative(inventory.getPart(0).get(LEFT), 0))
                .constrain(RIGHT, relative(inventory.primary.get(RIGHT), 0));

        GuiButton upButton = GuiButton.vanillaAnimated(root, Component.literal("^"))
                .onPress(() -> onButtonPress(true))
                .constrain(LEFT, midPoint(root.get(LEFT), root.get(RIGHT), -16))
                .constrain(BOTTOM, relative(title.get(BOTTOM), 32))
                .constrain(WIDTH, literal(32))
                .constrain(HEIGHT, literal(18));


        GuiButton downButton = GuiButton.vanillaAnimated(root, Component.literal("v"))
                .onPress(() -> onButtonPress(false))
                .constrain(LEFT, midPoint(root.get(LEFT), root.get(RIGHT), -16))
                .constrain(BOTTOM, midPoint(root.get(BOTTOM), title.get(BOTTOM)))
                .constrain(WIDTH, literal(32))
                .constrain(HEIGHT, literal(18));

        GuiText scale = new GuiText(root, () -> Component.literal(String.format("%.2f", SCALE)))
                .setTextColour(0x404040)
                .setShadow(false)
                .constrain(TOP, midPoint(upButton.get(BOTTOM), downButton.get(TOP)))
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(LEFT, relative(downButton.get(LEFT), 5))
                .constrain(RIGHT, relative(downButton.get(RIGHT), -5));

        var energyBar = GuiEnergyBar.simpleBar(root);
        energyBar.container
                .constrain(LEFT, midPoint(root.get(LEFT), invLabel.get(LEFT), +4))
                .constrain(BOTTOM, relative(invLabel.get(TOP), -6))
                .constrain(WIDTH, literal(18))
                .constrain(TOP, relative(title.get(BOTTOM), 8));

        energyBar.primary.setCapacity(menu.maxEnergy::get).setEnergy(menu.energy::get);
    }

    public void onButtonPress(boolean plus)
    {
        boolean shift = Screen.hasShiftDown();
        if(plus)
        {
            if(SCALE <= Shrink.shrinkConfig.maxSize)
            {
                if (shift)
                {
                    SCALE += 1.0D;
                }
                else
                {
                    SCALE += 0.1D;
                }
            }
        }
        else
        {
            if (SCALE >= Shrink.shrinkConfig.minSize)
            {
                if (shift)
                {
                    SCALE -= 1.0D;
                }
                else
                {
                    SCALE -= 0.1D;
                }
            }
        }
        if(SCALE > Shrink.shrinkConfig.maxSize) SCALE = Shrink.shrinkConfig.maxSize;
        if(SCALE < Shrink.shrinkConfig.minSize) SCALE = Shrink.shrinkConfig.minSize;

        PacketHandler.HANDLER.sendToServer(new PacketShrinkDevice(InteractionHand.MAIN_HAND, SCALE));
    }

    public static ModularGuiContainer<ShrinkingDeviceContainer> create(ShrinkingDeviceContainer menu, Inventory inventory, Component component)
    {
        return new ModularGuiContainer<>(menu, inventory, new ShrinkScreen());
    }
}
