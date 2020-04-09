package sonar.fluxnetworks.common.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class ContainerCore extends Container {

    public INetworkConnector connector;

    public ContainerCore(int windowId, PlayerInventory inv, INetworkConnector connector) {
        super(RegistryBlocks.CONTAINER_CORE, windowId);
        this.connector = connector;
        this.connector.open(inv.player);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        connector.close(playerIn);
    }
}
