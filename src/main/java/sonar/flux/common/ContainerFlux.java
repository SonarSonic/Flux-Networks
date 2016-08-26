package sonar.flux.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.inventory.ContainerSync;
import sonar.flux.FluxNetworks;
import sonar.flux.client.GuiState;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.FluxNetworkCache.ViewingType;

public class ContainerFlux extends ContainerSync {

	public GuiState state;

	public ContainerFlux(EntityPlayer player, TileEntityFlux entity, boolean network) {
		super(entity);
	}

	public void switchState(EntityPlayer player, TileEntityFlux entity, GuiState state) {
		if (entity.isServer()) {
			FluxNetworks.getServerCache().removeViewer(player);
			FluxNetworks.getServerCache().addViewer(player, state.getViewingType(), entity.getNetwork().getNetworkID());
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
			FluxNetworks.getServerCache().removeViewer(player);
	}
}