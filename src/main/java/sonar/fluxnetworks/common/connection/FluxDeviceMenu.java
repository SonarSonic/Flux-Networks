package sonar.fluxnetworks.common.connection;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import sonar.fluxnetworks.api.device.IFluxProvider;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.register.RegistryBlocks;
import sonar.fluxnetworks.register.RegistryItems;

import javax.annotation.Nonnull;

/**
 * Communication menu between client and server.
 */
public class FluxDeviceMenu extends AbstractContainerMenu {

    public final IFluxProvider mProvider;
    public OnResultListener mOnResultListener;

    public FluxDeviceMenu(int containerId, @Nonnull Inventory inventory, @Nonnull IFluxProvider provider) {
        super(RegistryBlocks.FLUX_MENU, containerId);
        mProvider = provider;
        provider.onMenuOpened(this, inventory.player);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (mProvider instanceof TileFluxDevice device) {
            return device.isChunkLoaded() && device.getLevel() == player.level;
        } else if (mProvider instanceof ItemFluxConfigurator.Provider) {
            return player.getMainHandItem().is(RegistryItems.FLUX_CONFIGURATOR);
        }
        return player.getMainHandItem().is(RegistryItems.ADMIN_CONFIGURATOR);
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        mProvider.onMenuClosed(this, player);
    }

    @FunctionalInterface
    public interface OnResultListener {

        void onResult(int key, int code);
    }
}
