package sonar.flux.api;

import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;

/** extended by IFluxPoint & IFluxPlug you must use them if you wish to send and receive energy from the network */
public interface IFlux {

	public enum ConnectionType {
		POINT, PLUG, STORAGE, CONTROLLER;
		public boolean canSend(){
			return this==PLUG || this==STORAGE;
		}
		public boolean canReceive(){
			return this==POINT || this==STORAGE || this==CONTROLLER;
		}
	}
	public UUID getConnectionOwner();
	
	/**the dimension in which this Flux Connection is located*/
	public World getDimension();
	
	/**the location of the Flux Connection*/
	public BlockCoords getCoords();

	/**the network this Flux connection is a part of*/
	public IFluxNetwork getNetwork();

	/**the type of Flux Connection*/
	public ConnectionType getConnectionType();

	/**the maximum RF/t this Flux connection can receive*/
	public long getTransferLimit();
	
	/**the current RF/t this Flux connection can receive*/
	public long getCurrentTransferLimit();
	
	public void onEnergyRemoved(long remove);
	
	public void onEnergyAdded(long added);

	/**the higher the priority the sooner the Flux connection will receive power*/
	public int getCurrentPriority();
	
	/**the custom name is assigned by the user, this allows easier identification of various Flux connections.*/
	public String getCustomName();
	
	public TileEntity[] cachedTiles();
	
	public boolean canTransfer();
	
	public void changeNetwork(IFluxNetwork network);
}
