package fluxnetworks.common.connection;

import fluxnetworks.FluxConfig;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.IFluxNetwork;
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

    public void clearNetworks() {
        FluxNetworkData.clear();
    }

    public boolean hasSpaceLeft(EntityPlayer player) {
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

    @Deprecated
    public void updateClientFromPacket(Map<Integer, NBTTagCompound> serverSideNetworks, NBTType type) {
        serverSideNetworks.forEach((i, n) -> {
            IFluxNetwork network = getNetwork(i);
            if(!network.isInvalid()) {
                network.readNetworkNBT(n, type);
            }
        });
    }

    public IFluxNetwork getNetwork(int id) {
        return FluxNetworkData.get().networks.getOrDefault(id, FluxNetworkInvalid.instance);
    }

    public List<IFluxNetwork> getAllNetworks() {
        return new ArrayList<>(FluxNetworkData.get().networks.values());
    }

}
