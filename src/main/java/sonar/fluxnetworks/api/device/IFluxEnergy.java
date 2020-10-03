package sonar.fluxnetworks.api.device;

/**
 * Defines a device can transfer FN energy (an internal energy type used in Flux Storages)
 */
//TODO request or buffer
public interface IFluxEnergy extends IFluxDevice {

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    long getEnergy();
}
