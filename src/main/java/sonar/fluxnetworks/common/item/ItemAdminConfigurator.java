package sonar.fluxnetworks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.common.misc.ContainerConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAdminConfigurator extends ItemFluxConfigurator {

    public ItemAdminConfigurator(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(hand)), buf -> buf.writeBoolean(false));
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public static class ContainerProvider implements INamedContainerProvider, INetworkConnector {

        public ItemStack stack;

        public ContainerProvider(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getNetworkID() {
            return FluxClientCache.adminViewingNetwork.getNetworkID();
        }

        @Override
        public IFluxNetwork getNetwork() {
            return FluxClientCache.adminViewingNetwork;
        }

        @Override
        public void onContainerOpened(PlayerEntity player) {

        }

        @Override
        public void onContainerClosed(PlayerEntity player) {

        }

        @Nonnull
        @Override
        public ITextComponent getDisplayName() {
            return stack.getDisplayName();
        }

        @Nullable
        @Override
        public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
            return new ContainerConnector<>(windowID, playerInventory, this);
        }
    }
}
