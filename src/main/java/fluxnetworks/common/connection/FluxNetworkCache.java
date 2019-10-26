package fluxnetworks.common.connection;

import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.Coord4D;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.data.FluxNetworkData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cached all flux networks
 */
public class FluxNetworkCache {

    public static FluxNetworkCache instance = new FluxNetworkCache();

    /** Client Cache **/
    public Map<Integer, IFluxNetwork> networks = new HashMap<>();
    public boolean superAdminClient = false;

    public void clearNetworks() {
        FluxNetworkData.clear();
    }

    public void clearClientCache() {
        networks.clear();
        superAdminClient = false;
    }

    public boolean hasSpaceLeft(EntityPlayer player) {
        if(FluxConfig.maximumPerPlayer == -1) {
            return true;
        }
        UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
        List<IFluxNetwork> created = getAllNetworks().stream().filter(s -> s.getSetting(NetworkSettings.NETWORK_OWNER).equals(uuid)).collect(Collectors.toList());
        return created.size() < FluxConfig.maximumPerPlayer;
    }

    public IFluxNetwork createdNetwork(EntityPlayer player, String name, int color, SecurityType securityType, EnergyType energyType, String password) {
        UUID uuid = EntityPlayer.getUUID(player.getGameProfile());

        NetworkMember owner = NetworkMember.createNetworkMember(player, AccessPermission.OWNER);
        FluxNetworkServer network = new FluxNetworkServer(getUniqueID(), name, securityType, color, uuid, energyType, password);
        network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(owner);

        FluxNetworkData.get().addNetwork(network);
        return network;
    }

    private int getUniqueID() {
        return FluxNetworkData.get().uniqueID++;
    }

    /** Client Only **/
    public void updateClientFromPacket(Map<Integer, NBTTagCompound> serverSideNetworks, NBTType type) {
        serverSideNetworks.forEach((i, n) -> {
            IFluxNetwork network = networks.get(i);
            if(type == NBTType.NETWORK_CLEAR) {
                if(network != null) {
                    networks.remove(i);
                    return;
                }
            }
            if(network == null) {
                network = new FluxLiteNetwork();
                network.readNetworkNBT(n, type);
                networks.put(i, network);
            } else {
                network.readNetworkNBT(n, type);
            }
        });
    }

    public void updateClientConnections(int networkID, List<NBTTagCompound> tags) {
        IFluxNetwork network = networks.get(networkID);
        if(network != null) {
            List<IFluxConnector> connectors = network.getSetting(NetworkSettings.ALL_CONNECTORS);
            tags.forEach(t -> {
                Coord4D coord4D = new Coord4D(t);
                connectors.stream().filter(f -> f.getCoords().equals(coord4D)).findFirst().ifPresent(f -> f.readCustomNBT(t, NBTType.DEFAULT));
            });
        }
    }

    /** Server Only **/
    public IFluxNetwork getNetwork(int id) {
        return FluxNetworkData.get().networks.getOrDefault(id, FluxNetworkInvalid.instance);
    }

    /** Server Only **/
    public Collection<IFluxNetwork> getAllNetworks() {
        return FluxNetworkData.get().networks.values();
    }

    /** Client Only **/
    public IFluxNetwork getClientNetwork(int id) {
        return networks.getOrDefault(id, FluxNetworkInvalid.instance);
    }

    /** Client Only **/
    public List<IFluxNetwork> getAllClientNetworks() {
        return new ArrayList<>(networks.values());
    }

}
