package sonar.flux.api.tiles;

public interface IFluxStorage extends IFluxPlug, IFluxPoint {
	
	public long getMaxEnergyStored();
	
	public long getEnergyStored();
	
}
