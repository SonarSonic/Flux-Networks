package icyllis.fluxnetworks.system.util;

import icyllis.fluxnetworks.api.tile.IFluxPhantom;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyWrapper implements IEnergyStorage {

    private IFluxPhantom tile;
    private Direction side;

    public ForgeEnergyWrapper(IFluxPhantom tile, Direction side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) tile.addPhantomEnergyToNetwork(side, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return tile.getConnectionType().isPoint() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
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
