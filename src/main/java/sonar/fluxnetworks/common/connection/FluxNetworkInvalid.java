package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.api.network.EnumSecurityType;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FluxConstants;

public class FluxNetworkInvalid extends SimpleFluxNetwork {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(FluxConstants.INVALID_NETWORK_ID, "Please select a network", EnumSecurityType.PUBLIC,
                FluxConstants.INVALID_NETWORK_COLOR, FluxConstants.DEFAULT_UUID, EnergyType.FE, "");
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
