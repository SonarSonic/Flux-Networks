package sonar.fluxnetworks.common.test;

import sonar.fluxnetworks.common.connection.FluxNetworkModel;

@Deprecated
public class NetworkSettings<T> {

    /*public static final NetworkSettings<Integer> NETWORK_ID = new NetworkSettings<>(s -> s.network_id);
    public static final NetworkSettings<String> NETWORK_NAME = new NetworkSettings<>(s -> s.network_name);
    public static final NetworkSettings<SecurityType> NETWORK_SECURITY = new NetworkSettings<>(s -> s.network_security);
    public static final NetworkSettings<String> NETWORK_PASSWORD = new NetworkSettings<>(s -> s.network_password);
    public static final NetworkSettings<Integer> NETWORK_COLOR = new NetworkSettings<>(s -> s.network_color);
    public static final NetworkSettings<UUID> NETWORK_OWNER = new NetworkSettings<>(s -> s.network_owner);
    public static final NetworkSettings<EnergyType> NETWORK_ENERGY = new NetworkSettings<>(s -> s.network_energy);
    public static final NetworkSettings<Integer> NETWORK_WIRELESS = new NetworkSettings<>(s -> s.network_wireless);
    public static final NetworkSettings<NetworkStatistics> NETWORK_STATISTICS = new NetworkSettings<>(s -> s.network_stats);
    public static final NetworkSettings<List<NetworkMember>> NETWORK_PLAYERS = new NetworkSettings<>(s -> s.network_players);
    public static final NetworkSettings<List<IFluxDevice>>   ALL_CONNECTORS  = new NetworkSettings<>(s -> s.all_connectors);*/

    public ISettingGetter<T> value;

    public NetworkSettings(ISettingGetter<T> value) {
        this.value = value;
    }

    public ICustomValue<T> getValue(FluxNetworkModel network) {
        return value.getValue(network);
    }

    private interface ISettingGetter<T> {
        ICustomValue<T> getValue(FluxNetworkModel network);
    }
}
