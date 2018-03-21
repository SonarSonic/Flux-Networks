package sonar.flux.common.tileentity;

import sonar.flux.api.tiles.IFluxPlug;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

	public TileFluxPlug() {
		super(ConnectionType.PLUG);
		customName.setDefault("Flux Plug");
	}
}