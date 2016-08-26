package sonar.flux.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.ActionType;

public class FluxWrapper {
	
	/**
	 * @param from the Flux Connection to pull energy from
	 * @param maxTransferRF the maximum amount of RF to extract from the Flux Connection
	 * @param simulate should this be performed or simulated
	 * @return the amount of energy pulled from the Flux Connection.
	 */
	public long pullEnergy(IFlux from, long maxTransferRF, ActionType simulate) {
		return 0;
	}

	/**returns amount of energy used*/
	public long pushEnergy(IFlux to, long maxTransferRF, ActionType simulate) {
		return 0;
	}
	
	/**gets all the nearby Tile Entities from a Flux Connection which can push/pull energy*/
	public Map<TileEntity, EnumFacing> getConnections(IFlux flux){
		return Collections.EMPTY_MAP;
	}
}
