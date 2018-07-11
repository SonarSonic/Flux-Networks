package sonar.flux.connection;

import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.PlayerAccess;

import java.util.Optional;
import java.util.UUID;

// TODO PACKETS FOR ALL CHANGES ? \\
public class FluxNetworkClient extends FluxNetworkBase {

    @Override
    public void addPlayerAccess(String username, PlayerAccess access) {}

    @Override
    public void removePlayerAccess(UUID uuid, PlayerAccess access) {}

    @Override
    public Optional<FluxPlayer> getValidFluxPlayer(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean isFakeNetwork() {
        return false;
    }
}
