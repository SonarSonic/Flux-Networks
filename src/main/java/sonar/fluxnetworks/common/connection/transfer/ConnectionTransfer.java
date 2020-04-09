package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.api.network.ISidedTransfer;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ConnectionTransfer implements IFluxTransfer, ISidedTransfer {

    public final ITransferHandler transferHandler;
    public final ITileEnergyHandler energyHandler;
    public final TileEntity tile;
    public final Direction dir;
    public final ItemStack displayStack;

    public long added;
    public long removed;

    public ConnectionTransfer(ITransferHandler transferHandler, ITileEnergyHandler energyHandler, TileEntity tile, Direction dir) {
        this.transferHandler = transferHandler;
        this.energyHandler = energyHandler;
        this.tile = tile;
        this.dir = dir;
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
        Direction dir = this.dir.getOpposite();
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
    public Direction getDir() {
        return dir;
    }

    @Override
    public ItemStack getDisplayStack() {
        return displayStack;
    }

    @Override
    public boolean isInvalid() {
        return tile.isRemoved();
    }
}
