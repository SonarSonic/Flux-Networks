package sonar.fluxnetworks.common.core;

import sonar.fluxnetworks.api.tiles.IFluxPhantomEnergy;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import gregtech.api.capability.IEnergyContainer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
        @Optional.Interface(iface = "gregtech.api.capability.IEnergyContainer", modid = "gregtech")
})
public class GTEnergyWrapper implements IEnergyContainer {

    public IFluxPhantomEnergy tileEntity;
    public EnumFacing side;

    public GTEnergyWrapper(IFluxPhantomEnergy tileEntity, EnumFacing side) {
        this.tileEntity = tileEntity;
        this.side = side;
    }

    @Override
    public long acceptEnergyFromNetwork(EnumFacing enumFacing, long l, long l1) {
        long a = tileEntity.addPhantomEnergyToNetwork(enumFacing, l * l1 << 2, true) / l >> 2;
        tileEntity.addPhantomEnergyToNetwork(enumFacing, l * a << 2, false);
        return a;
    }

    @Override
    public boolean inputsEnergy(EnumFacing enumFacing) {
        return false;
    }

    @Override
    public boolean outputsEnergy(EnumFacing side) {
        return false;
    }

    @Override
    public long changeEnergy(long l) {
        return 0;
    }

    @Override
    public long getEnergyStored() {
        return tileEntity instanceof IFluxPoint ? Long.MAX_VALUE : 0;
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
