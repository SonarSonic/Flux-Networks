package sonar.fluxnetworks.common.connection;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import sonar.fluxnetworks.api.device.IFluxProvider;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.register.RegistryItems;
import sonar.fluxnetworks.register.RegistryMenuTypes;

import javax.annotation.Nonnull;

/**
 * Communication menu between client and server.
 */
public class FluxMenu extends AbstractContainerMenu {

    // both side
    public final IFluxProvider mProvider;
    // client only
    public OnResultListener mOnResultListener;

    // both side
    public FluxMenu(int containerId, @Nonnull Inventory inventory, @Nonnull IFluxProvider provider) {
        super(RegistryMenuTypes.FLUX_MENU.get(), containerId);
        mProvider = provider;
        provider.onPlayerOpened(inventory.player);
    }

    // server only
    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (mProvider instanceof TileFluxDevice device) {
            return device.isChunkLoaded() && device.getLevel() == player.level();
        } else if (mProvider instanceof ItemFluxConfigurator.Provider) {
            return player.getMainHandItem().is(RegistryItems.FLUX_CONFIGURATOR.get());
        }
        return player.getMainHandItem().is(RegistryItems.ADMIN_CONFIGURATOR.get());
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player pPlayer, int pIndex) {
        return slots.get(pIndex).getItem();
    }

    // both side
    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        mProvider.onPlayerClosed(player);
    }

    // client only
    @FunctionalInterface
    public interface OnResultListener {

        void onResult(FluxMenu menu, int key, int code);
    }
}
