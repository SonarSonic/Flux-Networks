package sonar.flux.common;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.inventory.ContainerSync;
import sonar.flux.FluxNetworks;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.common.tileentity.TileEntityFlux;

public class ContainerFlux extends ContainerSync {

	public GuiTypeMessage state;
	public TileEntityFlux entity;
	public EntityPlayer player;

	public ContainerFlux(EntityPlayer player, TileEntityFlux entity, boolean network) {
		super(entity);
		this.entity = entity;
		this.player = player;
	}

	public void switchState(GuiTypeMessage state) {
		if (entity.isServer()) {
			int networkID = entity.getNetworkID();
			entity.listeners.clearListener(entity.listeners.findListener(player));
			entity.listeners.addListener(player, state.getViewingType());
		}
		this.state = state;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (entity.isServer()){
			entity.listeners.clearListener(entity.listeners.findListener(player));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}