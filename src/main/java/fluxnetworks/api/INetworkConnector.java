package fluxnetworks.api;

import fluxnetworks.api.network.IFluxNetwork;
import net.minecraft.entity.player.EntityPlayer;

///refers to anything which can be connected to a specific network, flux connectors & configurators
public interface INetworkConnector {

    int getNetworkID();

    IFluxNetwork getNetwork();

    void open(EntityPlayer player);

    void close(EntityPlayer player);

    //void setNetworkFromClientSide(int network);
}
