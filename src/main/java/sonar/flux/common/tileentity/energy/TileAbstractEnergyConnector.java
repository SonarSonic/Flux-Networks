package sonar.flux.common.tileentity.energy;

import net.minecraft.util.EnumFacing;
import sonar.flux.api.tiles.IFluxConnection;
import sonar.flux.common.tileentity.TileFlux;

import java.util.HashMap;
import java.util.Map;

public abstract class TileAbstractEnergyConnector extends TileFlux implements IFluxConnection {

	public Map<EnumFacing, FluxConnectionWrapper> wrappers = new HashMap<>();
	{
		wrappers.put(null, new FluxConnectionWrapper(null, this));
		for(EnumFacing face : EnumFacing.VALUES){
			wrappers.put(face, new FluxConnectionWrapper(face, this));
		}
	}
	public TileAbstractEnergyConnector(ConnectionType type) {
		super(type);
	}

	public FluxConnectionWrapper getConnectionWrapper(EnumFacing side){
		return wrappers.get(side);
	}
	
	public void onNeighborChange(EnumFacing direction) {
		updateTransfers(direction);
	}
	
}
