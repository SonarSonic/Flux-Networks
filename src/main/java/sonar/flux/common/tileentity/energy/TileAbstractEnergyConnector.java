package sonar.flux.common.tileentity.energy;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.EnumFacing;
import sonar.flux.api.tiles.IFluxConnection;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.transfer.handlers.FluxConnectionWrapper;

public abstract class TileAbstractEnergyConnector extends TileFlux implements IFluxConnection {

	public Map<EnumFacing, FluxConnectionWrapper> wrappers = Maps.newHashMap();
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
	
}
