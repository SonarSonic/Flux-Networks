package fluxnetworks.api.tileentity;

public interface IFluxEnergy extends IFluxConnector {

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    long getEnergy();
}
