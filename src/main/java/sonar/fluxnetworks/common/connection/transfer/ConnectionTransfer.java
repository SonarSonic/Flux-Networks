package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.api.network.ISidedTransfer;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ConnectionTransfer implements IFluxTransfer, ISidedTransfer {

    public final ITileEnergyHandler energyHandler;
    public final TileEntity tile;
    public final Direction dir;
    public final ItemStack displayStack;

    public long outbound;
    public long inbound;

    public ConnectionTransfer(ITileEnergyHandler energyHandler, TileEntity tile, Direction dir) {
        this.energyHandler = energyHandler;
        this.tile = tile;
        this.dir = dir;
        this.displayStack = FluxUtils.getBlockItem(tile.getWorld(), tile.getPos());
    }

    @Override
    public long addEnergy(long amount, boolean simulate) {
        Direction dir = this.dir.getOpposite();
        if(energyHandler.canAddEnergy(tile, dir)) {
            long added = energyHandler.addEnergy(amount, tile, dir, simulate);
            if(!simulate) {
                onEnergyAdded(added);
            }
            return added;
        }
        return 0;
    }

    @Override
    public long removeEnergy(long amount, boolean simulate) {
        return 0; //we only receive energy from nearby tiles passively.
    }

    @Override
    public void onEnergyAdded(long amount) {
        inbound += amount;
    }

    @Override
    public void onEnergyRemoved(long amount) {
        outbound += amount;
    }

    @Override
    public void onStartCycle() {
        outbound = 0;
        inbound = 0;
    }

    @Override
    public void onEndCycle() {}

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
