package sonar.flux.api.network;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public interface IFluxNetworkCache {

    IFluxNetwork getNetwork(int iD);

    ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin);

    ArrayList<IFluxNetwork> getAllNetworks();
}
