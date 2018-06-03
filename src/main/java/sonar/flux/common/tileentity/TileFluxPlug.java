package sonar.flux.common.tileentity;

import net.minecraft.item.ItemStack;
import sonar.flux.FluxNetworks;
import sonar.flux.api.tiles.IFluxPlug;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

	public TileFluxPlug() {
		super(ConnectionType.PLUG);
		customName.setDefault("Flux Plug");
	}

	@Override
	public ItemStack getDisplayStack() {
		return new ItemStack(FluxNetworks.fluxPlug, 1);
	}
}