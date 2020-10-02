package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

/**
 * Refers to anything which can be connected to a specific network, flux tiles & configurators
 */
public interface INetworkConnector {

    @Deprecated
    default int getNetworkID() {
        return getNetwork().getNetworkID();
    }

    IFluxNetwork getNetwork();

    void onContainerOpened(PlayerEntity player);

    void onContainerClosed(PlayerEntity player);
}
