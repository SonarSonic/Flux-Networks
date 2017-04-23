package sonar.flux.api.network;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

public interface IFluxNetworkCache {

	public IFluxNetwork getNetwork(int iD);

	public ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin);

	public ArrayList<IFluxNetwork> getAllNetworks();

}
