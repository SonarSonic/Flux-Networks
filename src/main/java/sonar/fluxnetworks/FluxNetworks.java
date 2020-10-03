package sonar.fluxnetworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.register.IProxy;
import sonar.fluxnetworks.register.ProxyClient;
import sonar.fluxnetworks.register.ProxyServer;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";
    public static final String NAME = "Flux Networks";
    public static final String NAME_COMPACT = "FluxNetworks";

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ProxyClient::new, () -> ProxyServer::new);

    public static final Logger LOGGER = LogManager.getLogger(NAME_COMPACT);

    public static boolean modernUILoaded;

    public FluxNetworks() {
        modernUILoaded = ModList.get().isLoaded("modernui");

        FluxConfig.init();
        MinecraftForge.EVENT_BUS.register(PROXY);
    }
}
