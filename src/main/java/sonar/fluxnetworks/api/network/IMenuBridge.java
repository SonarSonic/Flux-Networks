package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Refers to anything which can be connected to a specific network, flux devices or configurators
 */
public interface IMenuBridge {

    /**
     * Returns the network ID that this connector should be, however, this connector
     * may not be currently connected to the network logically or waiting to connect.
     *
     * @return the network ID
     */
    int getNetworkID();

    void onMenuOpened(PlayerEntity player);

    void onMenuClosed(PlayerEntity player);
}
