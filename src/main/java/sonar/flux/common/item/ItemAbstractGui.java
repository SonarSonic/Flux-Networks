package sonar.flux.common.item;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import sonar.core.common.item.SonarItem;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.common.containers.ContainerFluxItem;
import sonar.flux.network.ListenerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ItemAbstractGui extends SonarItem implements IFluxItemGui {

    @Override
    public void onGuiOpened(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
        ListenerHelper.onPlayerOpenItemGui(obj, player);
        ListenerHelper.onPlayerOpenItemTab(obj, player, EnumGuiTab.INDEX);
    }

    @Override
    public Object getServerElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
        return new ContainerFluxItem(player, obj);
    }

    @Override
    public Object getClientElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
        FluxNetworks.proxy.setFluxStack(obj);
        return EnumGuiTab.INDEX.getGuiScreen(Lists.newArrayList(getTabs()));
    }

    @Nonnull
    @Override
    public abstract Object getIndexScreen(ItemStack stack, List<EnumGuiTab> tabs);

    public List<EnumGuiTab> getTabs(){
        return Lists.newArrayList(EnumGuiTab.INDEX, EnumGuiTab.NETWORK_SELECTION, EnumGuiTab.CONNECTIONS, EnumGuiTab.NETWORK_STATISTICS, EnumGuiTab.PLAYERS, EnumGuiTab.DEBUG, EnumGuiTab.NETWORK_EDIT, EnumGuiTab.NETWORK_CREATE);
    }
}
