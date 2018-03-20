package sonar.flux.common.tileentity;

import sonar.flux.api.tiles.IFluxPoint;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {
	
	public TileFluxPoint() {
		super(ConnectionType.POINT);
		customName.setDefault("Flux Point");
	}
}