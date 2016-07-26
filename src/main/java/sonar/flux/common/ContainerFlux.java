package sonar.flux.common;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.inventory.ContainerSync;
import sonar.flux.FluxNetworks;
import sonar.flux.client.GuiState;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.CommonNetworkCache.ViewingType;

public class ContainerFlux extends ContainerSync {

	public GuiState state;

	public ContainerFlux(EntityPlayer player, TileEntityFlux entity, boolean network) {
		super(entity);
	}

	public void switchState(EntityPlayer player, TileEntityFlux entity, GuiState state) {
		FluxNetworks.cache.removeViewer(player);
		switch (state) {
		case INDEX:
			FluxNetworks.cache.addViewer(player, ViewingType.ADMIN, -1);
			break;
		case NETWORK_STATS:
			FluxNetworks.cache.addViewer(player, ViewingType.ONE_NET, entity.getNetwork().getNetworkID());
			break;
		case NETWORK_SELECT:
			FluxNetworks.cache.addViewer(player, ViewingType.CLIENT, entity.getNetwork().getNetworkID());
			break;
		case NETWORK_CREATE:
			break;
		case NETWORK_EDIT:
			break;
		case CONNECTIONS:
			FluxNetworks.cache.addViewer(player, ViewingType.CONNECTIONS, entity.getNetwork().getNetworkID());
			break;
		default:
			break;
		}
		this.state = state;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (!player.getEntityWorld().isRemote)
			FluxNetworks.cache.removeViewer(player);
	}
}