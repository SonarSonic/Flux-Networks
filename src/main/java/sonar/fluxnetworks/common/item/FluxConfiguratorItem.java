package sonar.fluxnetworks.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.translate.StyleUtils;
import sonar.fluxnetworks.api.utils.FluxConfigurationType;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.core.ContainerConnector;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FluxConfiguratorItem extends Item {

    public FluxConfiguratorItem(Properties props) {
        super(props);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getWorld().isRemote) {
            return ActionResultType.SUCCESS;
        }
        PlayerEntity player = context.getPlayer();
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        if (tile instanceof TileFluxCore) {
            TileFluxCore fluxCore = (TileFluxCore) tile;
            if (!fluxCore.canAccess(context.getPlayer())) {
                player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.ACCESS_DENIED_KEY), true);
                return ActionResultType.FAIL;
            }
            ItemStack stack = player.getHeldItem(context.getHand());
            if (player.isSneaking()) {
                stack.setTagInfo(FluxUtils.CONFIGS_TAG, fluxCore.copyConfiguration(new CompoundNBT()));
                player.sendMessage(new StringTextComponent("Copied Configuration"), UUID.randomUUID());
            } else {
                CompoundNBT configs = stack.getOrCreateChildTag(FluxUtils.CONFIGS_TAG);
                if (!configs.isEmpty()) {
                    fluxCore.pasteConfiguration(configs);
                    player.sendMessage(new StringTextComponent("Pasted Configuration"), UUID.randomUUID());
                }
            }
            return ActionResultType.SUCCESS;
        }
        NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(context.getHand())), buf -> buf.writeBoolean(false));
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(hand)), buf -> buf.writeBoolean(false));
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT tag = stack.getChildTag(FluxUtils.CONFIGS_TAG);
        if (tag != null) {
            tooltip.add(new StringTextComponent(FluxTranslate.NETWORK_FULL_NAME.t() + ": " + TextFormatting.WHITE + FluxColorHandler.getOrRequestNetworkName(tag.getInt(FluxConfigurationType.NETWORK.getNBTName()))));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static class ContainerProvider implements INamedContainerProvider, INetworkConnector {

        public ItemStack stack;
        public int networkID;
        public IFluxNetwork network;

        public ContainerProvider(ItemStack stack) {
            this.stack = stack;
            CompoundNBT tag = stack.getChildTag(FluxUtils.CONFIGS_TAG);
            networkID = tag != null ? tag.getInt(FluxConfigurationType.NETWORK.getNBTName()) : -1;
            network = FluxNetworks.PROXY.getNetwork(networkID);
        }

        @Override
        public int getNetworkID() {
            return networkID;
        }

        @Override
        public IFluxNetwork getNetwork() {
            return network;
        }

        @Override
        public void open(PlayerEntity player) {
        }

        @Override
        public void close(PlayerEntity player) {
        }

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
