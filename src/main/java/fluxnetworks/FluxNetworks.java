package fluxnetworks;

import fluxnetworks.common.core.proxy.ClientProxy;
import fluxnetworks.common.core.proxy.IProxy;
import fluxnetworks.common.core.proxy.ServerProxy;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";

    public static Logger logger = LogManager.getLogger("FluxNetworks");

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public FluxNetworks() {

    }

}