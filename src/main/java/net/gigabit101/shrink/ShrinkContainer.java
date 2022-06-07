package net.gigabit101.shrink;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShrinkContainer extends AbstractContainerMenu
{
    public ShrinkContainer(int id, Inventory playerInv, FriendlyByteBuf friendlyByteBuf)
    {
        super(ModContainers.SHRINK_CONTAINER.get(), id);

        drawPlayersInv(playerInv, 8, 85);
        drawPlayersHotBar(playerInv, 8, 85 + 58);
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
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int p_38942_)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }
}
