package sonar.flux.api.network;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

public interface IFluxNetworkCache {

    IFluxNetwork getNetwork(int iD);

    ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin);

    ArrayList<IFluxNetwork> getAllNetworks();
}
