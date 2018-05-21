package sonar.flux.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**the handler Flux Networks uses to transfer energy*/
public interface ITileEnergyHandler {
	
	EnergyType getEnergyType();
	
	/**if flux networks can render the block as connected*/
    boolean canRenderConnection(@Nonnull TileEntity tile, @Nullable EnumFacing dir);
	
	/**if flux networks can energy transfer in the given direction*/
    boolean canAddEnergy(TileEntity tile, EnumFacing dir);
	
	/**if flux networks can energy transfer in the given direction*/
    boolean canRemoveEnergy(TileEntity tile, EnumFacing dir);
	
	/**returns how much energy was added to the tile depending on the TransferType called, this will always be called after canAddEnergy*/
    long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType);

	/**returns how much energy was removed from the tile depending on the TransferType called, this will always be called after canRemoveEnergy*/
    long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType);
	
}
