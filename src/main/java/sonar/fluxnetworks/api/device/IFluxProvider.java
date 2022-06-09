package sonar.fluxnetworks.api.device;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Refers to anything which can be connected to a specific network: flux devices and configurators,
 * and there will be a UI.
 */
public interface IFluxProvider extends MenuProvider {

    /**
     * Returns the network ID that this connector should be. However, this connector
     * may not be currently connected to the network (waiting to connect).
     *
     * @return the network ID
     */
    int getNetworkID();

    void onMenuOpened(@Nonnull FluxDeviceMenu menu, @Nonnull Player player);

    void onMenuClosed(@Nonnull FluxDeviceMenu menu, @Nonnull Player player);

    @Nullable
    @Override
    FluxDeviceMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player);

    @Nonnull
    @Override
    default Component getDisplayName() {
        return TextComponent.EMPTY;
    }
}
