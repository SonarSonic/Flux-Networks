package sonar.fluxnetworks.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxProvider;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
            if (!device.canPlayerAccess(player)) {
                player.displayClientMessage(FluxTranslate.ACCESS_DENIED, true);
                return InteractionResult.FAIL;
            }
            if (player.isShiftKeyDown()) {
                CompoundTag tag = stack.getOrCreateTagElement(FluxConstants.TAG_FLUX_CONFIG);
                for (FluxConfigurationType type : FluxConfigurationType.VALUES) {
                    type.copy(player, tag, device);
                }
                player.displayClientMessage(FluxTranslate.CONFIG_COPIED, false);
            } else {
                CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
                if (tag != null) {
                    for (FluxConfigurationType type : FluxConfigurationType.VALUES) {
                        type.paste(player, tag, device);
                    }
                    player.displayClientMessage(FluxTranslate.CONFIG_PASTED, false);
                }
            }
            return InteractionResult.SUCCESS;
        }
        NetworkHooks.openScreen((ServerPlayer) player,
                new Provider(stack), buf -> buf.writeBoolean(false));
        return InteractionResult.SUCCESS;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player,
                                                  @Nonnull InteractionHand hand) {
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip,
                                @Nonnull TooltipFlag flag) {
        CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
        if (tag != null) {
            final FluxNetwork network = ClientCache.getNetwork(tag.getInt(FluxConstants.NETWORK_ID));
            if (network.isValid()) {
                tooltip.add(Component.literal(ChatFormatting.BLUE + FluxTranslate.NETWORK_FULL_NAME.get() + ": " +
                        ChatFormatting.RESET + network.getNetworkName()));
            }

            if (tag.contains(FluxConstants.LIMIT)) {
                tooltip.add(Component.literal(ChatFormatting.BLUE + FluxTranslate.TRANSFER_LIMIT.get() + ": " +
                        ChatFormatting.RESET + EnergyType.FE.getStorage(tag.getLong(FluxConstants.LIMIT))));
            }

            if (tag.contains(FluxConstants.PRIORITY)) {
                tooltip.add(Component.literal(ChatFormatting.BLUE + FluxTranslate.PRIORITY.get() + ": " +
                        ChatFormatting.RESET + tag.getInt(FluxConstants.PRIORITY)));
            }
        }
    }

    public static class Provider implements IFluxProvider {

        public final ItemStack mStack;

        public Provider(@Nonnull ItemStack stack) {
            mStack = stack;
        }

        @Override
        public int getNetworkID() {
            CompoundTag tag = mStack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
            return tag != null ? tag.getInt(FluxConstants.NETWORK_ID) : FluxConstants.INVALID_NETWORK_ID;
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
