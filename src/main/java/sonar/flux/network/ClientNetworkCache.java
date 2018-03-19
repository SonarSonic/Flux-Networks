package sonar.flux.network;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.connection.EmptyFluxNetwork;

public class ClientNetworkCache implements IFluxNetworkCache {

	public Map<Integer, IFluxNetwork> networks = Maps.newHashMap();

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
			networks2.forEach(network -> readNetworkFromPacket(network));
		} else {
			Map<Integer, IFluxNetwork> newMap = Maps.newHashMap();
			networks2.forEach(network -> newMap.put(network.getNetworkID(), network));
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
		List<IFluxNetwork> available = Lists.newArrayList();
		for (IFluxNetwork network : getAllNetworks()) {
			if (network.getPlayerAccess(player).canConnect()) {
				available.add(network);
			}
		}
		return available;
	}

	@Override
	public List<IFluxNetwork> getAllNetworks() {
		List<IFluxNetwork> available = Lists.newArrayList();
		networks.values().forEach(net -> available.add(net));
		return available;
	}

	public static ClientNetworkCache instance() {
		return FluxNetworks.getClientCache();
	}
}
