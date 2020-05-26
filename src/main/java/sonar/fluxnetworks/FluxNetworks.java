package sonar.fluxnetworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.register.EventHandler;
import sonar.fluxnetworks.register.IProxy;
import sonar.fluxnetworks.register.ProxyClient;
import sonar.fluxnetworks.register.ProxyServer;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";
    public static final String NAME = "Flux Networks";
    //public static final String VERSION = "5.0.0";

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ProxyClient::new, () -> ProxyServer::new);
    public static final Logger LOGGER = LogManager.getLogger("FluxNetworks");

    public static boolean modernUILoaded = false;

    public FluxNetworks() {
        LOGGER.info("FLUX NETWORKS INIT");
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FluxConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FluxConfig.CLIENT_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().register(FluxConfig.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(PROXY);
        modernUILoaded = ModList.get().isLoaded("modernui");
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("carryon", "blacklistBlock", () -> FluxNetworks.MODID + ":*");

        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIntegration::new);
        }
    }

    private void processIMC(final InterModProcessEvent event) {
    }

}
