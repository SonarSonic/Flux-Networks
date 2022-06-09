package sonar.fluxnetworks.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxProvider;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.client.ClientRepository;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemFluxConfigurator extends Item {

    public ItemFluxConfigurator(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TileFluxDevice device) {
            if (!device.canPlayerAccess(context.getPlayer())) {
                player.displayClientMessage(FluxTranslate.ACCESS_DENIED, true);
                return InteractionResult.FAIL;
            }
            if (player.isShiftKeyDown()) {
                CompoundTag configs = new CompoundTag();
                for (FluxConfigurationType type : FluxConfigurationType.values()) {
                    type.copy(configs, device);
                }
                stack.addTagElement(FluxConstants.TAG_FLUX_CONFIG, configs);
                player.sendMessage(new TextComponent("Copied Configuration"), UUID.randomUUID());
            } else {
                CompoundTag configs = stack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
                if (configs != null) {
                    for (FluxConfigurationType type : FluxConfigurationType.values()) {
                        type.paste(configs, device);
                    }
                    player.sendMessage(new TextComponent("Pasted Configuration"), UUID.randomUUID());
                }
            }
            return InteractionResult.SUCCESS;
        }
        NetworkHooks.openGui((ServerPlayer) player,
                new Provider(), buf -> buf.writeBoolean(false));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
        if (tag != null) {
            tooltip.add(new TextComponent(FluxTranslate.NETWORK_FULL_NAME.get() + ": " + ChatFormatting.WHITE +
                    ClientRepository.getDisplayName(tag)));
        }
    }

    public static class Provider implements IFluxProvider {

        public final ItemStack mStack;
        public int mNetworkID;

        Provider() {
            mStack = null;
        }

        public Provider(@Nonnull ItemStack stack) {
            this.mStack = stack;
            CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
            mNetworkID = tag != null ? tag.getInt(FluxConstants.NETWORK_ID) : FluxConstants.INVALID_NETWORK_ID;
        }

        @Override
        public int getNetworkID() {
            return mNetworkID;
        }

        @Override
        public void onMenuOpened(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        }

        @Override
        public void onMenuClosed(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        }

        @Nullable
        @Override
        public FluxDeviceMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) {
            return new FluxDeviceMenu(containerId, inventory, this);
        }
    }
}
