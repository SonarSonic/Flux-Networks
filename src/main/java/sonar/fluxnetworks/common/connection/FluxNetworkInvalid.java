package sonar.fluxnetworks.common.connection;

import net.minecraft.util.Util;
import sonar.fluxnetworks.api.misc.FluxConstants;

public class FluxNetworkInvalid extends BasicFluxNetwork {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(FluxConstants.INVALID_NETWORK_ID, "Please select a network",
                FluxConstants.INVALID_NETWORK_COLOR, Util.DUMMY_UUID);
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
