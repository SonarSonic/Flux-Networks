package sonar.fluxnetworks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.api.network.IMenuBridge;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.common.misc.FluxMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAdminConfigurator extends Item {

    public ItemAdminConfigurator(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player,
                    new ContainerProvider(), buf -> buf.writeBoolean(false));
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public static class MenuBridge implements IMenuBridge {

        @Override
        public int getNetworkID() {
            return FluxClientCache.adminViewingNetwork;
        }

        @Override
        public void onMenuOpened(PlayerEntity player) {

        }

        @Override
        public void onMenuClosed(PlayerEntity player) {

        }
    }

    private static class ContainerProvider implements INamedContainerProvider {

        @Nonnull
        @Override
        public ITextComponent getDisplayName() {
            return StringTextComponent.EMPTY;
        }

        @Nullable
        @Override
        public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
            return new FluxMenu(windowID, playerInventory, new MenuBridge());
        }
    }
}
