package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.client.FluxColourHandler;
import sonar.flux.connection.FluxNetworkClient;
import sonar.flux.connection.FluxNetworkInvalid;
import sonar.flux.connection.NetworkSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientNetworkCache implements IFluxNetworkCache {

	public Map<Integer, IFluxNetwork> networks = new HashMap<>();
	public List<ClientFlux> disconnected_tiles = new ArrayList<>();

	public void clearNetworks() {
		networks.clear();
		disconnected_tiles.clear();
	}

	@Override
	public IFluxNetwork getNetwork(int iD) {
		IFluxNetwork network = networks.get(iD);
		if (network != null && !network.isFakeNetwork()) {
			return network;
		}
		return FluxNetworkInvalid.INVALID;
	}

	public void updateNetworksFromPacket(Map<Integer, NBTTagCompound> network_updates, NBTHelper.SyncType type){
		network_updates.forEach((I, NBT) ->{
			IFluxNetwork network = getNetwork(I);
			if(network.isFakeNetwork() && type.isType(NBTHelper.SyncType.SAVE)){
				network = new FluxNetworkClient();
				network.readData(NBT, type);
				networks.put(network.getNetworkID(), network);
			}else{
				network.readData(NBT, type);
			}
			FluxColourHandler.loadColourCache(network.getNetworkID(), network.getSetting(NetworkSettings.NETWORK_COLOUR).getRGB());
			FluxColourHandler.loadNameCache(network.getNetworkID(), network.getSetting(NetworkSettings.NETWORK_NAME));
		});
	}

	@Override
	public List<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		List<IFluxNetwork> available = new ArrayList<>();
		for (IFluxNetwork network : getAllNetworks()) {
			if (network.getPlayerAccess(player).canConnect()) {
				available.add(network);
			}
		}
		return available;
	}

	@Override
	public List<IFluxNetwork> getAllNetworks() {
		return new ArrayList<>(networks.values());
	}

	public static ClientNetworkCache instance() {
		return FluxNetworks.getClientCache();
	}
}
