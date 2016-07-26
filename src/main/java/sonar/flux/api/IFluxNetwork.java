package sonar.flux.api;

import sonar.core.api.utils.ActionType;
import sonar.core.utils.CustomColour;

/**all server stored Networks will implement this*/
public interface IFluxNetwork extends IFluxCommon{

	/**called every tick like TileEntities, you shouldn't be calling this method*/
	public void updateNetwork();
	
	/**returns true if a {@link IFluxController} has been connected*/
	public boolean hasController();

	/**obtains the IFluxController currently connected, can be null*/
	public IFluxController getController();

	/**returns if the given player can access this network and pull/push energy from/to it*/
	public boolean isPlayerAllowed(String playerName);
	
	/**sets the custom name of this network*/
	public void setNetworkName(String name);
	
	/**sets the access setting of this network */
	public void setAccessType(AccessType type);	
	
	/**sets the colour of this network */
	public void setCustomColour(CustomColour colour);	

	/**pairs the given controller with the network, returns if this pairing was successful*/
	public boolean setController(IFluxController controller);
	
	public void sendChanges();
	
	/**removes access to the network from the given player, points/plugs associated with them and on the network will then be blocked*/
	public void removePlayerAccess(String playerName);
	
	/**adds access to the network for a given player, the owner is added as default*/
	public void addPlayerAccess(String playerName);
	
	/**used for pushing energy into the network it returns the amount received*/
	public long receiveEnergy(long maxReceive, ActionType type);

	/**used for pulling energy from the network it returns the amount extracted*/
	public long extractEnergy(long maxExtract, ActionType type);	
		
	/**adds a Flux Connection to the network, this could be a PLUG, POINT or STORAGE*/
	public void addFluxConnection(IFlux flux);

	/**removes a Flux Connection from the network*/
	public void removeFluxConnection(IFlux flux);

	public void buildFluxConnections();
}
