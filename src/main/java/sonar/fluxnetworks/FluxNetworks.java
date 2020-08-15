package sonar.fluxnetworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.register.IProxy;
import sonar.fluxnetworks.register.ProxyClient;
import sonar.fluxnetworks.register.ProxyServer;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID        = "fluxnetworks";
    public static final String NAME_COMPACT = "FluxNetworks";

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ProxyClient::new, () -> ProxyServer::new);

    public static final Logger LOGGER = LogManager.getLogger(NAME_COMPACT);

    public static boolean modernUILoaded = false;

    public FluxNetworks() {
        LOGGER.info("FLUX NETWORKS INIT");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        FluxConfig.init();

        MinecraftForge.EVENT_BUS.register(PROXY);

        modernUILoaded = ModList.get().isLoaded("modernui");
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        //TODO waiting for update
        //InterModComms.sendTo("carryon", "blacklistBlock", () -> FluxNetworks.MODID + ":*");

        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIntegration::new);
        }
    }
}
