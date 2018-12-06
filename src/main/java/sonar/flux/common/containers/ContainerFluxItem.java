package sonar.flux.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import sonar.core.handlers.inventories.containers.ContainerSonar;
import sonar.flux.network.ListenerHelper;

public class ContainerFluxItem extends ContainerSonar {
    public ItemStack stack;
    public EntityPlayer player;

    public ContainerFluxItem(EntityPlayer player, ItemStack stack) {
        super();
        this.stack = stack;
        this.player = player;
    }

    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if(!player.getEntityWorld().isRemote) {
            ListenerHelper.onPlayerCloseItemGui(stack, player);
        }
    }
}