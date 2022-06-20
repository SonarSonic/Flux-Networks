package sonar.fluxnetworks;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.common.integration.MuiIntegration;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";
    public static final String NAME = "Flux Networks";
    public static final String NAME_CPT = "FluxNetworks";

    public static final Logger LOGGER = LogManager.getLogger(NAME_CPT);

    private static boolean sCuriosLoaded;
    private static boolean sModernUILoaded;

    public FluxNetworks() {
        sCuriosLoaded = ModList.get().isLoaded("curios");
        sModernUILoaded = ModList.get().isLoaded("modernui");

        FluxConfig.init();
        if (sModernUILoaded) {
            FMLJavaModLoadingContext.get().getModEventBus().register(MuiIntegration.class);
        }
    }

    public static boolean isCuriosLoaded() {
        return sCuriosLoaded;
    }

    public static boolean isModernUILoaded() {
        return sModernUILoaded;
    }
}
