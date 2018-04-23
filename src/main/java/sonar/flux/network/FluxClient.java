package sonar.flux.network;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import sonar.core.network.SonarClient;
import sonar.core.translate.ILocalisationHandler;
import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;
import sonar.flux.client.RenderFluxStorage;
import sonar.flux.common.tileentity.TileStorage;

public class FluxClient extends FluxCommon implements ILocalisationHandler {

	@Override
	public void registerRenderThings() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileStorage.class, new RenderFluxStorage());
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		SonarClient.translator.add(this);
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
			IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		}
	}

	@Override
	public List<Localisation> getLocalisations(List<Localisation> current) {
		current.addAll(FluxTranslate.locals);
		return current;
	}
}
