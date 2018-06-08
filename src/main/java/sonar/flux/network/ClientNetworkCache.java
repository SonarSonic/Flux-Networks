package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.client.FluxColourHandler;
import sonar.flux.connection.EmptyFluxNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientNetworkCache implements IFluxNetworkCache {

	public Map<Integer, IFluxNetwork> networks = new HashMap<>();

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

	public void updateNetworksFromPacket(List<? extends IFluxNetwork> networks2, boolean updateEntireList) {
		if (updateEntireList) {
			networks2.forEach(this::readNetworkFromPacket);
		} else {
			Map<Integer, IFluxNetwork> newMap = new HashMap<>();
			networks2.forEach(network -> {
				newMap.put(network.getNetworkID(), network);
				FluxColourHandler.loadColourCache(network.getNetworkID(), network.getNetworkColour().getRGB());
				FluxColourHandler.loadNameCache(network.getNetworkID(), network.getNetworkName());
			});
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
		FluxColourHandler.loadColourCache(network.getNetworkID(), network.getNetworkColour().getRGB());
		FluxColourHandler.loadNameCache(network.getNetworkID(), network.getNetworkName());
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
		List<IFluxNetwork> available = new ArrayList<>(networks.values());
		return available;
	}

	public static ClientNetworkCache instance() {
		return FluxNetworks.getClientCache();
	}
}
