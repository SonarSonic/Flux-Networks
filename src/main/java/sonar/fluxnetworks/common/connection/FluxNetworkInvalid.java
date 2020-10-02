package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.api.network.EnumSecurityType;
import sonar.fluxnetworks.api.misc.EnergyType;

import java.util.UUID;

public class FluxNetworkInvalid extends FluxNetworkBase {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(-1, "Please select a network", EnumSecurityType.PUBLIC, 0xb6b6b6, new UUID(-1, -1), EnergyType.FE, "");
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
