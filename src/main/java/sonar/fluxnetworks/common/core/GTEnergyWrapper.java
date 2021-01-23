package sonar.fluxnetworks.common.core;

import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import gregtech.api.capability.IEnergyContainer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
        @Optional.Interface(iface = "gregtech.api.capability.IEnergyContainer", modid = "gregtech")
})
public class GTEnergyWrapper implements IEnergyContainer {

    private final IFluxConnector tile;

    public GTEnergyWrapper(IFluxConnector tile) {
        this.tile = tile;
    }

    @Override
    public long acceptEnergyFromNetwork(EnumFacing side, long vol, long amp) {
        if (side != null && tile.getConnectionType().isPlug() && tile.isActive()) {
            long actualAmp = tile.getTransferHandler().receiveFromSupplier(vol * amp << 2, side, true) / vol >> 2;
            tile.getTransferHandler().receiveFromSupplier(vol * actualAmp << 2, side, false);
            return actualAmp;
        }
        return 0;
    }

    @Override
    public boolean inputsEnergy(EnumFacing side) {
        return tile.getConnectionType().isPlug();
    }

    @Override
    public boolean outputsEnergy(EnumFacing side) {
        return tile.getConnectionType().isPoint();
    }

    @Override
    public long changeEnergy(long l) {
        return 0;
    }

    @Override
    public long getEnergyStored() {
        return tile instanceof IFluxPoint ? Long.MAX_VALUE : 0;
    }

    @Override
    public long getEnergyCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getInputAmperage() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getInputVoltage() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isOneProbeHidden() {
        return true;
    }
}
