package sonar.fluxnetworks.common.core;

import sonar.fluxnetworks.api.tiles.IFluxConnector;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyWrapper implements IEnergyStorage {

    private final IFluxConnector tile;
    private final EnumFacing side;

    public ForgeEnergyWrapper(IFluxConnector tile, EnumFacing side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        // so other mods didn't check if this canReceive() at all
        if (tile.getConnectionType().isPlug() && tile.isActive()) {
            return (int) tile.getTransferHandler().receiveFromSupplier(maxReceive, side, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(tile.getTransferBuffer(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(tile.getMaxTransferLimit(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return tile.getConnectionType().isPlug();
    }
}
