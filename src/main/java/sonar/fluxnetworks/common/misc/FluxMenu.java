package sonar.fluxnetworks.common.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.IMenuBridge;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;

/**
 * Communication menu window between client and server
 */
public class FluxMenu extends Container {

    @Nonnull
    public final IMenuBridge bridge;

    public FluxMenu(int windowId, @Nonnull PlayerInventory inventory, @Nonnull IMenuBridge bridge) {
        super(RegistryBlocks.FLUX_MENU, windowId);
        this.bridge = bridge;
        bridge.onMenuOpened(inventory.player);
    }

    // Server logic
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        if (bridge instanceof IFluxDevice) {
            IFluxDevice flux = ((IFluxDevice) bridge);
            return flux.isChunkLoaded() && flux.getFluxWorld() == playerIn.getEntityWorld();
        } else if (bridge instanceof ItemFluxConfigurator.MenuBridge) {
            return playerIn.getHeldItemMainhand().getItem() == RegistryItems.FLUX_CONFIGURATOR;
        }
        return playerIn.getHeldItemMainhand().getItem() == RegistryItems.ADMIN_CONFIGURATOR;
    }

    @Override
    public void onContainerClosed(@Nonnull PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        bridge.onMenuClosed(playerIn);
    }
}
