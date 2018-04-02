package sonar.flux.api.tiles;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;

/**
 * extended by IFluxPoint & IFluxPlug you must use them if you wish to send and receive energy from the network
 */
public interface IFlux {

    public enum ConnectionType {
        POINT(FontHelper.getIntFromColor(136, 40, 40)), PLUG(FontHelper.getIntFromColor(96, 151, 50)), STORAGE(FontHelper.getIntFromColor(41, 94, 138)), CONTROLLER(FontHelper.getIntFromColor(100, 100, 120));
    	
    	public int gui_colour;
    	
    	ConnectionType(int gui_colour){
    		this.gui_colour = gui_colour;
    	}
    	
        public boolean canAdd() {
            return this == PLUG || this == STORAGE;
        }

        public boolean canRemove() {
            return this == POINT || this == STORAGE || this == CONTROLLER;
        }
        
    	public boolean canAddPhantomPower(){
            return this == PLUG;
    	}
    	
    	public boolean canRemovePhantomPower(){
            return this == POINT;
    	}
    	
    	public ItemStack getRepresentiveStack(){
    		switch(this){
			case CONTROLLER:
				return new ItemStack(FluxNetworks.fluxController);
			case PLUG:
				return new ItemStack(FluxNetworks.fluxPlug);
			case POINT:
				return new ItemStack(FluxNetworks.fluxPoint);
			case STORAGE:
				return new ItemStack(FluxNetworks.fluxStorage);
    		}
    		return ItemStack.EMPTY;
    	}
    	
    }

	ItemStack getDisplayStack();

    int getNetworkID();

    /**
     * the network this Flux connection is a part of
     */
    IFluxNetwork getNetwork();

    UUID getConnectionOwner();

    PlayerAccess canAccess(EntityPlayer player);

    /**
     * the dimension in which this Flux Connection is located
     */
    World getDimension();

    /**
     * the location of the Flux Connection
     */
    BlockCoords getCoords();

    /**
     * the type of Flux Connection
     */
    ConnectionType getConnectionType();

    /**
     * the maximum RF/t this Flux connection can receive
     */
    long getTransferLimit();
    

    /*
    void onEnergyRemoved(EnumFacing face, long remove);

    void onEnergyAdded(EnumFacing face, long added);
    */

    void setMaxSend(long send);

    void setMaxReceive(long receive);

    /**
     * the higher the priority the sooner the Flux connection will receive power
     */
    int getCurrentPriority();

    boolean isChunkLoaded();
    
    /**
     * the custom name is assigned by the user, this allows easier identification of various Flux connections.
     */
    String getCustomName();

    ITransferHandler getTransferHandler();
    
    void connect(IFluxNetwork network);

    void disconnect(IFluxNetwork network);
}
