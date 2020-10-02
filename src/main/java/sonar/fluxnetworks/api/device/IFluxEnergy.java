package sonar.fluxnetworks.api.device;

public interface IFluxEnergy extends IFluxDevice {

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    long getEnergy();
}
