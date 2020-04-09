package sonar.fluxnetworks.api.utils;

import sonar.fluxnetworks.api.network.ISuperAdmin;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

    @CapabilityInject(ISuperAdmin.class)
    public static Capability<ISuperAdmin> SUPER_ADMIN = null;
}
