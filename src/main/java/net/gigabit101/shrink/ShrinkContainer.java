package net.gigabit101.shrink;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class ShrinkContainer extends AbstractContainerMenu
{
    public ShrinkContainer(int id, Inventory playerInv)
    {
        super(Shrink.SHRINK_CONTAINER.get(), id);

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
    public boolean stillValid(Player player)
    {
        return true;
    }
}
