package icyllis.fluxnetworks.common.tileentity.component;

import icyllis.fluxnetworks.api.tile.IFluxTransfer;
import icyllis.fluxnetworks.api.util.ITileEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

public class ConnectionTransfer implements IFluxTransfer {

    private final ITileEnergyHandler energyHandler;
    private final TileEntity tile;
    private final Direction side;

    ConnectionTransfer(ITileEnergyHandler energyHandler, TileEntity tile, Direction side) {
        this.energyHandler = energyHandler;
        this.tile = tile;
        this.side = side;
    }

    @Override
    public long addToNetwork(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long removeFromNetwork(long amount, boolean simulate) {
        return energyHandler.addEnergy(amount, tile, side, simulate);
    }

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Override
    public boolean isInvalid() {
        return tile.isRemoved();
    }
}
