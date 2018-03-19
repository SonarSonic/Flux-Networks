package sonar.flux.network;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import sonar.flux.client.RenderFluxStorage;
import sonar.flux.common.tileentity.TileStorage;

public class FluxClient extends FluxCommon {

	@Override
	public void registerRenderThings() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileStorage.class, new RenderFluxStorage());
	}
}
