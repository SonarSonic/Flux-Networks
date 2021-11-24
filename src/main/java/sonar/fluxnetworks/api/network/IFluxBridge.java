package sonar.fluxnetworks.api.network;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

/**
 * Refers to anything which can be connected to a specific network, flux devices or configurators.
 */
public interface IFluxBridge {

    /**
     * Returns the network ID that this connector should be. However, this connector
     * may not be currently connected to the network (waiting to connect).
     *
     * @return the network ID
     */
    int getNetworkID();

    /**
     * Called when the player started to interact with this connector.
     *
     * @param player the player
     */
    void onPlayerOpen(@Nonnull Player player);

    /**
     * Called when the player stopped interacting with this connector.
     *
     * @param player the player
     */
    void onPlayerClose(@Nonnull Player player);
}
