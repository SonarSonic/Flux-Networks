package sonar.flux.network;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import sonar.flux.BlockRenderRegister;
import sonar.flux.ItemRenderRegister;
import sonar.flux.client.RenderFluxStorage;
import sonar.flux.common.tileentity.TileEntityStorage;

public class FluxClient extends FluxCommon {

	@Override
	public void registerRenderThings() {
		BlockRenderRegister.register();
		ItemRenderRegister.register();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStorage.class, new RenderFluxStorage());
	}
}
