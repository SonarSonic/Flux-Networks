package icyllis.fluxnetworks.system;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";

    public static Logger logger = LogManager.getLogger("FluxNetworks");

    public FluxNetworks() {
        FluxConfig.setup();
    }

}