package sonar.fluxnetworks.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.GuiFluxConfiguratorHome;
import sonar.fluxnetworks.client.gui.GuiFluxConnectorHome;
import sonar.fluxnetworks.common.core.ContainerCore;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemConfigurator;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

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
            if (stack.getItem() instanceof ItemAdminConfigurator) {
                return new ContainerCore(player, ItemAdminConfigurator.getAdminConnector());
            }
            if (stack.getItem() instanceof ItemConfigurator) {
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
        if (ID == 1) {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof ItemAdminConfigurator) {
                return new GuiFluxAdminHome(player, ItemAdminConfigurator.getAdminConnector());
            }
            if (stack.getItem() instanceof ItemConfigurator) {
                return new GuiFluxConfiguratorHome(player, ItemConfigurator.getNetworkConnector(stack, world));
            }
        }
        return null;
    }
}
