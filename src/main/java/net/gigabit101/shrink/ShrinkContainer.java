package net.gigabit101.shrink;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class ShrinkContainer extends Container
{
    public ShrinkContainer(int id, PlayerInventory playerInv)
    {
        super(Shrink.shrinkingdevice, id);

        drawPlayersInv(playerInv, 8, 85);
        drawPlayersHotBar(playerInv, 8, 85 + 58);
    }

    public void drawPlayersInv(PlayerInventory player, int x, int y)
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

    public void drawPlayersHotBar(PlayerInventory player, int x, int y)
    {
        int i;
        for (i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(player, i, x + i * 18, y));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity)
    {
        return true;
    }
}
