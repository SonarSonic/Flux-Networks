package sonar.fluxnetworks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import sonar.fluxnetworks.common.core.ContainerConnector;

import javax.annotation.Nullable;

public class AdminConfiguratorItem extends FluxConfiguratorItem {

    public AdminConfiguratorItem(Properties props) {
        super(props);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(hand)), buf -> buf.writeBoolean(false));
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public static class ContainerProvider implements INamedContainerProvider, INetworkConnector {

        public ItemStack stack;

        public ContainerProvider(ItemStack stack){
            this.stack = stack;
        }

        @Override
        public int getNetworkID() {
            return FluxNetworks.proxy.getAdminViewingNetworkID();
        }

        @Override
        public IFluxNetwork getNetwork() {
            return FluxNetworks.proxy.getAdminViewingNetwork();
        }

        @Override
        public void open(PlayerEntity player) {}

        @Override
        public void close(PlayerEntity player) {}

        @Override
        public ITextComponent getDisplayName() {
            return stack.getDisplayName();
        }

        @Nullable
        @Override
        public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
            return new ContainerConnector<>(windowID, playerInventory, this);
        }
    }

}
