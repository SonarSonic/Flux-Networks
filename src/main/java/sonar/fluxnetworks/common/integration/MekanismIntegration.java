package sonar.fluxnetworks.common.integration;

import sonar.fluxnetworks.FluxNetworks;
import mekanism.api.MekanismAPI;

public class MekanismIntegration {

    public static void preInit(){
        MekanismAPI.addBoxBlacklistMod(FluxNetworks.MODID);
    }
}
