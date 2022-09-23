package sonar.fluxnetworks.api.device;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

/**
 * Refers to anything which can be connected to a specific network: flux devices and configurators,
 * and there will be a UI.
 */
public interface IFluxProvider extends MenuProvider {

    /**
     * Returns the network ID that this provider currently connected to.
     *
     * @return the network ID
     */
    int getNetworkID();

    /**
     * Called when the player started to interact with this connector.
     *
     * @param player the player
     */
    void onPlayerOpened(@Nonnull Player player);

    /**
     * Called when the player stopped interacting with this connector.
     *
     * @param player the player
     */
    void onPlayerClosed(@Nonnull Player player);

    @Nonnull
    @Override
    default Component getDisplayName() {
        return CommonComponents.EMPTY;
    }
}
