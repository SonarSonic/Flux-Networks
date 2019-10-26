package fluxnetworks.common.item;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.FluxConfigurationType;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.client.FluxColorHandler;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static fluxnetworks.common.core.FluxUtils.CONFIGS_TAG;

public class ItemConfigurator extends ItemCore {

    public ItemConfigurator() {
        super("FluxConfigurator");
    }

    public ItemConfigurator(String name) {
        super(name);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileFluxCore) {
            TileFluxCore fluxCore = (TileFluxCore) tile;
            if (!fluxCore.canAccess(player)) {
                TextComponentTranslation textComponents = new TextComponentTranslation(FluxTranslate.ACCESS_DENIED_KEY);
                textComponents.getStyle().setBold(true);
                textComponents.getStyle().setColor(TextFormatting.DARK_RED);
                player.sendStatusMessage(textComponents, true);
                return EnumActionResult.FAIL;
            }
            ItemStack stack = player.getHeldItem(hand);
            if (player.isSneaking()) {
                stack.setTagInfo(CONFIGS_TAG, fluxCore.copyConfiguration(new NBTTagCompound()));
                player.sendMessage(new TextComponentString("Copied Configuration"));
            } else {
                NBTTagCompound configs = stack.getOrCreateSubCompound(CONFIGS_TAG);
                if (!configs.isEmpty()) {
                    fluxCore.pasteConfiguration(configs);
                    player.sendMessage(new TextComponentString("Pasted Configuration"));
                }
            }
            return EnumActionResult.SUCCESS;
        }
        player.openGui(FluxNetworks.instance, 1, worldIn, 0, 0, 0);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            playerIn.openGui(FluxNetworks.instance, 1, worldIn, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound tag = stack.getSubCompound(CONFIGS_TAG);
        if (tag != null) {
            tooltip.add(FluxTranslate.NETWORK_FULL_NAME.t() + ": " + TextFormatting.WHITE + FluxColorHandler.getOrRequestNetworkName(tag.getInteger(FluxConfigurationType.NETWORK.getNBTName())));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static class NetworkConnector implements INetworkConnector {

        public ItemStack stack;
        public int networkID;
        public IFluxNetwork network;

        public NetworkConnector(ItemStack stack, int networkID, IFluxNetwork network) {
            this.stack = stack;
            this.networkID = networkID;
            this.network = network;
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
        public void open(EntityPlayer player) {

        }

        @Override
        public void close(EntityPlayer player) {

        }
    }

    public static NetworkConnector getNetworkConnector(ItemStack stack, World world) {
        NBTTagCompound tag = stack.getSubCompound(CONFIGS_TAG);
        int networkID = tag != null ? tag.getInteger(FluxConfigurationType.NETWORK.getNBTName()) : -1;
        IFluxNetwork network = world.isRemote ? FluxNetworkCache.instance.getClientNetwork(networkID) : FluxNetworkCache.instance.getNetwork(networkID);
        return new NetworkConnector(stack, networkID, network);
    }
}
