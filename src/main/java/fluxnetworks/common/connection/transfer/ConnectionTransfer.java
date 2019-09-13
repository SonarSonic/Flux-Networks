package fluxnetworks.common.connection.transfer;

import fluxnetworks.api.energy.ITileEnergyHandler;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.api.network.ISidedTransfer;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class ConnectionTransfer implements IFluxTransfer, ISidedTransfer {

    public final ITransferHandler transferHandler;
    public final ITileEnergyHandler energyHandler;
    public final TileEntity tile;
    public final EnumFacing side;
    public final ItemStack displayStack;

    public long added;
    public long removed;

    public ConnectionTransfer(ITransferHandler transferHandler, ITileEnergyHandler energyHandler, TileEntity tile, EnumFacing side) {
        this.transferHandler = transferHandler;
        this.energyHandler = energyHandler;
        this.tile = tile;
        this.side = side;
        this.displayStack = FluxUtils.getBlockItem(tile.getWorld(), tile.getPos());
    }

    /**
     * Flux Plug
     * @param amount
     * @return
     */
    @Override
    public long addToNetwork(long amount, boolean simulate) {
        return 0;
    }

    /**
     * Flux point
     * @param amount
     * @param simulate
     * @return
     */
    @Override
    public long removeFromNetwork(long amount, boolean simulate) {
        EnumFacing dir = side.getOpposite();
        if(energyHandler.canAddEnergy(tile, dir)) {
            long added = energyHandler.addEnergy(amount, tile, dir, simulate);
            if(!simulate) {
                removedFromNetwork(added);
            }
            return added;
        }
        return 0;
    }

    @Override
    public void addedToNetwork(long amount) {
        added += amount;
    }

    @Override
    public void removedFromNetwork(long amount) {
        removed += amount;
    }

    @Override
    public void onServerStartTick() {
        added = 0;
        removed = 0;
    }

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Override
    public EnumFacing getSide() {
        return side;
    }

    @Override
    public ItemStack getDisplayStack() {
        return displayStack;
    }

    @Override
    public boolean isInvalid() {
        return tile.isInvalid();
    }
}
