package fluxnetworks.common.handler;

import fluxnetworks.api.INetworkConnector;
import fluxnetworks.client.gui.GuiFluxAdminHome;
import fluxnetworks.client.gui.GuiFluxConfiguratorHome;
import fluxnetworks.client.gui.GuiFluxConnectorHome;
import fluxnetworks.common.core.ContainerCore;
import fluxnetworks.common.item.ItemAdminConfigurator;
import fluxnetworks.common.item.ItemConfigurator;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) { // TILE
            return new ContainerCore(player, (TileFluxCore) world.getTileEntity(new BlockPos(x, y, z)));
        }
        if (ID == 1) { //ITEM
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() instanceof ItemAdminConfigurator) {
                return new ContainerCore(player, ItemAdminConfigurator.getAdminConnector());
            }
            if(stack.getItem() instanceof ItemConfigurator) {
                return new ContainerCore(player, ItemConfigurator.getNetworkConnector(stack, world));
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            return new GuiFluxConnectorHome(player, (TileFluxCore) world.getTileEntity(new BlockPos(x, y, z)));
        }
        if(ID == 1){
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() instanceof ItemAdminConfigurator){
                return new GuiFluxAdminHome(player, ItemAdminConfigurator.getAdminConnector());
            }
            if(stack.getItem() instanceof ItemConfigurator) {
                return new GuiFluxConfiguratorHome(player, ItemConfigurator.getNetworkConnector(stack, world));
            }
        }
        return null;
    }
}
