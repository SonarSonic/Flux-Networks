package fluxnetworks.common.integration.oc;

import li.cil.oc.api.Driver;

public class OCIntegration {

    public static void init() {
        Driver.add(new OCDriver());
    }
}
