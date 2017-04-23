package sonar.flux.api.configurator;

public enum FluxConfigurationType{
	NETWORK, PRIORITY, TRANSFER, DISABLE_LIMIT;
	
	public String getNBTName(){
		return name().toLowerCase();
	}
}
