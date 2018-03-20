package sonar.flux.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import sonar.flux.api.tiles.IFluxPlug;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

	public TileFluxPlug() {
		super(ConnectionType.PLUG);
		customName.setDefault("Flux Plug");
	}
}