package sonar.flux.connection;

import sonar.core.api.energy.EnergyType;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.PlayerAccess;

import java.util.Optional;
import java.util.UUID;

public class FluxNetworkInvalid extends FluxNetworkBase {

    //public FluxNetworkBase(int ID, UUID owner, String cached_player, String name, CustomColour colour, AccessType type, boolean conversion, EnergyType energy_type) {
    public static final FluxNetworkInvalid INVALID = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(-1, new UUID(-1,-1), "", "Please select a network", new CustomColour(178, 178, 178), AccessType.PRIVATE, true, EnergyType.FE);
    }

    @Override
    public void removePlayerAccess(UUID uuid, PlayerAccess access) {}

    @Override
    public void addPlayerAccess(String username, PlayerAccess access) {}

    @Override
    public Optional<FluxPlayer> getValidFluxPlayer(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean isFakeNetwork() {
        return true;
    }

}