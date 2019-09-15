package fluxnetworks.common.integration;

import fluxnetworks.FluxNetworks;
import mekanism.api.MekanismAPI;

public class MekanismIntegration {

    public static void preInit(){
        MekanismAPI.addBoxBlacklistMod(FluxNetworks.MODID);
    }
}
