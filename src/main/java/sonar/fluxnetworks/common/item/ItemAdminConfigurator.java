package sonar.fluxnetworks.common.item;

import net.minecraft.world.item.Item;

public class ItemAdminConfigurator extends Item {

    public ItemAdminConfigurator(Properties props) {
        super(props);
    }

    /*@Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand
     hand) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player,
                    new ContainerProvider(), buf -> buf.writeBoolean(false));
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public static class MenuBridge implements IFluxBridge {

        @Override
        public int getNetworkID() {
            return FluxClientCache.adminViewingNetwork;
        }

        @Override
        public void onPlayerOpen(PlayerEntity player) {

        }

        @Override
        public void onPlayerClose(PlayerEntity player) {

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
        public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity
        player) {
            return new FluxContainerMenu(windowID, playerInventory, new MenuBridge());
        }
    }*/
}
