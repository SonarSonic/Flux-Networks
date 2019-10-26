package fluxnetworks.common.item;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.api.network.IFluxNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAdminConfigurator extends ItemConfigurator {

    public ItemAdminConfigurator() {
        super("AdminConfigurator");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            playerIn.openGui(FluxNetworks.instance, 1, worldIn, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    private static AdminConnector connector = new AdminConnector();

    public static INetworkConnector getAdminConnector() {
        return connector;
    }

    public static class AdminConnector implements INetworkConnector {

        @Override
        public int getNetworkID() {
            return FluxNetworks.proxy.admin_viewing_network_id;
        }

        @Override
        public IFluxNetwork getNetwork() {
            return FluxNetworks.proxy.admin_viewing_network;
        }

        @Override
        public void open(EntityPlayer player) {
        }

        @Override
        public void close(EntityPlayer player) {
        }

    }

}
