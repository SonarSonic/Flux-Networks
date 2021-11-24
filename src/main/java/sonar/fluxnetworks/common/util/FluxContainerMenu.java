package sonar.fluxnetworks.common.util;

import icyllis.modernui.mcgui.ContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.IFluxBridge;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;

/**
 * Communication menu between client and server.
 */
public class FluxContainerMenu extends ContainerMenu {

    @Nonnull
    public final IFluxBridge bridge;

    public FluxContainerMenu(int containerId, @Nonnull Inventory inventory, @Nonnull IFluxBridge bridge) {
        super(RegistryBlocks.FLUX_MENU, containerId);
        this.bridge = bridge;
        bridge.onPlayerOpen(inventory.player);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (bridge instanceof IFluxDevice flux) {
            return flux.isChunkLoaded() && flux.getFluxWorld() == player.level;
        }/* else if (bridge instanceof ItemFluxConfigurator.MenuBridge) {
            return playerIn.getHeldItemMainhand().getItem() == RegistryItems.FLUX_CONFIGURATOR;
        }*/
        return player.getMainHandItem().is(RegistryItems.ADMIN_CONFIGURATOR);
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        bridge.onPlayerClose(player);
    }
}
