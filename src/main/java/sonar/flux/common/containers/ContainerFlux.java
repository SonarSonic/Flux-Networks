package sonar.flux.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.inventory.ContainerSync;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.common.tileentity.TileEntityFlux;

public class ContainerFlux extends ContainerSync {
    public TileEntityFlux entity;
    public EntityPlayer player;

    public GuiTypeMessage state;

	public ContainerFlux(EntityPlayer player, TileEntityFlux entity, boolean network) {
		super(entity);
        this.entity = entity;
        this.player = player;
	}

    public void switchState(GuiTypeMessage state) {
		if (entity.isServer()) {
            entity.listeners.clearListener(entity.listeners.findListener(player));
            entity.listeners.addListener(player, state.getViewingType());//I think this sends the packet again or messes with block pos
		}
		this.state = state;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
        if (entity.isServer()) {
            entity.listeners.clearListener(entity.listeners.findListener(player));
        }
	}
}