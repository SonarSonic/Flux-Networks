package sonar.fluxnetworks.common.test;

import sonar.fluxnetworks.api.device.IFluxDevice;

@Deprecated
public interface IFluxEnergy extends IFluxDevice {

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    long getEnergyStored();

    long getMaxEnergyStorage();
}
