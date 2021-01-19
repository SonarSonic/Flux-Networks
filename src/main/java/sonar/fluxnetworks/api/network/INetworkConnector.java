package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.EntityPlayer;

///refers to anything which can be connected to a specific network, flux tiles & configurators
public interface INetworkConnector {

    int getNetworkID();

    IFluxNetwork getNetwork();

    void open(EntityPlayer player);

    void close(EntityPlayer player);

    //void setNetworkFromClientSide(int network);
}
