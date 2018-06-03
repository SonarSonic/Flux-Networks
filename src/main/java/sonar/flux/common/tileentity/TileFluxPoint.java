package sonar.flux.common.tileentity;

import net.minecraft.item.ItemStack;
import sonar.flux.FluxNetworks;
import sonar.flux.api.tiles.IFluxPoint;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {
	
	public TileFluxPoint() {
		super(ConnectionType.POINT);
		customName.setDefault("Flux Point");
	}

	@Override
	public ItemStack getDisplayStack() {
		return new ItemStack(FluxNetworks.fluxPoint, 1);
	}
}