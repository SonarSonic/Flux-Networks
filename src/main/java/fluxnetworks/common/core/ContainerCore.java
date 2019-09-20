package fluxnetworks.common.core;

import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerCore extends Container {

    public TileFluxCore tileEntity;

    public ContainerCore(EntityPlayer player, TileFluxCore tileEntity) {
        this.tileEntity = tileEntity;
        this.tileEntity.open(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        tileEntity.close(playerIn);
    }
}
