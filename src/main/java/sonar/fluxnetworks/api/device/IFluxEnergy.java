package sonar.fluxnetworks.api.device;

/**
 * Defines a device can transfer flux energy (an internal energy
 * type used in Flux Storages across flux networks)
 */
//TODO request or buffer
public interface IFluxEnergy extends IFluxDevice {

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    long getEnergyStored();

    long getMaxEnergyStorage();
}
