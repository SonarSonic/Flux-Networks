package fluxnetworks.common.connection;

import fluxnetworks.api.MemberPermission;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class FluxNetworkInvalid extends FluxNetworkBase {

    public static final FluxNetworkInvalid instance = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(-1, "Please select a network", SecurityType.PUBLIC, 0xb6b6b6, new UUID(-1 ,-1), EnergyType.RF, "");
    }

    @Override
    public boolean isInvalid() {
        return true;
    }
}
