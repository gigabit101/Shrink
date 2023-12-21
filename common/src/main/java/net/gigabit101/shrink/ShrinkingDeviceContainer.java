package net.gigabit101.shrink;

import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.data.serializable.LongData;
import net.gigabit101.shrink.init.ModContainers;
import net.gigabit101.shrink.items.ItemShrinkDevice;
import net.gigabit101.shrink.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShrinkingDeviceContainer extends ModularGuiContainerMenu
{
    public final SlotGroup main = createSlotGroup(0, 1, 3); //zone id is 0, Quick move to zone 1, then 3
    public final SlotGroup hotBar = createSlotGroup(0, 1, 3);

    public final DataSync<Long> energy;
    public final DataSync<Long> maxEnergy;


    public ShrinkingDeviceContainer(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        super(ModContainers.SHRINKING_DEVICE.get(), containerId, inventory);
        ItemStack stack = inventory.player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemShrinkDevice itemShrinkDevice = (ItemShrinkDevice) stack.getItem();
        setServerToClientPacketHandler(PacketHandler::sendContainerPacketToClient);
        setClientToServerPacketHandler(PacketHandler::sendContainerPacketToServer);

        //TODO make sure its a shrinking device
        energy = new DataSync<>(this, new LongData(), () -> itemShrinkDevice.getEnergyStorage(stack).getStoredEnergy());
        maxEnergy = new DataSync<>(this, new LongData(), () -> itemShrinkDevice.getEnergyStorage(stack).getMaxCapacity());
        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }
}
