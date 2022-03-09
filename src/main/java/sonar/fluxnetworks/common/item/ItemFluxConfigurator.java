package sonar.fluxnetworks.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.client.ClientRepository;
import sonar.fluxnetworks.common.device.TileFluxDevice;

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
        /*NetworkHooks.openGui((ServerPlayerEntity) player,
                new ContainerProvider(), buf -> buf.writeBoolean(false));*/
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
            tooltip.add(new TextComponent(FluxTranslate.NETWORK_FULL_NAME.t() + ": " + ChatFormatting.WHITE +
                    ClientRepository.getDisplayName(tag)));
        }
    }

    /*public static class MenuBridge implements IFluxBridge {

        public final ItemStack stack;
        public int networkID;

        MenuBridge() {
            stack = null;
        }

        public MenuBridge(@Nonnull ItemStack stack) {
            this.stack = stack;
            CompoundNBT tag = stack.getChildTag(FluxConstants.TAG_FLUX_CONFIG);
            networkID = tag != null ? tag.getInt(FluxConstants.NETWORK_ID) : FluxConstants.INVALID_NETWORK_ID;
        }

        @Override
        public int getNetworkID() {
            return networkID;
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
        public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory,
                                    @Nonnull PlayerEntity player) {
            return new FluxContainerMenu(windowID, playerInventory, new MenuBridge());
        }
    }*/
}
