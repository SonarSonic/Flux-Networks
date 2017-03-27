package sonar.flux.api;

import net.minecraftforge.fml.common.Loader;

public final class FluxAPI {

	public static final String MODID = "fluxnetworks";
	public static final String NAME = "fluxapi";
	public static final String VERSION = "1.0";

	public static void init() {
		if (Loader.isModLoaded("sonarcore") || Loader.isModLoaded("SonarCore")) {
			
		}
	}
}
