package sonar.fluxnetworks.common.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nonnull;

/**
 * Communication menu between client and server.
 */
public class FluxDeviceMenu extends AbstractContainerMenu {

    public final TileFluxDevice mDevice;
    public OnResultListener mOnResultListener;

    public FluxDeviceMenu(int containerId, @Nonnull Inventory inventory, @Nonnull FriendlyByteBuf buf) {
        super(RegistryBlocks.FLUX_MENU, containerId);
        if (inventory.player.level.getBlockEntity(buf.readBlockPos()) instanceof TileFluxDevice device) {
            mDevice = device;
            device.onPlayerOpen(inventory.player);
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                device.readCustomTag(tag, FluxConstants.TYPE_TILE_UPDATE);
            }
        } else {
            mDevice = null;
        }
    }

    public FluxDeviceMenu(int containerId, @Nonnull Inventory inventory, @Nonnull TileFluxDevice device) {
        super(RegistryBlocks.FLUX_MENU, containerId);
        mDevice = device;
        device.onPlayerOpen(inventory.player);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return mDevice.isChunkLoaded() && mDevice.getLevel() == player.level;
    /* else if (bridge instanceof ItemFluxConfigurator.MenuBridge) {
            return playerIn.getHeldItemMainhand().getItem() == RegistryItems.FLUX_CONFIGURATOR;
        }*/
        //return player.getMainHandItem().is(RegistryItems.ADMIN_CONFIGURATOR);
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        mDevice.onPlayerClose(player);
    }

    @FunctionalInterface
    public interface OnResultListener {

        void onResult(int key, int code);
    }
}
