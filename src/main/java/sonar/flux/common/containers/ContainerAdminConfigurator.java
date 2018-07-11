package sonar.flux.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import sonar.flux.connection.FluxListener;
import sonar.flux.network.FluxNetworkCache;

import javax.annotation.Nonnull;

public class ContainerAdminConfigurator extends Container {
	public EntityPlayer player;

	public ContainerAdminConfigurator(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player) {
		return true;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (!this.player.getEntityWorld().isRemote) {
			FluxNetworkCache.instance().getListenerList().removeListener(player, false, FluxListener.ADMIN);
		}
	}

	@Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return ItemStack.EMPTY;
	}

}