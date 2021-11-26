package sonar.fluxnetworks.common.blockentity;

import icyllis.modernui.mcgui.ContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nonnull;

/**
 * Communication menu between client and server.
 */
public class FluxContainerMenu extends ContainerMenu {

    public final FluxDeviceEntity mDevice;

    public FluxContainerMenu(int containerId, @Nonnull Inventory inventory, @Nonnull FriendlyByteBuf buf) {
        super(RegistryBlocks.FLUX_MENU, containerId);
        if (inventory.player.level.getBlockEntity(buf.readBlockPos()) instanceof FluxDeviceEntity device) {
            mDevice = device;
            device.onPlayerOpen(inventory.player);
        } else {
            mDevice = null;
        }
    }

    public FluxContainerMenu(int containerId, @Nonnull Inventory inventory, @Nonnull FluxDeviceEntity device) {
        super(RegistryBlocks.FLUX_MENU, containerId);
        mDevice = device;
        mDevice.onPlayerOpen(inventory.player);
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
}
