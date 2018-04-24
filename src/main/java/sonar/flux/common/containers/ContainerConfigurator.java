package sonar.flux.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerConfigurator extends Container {
    public EntityPlayer player;

    public ContainerConfigurator(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return true;
    }

    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        return ItemStack.EMPTY;
    }

}