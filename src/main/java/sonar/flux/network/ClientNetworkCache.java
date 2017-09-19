package sonar.flux.network;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.connection.EmptyFluxNetwork;

public class ClientNetworkCache implements IFluxNetworkCache {

    public ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>> networks = new ConcurrentHashMap<>();

	@Override
	public IFluxNetwork getNetwork(int iD) {
		for (Entry<UUID, ArrayList<IFluxNetwork>> entry : networks.entrySet()) {
			for (IFluxNetwork common : entry.getValue()) {
				if (!common.isFakeNetwork() && iD == common.getNetworkID()) {
					return common;
				}
			}
		}
		return EmptyFluxNetwork.INSTANCE;
	}

	@Override
	public ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
        ArrayList<IFluxNetwork> available = new ArrayList<>();
		for (IFluxNetwork network : getAllNetworks()) {
			if (network.getPlayerAccess(player).canConnect()) {
				available.add(network);
			}
		}
		return available;
	}

	@Override
	public ArrayList<IFluxNetwork> getAllNetworks() {
        ArrayList<IFluxNetwork> available = new ArrayList<>();
		for (Entry<UUID, ArrayList<IFluxNetwork>> entry : networks.entrySet()) {
			available.addAll(entry.getValue());
		}
		return available;
	}
}
