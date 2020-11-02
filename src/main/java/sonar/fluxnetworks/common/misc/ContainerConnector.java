package sonar.fluxnetworks.common.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Server and client are same class
 */
public class ContainerConnector<T extends INetworkConnector> extends Container {

    @Nullable
    public T connector;

    public ContainerConnector(int windowId, @Nonnull PlayerInventory inv, @Nullable T connector) {
        super(RegistryBlocks.CONTAINER_CONNECTOR, windowId);
        this.connector = connector;
        if (connector != null) connector.onContainerOpened(inv.player);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        if (connector instanceof IFluxDevice) {
            return ((IFluxDevice) connector).getFluxWorld() == playerIn.getEntityWorld();
        } else if (connector instanceof ItemFluxConfigurator.NetworkConnector) {
            return playerIn.getHeldItemMainhand().getItem() == RegistryItems.FLUX_CONFIGURATOR;
        } else if (connector instanceof ItemAdminConfigurator.AdminNetworkConnector) {
            return playerIn.getHeldItemMainhand().getItem() == RegistryItems.ADMIN_CONFIGURATOR;
        }
        return true;
    }

    @Override
    public void onContainerClosed(@Nonnull PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (connector != null) connector.onContainerClosed(playerIn);
    }
}
