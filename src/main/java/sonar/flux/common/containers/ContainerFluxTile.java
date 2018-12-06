package sonar.flux.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.common.tile.ContainerSyncable;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.ListenerHelper;

public class ContainerFluxTile extends ContainerSyncable {
	public TileFlux entity;
	public EntityPlayer player;

	public ContainerFluxTile(EntityPlayer player, TileFlux entity) {
		super(entity);
		this.entity = entity;
		this.player = player;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if(!entity.getWorld().isRemote) {
			ListenerHelper.onPlayerCloseTileGui(entity, player);
		}
	}
}