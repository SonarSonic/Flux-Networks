package sonar.flux.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.inventory.containers.ContainerSync;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.ListenerHelper;

public class ContainerFlux extends ContainerSync {
	public TileFlux entity;
	public EntityPlayer player;

	public ContainerFlux(EntityPlayer player, TileFlux entity) {
		super(entity);
		this.entity = entity;
		this.player = player;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		ListenerHelper.onPlayerCloseTileGui(entity, player);
	}
}