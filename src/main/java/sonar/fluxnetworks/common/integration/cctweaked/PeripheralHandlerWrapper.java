package sonar.fluxnetworks.common.integration.cctweaked;

import dan200.computercraft.api.ForgeComputerCraftAPI;

/**
 * This class exists, so we do not import PeripheralHandler directly into the FluxNetworks class
 * as PeripheralHandler implements a computercraft class. This would break if the computercraft mod
 * is not available
 */
public class PeripheralHandlerWrapper {

    public static void init() {
        ForgeComputerCraftAPI.registerPeripheralProvider(new PeripheralHandler());
    }

}
