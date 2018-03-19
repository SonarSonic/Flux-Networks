package sonar.flux.common.tileentity;

import sonar.core.utils.IGuiTile;
import sonar.flux.api.tiles.IFluxPoint;

public class TileFluxPoint extends TileFluxConnector implements IGuiTile, IFluxPoint {
	
	public TileFluxPoint() {
		super(ConnectionType.POINT);
		customName.setDefault("Flux Point");
	}
}