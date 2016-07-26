package sonar.flux.api;

import net.minecraftforge.fml.common.Loader;

public final class FluxAPI {

	public static final String MODID = "FluxNetworks";
	public static final String NAME = "FluxAPI";
	public static final String VERSION = "1.0";

	private static FluxWrapper flux = new FluxWrapper();

	public static void init() {
		if (Loader.isModLoaded("SonarCore")) {
			try {
				flux = (FluxWrapper) Class.forName("sonar.flux.connection.FluxHelper").newInstance();
			} catch (Exception exception) {
				System.err.println(NAME + " : FAILED TO INITILISE API");
				exception.printStackTrace();
			}
		}
	}
	/**the Flux Helper class used to manage networks and help with energy flow through networks */
	public static FluxWrapper getFluxHelper() {
		return flux;
	}
}
