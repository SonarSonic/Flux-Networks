package sonar.flux.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.connection.EmptyFluxNetwork;

public class ClientNetworkCache implements IFluxNetworkCache {

	public HashMap<Integer, IFluxNetwork> networks = new HashMap<>();

	public void clearNetworks() {
		networks.clear();
	}

	@Override
	public IFluxNetwork getNetwork(int iD) {
		IFluxNetwork network = networks.get(iD);
		if (network!=null && !network.isFakeNetwork()) {
			return network;
		}
		return EmptyFluxNetwork.INSTANCE;
	}

	public void updateNetworksFromPacket(ArrayList<? extends IFluxNetwork> packet, boolean updateEntireList) {
		if (updateEntireList) {
			packet.forEach(network -> readNetworkFromPacket(network));
		} else {
			HashMap newMap = new HashMap();
			packet.forEach(network -> newMap.put(network.getNetworkID(), network));
			networks = newMap;
		}
	}

	public void readNetworkFromPacket(IFluxNetwork network) {
		IFluxNetwork storedNet = getNetwork(network.getNetworkID());
		if (storedNet == null || storedNet.isFakeNetwork()) {
			networks.put(network.getNetworkID(), network);
		} else {
			storedNet.updateNetworkFrom(network); //potentially the cause of massive lag/crash when creating networks for first time
		}
	}

	@Override
	public List<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		ArrayList<IFluxNetwork> available = new ArrayList<>();
		for (IFluxNetwork network : getAllNetworks()) {
			if (network.getPlayerAccess(player).canConnect()) {
				available.add(network);
			}
		}
		return available;
	}

	@Override
	public List<IFluxNetwork> getAllNetworks() {
		ArrayList<IFluxNetwork> available = new ArrayList<>();
		networks.values().forEach(net -> available.add(net));
		return available;
	}

	public static ClientNetworkCache instance() {
		return FluxNetworks.getClientCache();
	}
}
