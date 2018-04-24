package sonar.flux.connection.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.flux.api.energy.internal.IFluxTransfer;

public interface ISidedTransfer extends IFluxTransfer {

	EnumFacing getDirection();

	TileEntity getTile();
}
