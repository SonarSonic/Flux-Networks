package icyllis.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;

public interface INetworkConnector {

    int getNetworkID();

    IFluxNetwork getNetwork();

    void open(PlayerEntity player);

    void close(PlayerEntity player);
}
