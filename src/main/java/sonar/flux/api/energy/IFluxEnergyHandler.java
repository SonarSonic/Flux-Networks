package sonar.flux.api.energy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

/**the handler Flux Networks uses to transfer energy*/
public interface IFluxEnergyHandler {
	
	public EnergyType getEnergyType();
	
	/**if flux networks can render the block as connected*/
	public boolean canRenderConnection(@Nonnull TileEntity tile, @Nullable EnumFacing dir);	
	
	/**if flux networks can energy transfer in the given direction*/
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir);
	
	/**if flux networks can energy transfer in the given direction*/
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir);
	
	/**returns how much energy was added to the tile depending on the TransferType called, this will always be called after canAddEnergy*/
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType);

	/**returns how much energy was added to the tile depending on the TransferType called, this will always be called after canRemoveEnergy*/
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType);
	
}
