package net.gigabit101.shrink;

import net.gigabit101.shrink.init.ModContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShrinkingDeviceContainer extends AbstractContainerMenu
{
    public ShrinkingDeviceContainer(int id, Inventory playerInv, FriendlyByteBuf friendlyByteBuf)
    {
        super(ModContainers.SHRINKING_DEVICE.get(), id);
        drawPlayersInv(playerInv, 8, 84);
        drawPlayersHotBar(playerInv, 8, 84 + 58);
    }

    public void drawPlayersInv(Inventory player, int x, int y)
    {
        int i;
        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(player, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
    }

    public void drawPlayersHotBar(Inventory player, int x, int y)
    {
        int i;
        for (i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(player, i, x + i * 18, y));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }
}
