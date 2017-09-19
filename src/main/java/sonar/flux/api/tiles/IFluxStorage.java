package sonar.flux.api.tiles;

public interface IFluxStorage extends IFluxPlug, IFluxPoint {

    long getMaxEnergyStored();

    long getEnergyStored();

}