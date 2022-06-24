package sonar.fluxnetworks.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkHooks;
import sonar.fluxnetworks.api.device.IFluxProvider;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.common.connection.FluxMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAdminConfigurator extends Item {

    public ItemAdminConfigurator(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player,
                                                  @Nonnull InteractionHand hand) {
        if (!level.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player,
                    new Provider(), buf -> buf.writeBoolean(false));
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static class Provider implements IFluxProvider {

        @Override
        public int getNetworkID() {
            assert EffectiveSide.get().isClient();
            return ClientCache.sAdminViewingNetwork;
        }

        @Override
        public void onPlayerOpened(@Nonnull Player player) {
        }

        @Override
        public void onPlayerClosed(@Nonnull Player player) {
        }

        @Nullable
        @Override
        public FluxMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) {
            return new FluxMenu(containerId, inventory, this);
        }
    }
}
