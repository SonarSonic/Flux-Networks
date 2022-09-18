package sonar.fluxnetworks;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.common.integration.MUIIntegration;
import sonar.fluxnetworks.register.Registration;

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

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        if (sModernUILoaded) {
            bus.register(MUIIntegration.class);
        }
        Registration.register(bus);
    }

    public static boolean isCuriosLoaded() {
        return sCuriosLoaded;
    }

    // TODO: re-enable the MUI integration
    public static boolean isModernUILoaded() {
//        return sModernUILoaded;
        return false;
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }
}
