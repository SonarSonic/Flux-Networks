package sonar.fluxnetworks.api.tiles;

public interface IFluxEnergy extends IFluxDevice {

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    long getEnergy();
}
