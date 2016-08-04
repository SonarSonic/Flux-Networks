package sonar.flux.api;

import java.util.ArrayList;
import java.util.UUID;

import sonar.core.api.nbt.INBTSyncable;
import sonar.core.utils.CustomColour;

/**both client and server networks implement this*/
public interface IFluxCommon extends INBTSyncable {

	public enum AccessType {
		PUBLIC, PRIVATE, RESTRICTED;	
		
		public String getName(){
			switch(this){
			case PUBLIC: return "network.public";
			case PRIVATE: return "network.private";
			case RESTRICTED: return "network.restricted";	
			}
			return "";
		}			
	}		
	/**the access settings of this network*/
	public AccessType getAccessType();

	/**the id this network is registered by under the Owners name*/
	public int getNetworkID();
	
	/**the custom set network name of this network*/
	public String getNetworkName();
	
	/**the player name of the owner, this may change to a UUID in the future be warned...*/	
	public String getCachedPlayerName();	
	
	public UUID getOwnerUUID();
	
	/**gets the custom set network colour**/
	public CustomColour getNetworkColour();	
	
	/**gets the latest network statistics for display*/
	public INetworkStatistics getStatistics();

	/**gets the last full count of energy available in the system from connected Flux Storage*/
	public long getEnergyAvailable();

	/**gets the last full count of the max energy stored allowed in connected Flux Storage*/
	public long getMaxEnergyStored();
	
	public void setClientConnections(ArrayList<ClientFlux> flux);
	
	public ArrayList<ClientFlux> getClientFluxConnection();
	
	public boolean isFakeNetwork();
	
	public FluxPlayersList getPlayers();
}

