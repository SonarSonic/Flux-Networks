package sonar.fluxnetworks.common.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;

/**
 * Server and client are same class
 */
public class ContainerConnector<T extends INetworkConnector> extends Container {

    public T connector;

    public ContainerConnector(int windowId, @Nonnull PlayerInventory inv, T connector) {
        super(RegistryBlocks.CONTAINER_CONNECTOR, windowId);
        this.connector = connector;
        this.connector.open(inv.player);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        connector.close(playerIn);
    }
}
