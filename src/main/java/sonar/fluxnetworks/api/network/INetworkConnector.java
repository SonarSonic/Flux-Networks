package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

/**
 * Refers to anything which can be connected to a specific network, flux tiles & configurators
 */
public interface INetworkConnector {

    /**
     * Returns the network ID that this connector should be, however,
     * this connector may not be currently connected to the network or waiting to connect
     *
     * @return the network ID
     */
    int getNetworkID();

    IFluxNetwork getNetwork();

    void onContainerOpened(PlayerEntity player);

    void onContainerClosed(PlayerEntity player);
}
