package sonar.flux.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.InventoryHelper;

public class ContainerConfigurator extends Container {
    public EntityPlayer player;

    public ContainerConfigurator(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        return InventoryHelper.EMPTY;
    }

}