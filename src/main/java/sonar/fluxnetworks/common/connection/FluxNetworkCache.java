package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.api.tiles.IFluxDevice;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cached all flux networks
 */
//TODO make client only, use FluxNetworkData on logic server side
public class FluxNetworkCache {

    public static final FluxNetworkCache INSTANCE = new FluxNetworkCache();

    /**
     * Client Cache
     */
    public Map<Integer, IFluxNetwork> networks = new HashMap<>();
    public boolean superAdminClient = false;

    public void clearClientCache() {
        networks.clear();
        superAdminClient = false;
    }

    public boolean hasSpaceLeft(PlayerEntity player) {
        if (FluxConfig.maximumPerPlayer == -1) {
            return true;
        }
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());
        List<IFluxNetwork> created = getAllNetworks().stream().filter(s -> s.getSetting(NetworkSettings.NETWORK_OWNER).equals(uuid)).collect(Collectors.toList());
        return created.size() < FluxConfig.maximumPerPlayer;
    }

    public void createdNetwork(@Nonnull PlayerEntity player, String name, int color, EnumSecurityType securityType, EnergyType energyType, String password) {
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());

        NetworkMember owner = NetworkMember.createNetworkMember(player, EnumAccessType.OWNER);
        FluxNetworkServer network = new FluxNetworkServer(nextUID(), name, securityType, color, uuid, energyType, password);
        network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(owner);

        FluxNetworkData.get().addNetwork(network);
    }

    private int nextUID() {
        return FluxNetworkData.get().uniqueID++;
    }

    /**
     * Client Only
     **/
    public void updateClientFromPacket(Map<Integer, CompoundNBT> serverSideNetworks, NBTType type) {
        serverSideNetworks.forEach((i, n) -> {
            IFluxNetwork network = networks.get(i);
            if (type == NBTType.NETWORK_CLEAR) {
                if (network != null) {
                    networks.remove(i);
                    return;
                }
            }
            if (network == null) {
                network = new FluxLiteNetwork();
                network.readNetworkNBT(n, type);
                networks.put(i, network);
            } else {
                network.readNetworkNBT(n, type);
            }
        });
    }

    public void updateClientConnections(int networkID, List<CompoundNBT> tags) {
        IFluxNetwork network = networks.get(networkID);
        if (network != null) {
            List<IFluxDevice> connectors = network.getSetting(NetworkSettings.ALL_CONNECTORS);
            tags.forEach(t -> {
                Coord4D coord4D = new Coord4D(t);
                connectors.stream().filter(f -> f.getCoords().equals(coord4D)).findFirst().ifPresent(f -> f.readCustomNBT(t, NBTType.DEFAULT));
            });
        }
    }

    /**
     * Server Only
     **/
    public IFluxNetwork getNetwork(int id) {
        return FluxNetworkData.get().networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    /**
     * Server Only
     **/
    public Collection<IFluxNetwork> getAllNetworks() {
        return FluxNetworkData.get().networks.values();
    }

    /**
     * Client Only
     **/
    public IFluxNetwork getClientNetwork(int id) {
        return networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    /**
     * Client Only
     **/
    public List<IFluxNetwork> getAllClientNetworks() {
        return new ArrayList<>(networks.values());
    }

}
