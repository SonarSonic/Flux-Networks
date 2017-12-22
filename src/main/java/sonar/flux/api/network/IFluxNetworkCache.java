package sonar.flux.api.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface IFluxNetworkCache {

    IFluxNetwork getNetwork(int iD);

    List<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin);

    List<IFluxNetwork> getAllNetworks();
}
