package sonar.flux.connection;

import sonar.core.api.energy.EnergyType;
import sonar.core.sync.ISonarValue;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.NetworkFluxFolder;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.transfer.stats.NetworkStatistics;

import java.util.List;
import java.util.UUID;

public class NetworkSettings<T> {

    public static final NetworkSettings<Integer> NETWORK_ID = new NetworkSettings<>(n -> n.network_id);
    public static final NetworkSettings<String> NETWORK_NAME = new NetworkSettings<>(n -> n.network_name);
    public static final NetworkSettings<AccessType> NETWORK_ACCESS = new NetworkSettings<>(n -> n.network_access);
    public static final NetworkSettings<CustomColour> NETWORK_COLOUR = new NetworkSettings<>(n -> n.network_colour);
    public static final NetworkSettings<UUID> NETWORK_OWNER = new NetworkSettings<>(n -> n.network_owner);
    public static final NetworkSettings<Boolean> NETWORK_CONVERSION = new NetworkSettings<>(n -> n.network_conversion);
    public static final NetworkSettings<EnergyType> NETWORK_ENERGY_TYPE = new NetworkSettings<>(n -> n.network_energy_type);
    public static final NetworkSettings<String> NETWORK_CACHED_NAME = new NetworkSettings<>(n -> n.cached_player_name);
    public static final NetworkSettings<List<ClientFlux>> CLIENT_CONNECTIONS = new NetworkSettings<>(n -> n.client_connections);
    public static final NetworkSettings<List<ClientFlux>> UNLOADED_CONNECTIONS = new NetworkSettings<>(n -> n.unloaded_connections);
    public static final NetworkSettings<NetworkStatistics> NETWORK_STATISTICS = new NetworkSettings<>(n -> n.network_stats);
    public static final NetworkSettings<List<FluxPlayer>> NETWORK_PLAYERS = new NetworkSettings<>(n -> n.network_players);
    public static final NetworkSettings<List<NetworkFluxFolder>> NETWORK_FOLDERS = new NetworkSettings<>(n -> n.network_folders);

    public static final NetworkSettings[] SAVED = new NetworkSettings[]{NETWORK_ID, NETWORK_NAME, NETWORK_ACCESS, NETWORK_COLOUR, NETWORK_OWNER, NETWORK_CONVERSION, NETWORK_ENERGY_TYPE, NETWORK_CACHED_NAME};
    public static final NetworkSettings[] SYNCED = new NetworkSettings[]{CLIENT_CONNECTIONS, UNLOADED_CONNECTIONS, NETWORK_STATISTICS, NETWORK_PLAYERS, NETWORK_FOLDERS};

    public ISettingGetter<T> network_value;

    public NetworkSettings(ISettingGetter<T> network_value){
        this.network_value = network_value;
    }

    public ISonarValue<T> getSyncValue(FluxNetworkBase network){
        return network_value.getValue(network);
    }

    public T getValue(IFluxNetwork network){
        return network.getSyncSetting(this).getValue();
    }

    private interface ISettingGetter<T>{
        ISonarValue<T> getValue(FluxNetworkBase network);
    }

}
